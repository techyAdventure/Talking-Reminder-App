package com.example.reminder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlarmManager;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.reminder.databinding.ActivityMainBinding;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.theme.overlay.MaterialThemeOverlay;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;


import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


import android.os.Bundle;

public class SettingsActivity extends AppCompatActivity implements TextToSpeech.OnInitListener{

    private ActivityMainBinding binding;
    private MaterialTimePicker picker;
    private Calendar calendar;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private ImageView Tm_btn;
    private ImageView spk;
    dbManager DB;
    private TextToSpeech mTTS;
    private EditText mEditText;
    private SeekBar mSeekBarPitch;
    private SeekBar mSeekBarSpeed;
    private Button mButtonSpeak;
    private TextView date_pc;

    private EditText mTitleText;
    String timeTonotify;
    String text;
    String title;
    String date_to;
    Date date1;
    SimpleDateFormat simpleDateFormat;

    public boolean textToSpeechIsInitialized = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        DB = new dbManager(SettingsActivity.this);
        Tm_btn = findViewById(R.id.TimePickerBTN);
        mButtonSpeak = findViewById(R.id.button_speak);
        mEditText = findViewById(R.id.edit_text);
        mSeekBarPitch = findViewById(R.id.seek_bar_pitch);
        mSeekBarSpeed = findViewById(R.id.seek_bar_speed);
        mTitleText = findViewById(R.id.title_text);
        date_pc = findViewById(R.id.date);
        spk = findViewById(R.id.volume);


        createNotificationChannel();
        spk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speak();
            }
        });

        Tm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showTimePicker();

            }
        });

        Date date = new Date();
        simpleDateFormat = new SimpleDateFormat("E, d MMMM");
        String dateformat = simpleDateFormat.format(date);

        date_pc.setText(dateformat);

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
                       spk.setEnabled(true);
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
    public void saveAll(String title, String date, String time){



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

        String state = Environment.getExternalStorageState();
        boolean mExternalStorageWriteable = false;
        boolean mExternalStorageAvailable = false;
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // Can read and write the media
            mExternalStorageAvailable = mExternalStorageWriteable = true;

        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // Can only read the media
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
        } else {
            // Can't read or write
            mExternalStorageAvailable = mExternalStorageWriteable = false;
        }
        File root = android.os.Environment.getExternalStorageDirectory();
        File dir = new File(root.getAbsolutePath());
        dir.mkdirs();
        File file = new File(dir, "myTone.mp3");


        int test = mTTS.synthesizeToFile((CharSequence) text, null, file,
                "tts");

        text = mEditText.getText().toString();
        title = mTitleText.getText().toString().trim();                               //access the data form the input field
        date_to = date_pc.getText().toString().trim();

        if (title.isEmpty()) {
            Toast.makeText(SettingsActivity.this, "Please Enter text", Toast.LENGTH_SHORT).show();   //shows the toast if input field is empty
        } else {
            if (timeTonotify == null || date_to == null) {                                               //shows toast if date and time are not selected //|| date.equals("date")
                Toast.makeText(SettingsActivity.this, "Please select time", Toast.LENGTH_SHORT).show();
            } else {
                String result = DB.addreminder(title, date_to, timeTonotify);
                mTitleText.setText("");
                mEditText.setText("");
                Intent intent = new Intent(SettingsActivity.this, ListActivity.class);
                intent.putExtra("tile_text", title);
                startActivity(intent);

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
                intent.putExtra("event", title);                                                       //sending data to alarm class to create channel and notification
                intent.putExtra("stext", text);
                pendingIntent = PendingIntent.getBroadcast(SettingsActivity.this,0,intent,0);

;

                calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY,picker.getHour());
                calendar.set(Calendar.MINUTE,picker.getMinute());
                calendar.set(Calendar.SECOND,0);
                calendar.set(Calendar.MILLISECOND,0);

                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY,pendingIntent);

                timeTonotify = FormatTime(picker.getHour(),picker.getMinute());

                Toast.makeText(SettingsActivity.this,"Alarm Set Successfully at "+ timeTonotify + title+text, Toast.LENGTH_SHORT).show();




            }

        });


    }
    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "foxandroidReminderChannel";
            String description = "Channel For Alarm Manager";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("foxandroid",name,importance);
            channel.setDescription(description);

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
            mTTS.setLanguage(Locale.ENGLISH);
            mTTS.setPitch(1);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        mTTS = new TextToSpeech(this, this);
    }
}









