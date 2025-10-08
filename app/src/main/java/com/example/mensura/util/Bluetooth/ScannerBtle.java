package com.example.mensura.util.Bluetooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.util.Log;

import com.example.mensura.ui.main.MainActivity;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class ScannerBtle {

    private MainActivity ma;

    private BluetoothAdapter mBluetoothAdapter;
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

    private long scanPeriod;
    private int signalStrength;

    public ScannerBtle(MainActivity mainActivity, long scanPeriod, int signalStrength) {
        ma = mainActivity;

        this.scanPeriod = scanPeriod;
        this.signalStrength = signalStrength;

        final BluetoothManager bluetoothManager =
                (BluetoothManager) ma.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
    }

    public boolean isScanning() {
        return mScanning;
    }

    public void start() {
        Log.e("uaaa", "Entrou no start do scanner");
        if (!BtleUtils.checkBluetooth(mBluetoothAdapter)) {
            Log.e("uaaa", "Entrou no if do start");
            BtleUtils.requestUserBluetooth(ma);
            ma.stopScan();
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
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        }
        else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {

                    final int new_rssi = rssi;
                    if (rssi > signalStrength) {
                        ma.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                BtleDevice btleDevice = new BtleDevice(device);
                                btleDevice.setRssi(new_rssi);
                                if (btleDevice.getName() == "BT05") {

                                    ma.stopScan();
                                }
                                BtleUtils.toast(ma.getApplicationContext(),
                                        "Dispositivo: " + btleDevice.getName() + " - " + btleDevice.getAdress() +
                                                " - RSSI: " + btleDevice.getRssi());
                            }
                        });
                    }
                }
            };

}
