package com.kanzelmeyer.alfred.network;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.alfred.common.messages.StateDeviceProtos;
import com.kanzelmeyer.alfred.R;
import com.kanzelmeyer.alfred.SettingsActivity;
import com.kanzelmeyer.alfred.notifications.Notifications;
import com.kanzelmeyer.alfred.plugins.DoorbellPlugin;

import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;

public class NetworkService extends Service {

    private int mHostPort = 56;
    private String mHostAddress = "192.168.1.25";
    private Socket mSocket = null;
    private static final String TAG = "NetService";
    private Notification mServiceNotification;
    private NetworkThread mNetworkThread = null;
    private Context mContext;
    // Plugins
    private DoorbellPlugin mDoorbellPlugin;

    private void setPort(String port) {
        mHostPort = Integer.valueOf(port);
    }

    private void setHostAddress(String hostAddress) {
        mHostAddress = hostAddress;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Starting network listener");
        mContext = getApplicationContext();

        // Get Preferences
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        // Set network info from preferences
        setPort(sharedPref.getString(SettingsActivity.KEY_HOST_PORT, getResources().getString(R.string.default_host_port)));
        setHostAddress(sharedPref.getString(SettingsActivity.KEY_HOST_ADDRESS, getResources().getString(R.string.default_host_address)));

        // Activate plugins
        mDoorbellPlugin = new DoorbellPlugin("doorbell1", mContext);
        mDoorbellPlugin.activate();

        // start service
        showServiceNotification();
        runListener();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "Stopping network listener");
        super.onDestroy();
        mNetworkThread.interrupt();
        if(mSocket != null) {
            try {
                mSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void runListener() {
        Log.i(TAG, "Starting Foreground service");
        startForeground(Notifications.SERVICE_NOTIFICATION_ID, mServiceNotification);
        mNetworkThread = new NetworkThread();
        mNetworkThread.start();
    }

    /**
     * This thread is responsible for waiting on incoming messages
     * and updating the client handlers when a message is received
     */
    private class NetworkThread extends Thread {
        public void run() {
            try {
                InetAddress host = InetAddress.getByName(mHostAddress);
                mSocket = new Socket(host, mHostPort);
                // Add connection to client
                Client.addConnection(mSocket);
                InputStream inputStream;
                while (true) {
                    if (mSocket.isConnected()) {
                        inputStream = mSocket.getInputStream();
                        StateDeviceProtos.StateDeviceMessage msg = StateDeviceProtos.StateDeviceMessage.parseDelimitedFrom(inputStream);
                        Log.i(TAG, "Message Received.\n");

                        // Notify network handlers
                        Client.messageReceived(msg);
                    }
                }
            } catch (ConnectException ce) {
                Log.e(TAG, "Unable to connect", ce);
                // TODO make a notification for connection errors
            } catch (IOException ex) {
                Log.e(TAG, "Connection error", ex);
            }
            // deactivate plugins
            mDoorbellPlugin.deactivate();

            // remove client connection
            Client.removeConnection();

            // stop the background service
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean(SettingsActivity.KEY_SERVICE_RUN, false);
            editor.commit();
            this.interrupt();
        }
    }


    private void showServiceNotification() {
        Log.i(TAG, "Building notification");
        mServiceNotification = Notifications.showServiceNotification(this);
    }
}
