package com.av.notify;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

//https://stackoverflow.com/questions/49922455/uwp-mi-band-2-how-to-take-a-value-from-characteriscic?answertab=oldest#tab-top
//https://github.com/Freeyourgadget/Gadgetbridge/issues/1049
public class MainActivity extends AppCompatActivity {
    EditText etInput;
    Button btnNotify, btnExpNotify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnNotify = findViewById(R.id.btnNotify);
        etInput = findViewById(R.id.etInput);
        btnExpNotify = findViewById(R.id.btnExpNotify);

        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "channel id")
                .setSmallIcon(R.drawable.ic_brightness_7_black_24dp)
                .setContentTitle("Title of notify")
                .setContentText("Content Content Content Content Content Content Content ")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        btnNotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MainActivity.this);
                // notificationId is a unique int for each notification that you must define
                notificationManager.notify(101, mBuilder.build());
            }
        });

        btnExpNotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}
