package com.av.mainscreen.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.ToggleButton;

import com.av.mainscreen.R;
import com.av.mainscreen.constants.SETTINGS;
import com.rm.rmswitch.RMSwitch;

public class TimerSettingActivity extends AppCompatActivity {

    ImageButton btn_back;
    RMSwitch tog_showText, tog_vibrateAtInterval;
    Spinner spinner_stopDelay, spinner_stopRepeat, spinner_intervalDelay, spinner_intervalRepeat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer_setting);
    }
}
