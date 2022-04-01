package com.example.reminder;

import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlarmManager;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;


import android.content.Context;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;

import android.os.SystemClock;
import android.view.View;

import android.widget.Button;

import android.widget.ImageView;

import android.widget.Toast;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.theme.overlay.MaterialThemeOverlay;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;


import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.EditText;
import android.widget.SeekBar;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;


import android.os.Bundle;

public class SettingsActivity extends AppCompatActivity implements TextToSpeech.OnInitListener{


    private MaterialTimePicker picker;
    private Calendar calendar;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;

    public Button Tm_btn;
    private TextToSpeech mTTS;
    private EditText mEditText;
    private SeekBar mSeekBarPitch;
    private SeekBar mSeekBarSpeed;
    private Button mButtonSpeak;
    private Button date_pc;


    private Context context;
    private EditText mTitleText;
    public String timeTonotify;
    public static String text;
    public static String title;
    public static String folder_main;
    public static String status_text;

    String date_to;
    Date date1;
    SimpleDateFormat simpleDateFormat;
    Random random;
    myAdapter adapter;
    dbManager DB;
    Button snooze;
    public static final String NOTIFICATION_CHANNEL_ID = "10001" ;
    private final static String default_notification_channel_id = "default" ;
    ArrayList<Model> dataholder = new ArrayList<Model>();                                               //Array list to add reminders and display in recyclerview
    public boolean textToSpeechIsInitialized = false;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        random = new Random();
        adapter = new myAdapter(dataholder,context);
        context = getApplicationContext();

        DB = new dbManager(SettingsActivity.this);
        Tm_btn = findViewById(R.id.TimePickerBTN);
        mButtonSpeak = findViewById(R.id.button_speak);
        mEditText = findViewById(R.id.edit_text);
        mSeekBarPitch = findViewById(R.id.seek_bar_pitch);
        mSeekBarSpeed = findViewById(R.id.seek_bar_speed);
        mTitleText = findViewById(R.id.title_text);
        date_pc = findViewById(R.id.date);

        createNotificationChannel();
        getWindow().setStatusBarColor(ContextCompat.getColor(SettingsActivity.this,R.color.toolbar));

        Date date = new Date();
        simpleDateFormat = new SimpleDateFormat("E, dd MMM");
        String dateformat = simpleDateFormat.format(date);

        folder_main = String.valueOf(random.nextInt(1000));

        date_pc.setText(dateformat);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("h:mm a");
        Tm_btn.setText(simpleDateFormat2.format(calendar.getTime()));

