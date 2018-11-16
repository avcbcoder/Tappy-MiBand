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
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.av.mainscreen.MainActivity;
import com.av.mainscreen.R;
import com.av.mainscreen.constants.MIBandConsts;
import com.av.mainscreen.constants.SETTINGS;
import com.av.mainscreen.util.MIBand;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;

public class ForegroundService extends Service {
    public static final String ACTION_START_FOREGROUND_SERVICE = "ACTION_START_FOREGROUND_SERVICE";
    public static final String ACTION_STOP_FOREGROUND_SERVICE = "ACTION_STOP_FOREGROUND_SERVICE";
    private static final String TAG = "ForegroundService";

    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice bluetoothDevice;
    BluetoothGatt bluetoothGatt;

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
            Log.e("test", "onCharacteristicChanged " + characteristic
                    + "\n" + characteristic.getUuid()
                    + "\n" + characteristic.getStringValue(1)
            );
            tapper();
            // newThread((SETTINGS.DELAY_TAP + 1) * 100);
        }
    };

    private long lastTap;
    private int tapCount;

    private void TAP(int x) {
        Log.e(TAG, "TAP: " + x);
        toaster(x + " Tap");
        switch (x) {
            case 1:
                break;
            case 2:
                break;
            case 3:
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
                } else if (curr - lastTap >= delay) {
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

    public class TT {
        long time;
        int count;

        TT(long time, int count) {
            this.time = time;
            this.count = count;
        }

        @Override
        public String toString() {
            return "{" + time + "=>" + count + "}";
        }
    }

    ArrayList<TT> al = new ArrayList<>(Arrays.asList(new TT(0, 0)));
    int tc = 0;
    long tt = 0;

    private void tapper() {
        Log.e(TAG, "tapper: ----------------- #" + tc++);
        long diff = System.currentTimeMillis() - al.get(al.size() - 1).time;
        if (diff > SETTINGS.DIFF_BTW_MULTIPLE_COMMANDS) {
            al.add(new TT(System.currentTimeMillis(), 1));
            waitForClicks();
        } else {
            al.get(al.size() - 1).count += 1;
        }
        /*Log.e(TAG, "tapper: "+(System.currentTimeMillis()-tt) );
        tt=System.currentTimeMillis();
        long curr = System.currentTimeMillis();
        int delay = SETTINGS.DELAY_TAP;
        long diff = curr - al.get(al.size() - 1).time;
        Log.e(TAG, "tapper: DIFFERENCE:" + diff);
        if (diff < delay) {
            Log.e(TAG, "OLD");
            al.get(al.size() - 1).count += 1;
            al.get(al.size() - 1).time = curr;
        } else {
            Log.e(TAG, "NEW");
            al.add(new TT(curr, 1));
        }
        Log.e(TAG, "tapper: " + al);
        //isIt(al.get(al.size() - 1).count);
        */
    }

    private void waitForClicks() {
        Log.e(TAG, "waitForClicks: WaitLaunch" );
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(SETTINGS.CLICK_INTERVAL);
                    Log.e(TAG, "waitComplete: "+al.get(al.size()-1).count );
                } catch (Exception e) {
                }
            }
        }).start();
    }

    private void isIt(final int x) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.e(TAG, "thread started: ");
                    Thread.sleep(SETTINGS.DELAY_TAP + 50);
                    Log.e(TAG, "run: actual thread diff" + (System.currentTimeMillis() - (al.get(al.size() - 1).time)));
                    if (System.currentTimeMillis() - (al.get(al.size() - 1).time) > SETTINGS.DELAY_TAP) {
                        if (al.get(al.size() - 1).count == x) {
                            toaster("Tap " + al.get(al.size() - 1).count);
                            Log.e(TAG, "run: success " + al.get(al.size() - 1).count);
                        } else {
                            Log.e(TAG, "run: denied----");
                        }
                    } else {
                        Log.e(TAG, "run: denied----");
                    }
                } catch (Exception e) {
                }
            }
        }).start();
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
    }

    void stateDisconnected() {
        Log.e(TAG, "stateDisconnected: ");
        bluetoothGatt.disconnect();
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
