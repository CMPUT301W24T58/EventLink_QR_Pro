package com.example.eventlink_qr_pro;


// Used the Android Open Source Project's NotificationHelper.kt from
// https://github.com/android/user-interface-samples/blob/main/People/app/src/main/java/com/example/android/people/data/NotificationHelper.kt
// as a reference while creating this class

import static androidx.core.content.ContextCompat.getSystemService;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
/**
 * A utility class that aids in the creation and management of notification channels and notifications
 * within the application. It allows for sending notifications to the user from the application, specifically
 * designed for messages from organizers to attendees.
 */
public class NotificationHelper {

    private final static String CHANNEL_ID = "EVENTLINK_QR_PRO_NOTIF_CHANNEL";
    private static int notifID;

    /**
     * Gets and increments the notification ID
     * @return the new notification ID
     */
    public static int getNewNotifID() {
        return ++notifID;
    }

    /**
     * Sets up the notification channel for the notifications to go through for the android os
     * Based heavily on the Android docs "Create and manage notification channels" page at:
     *     <a href="https://developer.android.com/develop/ui/views/notifications/channels#java">...</a>
     * @param context The context to be used for within the NotificationManager to push to the device
     */
    public static void setUpNotificationChannels(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            CharSequence name = "EVENTLINK_QR_PRO_NOTIF_CHANNEL";
            String description = "For messages from organizers to attendees";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = null;
            channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system. You can't change the importance
            // or other notification behaviors after this.
            // the following line was adapted as in https://stackoverflow.com/questions/76383987/notification-manager-getsystemservice-call-not-working
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * Builds a notification with a given text and content
     * @param titleText The title text to be displayed
     * @param contentText The content of the notification to be displayed
     * @param context The source context for use in the builder
     */
    public static void buildNotification(String titleText, String contentText, Context context) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(titleText)
                .setContentText(contentText)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify();
    }
}