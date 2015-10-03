package com.kanzelmeyer.alfred.network;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.alfred.common.datamodel.StateDevice;
import com.alfred.common.datamodel.StateDeviceManager;
import com.alfred.common.messages.StateDeviceProtos;
import com.google.protobuf.ByteString;
import com.kanzelmeyer.alfred.R;
import com.kanzelmeyer.alfred.SettingsActivity;
import com.kanzelmeyer.alfred.notifications.Notifications;
import com.kanzelmeyer.alfred.storage.Visitor;
import com.kanzelmeyer.alfred.storage.VisitorLog;
import com.kanzelmeyer.alfred.utils.ConstantManager;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;

public class NetworkListenerService extends Service {

    private int mHostPort = 56;
    private String mHostAddress = "192.168.1.25";
    private Socket mSocket = null;
    private static final String TAG = "NetService";
    private Notification mServiceNotification;
    private NetworkThread mNetworkThread = null;

    public NetworkListenerService() {
    }

    public void setPort(String port) {
        this.mHostPort = Integer.valueOf(port);
    }

    public void setHostAddress(String hostAddress) {
        this.mHostAddress = hostAddress;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Starting network listener");

        // Get Preferences
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        // Set network info from preferences
        setPort(sharedPref.getString(SettingsActivity.KEY_HOST_PORT, getResources().getString(R.string.default_host_port)));
        setHostAddress(sharedPref.getString(SettingsActivity.KEY_HOST_ADDRESS, getResources().getString(R.string.default_host_address)));

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

    private class NetworkThread extends Thread {
        public void run() {
            try {
                InetAddress host = InetAddress.getByName(mHostAddress);
                mSocket = new Socket(host, mHostPort);

                while (true) {
                    if (mSocket.isConnected()) {
                        StateDeviceProtos.StateDeviceMessage msg = StateDeviceProtos.StateDeviceMessage.parseDelimitedFrom(mSocket.getInputStream());
                        Log.i(TAG, "Message Received.\n");

                        // Update data model
                        StateDevice device = new StateDevice(msg);
                        StateDeviceManager.updateStateDevice(device);

                        // notification and save image
                        if (msg.getType() == StateDeviceProtos.StateDeviceMessage.Type.DOORBELL) {
                            if(msg.hasData()) {
                                sendDoorbellAlertNotification();
                                saveVisitor(msg);
                            } else {
                                Log.i(TAG, msg.toString());
                            }
                        } else {
                            Log.i(TAG, msg.toString());
                        }
                    }
                }
            } catch (ConnectException ce) {
                Log.e(TAG, "Unable to connect", ce);
            } catch (IOException ex) {
                Log.e(TAG, "Connection error", ex);
            }
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


    private void sendDoorbellAlertNotification() {
        Log.i(TAG, "Building Doorbell Alert notification");
        Notifications.sendDoorbellAlertNotification(this);
    }

    private void saveVisitor(StateDeviceProtos.StateDeviceMessage msg) {
        Context context = this.getApplicationContext();
        Log.i(TAG, "Logging Event");
        Visitor visitor = new Visitor();
        Long time = System.currentTimeMillis();
        ByteString data = msg.getData();
        String filename = "visitor" + System.currentTimeMillis() + ".jpg";
        visitor.setImagePath(filename);
        File imageDirectory = new File(context.getFilesDir() + ConstantManager.IMAGE_DIR);

        // create the image directory
        if(!imageDirectory.exists()) {
            imageDirectory.mkdirs();
        }

        File image = new File(imageDirectory, filename);
        try {
            if(!image.exists()) {
                Log.i(TAG, "File being created? " + image.createNewFile());
            }
            FileOutputStream fos = new FileOutputStream(image, true);
            fos.write(data.toByteArray());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // TODO manage number of photos stored on device (remove old files)
        visitor.setTime(time);
        visitor.setLocation(msg.getName());
        VisitorLog.logVisitor(visitor, context);
    }
}
