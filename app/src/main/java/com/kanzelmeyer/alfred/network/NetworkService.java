package com.kanzelmeyer.alfred.network;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.alfred.common.messages.StateDeviceProtos;
import com.kanzelmeyer.alfred.MainActivity;
import com.kanzelmeyer.alfred.R;
import com.kanzelmeyer.alfred.SettingsActivity;
import com.kanzelmeyer.alfred.plugins.DoorbellPlugin;

import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;

public class NetworkService extends Service {

    // Network members
    private int mHostPort = 56;
    private String mHostAddress = "192.168.1.25";
    private Socket mSocket = null;
    // Notification
    private NotificationManager mNm;
    private Notification mServiceNotification = null;
    // Logging tag
    private static final String TAG = "NetworkService";
    // Members
    private NetworkThread mNetworkThread = null;
    private Context mContext;
    // Plugins
    private DoorbellPlugin mDoorbellPlugin;

    /**
     * Setter for host port
     *
     * @param port
     */
    private void setPort(String port) {
        mHostPort = Integer.valueOf(port);
    }

    /**
     * Setter for host address
     *
     * @param hostAddress
     */
    private void setHostAddress(String hostAddress) {
        mHostAddress = hostAddress;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Starting network listener");
        mContext = getApplicationContext();

        // Get Preferences
        SharedPreferences sharedPref =
                PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        // Set network info from preferences
        setPort(sharedPref.getString(SettingsActivity.KEY_HOST_PORT,
                getResources().getString(R.string.default_host_port)));
        setHostAddress(sharedPref.getString(SettingsActivity.KEY_HOST_ADDRESS,
                getResources().getString(R.string.default_host_address)));

        // Activate plugins
        mDoorbellPlugin = new DoorbellPlugin(mContext);
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
        Client.removeConnection();
        mSocket = null;
        mDoorbellPlugin.deactivate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Helper method to start the network thread
     */
    private void runListener() {
        Log.i(TAG, "Starting Foreground service");
        if(mServiceNotification != null) {
            startForeground(R.string.network_service_notification_id,
                    mServiceNotification);
            mNetworkThread = new NetworkThread();
            mNetworkThread.start();
        }
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
                        StateDeviceProtos.StateDeviceMessage msg =
                                StateDeviceProtos.StateDeviceMessage.parseDelimitedFrom(inputStream);
                        Log.i(TAG, "Message Received");

                        // safely disconnect if the message is null
                        if (msg == null) {
                            break;
                        }

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
            Log.i(TAG, "Stopping service");

            // deactivate plugins
            mDoorbellPlugin.deactivate();

            // remove client connection
            Client.removeConnection();

            // stop the background service
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean(SettingsActivity.KEY_SERVICE_RUN, false);
            editor.commit();

            // stop service
            stopSelf();
        }
    }

    /**
     * Helper method to display the service notification
     */
    public void showServiceNotification() {
        CharSequence text = mContext.getText(R.string.local_service_started);

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(mContext.getApplicationContext(), 0,
                new Intent(mContext, MainActivity.class), 0);

        // Set the info for the views that show in the notification panel.
        mServiceNotification = new Notification.Builder(mContext)
                .setSmallIcon(R.mipmap.ic_bowtie_24dp)  // the status icon
                .setTicker(text)  // the status text
                .setWhen(System.currentTimeMillis())  // the time stamp
                .setContentTitle(mContext.getText(R.string.local_service_label))  // the label of the entry
                .setContentText(text)  // the contents of the entry
                .setContentIntent(contentIntent)  // The intent to send when the entry is clicked
                .build();

        mNm = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        // Send the notification.
        Log.i(TAG, "Sending notification");
        mNm.notify(R.string.network_service_notification_id, mServiceNotification);
    }
}
