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

public class TimerSettingActivity extends AppCompatActivity implements View.OnClickListener, RMSwitch.RMSwitchObserver, AdapterView.OnItemSelectedListener {

    ImageButton btn_back;
    RMSwitch tog_showText, tog_vibrateAtInterval;
    Spinner spinner_stopDelay, spinner_stopRepeat, spinner_intervalDelay, spinner_intervalRepeat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer_setting);

        init();
    }

    private void init() {
        btn_back = findViewById(R.id.btn_back);
        tog_showText = findViewById(R.id.tog_showText);
        tog_vibrateAtInterval = findViewById(R.id.tog_vibrateAtInterval);
        spinner_stopDelay = findViewById(R.id.spinner_stopDelay);
        spinner_stopRepeat = findViewById(R.id.spinner_stopRepeat);
        spinner_intervalDelay = findViewById(R.id.spinner_intervalDelay);
        spinner_intervalRepeat = findViewById(R.id.spinner_intervalRepeat);

        btn_back.setOnClickListener(this);
        tog_showText.addSwitchObserver(this);
        tog_vibrateAtInterval.addSwitchObserver(this);
        setupSpinner(spinner_stopDelay, R.array.array_vibration_delay, spinner_stopRepeat, R.array.array_vibration_repeat, true);
        setupSpinner(spinner_intervalDelay, R.array.array_vibration_delay, spinner_intervalRepeat, R.array.array_vibration_repeat, true);
    }

    private void setupSpinner(Spinner delay, int array_delay, Spinner repeat, int array_repeat, boolean isTap) {
        ArrayAdapter<CharSequence> adapterDelay = ArrayAdapter.createFromResource(this,
                array_delay, (isTap) ? R.layout.spinner_item2 : R.layout.spinner_item1);
        adapterDelay.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        delay.setAdapter(adapterDelay);

        ArrayAdapter<CharSequence> adapterRepeat = ArrayAdapter.createFromResource(this,
                array_repeat, (isTap) ? R.layout.spinner_item2 : R.layout.spinner_item1);
        adapterRepeat.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        repeat.setAdapter(adapterRepeat);

        /*Add listeners*/
        delay.setOnItemSelectedListener(this);
        repeat.setOnItemSelectedListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
        }
    }

    @Override
    public void onCheckStateChange(RMSwitch switchView, boolean isChecked) {
        switch (switchView.getId()) {
            case R.id.tog_showText:
                break;
            case R.id.tog_vibrateAtInterval:
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View child, int pos, long id) {
        int selected = new Integer(((String) parent.getItemAtPosition(pos)).split(" ")[0]);
        switch (parent.getId()) {
            case R.id.spinner_stopDelay:
                break;
            case R.id.spinner_stopRepeat:
                break;
            case R.id.spinner_intervalDelay:
                break;
            case R.id.spinner_intervalRepeat:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
