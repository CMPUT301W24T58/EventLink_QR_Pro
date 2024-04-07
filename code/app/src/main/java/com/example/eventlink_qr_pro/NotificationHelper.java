package com.example.eventlink_qr_pro;


// Used the Android Open Source Project's NotificationHelper.kt from
// https://github.com/android/user-interface-samples/blob/main/People/app/src/main/java/com/example/android/people/data/NotificationHelper.kt
// as a reference while creating this class

import static android.content.ContentValues.TAG;
import static androidx.core.content.ContextCompat.getSystemService;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.widget.ArrayAdapter;

import androidx.annotation.WorkerThread;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.ktx.Firebase;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "EVENTLINK_QR_PRO_NOTIF_CHANNEL";
            String description = "For messages from organizers to attendees";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
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
        // adapted from https://stackoverflow.com/questions/16045722/android-notification-is-not-showing
        // adapted version of android dev notification documentation at:
        // https://developer.android.com/develop/ui/views/notifications/build-notification#notify
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            builder.setChannelId("EVENTLINK_QR_PRO_NOTIF_CHANNEL");
        }

        notificationManager.notify(getNewNotifID(), builder.build());
    }

    /**
     * Searches the database to find if there are any new messages that have not been previously received in any events that the provided attendee is checked into
     * @param attendee The attendee for whom to check if messages have been received
     * @param context The context; needed for building a notification
     */
    public static void findNewMessages(Attendee attendee, Context context) {
        ArrayList<String> storedMessageIDs = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Get all of the messages that the attendee already has
        db.collection("attendees")
                .document(attendee.getId())
                .collection("messages")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            storedMessageIDs.add(document.getId());
                            Log.d("notification", "old message marked");
                        }
                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                    }


                    // Get all of the events that the attendee is a part of
                    ArrayList<String> participatingEventIDs = new ArrayList<>();
                    db.collection("events")
                            .get()
                            .addOnCompleteListener(task3 -> {
                                if (task3.isSuccessful()) {
                                    for (QueryDocumentSnapshot eventDoc : task3.getResult()) {
                                        db.collection("events").document(eventDoc.getId())
                                                .collection("attendees")
                                                .get()
                                                .addOnCompleteListener(task2 -> {
                                                    if (task2.isSuccessful()) {
                                                        for (QueryDocumentSnapshot document : task2.getResult()) {
                                                            if (document.getId().equals(attendee.getId())) {
                                                                storedMessageIDs.add(eventDoc.getId());
                                                            }
                                                        }
                                                    } else {
                                                        Log.w(TAG, "Error getting documents.", task2.getException());
                                                    }
                                                });
                                    }
                                } else {
                                    Log.w(TAG, "Error getting documents.", task3.getException());
                                }

                                // Get all new messages
                                db.collection("events")
                                        .get()
                                        .addOnCompleteListener(task4 -> {
                                            if (task4.isSuccessful()) {
                                                for (QueryDocumentSnapshot eventDoc : task4.getResult()) {
                                                    db.collection("events").document(eventDoc.getId())
                                                            .collection("messages")
                                                            .get()
                                                            .addOnCompleteListener(task5 -> {
                                                                if (task5.isSuccessful()) {
                                                                    for (QueryDocumentSnapshot document : task5.getResult()) {
                                                                        if (!storedMessageIDs.contains(document.getId())) {
                                                                            // put message in attendee's collection
                                                                            storedMessageIDs.add(eventDoc.getId());
                                                                            Map<String, String> messageInfo = new HashMap<>();
                                                                            messageInfo.put("title", document.get("title").toString());
                                                                            messageInfo.put("description", document.get("description").toString());
                                                                            db.collection("attendees")
                                                                                    .document(attendee.getId())
                                                                                    .collection("messages")
                                                                                    .document(document.getId())
                                                                                    .set(messageInfo);
                                                                            // pop up the notification
                                                                            buildNotification(document.get("title").toString(), document.get("description").toString(), context);
                                                                            Log.d("notification", "new message added: " + document.get("title").toString() + ", " + document.get("description").toString());
                                                                        }
                                                                    }
                                                                } else {
                                                                    Log.w(TAG, "Error getting documents.", task5.getException());
                                                                }
                                                            });
                                                }
                                            } else {
                                                Log.w(TAG, "Error getting documents.", task4.getException());
                                            }
                                        });
                            });
                });
    }
}