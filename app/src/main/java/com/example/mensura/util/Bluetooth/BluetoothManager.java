package com.example.mensura.util.Bluetooth;

import android.content.Context;

public class BluetoothManager {

    private static ScannerBtle scannerBtle;

    public static ScannerBtle getScannerBtle(Context context) {
        if (scannerBtle == null) {
            // Inicializa o ScannerBtle quando for necessário pela primeira vez
            scannerBtle = new ScannerBtle(context);
        }
        return scannerBtle;
    }
}