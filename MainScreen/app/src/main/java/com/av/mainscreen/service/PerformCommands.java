package com.av.mainscreen.service;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.av.mainscreen.constants.SETTINGS;

/**
 * Created by Ankit on 17-11-2018.
 */

public class PerformCommands {
    ForegroundService serviceContext;
    private static final String TAG = "PerformCommands";

    public PerformCommands(ForegroundService foregroundService) {
        serviceContext = foregroundService;
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
            vibrate();

        // Toggle music
        if (tap.NEXT)
            playNextSong();
        if (tap.PREV)
            playPrevSong();
        if (tap.PLAY_PAUSE)
            playPauseCurrSong();

        // Change Volume
        if (tap.VOL_INC)
            increaseVolume();
        if (tap.VOL_DEC)
            decreaseVolume();

        // Timer
        if (tap.TIMER)
            startStopTimer();
    }

    private void startStopTimer() {
    }

    private void decreaseVolume() {
    }

    private void increaseVolume() {
    }

    private void playPauseCurrSong() {
    }

    private void playPrevSong() {
    }

    private void playNextSong() {
    }

    private void vibrate() {
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
