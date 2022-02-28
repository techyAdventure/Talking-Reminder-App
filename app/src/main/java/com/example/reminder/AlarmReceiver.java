package com.example.reminder;


import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
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
//        String folder = intent.getStringExtra("folder");
//        System.out.println(folder);
//        Log.d(TAG, "The folder = " + folder);

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
        String audioPath = "/storage/emulated/0/Android/data/com.example.reminder/files/"+"dateformat"+"/"+"myTone" + ".mp3";


//        String audioPath = "/storage/emulated/0/Android/data/com.example.reminder/files/" + folder+ "/"+"myTone" + ".mp3";
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