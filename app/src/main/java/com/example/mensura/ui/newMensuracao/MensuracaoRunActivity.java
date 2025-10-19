package com.example.mensura.ui.newMensuracao;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mensura.data.model.RepeticaoDTO;
import com.example.mensura.util.Bluetooth.BluetoothManager;
import com.example.mensura.util.Bluetooth.ScannerBtle;
import com.example.myapplication.R;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MensuracaoRunActivity extends AppCompatActivity {

    private TextView tvPaciente, tvAngulo, tvMenorAngulo, tvMaiorAngulo, tvExcursao, tvDor;
    private EditText edtObservacoes;
    private Button btnFinalizarRepeticao, btnFinalizarMensuracao;
    private ProgressBar progress;
    private ScannerBtle scannerBtle;
    private List<RepeticaoDTO> repeticoes;
    private long pacienteId;
    private String pacienteNome;
    private int serieAtual = 1;
    private float anguloInicial = Float.MAX_VALUE, anguloFinal = Float.MIN_VALUE, excursao = 0;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mensuracao_run);

        tvPaciente = findViewById(R.id.tvPaciente);
        tvAngulo = findViewById(R.id.tvAngulo);
        tvMenorAngulo = findViewById(R.id.tvMenorAngulo);
        tvMaiorAngulo = findViewById(R.id.tvMaiorAngulo);
        tvExcursao = findViewById(R.id.tvExcursao);
        tvDor = findViewById(R.id.tvDor);
        edtObservacoes = findViewById(R.id.edtObservacoes);
        btnFinalizarRepeticao = findViewById(R.id.btnFinalizarRepeticao);
        btnFinalizarMensuracao = findViewById(R.id.btnFinalizarMensuracao);
        progress = findViewById(R.id.progress);

        pacienteId = getIntent().getLongExtra("PACIENTE_ID", -1);
        pacienteNome = getIntent().getStringExtra("PACIENTE_NOME");

        tvPaciente.setText("Paciente: " + pacienteNome);
        scannerBtle = BluetoothManager.getScannerBtle(this);

        repeticoes = new ArrayList<>();

        scannerBtle.setOnLeituraCallback(angulo -> {
            try {
                float anguloFloat = Float.parseFloat(angulo);
                atualizarLeitura(anguloFloat);
            } catch (NumberFormatException e) {

            }
        });

        btnFinalizarRepeticao.setOnClickListener(v -> finalizarRepeticao());
        btnFinalizarMensuracao.setOnClickListener(v -> finalizarMensuracao());
    }

    private void iniciarLeitura() {
        Log.e("debug", "Iniciando leitura...");
    }

    private void atualizarLeitura(float angulo) {
        runOnUiThread(() -> {
            Log.e("debug", "angulo: " + angulo);

            // Verificando se o novo ângulo é menor que o inicial
            if (angulo < anguloInicial) {
                String text = "Menor Angulo: " + angulo;
                Log.e("debug", text);
                tvMenorAngulo.setText(text);
                anguloInicial = angulo;
            }

            // Verificando se o novo ângulo é maior que o final
            if (angulo > anguloFinal) {
                String text = "Maior Angulo: " + angulo;
                Log.e("debug", text);
                tvMaiorAngulo.setText(text);
                anguloFinal = angulo;
            }

            // Atualizando a excursão (diferença entre ângulo final e inicial)
            excursao = anguloFinal - anguloInicial;
            String text = "Angulo Atual: " + angulo;
            Log.e("debug", text);
            tvAngulo.setText(text);

            // Atualizando excursão
            String text1 = "Excursão: " + excursao;
            Log.e("debug", text1);
            tvExcursao.setText(text1);
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void finalizarRepeticao() {
        String observacoes = edtObservacoes.getText().toString();
        RepeticaoDTO repeticao = new RepeticaoDTO(
                (int) anguloInicial,
                (int) anguloFinal,
                (int) excursao,
                0, // Valor de dor, será preenchido depois
                serieAtual++,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'hh:mm:ss")), // Data e hora fixa por enquanto
                observacoes,
                getIntent().getIntExtra("MENSURACAO_ID", -1)// MensuracaoId, pode ser substituído por outro valor
        );
        repeticoes.add(repeticao);

        Toast.makeText(this, "Repetição " + repeticao.getSerie() + " finalizada.", Toast.LENGTH_SHORT).show();

        anguloInicial = Float.MAX_VALUE;
        anguloFinal = Float.MIN_VALUE;
        excursao = 0;
        edtObservacoes.setText("");

        if (repeticoes.size() >= 5) {
            btnFinalizarMensuracao.setVisibility(View.VISIBLE); // Exibe a opção de finalizar mensuração
        } else {
            iniciarLeitura();
        }
    }

    private void finalizarMensuracao() {
        Toast.makeText(this, "Mensuração finalizada.", Toast.LENGTH_SHORT).show();


        Intent intent = new Intent(this, ConclusaoActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        scannerBtle.stop();
    }
}
