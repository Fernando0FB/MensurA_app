package com.example.mensura.util.Bluetooth;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.content.Context;
import android.util.Log;

public class ScannerBtle {

    private Context context;
    private BluetoothGatt bluetoothGatt;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bluetoothLeScanner;

    private boolean scanning;
    private boolean connected;

    public ScannerBtle(Context context) {
        this.context = context;

        final BluetoothManager bluetoothManager =
                (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        if (bluetoothAdapter != null) {
            bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        }
    }

    public void start() {
        if (!BtleUtils.checkBluetooth(bluetoothAdapter)) {
            if (context instanceof Activity) {
                BtleUtils.requestUserBluetooth((Activity) context);
            } else {
                Log.e("debug", "Contexto não é uma Activity.");
            }
            scanLeDevice(false);
        } else {
            scanLeDevice(true);
        }
    }

    public void stop() {
        scanLeDevice(false);
    }

    @SuppressLint("MissingPermission")
    private void scanLeDevice(final boolean enable) {
        if (enable && !scanning) {
            scanning = true;
            bluetoothLeScanner.startScan(scanCallback);
        } else {
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
            BtleUtils.toast(context, "BLE scan failed with code: " + errorCode);
        }
    };

    @SuppressLint("MissingPermission")
    private void connectToDevice(android.bluetooth.BluetoothDevice device) {
        bluetoothGatt = device.connectGatt(context, false, new android.bluetooth.BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(android.bluetooth.BluetoothGatt gatt, int status, int newState) {
                super.onConnectionStateChange(gatt, status, newState);

                if (status == android.bluetooth.BluetoothGatt.GATT_SUCCESS
                        && newState == android.bluetooth.BluetoothProfile.STATE_CONNECTED) {
                    connected = true;
                    gatt.discoverServices();
                } else if (newState == android.bluetooth.BluetoothProfile.STATE_DISCONNECTED) {
                    gatt.close();
                    bluetoothGatt = null;
                    connected = false;
                }
            }

            @Override
            public void onServicesDiscovered(android.bluetooth.BluetoothGatt gatt, int status) {
                super.onServicesDiscovered(gatt, status);
                if (status == android.bluetooth.BluetoothGatt.GATT_SUCCESS) {
                    java.util.UUID SERVICE_UUID = java.util.UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb");
                    java.util.UUID CHARACTERISTIC_UUID = java.util.UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb");

                    android.bluetooth.BluetoothGattService service = gatt.getService(SERVICE_UUID);
                    if (service != null) {
                        android.bluetooth.BluetoothGattCharacteristic characteristic = service.getCharacteristic(CHARACTERISTIC_UUID);
                        if (characteristic != null) {
                            gatt.setCharacteristicNotification(characteristic, true);

                            android.bluetooth.BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
                                    java.util.UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));
                            if (descriptor != null) {
                                descriptor.setValue(android.bluetooth.BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                                gatt.writeDescriptor(descriptor);
                            }

                            gatt.readCharacteristic(characteristic);
                        }
                    }
                }
            }

            @Override
            public void onCharacteristicChanged(android.bluetooth.BluetoothGatt gatt, android.bluetooth.BluetoothGattCharacteristic characteristic) {
                super.onCharacteristicChanged(gatt, characteristic);
                String data = characteristic.getStringValue(0);

                if (data != null) {
                    if (onLeituraCallback != null) {
                        onLeituraCallback.onLeitura(data);
                    }
                }
            }
        });
    }


    public boolean isConnected() {
        return connected;
    }

    @SuppressLint("MissingPermission")
    public boolean disconnect() {
        if (connected && bluetoothGatt != null) {
            bluetoothGatt.disconnect();
            bluetoothGatt.close();
            connected = false;
            return true;
        }
        return false;
    }

    public interface OnLeituraCallback {
        void onLeitura(String angulo);
    }

    private OnLeituraCallback onLeituraCallback;

    public void setOnLeituraCallback(OnLeituraCallback callback) {
        this.onLeituraCallback = callback;
    }
}
