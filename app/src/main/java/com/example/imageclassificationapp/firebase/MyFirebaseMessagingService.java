package com.example.imageclassificationapp.firebase;
import android.util.Log;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FirebaseService";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // If the application is in the foreground handle or display both data and notification FCM messages here.
        // Here is where you can display your own notifications built from a received FCM message.
        super.onMessageReceived(remoteMessage);
        System.out.println("entered onreceive");
        android.os.Debug.waitForDebugger();
        getSharedPreferences("_", MODE_PRIVATE).edit().putString("remote", remoteMessage.getData().toString()).apply();
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
    }


}