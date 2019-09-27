package com.hamidelmamoun.to_do.utils;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.hamidelmamoun.to_do.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import static androidx.core.app.NotificationCompat.DEFAULT_SOUND;
import static androidx.core.app.NotificationCompat.DEFAULT_VIBRATE;


public class NotificationReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "com.hamidelmamoun.to_do";
    private static final String TITLE = "ttl";
    private static final String MESSAGE = "msg";
    private static final String ID = "id";
    private static int incrimentalId = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        // Attempt to extract the "message" property from the payload: {"message":"Hello World!"}
        String notificationTitle = intent.getStringExtra(TITLE);
        String notificationText = intent.getStringExtra(MESSAGE);
        int notificationId = intent.getIntExtra(ID,0);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, notificationId, intent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo)
                .setDefaults(DEFAULT_SOUND | DEFAULT_VIBRATE)
                .setContentTitle(notificationTitle)
                .setContentText(notificationText)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(notificationText))
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_MAX);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

// notificationId is a unique int for each notification that you must define
        notificationManager.notify(notificationId, mBuilder.build());
    }

    public static void NotifyUserSoon(Context context, String title, String message, String reminderTime){
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent notificationIntent = new Intent(context, NotificationReceiver.class);
        notificationIntent.putExtra(TITLE,title);
        notificationIntent.putExtra(MESSAGE,message);
        int id = new Random().nextInt(100); // [0, 60] + 20 => [20, 80]
        notificationIntent.putExtra(ID,id);
        PendingIntent broadcast = PendingIntent.getBroadcast(context, id, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Date future = new Date();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

        try { future = format.parse(reminderTime);}
        catch (ParseException e) { e.printStackTrace();}
        Date now = new Date();
        if(now.getTime() < future.getTime())
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, future.getTime()  , broadcast);
    }

}