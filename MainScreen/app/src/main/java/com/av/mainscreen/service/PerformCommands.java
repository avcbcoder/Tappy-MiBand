package com.av.mainscreen.service;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.av.mainscreen.broadcastReceiver.CallReceiver;
import com.av.mainscreen.constants.MIBandConsts;
import com.av.mainscreen.constants.SETTINGS;
import com.av.mainscreen.constants.STRINGS;

import java.util.ArrayList;

import static com.av.mainscreen.constants.SETTINGS.taps;

/**
 * Created by Ankit on 17-11-2018.
 */

public class PerformCommands {
    private static final String TAG = "PerformCommands";

    ForegroundService serviceContext;
    BluetoothAdapter mbluetoothAdapter;
    BluetoothDevice mbluetoothDevice;
    BluetoothGatt mbluetoothGatt;
    AudioManager mAudioManager;
    IntentFilter mIntentFilterTrack;

    public PerformCommands(ForegroundService foregroundService, BluetoothGatt bluetoothGatt, BluetoothAdapter bluetoothAdapter, BluetoothDevice bluetoothDevice) {
        serviceContext = foregroundService;
        mbluetoothAdapter = bluetoothAdapter;
        mbluetoothDevice = bluetoothDevice;
        mbluetoothGatt = bluetoothGatt;
//        SETTINGS.taps[1].NEXT = true;
//        SETTINGS.taps[2].PLAY_PAUSE = true;
//        SETTINGS.taps[3].PREV = true;
//        taps[1].VOL_INC = true;
//        taps[2].VOL_DEC = true;
        taps[1].CALL = 1;
        taps[2].CALL = 2;

        mAudioManager = (AudioManager) serviceContext.getSystemService(Context.AUDIO_SERVICE);

        // setup intent filter for track change
        mIntentFilterTrack = new IntentFilter();
        for (String s : STRINGS.INTENT_FILTER_SONG)
            mIntentFilterTrack.addAction(s);
        //register receiver
        serviceContext.registerReceiver(mReceiverTrack, mIntentFilterTrack);
    }

    public void TAP(int x) {
        Log.e(TAG, "TAP: " + x);
        switch (x) {
            case 1:
                performAction(1);
                toaster("Single Tap");
                break;
            case 2:
                performAction(2);
                toaster("Double Tap");
                break;
            case 3:
                performAction(3);
                toaster("Tripple Tap");
                break;
            default:
                break;
        }
    }

    private void performAction(int t) {
        SETTINGS.TAP tap = taps[t];

        if (CallReceiver.isOutgoing()) {
            Log.e(TAG, "performAction: Outgoing 00000000000000");
            return;
        } else if (CallReceiver.isIncoming()) {
            Log.e(TAG, "performAction: Incoming 00000000000000");
            switch (tap.CALL) {
                case 0:
                    break;
                case 1:
                    muteCall();
                    break;
                case 2:
                    reply();
                    break;
            }
            return;
        }

        //vibrate first
        if (tap.VIBRATE)
            vibrate(taps[t].VIBRATE_DELAY);

        // Toggle music
        musicControl(tap);

        // Change Volume
        if (tap.VOL_INC)
            mAudioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND);
        else if (tap.VOL_DEC)
            mAudioManager.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND);

        // Timer
        if (tap.TIMER)
            startStopTimer();
    }

    private void muteCall() {
        Log.e(TAG, "muteCall: ");
    }

    private long lastRepliedCall;

    private void reply() {
        if (lastRepliedCall == CallReceiver.INCOMING_callStartTime) {
            Log.e(TAG, "reply: Not sent");
            return;
        }
        lastRepliedCall = CallReceiver.INCOMING_callStartTime;
        Log.e(TAG, "reply: sent");
        /*reply now*/
        /*
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(
                    CallReceiver.savedNumber,
                    null,
                    (SETTINGS.Call.TEXT.length() == 0) ? SETTINGS.Call.DEF_TEXT : SETTINGS.Call.TEXT,
                    null, null
            );
            ArrayList<String> parts = smsManager.divideMessage((SETTINGS.Call.TEXT.length() == 0) ? SETTINGS.Call.DEF_TEXT : SETTINGS.Call.TEXT);
            smsManager.sendMultipartTextMessage(CallReceiver.savedNumber, null, parts, null, null);
            Log.e(TAG, "reply: Message Sent");
        } catch (Exception ex) {
            Log.e(TAG, "reply: Something went wrong");
            ex.printStackTrace();
        }
        */
    }

    private void musicControl(SETTINGS.TAP tap) {
        Log.e(TAG, "syncMusic: ");
        if (mAudioManager == null)
            mAudioManager = (AudioManager) serviceContext.getSystemService(Context.AUDIO_SERVICE);
        long eventtime = SystemClock.uptimeMillis();
        KeyEvent downEvent, upEvent;
        if (tap.NEXT) {
            downEvent = new KeyEvent(eventtime, eventtime, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_NEXT, 0);
            upEvent = new KeyEvent(eventtime, eventtime, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_NEXT, 0);
        } else if (tap.PREV) {
            downEvent = new KeyEvent(eventtime, eventtime, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PREVIOUS, 0);
            upEvent = new KeyEvent(eventtime, eventtime, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PREVIOUS, 0);
        } else if (tap.PLAY_PAUSE) {
            downEvent = new KeyEvent(eventtime, eventtime, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE, 0);
            upEvent = new KeyEvent(eventtime, eventtime, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE, 0);
        } else return;

        mAudioManager.dispatchMediaKeyEvent(downEvent);
        mAudioManager.dispatchMediaKeyEvent(upEvent);
    }

    private BroadcastReceiver mReceiverTrack = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String cmd = intent.getStringExtra("command");
            Log.e("Music", cmd + " : " + action);
            String artist = intent.getStringExtra("artist");
            String album = intent.getStringExtra("album");
            String track = intent.getStringExtra("track");
            //boolean playing = intent.getBooleanExtra("playing", false);
            Log.e("Music", artist + " : " + album + " : " + track);
        }
    };

    private void startStopTimer() {
    }

    /**
     * This function will vibrate band for specified ms
     *
     * @param ms(millisec to vibrate)
     */
    private void vibrate(final int ms) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    startVibrate();
                    Thread.sleep(ms);
                    stopVibrate();
                } catch (Exception e) {
                }
            }
        }).start();
    }

    void startVibrate() {
        BluetoothGattCharacteristic bchar = mbluetoothGatt.getService(MIBandConsts.AlertNotification.service)
                .getCharacteristic(MIBandConsts.AlertNotification.alertCharacteristic);
        bchar.setValue(new byte[]{3});
        mbluetoothGatt.writeCharacteristic(bchar);
    }

    void stopVibrate() {
        BluetoothGattCharacteristic bchar = mbluetoothGatt.getService(MIBandConsts.AlertNotification.service)
                .getCharacteristic(MIBandConsts.AlertNotification.alertCharacteristic);
        bchar.setValue(new byte[]{0});
        mbluetoothGatt.writeCharacteristic(bchar);
    }

    private void toaster(final String text) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, "run: " + text);
                Toast.makeText(serviceContext.getApplicationContext(), text, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
