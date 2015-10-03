package com.kanzelmeyer.alfred.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import com.kanzelmeyer.alfred.MainActivity;
import com.kanzelmeyer.alfred.R;
import com.kanzelmeyer.alfred.VisitorActivity;

/**
 * Created by kevin on 9/27/15.
 */
public class Notifications {

    public static final int SERVICE_NOTIFICATION_ID = 7986;
    public static final int EVENT_NOTIFICATION_ID = 31912;
    private static final String TAG = "NM";


    /**
     * Notification for when the background service is running
     * @param mContext
     * @return
     */
    public static Notification showServiceNotification(Context mContext) {
        NotificationManager mNM;
        Notification serviceNotification;
        Log.i(TAG, "Building notification");

        CharSequence text = mContext.getText(R.string.local_service_started);

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(mContext.getApplicationContext(), 0,
                new Intent(mContext, MainActivity.class), 0);

        // Set the info for the views that show in the notification panel.
        serviceNotification = new Notification.Builder(mContext)
                .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)  // the status icon
                .setTicker(text)  // the status text
                .setWhen(System.currentTimeMillis())  // the time stamp
                .setContentTitle(mContext.getText(R.string.local_service_label))  // the label of the entry
                .setContentText(text)  // the contents of the entry
                .setContentIntent(contentIntent)  // The intent to send when the entry is clicked
                .build();

        mNM = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        // Send the notification.
        Log.i(TAG, "Sending notification");
        mNM.notify(SERVICE_NOTIFICATION_ID, serviceNotification);
        return serviceNotification;
    }

    /**
     * Notification for when a visitor is detected
     * @param mContext
     */
    public static void sendDoorbellAlertNotification(Context mContext) {
        NotificationManager mNM;
        Notification doorbellNotification;
        Log.i(TAG, "Building Doorbell Alert notification");
        // In this sample, we'll use the same text for the ticker and the expanded notification
        CharSequence text = mContext.getText(R.string.doorbell_alert);

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(mContext.getApplicationContext(), 0,
                new Intent(mContext, VisitorActivity.class), 0);

        // The notification sound
        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        // Set the info for the views that show in the notification panel.
        doorbellNotification = new Notification.Builder(mContext)
                .setSmallIcon(android.R.drawable.ic_dialog_alert)  // the status icon
                .setTicker(text)  // the status text
                .setWhen(System.currentTimeMillis())  // the time stamp
                .setContentTitle(mContext.getText(R.string.doorbell_alert_label))  // the label of the entry
                .setContentText(text)  // the contents of the entry
                .setAutoCancel(true)
                .setContentIntent(contentIntent)  // The intent to send when the entry is clicked
                .build();
        doorbellNotification.defaults = Notification.DEFAULT_ALL;
        doorbellNotification.priority = Notification.PRIORITY_HIGH;
        doorbellNotification.flags |= Notification.FLAG_SHOW_LIGHTS;

        mNM = (NotificationManager) mContext.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        // Send the notification.
        Log.i(TAG, "Sending Doorbell Alert notification");
        mNM.notify(EVENT_NOTIFICATION_ID, doorbellNotification);
    }
}
