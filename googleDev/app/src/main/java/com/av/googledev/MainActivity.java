package com.av.googledev;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothProfile;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    private Button mconnect, mlisten, mstart, mstop, mShort, mBattery;
    private EditText mAddress;
    private TextView mPhysicalAddress, mState;

    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice bluetoothDevice;
    BluetoothGatt bluetoothGatt;

    private String mDeviceName;
    private String mDeviceAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mconnect = findViewById(R.id.ble_connect);
        mlisten = findViewById(R.id.ble_listen);
        mstart = findViewById(R.id.ble_startVibrate);
        mstop = findViewById(R.id.ble_stopVibrate);
        mPhysicalAddress = findViewById(R.id.ble_physicalAddress);
        mState = findViewById(R.id.ble_state);
        mAddress = findViewById(R.id.macAddress);
        mShort = findViewById(R.id.ble_shortVibrate);
        mBattery = findViewById(R.id.ble_battery);

        mconnect.setOnClickListener(this);
        mlisten.setOnClickListener(this);
        mstart.setOnClickListener(this);
        mstop.setOnClickListener(this);
        mShort.setOnClickListener(this);
        mState.setOnClickListener(this);
        mBattery.setOnClickListener(this);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        getBoundedDevice();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ble_connect:
                connect();
                break;
            case R.id.ble_listen:
                listen();
                break;
            case R.id.ble_startVibrate:
                startVibrate();
                break;
            case R.id.ble_stopVibrate:
                stopVibrate();
                break;
            case R.id.ble_shortVibrate:
                shortVibrate();
                break;
            case R.id.ble_state:
                showNotification();
                break;
            case R.id.ble_battery:
                getBatteryInfo();
                break;
        }
    }

    private void getBatteryInfo() {
        Log.e(TAG, "listen: Battery");
        BluetoothGattCharacteristic bchar = bluetoothGatt.getService(BLEConstants.Basic.service)
                .getCharacteristic(BLEConstants.Basic.batteryCharacteristic);
        bluetoothGatt.setCharacteristicNotification(bchar, true);
        BluetoothGattDescriptor descriptor = bchar.getDescriptor(BLEConstants.HeartRate.descriptor);
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        bluetoothGatt.writeDescriptor(descriptor);
    }

    private void showNotification() {
    }

    private void shortVibrate() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                startVibrate();
                long c = System.currentTimeMillis();
                while (System.currentTimeMillis() - c < 500) ;
                stopVibrate();
            }
        }).start();
    }

    private void connect() {
        String macAddress = mAddress.getText().toString();
        try {
            // get the device with this address
            bluetoothDevice = bluetoothAdapter.getRemoteDevice(macAddress);
            bluetoothGatt = bluetoothDevice.connectGatt(this, true, bluetoothGattCallback);
        } catch (Exception e) {
            Log.e(TAG, "connect: Wrong mac Address = " + macAddress);
        }
    }

    void startVibrate() {
        try {
            BluetoothGattCharacteristic bchar = bluetoothGatt.getService(BLEConstants.AlertNotification.service)
                    .getCharacteristic(BLEConstants.AlertNotification.alertCharacteristic);
            bchar.setValue(new byte[]{2});
            if (!bluetoothGatt.writeCharacteristic(bchar)) {
                Toast.makeText(this, "Failed start vibrate", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "startVibrate: Error " + e);
            e.printStackTrace();
        }
    }

    void stopVibrate() {
        BluetoothGattCharacteristic bchar = bluetoothGatt.getService(BLEConstants.AlertNotification.service)
                .getCharacteristic(BLEConstants.AlertNotification.alertCharacteristic);
        bchar.setValue(new byte[]{0});
        if (!bluetoothGatt.writeCharacteristic(bchar)) {
            Toast.makeText(this, "Failed stop vibrate", Toast.LENGTH_SHORT).show();
        }
    }

    private void listen() {
        Log.e(TAG, "listen: Starting listening");
        BluetoothGattCharacteristic bchar = bluetoothGatt.getService(BLEConstants.Basic.service)
                .getCharacteristic(BLEConstants.Basic.btn);
        bluetoothGatt.setCharacteristicNotification(bchar, true);
        BluetoothGattDescriptor descriptor = bchar.getDescriptor(BLEConstants.HeartRate.descriptor);
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        bluetoothGatt.writeDescriptor(descriptor);
    }


    void stateConnected() {
        Log.e(TAG, "stateConnected: ");
        bluetoothGatt.discoverServices();
        mState.setText("Connected");
    }

    void stateDisconnected() {
        Log.e(TAG, "stateDisconnected: ");
        bluetoothGatt.disconnect();
        mState.setText("Disconnected");
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
//            listenHeartRate();
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            Log.e("test", "onCharacteristicRead");
            byte[] data = characteristic.getValue();
            mPhysicalAddress.setText(Arrays.toString(data));
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            Log.e("test", "onCharacteristicWrite");
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            Log.e("test", "onCharacteristicChanged " + characteristic
                    + "\n" + characteristic.getUuid()
                    + "\n" + characteristic.getStringValue(1)
            );
            byte[] data = characteristic.getValue();
            mPhysicalAddress.setText(Arrays.toString(data));
        }
    };

    void getBoundedDevice() {
        mDeviceName = getIntent().getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = getIntent().getStringExtra(EXTRAS_DEVICE_ADDRESS);
        mPhysicalAddress.setText(mDeviceAddress);

        Set<BluetoothDevice> boundedDevice = bluetoothAdapter.getBondedDevices();
        for (BluetoothDevice bd : boundedDevice) {
            if (bd.getName().contains("MI Band 2")) {
                mPhysicalAddress.setText(bd.getAddress());
                Log.e(TAG, "getBoundedDevice: " + bd.getName());
            } else if (bd.getName().contains("MI") || bd.getName().contains("mi")) {
                mPhysicalAddress.setText(bd.getAddress());
                Log.e(TAG, "getBoundedDevice: " + bd.getName());
            } else {
                Log.e(TAG, "getBoundedDevice: else case " + bd.getName());
            }
        }
    }
}
