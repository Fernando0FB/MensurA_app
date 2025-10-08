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
    private boolean mScanning;
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
    }

    public boolean isScanning() {
        return mScanning;
    }

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
        if (enable && !mScanning) {
            BtleUtils.toast(ma.getApplicationContext(), "Starting BLE scan...");

            mScanning = true;
            bluetoothLeScanner.startScan(scanCallback);
        }
        else {
            mScanning = false;
            bluetoothLeScanner.stopScan(scanCallback);
        }
    }

    private ScanCallback scanCallback = new ScanCallback() {
        @SuppressLint("MissingPermission")
        @Override
        public void onScanResult(int callbackType, android.bluetooth.le.ScanResult result) {
            super.onScanResult(callbackType, result);
            Log.e("uaaa", "onScanResult: " + result.toString());

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
        BtleUtils.toast(ma.getApplicationContext(), "Conectado ao dispositivo: " + device.getName());
        Log.e("uaaa", "Conectado ao dispositivo: " + device.getName());
        // Implementar a lógica de conexão aqui

    }
}
