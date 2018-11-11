package com.av.mainscreen.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.av.mainscreen.MainActivity;
import com.av.mainscreen.R;
import com.av.mainscreen.constants.SETTINGS;

import java.math.BigInteger;
import java.util.Arrays;

public class ForegroundService extends Service {
    public static final String ACTION_START_FOREGROUND_SERVICE = "ACTION_START_FOREGROUND_SERVICE";
    public static final String ACTION_STOP_FOREGROUND_SERVICE = "ACTION_STOP_FOREGROUND_SERVICE";
    private static final String TAG = "ForegroundService";

    public ForegroundService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, "onBind: ");
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate: ");
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();

            switch (action) {
                case ACTION_START_FOREGROUND_SERVICE:
                    startForegroundService();
                    Toast.makeText(getApplicationContext(), "Foreground service is started.", Toast.LENGTH_LONG).show();
                    break;
                case ACTION_STOP_FOREGROUND_SERVICE:
                    stopForegroundService();
                    Toast.makeText(getApplicationContext(), "Foreground service is stopped.", Toast.LENGTH_LONG).show();
                    break;
            }
        }
        return START_STICKY;/*Now this service won't be killed*/
    }

    /* Used to build and start foreground service. */
    private void startForegroundService() {
        Log.d(TAG, "Start foreground service.");

        // Create notification default intent.
        // leave empty if nothing needs to be done on click on notification
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        Bitmap largeIconBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.timer_icon);

        // Create notification builder.
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "channel id")
                .setSmallIcon(R.drawable.timer_icon)
                .setContentTitle("MI TAP")
                .setContentText("Band is connected")
                .setWhen(System.currentTimeMillis())
                .setLargeIcon(largeIconBitmap)
                .setFullScreenIntent(pendingIntent, true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        /*
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.setBigContentTitle("Music player implemented by foreground service.");
        bigTextStyle.bigText("Android foreground service is a android service which can run in foreground always, it can be controlled by user via notification.");
        builder.setStyle(bigTextStyle);
        */

        Notification notification = builder.build();
        // Start foreground service.
        startForeground(1, notification);
    }

    private void stopForegroundService() {
        Log.d(TAG, "Stop foreground service.");
        // Stop foreground service and remove the notification.
        stopForeground(true);
        // Stop the foreground service.
        stopSelf();
    }

    final BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {
        private static final String TAG = "CALLBACK";

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            Log.e(TAG, "onConnectionStateChange");
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                stateConnected();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                stateDisconnected();
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            Log.e("test", "onServicesDiscovered");
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Toast.makeText(ForegroundService.this, "Band Connected", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            Log.e("test", "onCharacteristicRead newone");
            byte[] b = characteristic.getValue();
            int battery = b[1];
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            Log.e("test", "onCharacteristicChanged " + characteristic
                    + "\n" + characteristic.getUuid()
                    + "\n" + characteristic.getStringValue(1)
            );
            newThread((SETTINGS.DELAY_TAP + 1) * 100 + 50);
        }
    };

    private long lastTap;
    private int tapCount;

    private void TAP(int x) {
        switch (x) {
            case 1:
                Toast.makeText(this, "single tap", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                Toast.makeText(this, "double tap", Toast.LENGTH_SHORT).show();
                break;
            case 3:
                Toast.makeText(this, "tripple tap", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    private void newThread(final int delay) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                }
                long curr = System.currentTimeMillis();
                if (curr - lastTap > 1000) {
                    tapCount = 1;
                    lastTap = curr;
                } else if (curr - lastTap > delay) {
                    // perform Taps
                    TAP(tapCount);
                } else {
                    lastTap = curr;
                    tapCount++;
                    newThread(delay);
                }
            }
        }).start();
    }

    private void connect() {

    }

    private void disconnect() {

    }

    private void stateConnected() {

    }

    private void stateDisconnected() {

    }

    private void displayText(String text) {

    }

    private void startVibrate() {

    }

    private void stopVibrate() {

    }


}
