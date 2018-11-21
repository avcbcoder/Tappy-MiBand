package com.av.mainscreen.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.av.mainscreen.MainActivity;
import com.av.mainscreen.R;
import com.av.mainscreen.constants.MIBandConsts;
import com.av.mainscreen.constants.SETTINGS;

public class ForegroundService extends Service {
    public static final String ACTION_START_FOREGROUND_SERVICE = "ACTION_START_FOREGROUND_SERVICE";
    public static final String ACTION_STOP_FOREGROUND_SERVICE = "ACTION_STOP_FOREGROUND_SERVICE";
    private static final String TAG = "ForegroundService";

    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice bluetoothDevice;
    BluetoothGatt bluetoothGatt;

    PerformCommands performCommands;
    public static boolean state = false;

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
        return START_STICKY;/*Now this service won't be killed on devices except MI*/
    }

    /* Used to build and start foreground service. */
    private void startForegroundService() {
        if (state)
            return;
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
                .setFullScreenIntent(pendingIntent, false)
                .setPriority(NotificationCompat.PRIORITY_LOW);

        /*
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.setBigContentTitle("Music player implemented by foreground service.");
        bigTextStyle.bigText("Android foreground service is a android service which can run in foreground always, it can be controlled by user via notification.");
        builder.setStyle(bigTextStyle);
        */

        /* Setup bluetooth Connection*/
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        connect();
//        listen();

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
            listen();
            Log.e("test", "onServicesDiscovered");
            listen();
            Log.e(TAG, "onServicesDiscovered: c:" + BluetoothGatt.GATT_SUCCESS + " w:" + status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Toast.makeText(ForegroundService.this, "Band Connected", Toast.LENGTH_SHORT).show();
            } else
                Log.e(TAG, "onServicesDiscovered: Gatt unsuccess");
            Log.e(TAG, "onServicesDiscovered: calling listen");
            listen();
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
            /*Log.e("test", "onCharacteristicChanged " + characteristic
                    + "\n" + characteristic.getUuid()
                    + "\n" + characteristic.getStringValue(1)
            );
            */
            tapper();
            // newThread((SETTINGS.DELAY_TAP + 1) * 100);
        }
    };

    private long lastTap;
    private int tapCount;

    long mLastTap = 0;
    int mCurrTaps = 0;

    private void tapper() {
        long diff = System.currentTimeMillis() - mLastTap;
        if (diff > SETTINGS.COMMON_SETTING.DELAY_BTW_MULTIPLE_COMMANDS) {
            mLastTap = System.currentTimeMillis();
            mCurrTaps = 1;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(SETTINGS.COMMON_SETTING.CLICK_INTERVAL);
                        performCommands.TAP(mCurrTaps); /*Perform single/double/tripple clicks*/
                    } catch (Exception e) {
                    }
                }
            }).start();
        } else {
            mCurrTaps++;
        }
    }

    private void connect() {
        Log.e(TAG, "connect: ");
        String macAddress = SETTINGS.MAC_ADDRESS;
        try {
            // get the device with this address
            bluetoothDevice = bluetoothAdapter.getRemoteDevice(macAddress);
            bluetoothGatt = bluetoothDevice.connectGatt(this, true, bluetoothGattCallback);
            if (bluetoothGatt == null)
                Log.e(TAG, "connect: null");
            else
                Log.e(TAG, "connect: Cool");
        } catch (Exception e) {
            Toast.makeText(this, "Unable to connect to " + macAddress, Toast.LENGTH_SHORT).show();
        }
    }

    private void disconnect() {

    }

    void stateConnected() {
        Log.e(TAG, "stateConnected: ");
        bluetoothGatt.discoverServices();
        state = true;
    }

    void stateDisconnected() {
        Log.e(TAG, "stateDisconnected: ");
        bluetoothGatt.disconnect();
        state = false;
    }

    private void displayText(String text) {

    }

    void startVibrate() {
        try {
            BluetoothGattCharacteristic bchar = bluetoothGatt.getService(MIBandConsts.AlertNotification.service)
                    .getCharacteristic(MIBandConsts.AlertNotification.alertCharacteristic);
            bchar.setValue(new byte[]{2});
            if (!bluetoothGatt.writeCharacteristic(bchar)) {
                Toast.makeText(this, "Failed to start vibrate", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
        }
    }

    void stopVibrate() {
        BluetoothGattCharacteristic bchar = bluetoothGatt.getService(MIBandConsts.AlertNotification.service)
                .getCharacteristic(MIBandConsts.AlertNotification.alertCharacteristic);
        bchar.setValue(new byte[]{0});
        if (!bluetoothGatt.writeCharacteristic(bchar)) {
        }
    }

    private void listen() {
        Log.e(TAG, "listen: ");
        BluetoothGattService bluetoothGattService = bluetoothGatt.getService(MIBandConsts.Basic.service);
        if (bluetoothGattService == null) {
            Log.e(TAG, "listen: Service NULL");
        } else
            Log.e(TAG, "listen: Service NOT NULL");
        BluetoothGattCharacteristic bchar = bluetoothGattService.getCharacteristic(MIBandConsts.Basic.btn);
        bluetoothGatt.setCharacteristicNotification(bchar, true);
        BluetoothGattDescriptor descriptor = bchar.getDescriptor(MIBandConsts.HeartRate.descriptor);
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        bluetoothGatt.writeDescriptor(descriptor);
        performCommands = new PerformCommands(this, bluetoothGatt, bluetoothAdapter, bluetoothDevice);
    }

    public void toaster(final String text) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, "run: " + text);
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
