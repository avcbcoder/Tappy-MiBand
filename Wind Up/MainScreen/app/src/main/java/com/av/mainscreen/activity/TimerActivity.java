package com.av.mainscreen.activity;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.av.mainscreen.R;
import com.av.mainscreen.constants.SETTINGS;
import com.av.mainscreen.database.SyncWithDB;
import com.av.mainscreen.service.ForegroundService;

public class TimerActivity extends AppCompatActivity implements NumberPicker.OnValueChangeListener, View.OnClickListener {

    ImageButton btn_back, btn_setting;
    Button btn_start, btn_stop;
    NumberPicker time_hour, time_min, time_sec,
            interval_hour, interval_min, interval_sec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        init();
        changeStatusBarColor(R.color.activity_call_toolbar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SyncWithDB.extractSettingsFromDB(this);
        SYNC();
    }

    private void SYNC() {
        setValues(time_hour, time_min, time_sec, SETTINGS.TIMER.TIME);
        setValues(interval_hour, interval_min, interval_sec, SETTINGS.TIMER.INTERVAL);
        if (isRunning) {
            btn_start.setText("RESTART");
            btn_stop.setEnabled(true);
            btn_stop.setAlpha(1f);
        } else {
            btn_stop.setAlpha(0.5f);
            btn_stop.setEnabled(false);
            btn_start.setText("START");
        }
    }

    private void setValues(NumberPicker time_hour, NumberPicker time_min, NumberPicker time_sec, int time) {
        int min = time / 60;
        int sec = time % 60;
        int hr = min / 60;
        min = min % 60;
        time_hour.setValue(hr);
        time_min.setValue(min);
        time_sec.setValue(sec);
    }

    private void init() {
        btn_setting = findViewById(R.id.setting);
        btn_back = findViewById(R.id.back);
        btn_start = findViewById(R.id.btn_start);
        btn_stop = findViewById(R.id.btn_stop);
        time_hour = findViewById(R.id.time_hour);
        time_min = findViewById(R.id.time_min);
        time_sec = findViewById(R.id.time_sec);
        interval_hour = findViewById(R.id.interval_hour);
        interval_min = findViewById(R.id.interval_min);
        interval_sec = findViewById(R.id.interval_sec);

        btn_setting.setOnClickListener(this);
        btn_back.setOnClickListener(this);
        btn_start.setOnClickListener(this);
        btn_stop.setOnClickListener(this);

        setupNumberPicker(time_hour, 24);
        setupNumberPicker(time_min, 60);
        setupNumberPicker(time_sec, 60);
        setupNumberPicker(interval_hour, 24);
        setupNumberPicker(interval_min, 60);
        setupNumberPicker(interval_sec, 60);
    }

    private void setupNumberPicker(NumberPicker picker, int max) {
        picker.setMaxValue(max);
        picker.setWrapSelectorWheel(false);
        picker.setOnValueChangedListener(this);
    }

    private void changeStatusBarColor(int color) {
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, color));
    }

    @Override
    public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {
        int time = time_hour.getValue() * 60 + time_min.getValue() * 60 + time_sec.getValue();
        int interval = interval_hour.getValue() * 60 + interval_min.getValue() * 60 + interval_sec.getValue();
        SETTINGS.TIMER.TIME = time;
        SETTINGS.TIMER.INTERVAL = interval;
        SyncWithDB.putSettingsInDB(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.setting:
                startActivity(new Intent(TimerActivity.this, TimerSettingActivity.class));
                break;
            case R.id.back:
                finish();
                break;
            case R.id.btn_start:
                if (ForegroundService.state) {
                    Toast.makeText(this, "Connect your band first", Toast.LENGTH_SHORT).show();
                } else {
                    long time = SETTINGS.TIMER.TIME;
                    if (time < 5) {
                        Toast.makeText(this, "Minimum time can be 5 seconds", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    btn_start.setText("RESTART");
                    btn_stop.setEnabled(true);
                    btn_stop.setAlpha(1f);
                    View parentLayout = findViewById(android.R.id.content);
                    Toast.makeText(this, "Timer started for " + hms((int) time), Toast.LENGTH_SHORT).show();
                    /*Snackbar.make(parentLayout, "Timer started for " + hms((int) time), Snackbar.LENGTH_LONG)
                            .setAction("CLOSE", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                }
                            })
                            .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                            .show();
                    */
                    startTimer((int) time, (int) SETTINGS.TIMER.INTERVAL);
                }
                break;
            case R.id.btn_stop:
                btn_start.setText("START");
                btn_stop.setEnabled(false);
                btn_stop.setAlpha(0.5f);
                if (isRunning)
                    stopTimer();
                break;
        }
    }

    public static boolean isRunning;
    public static long lastStart = 0;

    private void startTimer(final int time, final int interval) {
        Log.e(TAG, "startTimer: " + time);
        final long curr = System.currentTimeMillis();
        lastStart = curr;
        isRunning = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(time * 1000);
                } catch (Exception e) {
                }
                if (isRunning)
                    stopTimer();
            }
        }).start();
        startIntervals(curr, interval);
    }

    private void startIntervals(final long curr, final int interval) {
        if (interval < 5)
            return;
        Log.e(TAG, "startIntervals: ");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.e(TAG, "run: sleeping for " + interval);
                    Thread.sleep(interval * 1000);
                } catch (Exception e) {
                }
                if (lastStart == curr && isRunning) {
                    interval();
                    startIntervals(curr, interval);
                } else {
                    Log.e(TAG, "run: not match " + lastStart + " " + curr);
                }
            }
        }).start();
    }

    private void interval() {
        // display on band
        Log.e(TAG, "interval: ");
    }

    private static final String TAG = "TimerActivity";

    private void stopTimer() {
        isRunning = false;
        try {
            btn_stop.setEnabled(false);
            btn_stop.setAlpha(0.5f);
            btn_start.setText("START");
        } catch (Exception e) {
        }
        Log.e(TAG, "stopTimer: ");
    }

    private String hms(int time) {
        int min = time / 60;
        int sec = time % 60;
        int hr = min / 60;
        min = min % 60;
        return ((hr != 0) ? (hr + " hour ") : "")
                + ((min != 0) ? (min + " minute ") : "")
                + (sec + " seconds ");
    }


}
