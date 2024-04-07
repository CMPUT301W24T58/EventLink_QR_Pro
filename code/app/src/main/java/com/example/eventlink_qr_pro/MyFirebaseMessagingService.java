package com.example.eventlink_qr_pro;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * A service that extends {@link FirebaseMessagingService}. This service is responsible for handling
 * messages received from Firebase Cloud Messaging (FCM). When a message is received, it extracts the title and body
 * from the notification and creates a local notification to inform the user.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    /**
     * Called when a message is received.
     *
     * This method is called on the app's main thread so long-running operations,
     * such as database or network access, should be performed asynchronously.
     *
     * @param remoteMessage An object representing the message received from Firebase Cloud Messaging.
     */
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        String title = remoteMessage.getNotification().getTitle();
        String content = remoteMessage.getNotification().getBody();
        NotificationHelper.buildNotification(title, content, this);
    }
}
