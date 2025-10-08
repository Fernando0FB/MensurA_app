package com.example.mensura.ui.main;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mensura.ui.base.BaseActivity;
import com.example.mensura.ui.mensuracoes.MensuracoesActivity;
import com.example.mensura.util.Bluetooth.BtleUtils;
import com.example.mensura.util.Bluetooth.ScannerBtle;
import com.example.myapplication.R;

public class MainActivity extends BaseActivity {
    private static final String TAG = "uaaaa";

    public static final int REQUEST_ENABLE_BT = 1;


    private ScannerBtle scannerBtle;
    private TextView tvWelcome;
    private Button btnBluetooth, btnPacientes, btnMedicoes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scannerBtle = new ScannerBtle(this);

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            BtleUtils.toast(getApplicationContext(), "BLE não suportado");
            finish();
        }

        Log.e("uaaa", "Começando");

        tvWelcome = findViewById(R.id.tvWelcome);
        btnBluetooth = findViewById(R.id.btnBluetooth);
        btnPacientes = findViewById(R.id.btnPacientes);
        btnMedicoes = findViewById(R.id.btnMedicoes);

        setLoadingOverlay(findViewById(R.id.loadingOverlay));

        btnMedicoes.setOnClickListener(v -> {
            Intent intent = new Intent(this, MensuracoesActivity.class);
            startActivity(intent);
        });

        btnBluetooth.setOnClickListener(v -> {
            //TODO entender como funciona a conexão BLE e conectar aqui
            startScan();
        });



    }

    public void startScan() {
        Log.e("uaaa", "StartScan do main ta ok");
        scannerBtle.start();
    }
}
