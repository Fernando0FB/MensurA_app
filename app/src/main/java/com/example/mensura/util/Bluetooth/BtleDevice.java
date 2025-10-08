package com.example.mensura.util.Bluetooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;

public class BtleDevice {
    private BluetoothDevice bluetoothDevice;
    private int rssi;

    public BtleDevice(BluetoothDevice device) {
        this.bluetoothDevice = device;
    }

    public String getAdress() {
        return bluetoothDevice.getAddress();
    }

    @SuppressLint("MissingPermission")
    public String getName() {
        return bluetoothDevice.getName();
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

}
