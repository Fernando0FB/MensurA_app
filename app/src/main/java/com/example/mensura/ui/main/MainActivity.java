package com.example.mensura.ui.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mensura.ui.base.BaseActivity;
import com.example.mensura.ui.newMensuracao.MensuracaoCreateActivity;
import com.example.mensura.ui.mensuracoes.MensuracoesActivity;
import com.example.mensura.ui.newMensuracao.PacienteSelectActivity;
import com.example.mensura.ui.pacientes.PacienteListActivity;
import com.example.mensura.util.Bluetooth.BluetoothManager;
import com.example.mensura.util.Bluetooth.BtleUtils;
import com.example.mensura.util.Bluetooth.ScannerBtle;
import com.example.myapplication.R;

public class MainActivity extends BaseActivity {

    public static final int REQUEST_ENABLE_BT = 1;


    private ScannerBtle scannerBtle;
    private TextView tvWelcome;
    private Button btnBluetooth, btnPacientes, btnMedicoes, btnNovaMedicao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scannerBtle =  BluetoothManager.getScannerBtle(this);

        SharedPreferences prefs = getSharedPreferences("APP_PREFS", MODE_PRIVATE);

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            BtleUtils.toast(getApplicationContext(), "BLE não suportado");
            finish();
        }

        tvWelcome = findViewById(R.id.tvWelcome);

        if (prefs.getString("USER_NAME", null) != null) {
            String nomeCompleto = prefs.getString("USER_NAME", null);
            tvWelcome.setText("Bem-vindo, " + nomeCompleto);
        } else {
            tvWelcome.setText("Olá Usuário!");
        }
        btnBluetooth = findViewById(R.id.btnBluetooth);
        btnNovaMedicao = findViewById(R.id.btnNovaMedicao);

        setLoadingOverlay(findViewById(R.id.loadingOverlay));

        findViewById(R.id.btnMedicoes).setOnClickListener(v -> {
            Intent intent = new Intent(this, MensuracoesActivity.class);
            startActivity(intent);
        });

        checkBtConectado();

        findViewById(R.id.btnPacientes).setOnClickListener(v -> {
            Intent intent = new Intent(this, PacienteListActivity.class);
            startActivity(intent);
        });

        btnBluetooth.setOnClickListener(v -> {
            startScan();
            new android.os.Handler().postDelayed(() -> {
                checkBtConectado();
            }, 3000);
        });

        btnNovaMedicao.setOnClickListener(v -> {
            if (scannerBtle.isConnected()) {
                Intent intent = new Intent(this, PacienteSelectActivity.class);
                intent.putExtra("NEW_MEASUREMENT", true);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Conecte-se a um dispositivo Bluetooth primeiro.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkBtConectado();
    }

    private void checkBtConectado() {
        if (scannerBtle.isConnected()) {
            btnBluetooth.setText("Conectado!");
            btnBluetooth.setBackgroundColor(getResources().getColor(R.color.grey));
            btnBluetooth.setClickable(false);
            btnBluetooth.setOnClickListener(v2 -> {
                Toast.makeText(this, "Dispositivo já conectado!", Toast.LENGTH_SHORT).show();
            });
        }
    }

    public void startScan() {
        scannerBtle.start();
    }

    public ScannerBtle getScannerBtle() {
        return scannerBtle;
    }
}
