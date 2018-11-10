package com.av.mainscreen.activity;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.NumberPicker;

import com.av.mainscreen.R;

public class TimerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        changeStatusBarColor(R.color.activity_call_toolbar);

        NumberPicker numberPicker=findViewById(R.id.time_hour);
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(24);
        numberPicker.setWrapSelectorWheel(false);
    }

    private void changeStatusBarColor(int color) {
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, color));
    }
}
