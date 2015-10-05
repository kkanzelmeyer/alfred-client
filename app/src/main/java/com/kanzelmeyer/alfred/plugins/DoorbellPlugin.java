package com.kanzelmeyer.alfred.plugins;

import android.content.Context;
import android.util.Log;

import com.alfred.common.datamodel.StateDevice;
import com.alfred.common.datamodel.StateDeviceManager;
import com.alfred.common.handlers.StateDeviceHandler;
import com.alfred.common.messages.StateDeviceProtos;
import com.google.protobuf.ByteString;
import com.kanzelmeyer.alfred.network.Client;
import com.kanzelmeyer.alfred.network.NetworkHandler;
import com.kanzelmeyer.alfred.notifications.Notifications;
import com.kanzelmeyer.alfred.storage.Visitor;
import com.kanzelmeyer.alfred.storage.VisitorLog;
import com.kanzelmeyer.alfred.utils.ConstantManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by kevin on 10/4/15.
 */
public class DoorbellPlugin {

    private static final String TAG = "DoorbellPlugin";
    private DoorbellNetworkHandler mNetworkHandler = null;
    private DoorbellStateHandler mStateHandler = null;
    private Context mContext;
    private String mId = "";

    public DoorbellPlugin(String id, Context context) {
        mContext = context;
        mId = id;
    }

    /**
     * Called to activate the plugin
     */
    public void activate() {
        if(mNetworkHandler == null) {
            mNetworkHandler = new DoorbellNetworkHandler();
            Client.addNetworkHandler(mNetworkHandler);
        }
        if(mStateHandler == null) {
            mStateHandler = new DoorbellStateHandler();
            StateDeviceManager.addDeviceHandler(mId, mStateHandler);
        }
    }

    /**
     * Called to deactivate the plugin
     */
    public void deactivate() {
        if(mNetworkHandler != null) {
            Client.removeNetworkHandler(mNetworkHandler);
            mNetworkHandler = null;
        }
        if(mStateHandler != null) {
            StateDeviceManager.removeDeviceHandler(mId);
            mStateHandler = null;
        }
    }



    /**
     * Nested class to handle network activity
     */
    private class DoorbellNetworkHandler implements NetworkHandler {

        @Override
        public void onConnect(Socket connection) {
            Log.i(TAG, "Network Connection added");
        }

        @Override
        public void onMessageReceived(StateDeviceProtos.StateDeviceMessage msg) {
            Log.i(TAG, "New message received");
            // Update data model
            if(msg != null) {
                StateDevice device = new StateDevice(msg);

                // add or update the device
                StateDeviceManager.updateStateDevice(device);

                // notification and save image
                if (msg.getType() == StateDeviceProtos.StateDeviceMessage.Type.DOORBELL) {
                    if (msg.hasData()) {
                        Notifications.sendDoorbellAlertNotification(mContext, msg);
                        saveVisitor(msg);
                    } else {
                        Log.i(TAG, msg.toString());
                    }
                }
            }
        }


        private void saveVisitor(StateDeviceProtos.StateDeviceMessage msg) {
            Log.i(TAG, "Logging Event");
            Visitor visitor = new Visitor();
            Long time = System.currentTimeMillis();
            ByteString data = msg.getData();
            String filename = "visitor" + System.currentTimeMillis() + ".jpg";
            visitor.setImagePath(filename);
            File imageDirectory = new File(mContext.getFilesDir() + ConstantManager.IMAGE_DIR);

            // create the image directory
            if(!imageDirectory.exists()) {
                Log.i(TAG, "Directory being created? " + imageDirectory.mkdirs());
            }

            // save the image file
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

            // Log the visitor
            visitor.setTime(time);
            visitor.setLocation(msg.getName());
            VisitorLog.logVisitor(visitor, mContext);
        }
    }


    /**
     * Nested class to handle state changes
     */
    private class DoorbellStateHandler implements StateDeviceHandler {

        @Override
        public void onAddDevice(StateDevice stateDevice) {
            Log.i(TAG, "Device added: " + stateDevice.toString());
        }

        @Override
        public void onUpdateDevice(StateDevice stateDevice) {
            // If the state is set to inactive by the client
            Log.i(TAG, "Device updated");
            if(stateDevice.getState() == StateDeviceProtos.StateDeviceMessage.State.INACTIVE) {
                StateDeviceProtos.StateDeviceMessage msg =
                        StateDeviceProtos.StateDeviceMessage.newBuilder()
                        .setId(stateDevice.getId())
                        .setName(stateDevice.getName())
                        .setState(stateDevice.getState())
                        .setType(stateDevice.getType())
                        .build();
                // Send the message
                try {
                    Log.i(TAG, "Sending message: \n" + msg.toString());
                    OutputStream out = Client.getConnection().getOutputStream();
                    msg.writeDelimitedTo(out);
                } catch (IOException e) {
                    Client.removeConnection();
                    Log.e(TAG, "unable to write to output stream", e);
                }
            }
        }

        @Override
        public void onRemoveDevice(StateDevice stateDevice) {

            Log.i(TAG, "Device removed");
        }
    }
}
