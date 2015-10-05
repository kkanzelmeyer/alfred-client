package com.kanzelmeyer.alfred.network;

import com.alfred.common.messages.StateDeviceProtos;

import java.net.Socket;

/**
 * Created by kevin on 10/4/15.
 */
public interface NetworkHandler {

    /**
     * Called when a connection to the server is established
     */
    public void onConnect(Socket connection);

    /**
     * Called when a new message is received
     * @param msg
     */
    public void onMessageReceived(StateDeviceProtos.StateDeviceMessage msg);
}
