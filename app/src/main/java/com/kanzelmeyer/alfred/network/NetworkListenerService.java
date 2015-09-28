package com.kanzelmeyer.alfred.network;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.EventLog;
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

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Calendar;

public class NetworkListenerService extends Service {

    private int _hostPort = 4321;
    private String _hostAddress = "127.0.0.1";
    private Socket _socket = null;
    private static final String TAG = "NetService";
    private Notification serviceNotification;
    private NetworkThread networkThread = null;

    public NetworkListenerService() {
    }

    public void setPort(String port) {
        this._hostPort = Integer.valueOf(port);
    }

    public void setHostAddress(String hostAddress) {
        this._hostAddress = hostAddress;
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
        networkThread.interrupt();
        try {
            _socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void runListener() {
        Log.i(TAG, "Starting Foreground service");
        startForeground(Notifications.SERVICE_NOTIFICATION_ID, serviceNotification);
        networkThread = new NetworkThread();
        networkThread.start();
    }

    private class NetworkThread extends Thread {
        public void run() {
            try {
                InetAddress host = InetAddress.getByName(_hostAddress);
                _socket = new Socket(host, _hostPort);

                while (true) {
                    if (_socket.isConnected()) {
                        StateDeviceProtos.StateDeviceMessage msg = StateDeviceProtos.StateDeviceMessage.parseDelimitedFrom(_socket.getInputStream());
                        Log.i(TAG, "Message Received.\n" + msg.toString());

                        // Update data model
                        StateDevice device = new StateDevice(msg);
                        StateDeviceManager.updateStateDevice(device);

                        // notification and save image
                        sendDoorbellAlertNotification();
                        if (msg.getType() == StateDeviceProtos.StateDeviceMessage.Type.DOORBELL) {
                            saveVisitor(msg);
                        }
                    }
                }
            } catch (IOException ex) {
                this.stop();
                ex.printStackTrace();
            }
        }
    }


    private void showServiceNotification() {
        Log.i(TAG, "Building notification");
        serviceNotification = Notifications.showServiceNotification(this);
    }


    private void sendDoorbellAlertNotification() {
        Log.i(TAG, "Building Doorbell Alert notification");
        Notifications.sendDoorbellAlertNotification(this);
    }

    private void saveVisitor(StateDeviceProtos.StateDeviceMessage msg) {
        Log.i(TAG, "Logging Event");
        Visitor visitor = new Visitor();
        Long time = System.currentTimeMillis();
        if (msg.hasData()) {
            // TODO save image
            ByteString data = msg.getData();
            String imagePath = "img/visitor" + System.currentTimeMillis() + ".jpg";
            File image = new File(imagePath);
            visitor.setImagePath(imagePath);
            try {
                FileUtils.writeByteArrayToFile(image, data.toByteArray());
                Log.i(TAG, "Saving image " + imagePath);
            } catch (IOException e) {
                Log.e(TAG, "Can't write to file", e);
            }
        }

        visitor.setTime(time);
        visitor.setLocation(msg.getName());
        VisitorLog.logEvent(visitor, this.getApplicationContext());
    }
}
