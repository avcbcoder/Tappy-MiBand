package com.av.mainscreen.activity;

import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.av.mainscreen.MainActivity;
import com.av.mainscreen.R;
import com.av.mainscreen.SETTINGS;
import com.rm.rmswitch.RMSwitch;

public class CallActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private RMSwitch mToggle;
    private ImageButton mBack, mEdit;
    private Spinner mOneTap, mDoubleTap;
    private TextView mReplyText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);

        // changing color of status bar
        changeStatusBarColor(R.color.activity_call_toolbar);

        //Extract all the things from layout
        init();

        // set everything according to database
        setState();
    }

    private void init() {
        // find view by ID's
        mBack = findViewById(R.id.activity_call_back);
        mEdit = findViewById(R.id.activity_call_edit);
        mToggle = findViewById(R.id.activity_call_toggle);
        mOneTap = findViewById(R.id.activity_call_spinner_one);
        mDoubleTap = findViewById(R.id.activity_call_spinner_two);
        mReplyText = findViewById(R.id.activity_call_text);

        // Click Listener of back and Edit button
        mBack.setOnClickListener(this);
        mEdit.setOnClickListener(this);

        // Toggle Button
        mToggle.addSwitchObserver(new RMSwitch.RMSwitchObserver() {
            @Override
            public void onCheckStateChange(RMSwitch switchView, boolean isChecked) {
                SETTINGS.Call.ENABLE = isChecked;
                Toast.makeText(CallActivity.this, "Action on call" + (isChecked ? "Enabled" : "Disabled"), Toast.LENGTH_SHORT).show();
            }
        });

        // Spinners
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.array_call, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mOneTap.setAdapter(adapter);
        mDoubleTap.setAdapter(adapter);
        mOneTap.setOnItemSelectedListener(this);
        mDoubleTap.setOnItemSelectedListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.activity_call_back:
                finish();
                break;
            case R.id.activity_call_edit:
                // TO-DO
                LayoutInflater inflater = this.getLayoutInflater();
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                 builder.setView(inflater.inflate(R.layout.activity_call_input, null));
                // Set up the input
//                final EditText input = new EditText(this);
//                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
//                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
//                builder.setView(input);
//                builder.setPositiveButton("POS", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        Toast.makeText(CallActivity.this, "Pos Clicked", Toast.LENGTH_SHORT).show();
//                    }
//                });
//                builder.setNegativeButton("NEG", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        Toast.makeText(CallActivity.this, "Neg Clicked", Toast.LENGTH_SHORT).show();
//                    }
//                });
                AlertDialog dialog = builder.create();
                dialog.show();
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
        switch (view.getId()) {
            case R.id.activity_call_spinner_one:
                SETTINGS.Call.ONE_TAP = pos;
                break;
            case R.id.activity_call_spinner_two:
                SETTINGS.Call.DOUBLE_TAP = pos;
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void setState() {
        mToggle.setChecked(SETTINGS.Call.ENABLE);
        mOneTap.setSelection(SETTINGS.Call.ONE_TAP);
        mDoubleTap.setSelection(SETTINGS.Call.DOUBLE_TAP);
        mReplyText.setText(SETTINGS.Call.TEXT);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void changeStatusBarColor(int color) {
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, color));
    }

}
