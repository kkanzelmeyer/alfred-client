# Alfred Android Client
**Under Construction**

## Summary
[**Project Alfred Overview**] (https://github.com/kkanzelmeyer/alfred-common "Alfred Overview")
This Android app is a client for project Alfred. It gives the user to control devices connected to Alfred from an Android phone.

In the [server application] (https://github.com/kkanzelmeyer/alfred-server "Alfred Server") email clients can be added so that they receive notifications when an event happens (doorbell is activated, garage door is opened, etc). With the Android application the user not only receives notifications but also has control over the device. For example, the user can open / close a garage door from the Android client.

This project was originally created 


## Software Overview
### Connecting to Alfred
Connecting the Android Alfred client to the Alfred server requires three components:

1. Connection to the same network as the server. As of now the server is only accessible on it's local network. To connect you must be on the same network (at home, for example). 
2. Alfred Server must be running. This seems obvious, but I wanted to add it here because I still goof this up from time to time.
3. Set the IP Address and Port Number to be the same as the Server setting. The Server IP Address and Server Port are system adjustable parameters (SAPs) on the server and on the Android client. The settings need to match. The IP Address should be the IP Address of your Raspberry Pi, and the port can any available TCP port on your network.

The Android Alfred client has a "Run In Background" setting. When enabled, a background service is constantly listening for messages sent from the Alfred server. It is recommended to enable this service in order to receive real time notifications from connected doorbell devices. At my house we have Alfred running on an old Android phone.
