package com.av.mainscreen.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.av.mainscreen.R;
import com.av.mainscreen.constants.SETTINGS;
import com.rm.rmswitch.RMSwitch;

public class CallActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private static final String TAG = "CallActivity";
    private static final int REQ_CODE_SMS = 101;
    private static final int REQ_CODE_CONTACTS = 201;
    private static final int REQ_CODE = 301;

    private RMSwitch mToggle;
    private ImageButton mBack, mEdit;
    private Spinner mOneTap, mDoubleTap;
    private TextView mReplyText;

    public static final int MAX_LIMIT = 120;

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

        // ask for sending sms permission
//        ActivityCompat.requestPermissions(CallActivity.this,
//                new String[]{Manifest.permission.SEND_SMS},
//                REQ_CODE_SMS);

        // ask for reading contacts permission
//        ActivityCompat.requestPermissions(CallActivity.this,
//                new String[]{Manifest.permission.READ_CONTACTS},
//                REQ_CODE_CONTACTS);

        ActivityCompat.requestPermissions(CallActivity.this,
                new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.SEND_SMS},
                REQ_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQ_CODE_SMS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e(TAG, "onRequestPermissionsResult: PermissionGranted for SMS");
                } else {
                    Log.e(TAG, "onRequestPermissionsResult: PermissionDenied for SMS");
                }
                return;
            }
            case REQ_CODE_CONTACTS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e(TAG, "onRequestPermissionsResult: PermissionGranted for READ CONTACT");
                } else {
                    Log.e(TAG, "onRequestPermissionsResult: PermissionDenied for READ CONTACT");
                }
                return;
            }
            case REQ_CODE: {
                for (int i = 0; i < grantResults.length; i++) {
                    int res = grantResults[i];
                    if (res == PackageManager.PERMISSION_DENIED)
                        Log.e(TAG, "onRequestPermissionsResult: Denied " + permissions[i]);
                }
                return;
            }
        }
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
                SETTINGS.CALL.ENABLE = isChecked;
                Toast.makeText(CallActivity.this, "Action on call" + (isChecked ? "Enabled" : "Disabled"), Toast.LENGTH_SHORT).show();
                setState();
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
                // Inflate
                final LayoutInflater inflater = this.getLayoutInflater();
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                View v = inflater.inflate(R.layout.activity_call_input, null);
                builder.setView(v);

                //show
                final AlertDialog dialog = builder.create();
                dialog.show();
                // fvb
                final Button save = v.findViewById(R.id.activity_call_alert_save);
                final Button cancel = v.findViewById(R.id.activity_call_alert_cancel);
                final TextView limit = v.findViewById(R.id.activity_call_alert_limit);
                final EditText input = v.findViewById(R.id.activity_call_alert_input);

                input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_LIMIT)});
                limit.setText("0/" + MAX_LIMIT);

                //listeners
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.cancel();
                    }
                });
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String msg = input.getText().toString();
                        if (msg.length() == 0)
                            Toast.makeText(CallActivity.this, "Enter some text", Toast.LENGTH_SHORT).show();
                        else
                            SETTINGS.CALL.TEXT = msg;
                        setState();
                        dialog.cancel();
                    }
                });
                input.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        limit.setText((editable.toString().length()) + "/" + MAX_LIMIT);
                    }
                });
                break;
        }
        setState();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
        switch (view.getId()) {
            case R.id.activity_call_spinner_one:
                break;
            case R.id.activity_call_spinner_two:
                break;
        }
        setState();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void setState() {
        if (mToggle.isChecked() != SETTINGS.CALL.ENABLE)
            mToggle.setChecked(SETTINGS.CALL.ENABLE);
//        if (mOneTap.getSelectedItemPosition() != SETTINGS.CALL.ONE_TAP) {
//            mOneTap.setSelection(SETTINGS.CALL.ONE_TAP);
//        }
//        if (mDoubleTap.getSelectedItemPosition() != SETTINGS.CALL.DOUBLE_TAP) {
//            mDoubleTap.setSelection(SETTINGS.CALL.DOUBLE_TAP);
//        }
        mReplyText.setText((SETTINGS.CALL.TEXT.length() == 0) ? SETTINGS.CALL.DEF_TEXT : SETTINGS.CALL.TEXT);
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
