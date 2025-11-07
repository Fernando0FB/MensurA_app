package com.example.mensura.ui.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.mensura.ui.base.BaseActivity;
import com.example.mensura.ui.login.LoginActivity;
import com.example.mensura.ui.mensuracoes.MensuracoesActivity;
import com.example.mensura.ui.newMensuracao.PacienteSelectActivity;
import com.example.mensura.ui.pacientes.PacienteListActivity;
import com.example.mensura.util.Bluetooth.BluetoothManager;
import com.example.mensura.util.Bluetooth.ScannerBtle;
import com.example.myapplication.R;

@RequiresApi(api = Build.VERSION_CODES.O)
public class MainActivity extends BaseActivity {

    public static final int REQUEST_ENABLE_BT = 1;
    private ScannerBtle scannerBtle;
    private Button btnBluetooth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scannerBtle =  BluetoothManager.getScannerBtle(this);
        btnBluetooth = findViewById(R.id.btnConectar);
        SharedPreferences prefs = getSharedPreferences("APP_PREFS", MODE_PRIVATE);

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            finish();
        }

        if (prefs.getString("USER_NAME", null) != null) {
            String nomeCompleto = prefs.getString("USER_NAME", null);
            ((TextView) findViewById(R.id.tvHelloName)).setText("Bem-vindo, " + nomeCompleto);
        }

        setLoadingOverlay(findViewById(R.id.loadingOverlay));

        findViewById(R.id.cardMensuracoes).setOnClickListener(v -> {
            Intent intent = new Intent(this, MensuracoesActivity.class);
            startActivity(intent);
        });

        checkConexaoBt();

        findViewById(R.id.cardPacientes).setOnClickListener(v -> {
            Intent intent = new Intent(this, PacienteListActivity.class);
            startActivity(intent);
        });

        btnBluetooth.setOnClickListener(v -> {
            if (scannerBtle.isConnected()) {
               scannerBtle.disconnect();
                Toast.makeText(this, "Desconectando...", Toast.LENGTH_SHORT).show();
            } else {
               scannerBtle.start();
                Toast.makeText(this, "Tentando se conectar...", Toast.LENGTH_SHORT).show();
            }
            new android.os.Handler().postDelayed(this::checkConexaoBt, 2000);
        });

        findViewById(R.id.cardIniciar).setOnClickListener(v -> {
            if (scannerBtle.isConnected()) {
                startActivity(new Intent(this, PacienteSelectActivity.class));
            } else {
                Toast.makeText(this, "Conecte-se a um dispositivo Bluetooth primeiro.", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.btnHeaderAction).setOnClickListener(v -> {
            scannerBtle.disconnect();
            prefs.edit().clear().apply();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkConexaoBt();
    }

    private void checkConexaoBt() {
        if (scannerBtle.isConnected()) {
            findViewById(R.id.frameBluetooth).setBackground(getDrawable(R.drawable.bg_icon_circle_soft_connected));
            findViewById(R.id.cardIniciar).setAlpha(1.0f);
            ((TextView) findViewById(R.id.tvBtSub)).setText("Conectado!");
            findViewById(R.id.chipRequerBt).setVisibility(View.GONE);
            btnBluetooth.setText("Desconectar");
        } else {
            findViewById(R.id.frameBluetooth).setBackground(getDrawable(R.drawable.bg_icon_circle_soft));
            findViewById(R.id.cardIniciar).setAlpha(0.45f);
            ((TextView) findViewById(R.id.tvBtSub)).setText("Desconectado");
            findViewById(R.id.chipRequerBt).setVisibility(View.VISIBLE);
            btnBluetooth.setText("Conectar");
        }
    }
}
