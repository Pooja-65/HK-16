package com.example.epinect.Activities.Reminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Handle the alarm here
        String message = intent.getStringExtra("message");

        // Play a custom sound
        NotificationHelper.playCustomSound(context);

        // Show a notification
        NotificationHelper.showNotification(context, message);
    }
}
