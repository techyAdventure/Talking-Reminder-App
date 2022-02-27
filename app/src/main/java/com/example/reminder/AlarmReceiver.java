package com.example.reminder;
//
//import android.app.Notification;
//import android.app.NotificationChannel;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.res.AssetFileDescriptor;
//import android.graphics.BitmapFactory;
//import android.graphics.Color;
//import android.media.AudioAttributes;
//import android.media.AudioManager;
//import android.media.MediaPlayer;
//import android.media.RingtoneManager;
//import android.net.Uri;
//import androidx.core.app.NotificationCompat;
//import androidx.core.app.NotificationManagerCompat;
//import androidx.core.content.ContextCompat;
//
//import android.app.NotificationManager;
//import android.content.Context;
//import android.media.MediaPlayer;
//import android.media.RingtoneManager;
//import android.net.Uri;
//
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Environment;
//import android.view.View;
//import android.widget.Button;
//import android.widget.RemoteViews;
//import android.widget.Toast;
//
//import java.io.File;
//import java.io.FileDescriptor;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.util.Objects;
//
//public class  AlarmReceiver extends BroadcastReceiver {
//
//    @Override
//    public void onReceive(Context context, Intent intent) {
//
//        Bundle bundle = intent.getExtras();
//        String text = bundle.getString("event");
//        String date = bundle.getString("date") + " " + bundle.getString("time");
//
//        Intent i = new Intent(context,ListActivity.class);
//        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        PendingIntent pendingIntent = PendingIntent.getActivity(context,1,i,PendingIntent.FLAG_ONE_SHOT);
//        i.putExtra("message", text);
//
//

//        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//
//        RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.notification_layout);
//        //contentView.setImageViewResource(R.id., R.mipmap.ic_launcher);
//        PendingIntent pendingSwitchIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
//        contentView.setOnClickPendingIntent(R.id.flashButton, pendingSwitchIntent);
//        contentView.setTextViewText(R.id.message, text);
//        contentView.setTextViewText(R.id.date, date);
//
//        contentView.setOnClickPendingIntent(R.id.flashButton, pendingSwitchIntent);
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"foxandroid")
//                .setContentTitle(text)
//                .setContentText(date)
//                .setAutoCancel(true)
//                .setSmallIcon(R.drawable.ic_baseline_notifications_24)
//                .setColor(ContextCompat.getColor(context,R.color.purple_500))
//                .setLights(Color.BLUE,100,1000)
//                .setDefaults(NotificationCompat.DEFAULT_ALL)
//                .setPriority(NotificationCompat.PRIORITY_HIGH)
//                .setWhen(System.currentTimeMillis())
//
//                .setVibrate(new long[] {0, 1000, 200,1000 })
//                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.mipmap.notifyme))
//                .setContent(contentView)
//                .setContentIntent(pendingIntent);
//        builder.build().flags  = Notification.FLAG_NO_CLEAR | Notification.PRIORITY_HIGH;
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
//            CharSequence name = "foxandroidReminderChannel";
//            String description = "Channel For Alarm Manager";
//            int importance = NotificationManager.IMPORTANCE_HIGH;
//            NotificationChannel channel = new NotificationChannel("foxandroid",text,importance);
//            channel.setDescription(text);
//            channel.enableVibration(true);
//
//            notificationManager.createNotificationChannel(channel);
//            builder.setChannelId(channel.getId());
//        }
//
//
//
////        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
////            builder.setChannelId("YOUR_PACKAGE_NAME");
////        }
////        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
////            NotificationChannel channel = new NotificationChannel(
////                    "YOUR_PACKAGE_NAME",
////                    "Reminder",
////                    NotificationManager.IMPORTANCE_DEFAULT
////            );
////            if (notificationManager != null) {
////                notificationManager.createNotificationChannel(channel);
////            }
////        }
////        notificationManager.notify(1,builder.build());
//
//        Notification notification = builder.build();
//        notificationManager.notify(1, notification);
//
//
//
//
//        AudioManager audioManager = ((AudioManager) context.getSystemService(Context.AUDIO_SERVICE));
//        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
//
////
////        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
////        notificationManagerCompat.notify(123,builder.build());
//
//
//    }
//}


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.reminder.ListActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class    AlarmReceiver extends BroadcastReceiver {
    private  MediaPlayer mp;
    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle bundle = intent.getExtras();
        String text = bundle.getString("event");
        String date = bundle.getString("stext") ;

        Intent i = new Intent(context, ListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,i,0);
        RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.notification_layout);
        //contentView.setImageViewResource(R.id., R.mipmap.ic_launcher);
        PendingIntent pendingSwitchIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        contentView.setOnClickPendingIntent(R.id.flashButton, pendingSwitchIntent);
        contentView.setTextViewText(R.id.message, text);
        contentView.setTextViewText(R.id.date, date);

        contentView.setOnClickPendingIntent(R.id.flashButton, pendingSwitchIntent);
                File baseDir = Environment.getExternalStorageDirectory();
        String audioPath = baseDir.getAbsolutePath() + "/"+"myTone" + ".mp3";
        File file = new File(audioPath);
        FileInputStream inputStream = null;

        try {
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        mp = new MediaPlayer();
        mp.setAudioAttributes(
                new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build());

        try {
                mp.setDataSource(inputStream.getFD());
                mp.prepare();
                mp.start();
                inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }catch (Exception e) {
            System.out.println("Exception of type : " + e.toString());
            e.printStackTrace();
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"foxandroid")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContent(contentView)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(123,builder.build());


    }
}