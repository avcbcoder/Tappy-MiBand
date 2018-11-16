package com.av.mainscreen.service;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

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
                toaster("Single Tap");
                break;
            case 2:
                toaster("Double Tap");
                break;
            case 3:
                toaster("Tripple Tap");
                break;
            default:
                break;
        }
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
