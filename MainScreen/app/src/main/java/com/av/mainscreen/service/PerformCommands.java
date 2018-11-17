package com.av.mainscreen.service;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Config;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.av.mainscreen.constants.MIBandConsts;
import com.av.mainscreen.constants.SETTINGS;

/**
 * Created by Ankit on 17-11-2018.
 */

public class PerformCommands {
    private static final String TAG = "PerformCommands";

    ForegroundService serviceContext;
    BluetoothAdapter mbluetoothAdapter;
    BluetoothDevice mbluetoothDevice;
    BluetoothGatt mbluetoothGatt;

    public PerformCommands(ForegroundService foregroundService, BluetoothGatt bluetoothGatt, BluetoothAdapter bluetoothAdapter, BluetoothDevice bluetoothDevice) {
        serviceContext = foregroundService;
        mbluetoothAdapter = bluetoothAdapter;
        mbluetoothDevice = bluetoothDevice;
        mbluetoothGatt = bluetoothGatt;
        SETTINGS.taps[1].NEXT = true;
        SETTINGS.taps[2].PLAY_PAUSE = true;
        SETTINGS.taps[3].PREV = true;
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
        SETTINGS.TAP tap = SETTINGS.taps[t];

        //vibrate first
        if (tap.VIBRATE)
            vibrate(SETTINGS.taps[t].VIBRATE_DELAY);

        // Toggle music
        // musicControll(tap);
        // musicControllUsingBroadcast(tap);
        //musicControlUsingAudioManager(tap);
        syncMusic(tap);

        // Change Volume
        if (tap.VOL_INC)
            increaseVolume();
        if (tap.VOL_DEC)
            decreaseVolume();

        // Timer
        if (tap.TIMER)
            startStopTimer();
    }

    private void syncMusic(SETTINGS.TAP tap) {
        Log.e(TAG, "syncMusic: " );
        AudioManager mAudioManager = (AudioManager) serviceContext.getSystemService(Context.AUDIO_SERVICE);

        long eventtime = SystemClock.uptimeMillis();

        KeyEvent downEvent = new KeyEvent(eventtime, eventtime, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_NEXT, 0);
        mAudioManager.dispatchMediaKeyEvent(downEvent);

        KeyEvent upEvent = new KeyEvent(eventtime, eventtime, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_NEXT, 0);
        mAudioManager.dispatchMediaKeyEvent(upEvent);
    }


    private void musicControlUsingAudioManager(SETTINGS.TAP tap) {
        AudioManager mAudioManager = (AudioManager) serviceContext.getSystemService(Context.AUDIO_SERVICE);
        if (tap.NEXT) {
            Log.e(TAG, "musicControlUsingAudioManager: NeXT");
            KeyEvent event = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_NEXT);
            mAudioManager.dispatchMediaKeyEvent(event);
            event = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_NEXT);
            mAudioManager.dispatchMediaKeyEvent(event);
        } else if (tap.PLAY_PAUSE) {
            Log.e(TAG, "musicControlUsingAudioManager: Play");
            KeyEvent event = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE);
            mAudioManager.dispatchMediaKeyEvent(event);
            event = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE);
            mAudioManager.dispatchMediaKeyEvent(event);
        } else if (tap.PREV) {
            Log.e(TAG, "musicControlUsingAudioManager: Prev");
            KeyEvent event = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PREVIOUS);
            mAudioManager.dispatchMediaKeyEvent(event);
            event = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PREVIOUS);
            mAudioManager.dispatchMediaKeyEvent(event);
        }
    }

    /**
     * sends a broadcast using commands to toggle music
     *
     * @param tap
     */
    private void musicControllUsingBroadcast(SETTINGS.TAP tap) {
        Intent i = new Intent("com.android.music.musicservicecommand");
        Intent mi = new Intent("com.miui.player.metachanged");
        if (tap.NEXT) {
            i.putExtra("command", "next");
            mi.putExtra("command", "next");
        } else if (tap.PREV) {
            i.putExtra("command", "previous");
            mi.putExtra("command", "previous");
        } else if (tap.PLAY_PAUSE) {
            i.putExtra("command", "pause");
            mi.putExtra("command", "pause");
        } else return;
        serviceContext.sendBroadcast(i);
        serviceContext.sendBroadcast(mi);
    }

    /**
     * sends a broadcast to toggle music usin keypress intents
     *
     * @param tap
     */
    private void musicControll(SETTINGS.TAP tap) {
        int command = 7854884;
        if (tap.NEXT)
            command = KeyEvent.KEYCODE_MEDIA_NEXT;
        else if (tap.PREV)
            command = KeyEvent.KEYCODE_MEDIA_PREVIOUS;
        else if (tap.PLAY_PAUSE)
            command = KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE;
        else return;
        // down -> keyPressed
        Intent i = new Intent(Intent.ACTION_MEDIA_BUTTON);
        i.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN, command));
        serviceContext.sendOrderedBroadcast(i, null);
        // up -> keyReleased
        i = new Intent(Intent.ACTION_MEDIA_BUTTON);
        i.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP, command));
        serviceContext.sendOrderedBroadcast(i, null);
    }

    private void startStopTimer() {
    }

    private void decreaseVolume() {
    }

    private void increaseVolume() {
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
