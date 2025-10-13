package com.example.mensura.util.Bluetooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.content.Context;
import android.util.Log;

import com.example.mensura.ui.main.MainActivity;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class ScannerBtle {

    private MainActivity ma;

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bluetoothLeScanner;

    private boolean scanning;
    private boolean connected;
    private Handler mHandler = new Handler() {
        @Override
        public void publish(LogRecord record) {

        }

        @Override
        public void flush() {

        }

        @Override
        public void close() throws SecurityException {

        }
    };

    private long scanPeriod = 10000; //10 segundos
    private int signalStrength = -70; //RSSI

    public ScannerBtle(MainActivity mainActivity) {
        ma = mainActivity;

        final BluetoothManager bluetoothManager =
                (BluetoothManager) ma.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        if (bluetoothAdapter != null) {
            bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        }
    }

    public boolean isScanning() {
        return scanning;
    }
    public boolean isConected() {return connected;}

    public void start() {
        Log.e("uaaa", "Entrou no start do scanner");
        if (!BtleUtils.checkBluetooth(bluetoothAdapter)) {
            Log.e("uaaa", "Entrou no if do start");
            BtleUtils.requestUserBluetooth(ma);
            scanLeDevice(false);
        }
        else {
            Log.e("uaaa", "Entrou no else do start");
            scanLeDevice(true);
        }
    }

    public void stop() {
        scanLeDevice(false);
    }
    @SuppressLint("MissingPermission")
    private void scanLeDevice(final boolean enable) {
        Log.e("uaaa", "scanLeDevice: " + enable);
        if (enable && !scanning) {
            BtleUtils.toast(ma.getApplicationContext(), "Starting BLE scan...");

            scanning = true;
            bluetoothLeScanner.startScan(scanCallback);
        }
        else {
            scanning = false;
            bluetoothLeScanner.stopScan(scanCallback);
        }
    }

    private ScanCallback scanCallback = new ScanCallback() {
        @SuppressLint("MissingPermission")
        @Override
        public void onScanResult(int callbackType, android.bluetooth.le.ScanResult result) {
            super.onScanResult(callbackType, result);
            if (result.getDevice() != null && "BT05".equals(result.getDevice().getName())) {
                BtleUtils.toast(ma.getApplicationContext(), "Dispositivo BT05 encontrado! Tentando conectar...");
                Log.e("uaaa", "Dispositivo BT05 encontrado! Tentando conectar...");
                connectToDevice(result.getDevice());
                stop();
            }
        }

        @Override
        public void onBatchScanResults(java.util.List<android.bluetooth.le.ScanResult> results) {
            super.onBatchScanResults(results);
            for (android.bluetooth.le.ScanResult result : results) {
                Log.e("uaaa", "onBatchScanResults: " + result.toString());
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Log.e("uaaa", "onScanFailed: " + errorCode);
            BtleUtils.toast(ma.getApplicationContext(), "BLE scan failed with code: " + errorCode);
        }
    };

    @SuppressLint("MissingPermission")
    private void connectToDevice(android.bluetooth.BluetoothDevice device) {
        device.connectGatt(ma, false, new android.bluetooth.BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(android.bluetooth.BluetoothGatt gatt, int status, int newState) {
                super.onConnectionStateChange(gatt, status, newState);

                if (status == android.bluetooth.BluetoothGatt.GATT_SUCCESS && newState == android.bluetooth.BluetoothProfile.STATE_CONNECTED) {
                    connected = true;
                } else if (newState == android.bluetooth.BluetoothProfile.STATE_DISCONNECTED) {
                    gatt.close();
                }
            }

            @Override
            public void onServicesDiscovered(android.bluetooth.BluetoothGatt gatt, int status) {
                super.onServicesDiscovered(gatt, status);
                if (status == android.bluetooth.BluetoothGatt.GATT_SUCCESS) {
                    java.util.UUID SERVICE_UUID = java.util.UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb");
                    java.util.UUID CHARACTERISTIC_UUID = java.util.UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb");

                    // Encontra o serviço FFE0
                    android.bluetooth.BluetoothGattService service = gatt.getService(SERVICE_UUID);
                    if (service != null) {
                        android.bluetooth.BluetoothGattCharacteristic characteristic = service.getCharacteristic(CHARACTERISTIC_UUID);
                        if (characteristic != null) {
                            // Habilita notificações para a característica
                            gatt.setCharacteristicNotification(characteristic, true);

                            android.bluetooth.BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
                                    java.util.UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));
                            if (descriptor != null) {
                                descriptor.setValue(android.bluetooth.BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                                gatt.writeDescriptor(descriptor);
                            }
                        }
                    }
                }
            }



            @Override
            public void onCharacteristicChanged(android.bluetooth.BluetoothGatt gatt, android.bluetooth.BluetoothGattCharacteristic characteristic) {
                super.onCharacteristicChanged(gatt, characteristic);
                //TODO tratar o valor recebido da característica e jogar na tela inicial
                Log.e("uaaa", "Característica alterada: " + characteristic.getStringValue(0));
            }
        });
    }
}
