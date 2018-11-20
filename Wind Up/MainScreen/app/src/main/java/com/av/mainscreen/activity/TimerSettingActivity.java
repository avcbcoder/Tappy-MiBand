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
import com.av.mainscreen.database.SyncWithDB;
import com.rm.rmswitch.RMSwitch;

import static com.av.mainscreen.constants.SETTINGS.TIMER.SHOW_TEXT;
import static com.av.mainscreen.constants.SETTINGS.TIMER.VIBRATE_AT_INTERVAL;
import static com.av.mainscreen.constants.SETTINGS.TIMER.VIBRATION_INTERVAL_DELAY;
import static com.av.mainscreen.constants.SETTINGS.TIMER.VIBRATION_INTERVAL_REPEAT;
import static com.av.mainscreen.constants.SETTINGS.TIMER.VIBRATION_STOP_DELAY;
import static com.av.mainscreen.constants.SETTINGS.TIMER.VIBRATION_STOP_REPEAT;
import static com.av.mainscreen.constants.SETTINGS.TIMER.getPosDelay;
import static com.av.mainscreen.constants.SETTINGS.TIMER.getPosRepeat;

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

    @Override
    protected void onResume() {
        super.onResume();
        SyncWithDB.extractSettingsFromDB(this);
        SYNC();
    }

    private void SYNC() {
        tog_showText.setChecked(SHOW_TEXT);
        tog_vibrateAtInterval.setChecked(VIBRATE_AT_INTERVAL);
        spinner_stopDelay.setSelection(getPosDelay(VIBRATION_STOP_DELAY));
        spinner_stopRepeat.setSelection(getPosRepeat(VIBRATION_STOP_REPEAT));
        spinner_intervalDelay.setSelection(getPosDelay(VIBRATION_INTERVAL_DELAY));
        spinner_intervalRepeat.setSelection(getPosRepeat(VIBRATION_INTERVAL_REPEAT));
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
                SHOW_TEXT = isChecked;
                break;
            case R.id.tog_vibrateAtInterval:
                VIBRATE_AT_INTERVAL = isChecked;
                break;
        }
        SyncWithDB.putSettingsInDB(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View child, int pos, long id) {
        int selected = new Integer(((String) parent.getItemAtPosition(pos)).split(" ")[0]);
        switch (parent.getId()) {
            case R.id.spinner_stopDelay:
                VIBRATION_STOP_DELAY = selected;
                break;
            case R.id.spinner_stopRepeat:
                VIBRATION_STOP_REPEAT = selected;
                break;
            case R.id.spinner_intervalDelay:
                VIBRATION_INTERVAL_DELAY = selected;
                break;
            case R.id.spinner_intervalRepeat:
                VIBRATION_INTERVAL_REPEAT = selected;
                break;
        }
        SyncWithDB.putSettingsInDB(this);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
