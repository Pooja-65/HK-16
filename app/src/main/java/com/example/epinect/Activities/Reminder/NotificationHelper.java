package com.example.epinect.Activities.Reminder;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;

import androidx.core.app.NotificationCompat;

import com.example.epinect.R;


public class NotificationHelper {
    public static void playCustomSound(final Context context) {
        // Delayed execution for 5 seconds
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Get the URI of the custom sound file
                Uri soundUri = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.reminder_alarm);

                // Create a MediaPlayer instance and set the custom sound file as the data source
                MediaPlayer mediaPlayer = MediaPlayer.create(context, soundUri);

                // Set loop to true if you want the sound to repeat until stopped
                mediaPlayer.setLooping(true);

                // Start playing the custom sound
                mediaPlayer.start();

                // Stop playing after 8 seconds
                try {
                    Thread.sleep(8000); // 8 seconds
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // Release the MediaPlayer resources
                mediaPlayer.release();
            }
        }, 5000); // 5000 milliseconds = 5 seconds
    }

    public static void showNotification(Context context, String message) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create a notification channel for Android Oreo and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "reminder_channel";
            String channelName = "Reminder Channel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            notificationManager.createNotificationChannel(channel);
        }

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "reminder_channel")
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Reminder")
                .setContentText(message)  // Set your message here
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // Show the notification
        notificationManager.notify(0, builder.build());
    }
}
