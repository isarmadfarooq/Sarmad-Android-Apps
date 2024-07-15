package com.sarmadtechempire.blogapp.NotificationUtils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.sarmadtechempire.blogapp.MainActivity;
import com.sarmadtechempire.blogapp.R;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    private static final String CHANNEL_ID = "CHANNEL_ID";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (remoteMessage.getNotification() != null) {
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();
            String imageUrl = remoteMessage.getNotification().getImageUrl() != null ? remoteMessage.getNotification().getImageUrl().toString() : null;

            Log.d(TAG, "Notification received: " + title + " - " + body);
            showNotification(title, body, imageUrl);
        } else {
            Log.e(TAG, "Notification payload is null");
        }
    }

    private void showNotification(String title, String body, String imageUrl) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.VIBRATE) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "Vibrate permission not granted");
            return;
        }

        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        try {
            RingtoneManager.getRingtone(getApplicationContext(), notificationSound).play();
        } catch (Exception e) {
            Log.e(TAG, "Failed to play ringtone", e);
        }

        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (v != null) {
            v.vibrate(new long[]{100, 300, 300, 300}, -1);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body));

        Intent resultIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (mNotificationManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Channel human readable title", NotificationManager.IMPORTANCE_HIGH);
                mNotificationManager.createNotificationChannel(channel);
                builder.setChannelId(CHANNEL_ID);
            }
            mNotificationManager.notify(1, builder.build());
        }
    }
}
