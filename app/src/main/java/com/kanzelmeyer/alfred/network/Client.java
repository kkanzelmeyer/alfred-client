package com.kanzelmeyer.alfred.network;

import com.alfred.common.messages.StateDeviceProtos;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kevin on 10/4/15.
 */
public class Client {

    private static Socket mConnection;

    private static List<NetworkHandler> networkHandlers = new ArrayList<>();

    /**
     * Add a Socket connection to the client
     * @param connection
     */
    public static void addConnection(Socket connection) {
        mConnection = connection;
        // notify network handlers
        if(networkHandlers.size() > 0) {
            for(NetworkHandler h : networkHandlers) {
                h.onConnect(connection);
            }
        }
    }

    /**
     * Set the network connection to null
     */
    public static void removeConnection() {

        mConnection = null;
    }

    /**
     *
     * @return
     */
    public static Socket getConnection() {

        return mConnection;
    }

    /**
     * Call whenever a new message is received. This method will notify
     * all registered handlers when a new message is received
     * @param msg
     */
    public static void messageReceived(StateDeviceProtos.StateDeviceMessage msg) {
        if(networkHandlers.size() > 0) {
            for(NetworkHandler h : networkHandlers) {
                h.onMessageReceived(msg);
            }
        }
    }

    /**
     * Add a network handler
     * @param handler
     */
    public static void addNetworkHandler(NetworkHandler handler) {
        if(!networkHandlers.contains(handler)) {
            networkHandlers.add(handler);
        }
    }

    /**
     * Remove network handler
     * @param handler
     */
    public static void removeNetworkHandler(NetworkHandler handler) {
        if(networkHandlers.contains(handler)) {
            networkHandlers.remove(handler);
        }
    }

    /**
     * Remove all network handlers
     */
    public static void removeAllNetworkHandlers() {
        networkHandlers = null;
    }




}
