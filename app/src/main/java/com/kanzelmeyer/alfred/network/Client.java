package com.kanzelmeyer.alfred.network;

import com.alfred.common.messages.StateDeviceProtos;
import com.alfred.common.network.NetworkHandler;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kevin on 10/4/15.
 */
public class Client {

    // Instance of the client connection
    private static Socket mConnection;

    // List of network handlers
    private static List<NetworkHandler> networkHandlers = new ArrayList<>();

    // List of service handlers
    private static List<ServiceHandler> serviceHandlers = new ArrayList<>();

    /* List of UI Update Handlers - an activity can register as one of these handlers
       if it needs to be notified when a device is updated, so it can redraw something
       if needed
     */
    private static List<UIUpdateHandler> uiUpdateHandlers = new ArrayList<>();

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
     * Get the client connection instance
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


    /**
     * Add a service handler
     * @param handler
     */
    public static void addServiceHandler(ServiceHandler handler) {
        if(!serviceHandlers.contains(handler)) {
            serviceHandlers.add(handler);
        }
    }

    /**
     * Remove service handler
     * @param handler
     */
    public static void removeServiceHandler(ServiceHandler handler) {
        if(serviceHandlers.contains(handler)) {
            serviceHandlers.remove(handler);
        }
    }

    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
     *  UI Update Handlers
     * - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */

    /**
     *
     * @param handler
     */
    public static void addUIUpdateHandler(UIUpdateHandler handler) {
        if(!uiUpdateHandlers.contains(handler)) {
            uiUpdateHandlers.add(handler);
        }
    }

    /**
     *
     * @param handler
     */
    public static void removeUIUpdateHandler(UIUpdateHandler handler) {
        if(uiUpdateHandlers.contains(handler)) {
            uiUpdateHandlers.remove(handler);
        }
    }

    /**
     *
     */
    public static void refreshUIHandlers() {
        for(UIUpdateHandler handler : uiUpdateHandlers) {
            handler.refreshOnDeviceUpdate();
        }
    }


}
