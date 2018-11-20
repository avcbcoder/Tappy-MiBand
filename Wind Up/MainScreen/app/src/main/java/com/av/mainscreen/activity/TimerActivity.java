package com.av.mainscreen.activity;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.NumberPicker;

import com.av.mainscreen.R;
import com.av.mainscreen.constants.SETTINGS;
import com.av.mainscreen.database.SyncWithDB;

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
        long time = time_hour.getValue() * 60 + time_min.getValue() * 60 + time_sec.getValue();
        long interval = interval_hour.getValue() * 60 + interval_min.getValue() * 60 + interval_sec.getValue();
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
                break;
            case R.id.btn_stop:
                break;
        }
    }
}