        Tm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker();
            }
        });

        date_pc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        mTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    textToSpeechIsInitialized = true;

                    int result = mTTS.setLanguage(Locale.US);

                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "Language not supported");
                    } else {
                       mButtonSpeak.setEnabled(true);
                    }
                } else {
                    Log.e("TTS", "Initialization failed");
                }
                if (textToSpeechIsInitialized) {  // <--- add this line
                    speak();
                } else {
                    // try again later
                }
            }
        });

        mButtonSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speak();
            }
        });

    }

    private void showDatePicker() {

        MaterialDatePicker.Builder builder = MaterialDatePicker.Builder.datePicker();
        builder.setTheme(R.style.AppBaseTheme);
        builder.setTitleText("Select Date");

        final MaterialDatePicker<Long>materialDatePicker = builder.build();
        materialDatePicker.show(getSupportFragmentManager(),"DATE_PICKER");
        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
            @Override
            public void onPositiveButtonClick(Long selection) {
                TimeZone timeZoneUTC = TimeZone.getDefault();
                // It will be negative, so that's the -1
                int offsetFromUTC = timeZoneUTC.getOffset(new Date().getTime()) * -1;
                date1 = new Date(selection + offsetFromUTC);
                date_pc.setText(simpleDateFormat.format(date1));
            }
        });

    }


    private void speak() {

        float pitch = (float) mSeekBarPitch.getProgress() / 50;
        if (pitch < 0.1) pitch = 0.1f;
        float speed = (float) mSeekBarSpeed.getProgress() / 50;
        if (speed < 0.1) speed = 0.1f;

        mTTS.setPitch(pitch);
        mTTS.setSpeechRate(speed);

        mTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null);


        text = mEditText.getText().toString();
        title = mTitleText.getText().toString().trim();                               //access the data form the input field
        date_to = date_pc.getText().toString().trim();

        if (!text.isEmpty()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                String fileName = "/myTone.mp3";
                File f = new File(getExternalFilesDir(null) + "/", folder_main);
                File file = new File(f, fileName);
                if (!f.exists()) {
                    f.mkdir();
                }
                Log.d(TAG, "The file path = " + file.getAbsolutePath());
                int test = mTTS.synthesizeToFile((CharSequence) text, null, file,
                        "tts");
            }
        }

            if (title.isEmpty()) {
                Toast.makeText(SettingsActivity.this, "Please Enter title", Toast.LENGTH_SHORT).show();   //shows the toast if input field is empty
            } else {
                if (timeTonotify == null || date_to == null ) {                                               //shows toast if date and time are not selected //|| date.equals("date")
                    Toast.makeText(SettingsActivity.this, "Please select time", Toast.LENGTH_SHORT).show();
                }else if(text.isEmpty()){
                    Toast.makeText(SettingsActivity.this, "Please Enter Message", Toast.LENGTH_SHORT).show();
                }
                else {
                    status_text = "Upcoming";
                    String result = DB.addreminder(title, date_to, timeTonotify);
                    mTitleText.setText("");
                    mEditText.setText("");
                    Intent intent = new Intent(SettingsActivity.this, ListActivity.class);
                    startActivity(intent);
                    Toast.makeText(SettingsActivity.this, "Alarm Set Successfully at " + timeTonotify, Toast.LENGTH_SHORT).show();
                }
            }

        }

    @Override
    protected void onDestroy() {
        if (mTTS != null) {
            mTTS.stop();
            mTTS.shutdown();
        }

        super.onDestroy();
    }
    private void showTimePicker() {

        text = mEditText.getText().toString();
        title = mTitleText.getText().toString().trim();

        picker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(12)
                .setMinute(0)
                .setTitleText("Select Alarm Time")
                .build();

        picker.show(getSupportFragmentManager(),"foxandroid");
        picker.addOnPositiveButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    Intent intent = new Intent(SettingsActivity.this,
                            AlarmReceiver.class);

                    pendingIntent = PendingIntent.getBroadcast(SettingsActivity.this, 0, intent, 0);

                    calendar = Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY, picker.getHour());
                    calendar.set(Calendar.MINUTE, picker.getMinute());
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MILLISECOND, 0);

                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

                    timeTonotify = FormatTime(picker.getHour(), picker.getMinute());
                    Tm_btn.setText(timeTonotify);
                    Log.d(TAG, "The folder Settings= " + folder_main);

            }

        });


    }

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "TalkingReminder";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("foxandroid",name,importance);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }


    }
    public String FormatTime(int hour, int minute) {                                                //this method converts the time into 12hr farmat and assigns am or pm

        String time;
        time = "";
        String formattedMinute;

        if (minute / 10 == 0) {
            formattedMinute = "0" + minute;
        } else {
            formattedMinute = "" + minute;
        }


        if (hour == 0) {
            time = "12" + ":" + formattedMinute + " AM";
        } else if (hour < 12) {
            time = hour + ":" + formattedMinute + " AM";
        } else if (hour == 12) {
            time = "12" + ":" + formattedMinute + " PM";
        } else {
            int temp = hour - 12;
            time = temp + ":" + formattedMinute + " PM";
        }

        return time;
    }

    @Override
    public void onInit(int i) {

            if (i == TextToSpeech.SUCCESS) {
                //Setting speech Language
                mTTS.setLanguage(Locale.US);
                mTTS.setPitch(1);
            }
    }
    @Override
    protected void onResume() {
        super.onResume();
        mTTS = new TextToSpeech(this, this);
    }
}









