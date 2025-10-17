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
import com.example.mensura.util.Bluetooth.ScannerBtle;
import com.example.myapplication.R;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MensuracaoRunActivity extends AppCompatActivity {

    private TextView tvPaciente, tvAngulo, tvExcursao, tvDor;
    private EditText edtObservacoes;
    private Button btnFinalizarRepeticao, btnFinalizarMensuracao;
    private ProgressBar progress;
    private ScannerBtle scannerBtle;
    private List<RepeticaoDTO> repeticoes;
    private long pacienteId;
    private String pacienteNome;
    private int serieAtual = 1;
    private float anguloInicial = Float.MAX_VALUE, anguloFinal = Float.MIN_VALUE, excursao = 0;
    private boolean isLeituraEmCurso = false;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mensuracao_run);

        tvPaciente = findViewById(R.id.tvPaciente);
        tvAngulo = findViewById(R.id.tvAngulo);
        tvExcursao = findViewById(R.id.tvExcursao);
        tvDor = findViewById(R.id.tvDor);
        edtObservacoes = findViewById(R.id.edtObservacoes);
        btnFinalizarRepeticao = findViewById(R.id.btnFinalizarRepeticao);
        btnFinalizarMensuracao = findViewById(R.id.btnFinalizarMensuracao);
        progress = findViewById(R.id.progress);

        pacienteId = getIntent().getLongExtra("PACIENTE_ID", -1);
        pacienteNome = getIntent().getStringExtra("PACIENTE_NOME");

        tvPaciente.setText("Paciente: " + pacienteNome);
        scannerBtle = new ScannerBtle(this);

        repeticoes = new ArrayList<>();

        btnFinalizarRepeticao.setOnClickListener(v -> finalizarRepeticao());
        btnFinalizarMensuracao.setOnClickListener(v -> finalizarMensuracao());

        // Inicia a leitura BLE
        Log.e("debug", "Lendo?...");

        // Inicializando o callback corretamente
        scannerBtle.setOnLeituraCallback(angulo -> {
            // Atualiza os valores com o angulo recebido
            Log.e("debug", "Leitura de angulo: " + angulo);
            atualizarLeitura(angulo);
        });

        iniciarLeitura();
    }

    private void iniciarLeitura() {
        // Inicia o scanner BLE
        scannerBtle.start();
        Log.e("debug", "Iniciando leitura...");
    }

    private void atualizarLeitura(float angulo) {
        // Atualiza angulo inicial e final
        Log.e("debug", "angulo: " + angulo);
        if (angulo < anguloInicial) anguloInicial = angulo;
        if (angulo > anguloFinal) anguloFinal = angulo;

        // Calcula a excursão (diferença entre os ângulos)
        excursao = anguloFinal - anguloInicial;

        // Atualiza os textos
        tvAngulo.setText("Angulo Atual: " + angulo);
        tvExcursao.setText("Excursão: " + excursao);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void finalizarRepeticao() {
        // Aqui, o usuário terminou a repetição
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

        // Mostra a opção de continuar ou finalizar a mensuração
        Toast.makeText(this, "Repetição " + repeticao.getSerie() + " finalizada.", Toast.LENGTH_SHORT).show();

        // Limpa os campos para a próxima repetição
        anguloInicial = Float.MAX_VALUE;
        anguloFinal = Float.MIN_VALUE;
        excursao = 0;
        edtObservacoes.setText("");

        // Pergunta ao usuário se quer continuar ou finalizar
        if (repeticoes.size() >= 5) {
            btnFinalizarMensuracao.setVisibility(View.VISIBLE); // Exibe a opção de finalizar mensuração
        } else {
            // Continuar a próxima repetição
            iniciarLeitura();
        }
    }

    private void finalizarMensuracao() {
        // Finaliza a mensuração, envia os dados e vai para a tela de conclusão
        Toast.makeText(this, "Mensuração finalizada.", Toast.LENGTH_SHORT).show();

        // Aqui você pode salvar os dados no servidor ou na base local

        // Vai para a próxima tela
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
