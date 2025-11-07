package com.example.mensura.ui.newMensuracao;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mensura.ui.widgets.AngleGaugeView;
import com.example.mensura.util.Bluetooth.BluetoothManager;
import com.example.mensura.util.Bluetooth.ScannerBtle;
import com.example.myapplication.R;


@RequiresApi(api = Build.VERSION_CODES.O)
public class MensuracaoCreateActivity extends AppCompatActivity {

    private long idPaciente = -1L;
    private String nomePaciente = "";
    private static float menorAngulo = Integer.MAX_VALUE, maiorAngulo = Integer.MIN_VALUE;
    private Spinner spinnerArticulacao, spinnerLado, spinnerMovimento;
    private static TextView menorValor, maiorValor;
    private boolean realizandoLeitura = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mensuracao_create);

        AngleGaugeView gauge = findViewById(R.id.gauge);
        TextView tvMiolo  = findViewById(R.id.tvAnguloMiolo);

        idPaciente = getIntent().getLongExtra("ID_PACIENTE", -1L);
        nomePaciente = getIntent().getStringExtra("NOME_PACIENTE");

        if (idPaciente == -1L) {
            Toast.makeText(this, "ID do paciente inválido.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        ScannerBtle scannerBtle = BluetoothManager.getScannerBtle(this);
        setLeituraOnlyListener(scannerBtle, gauge, tvMiolo);

        spinnerArticulacao = findViewById(R.id.edtArticulacao);
        spinnerLado = findViewById(R.id.edtLado);
        spinnerMovimento = findViewById(R.id.edtMovimento);
        menorValor = findViewById(R.id.menorValor);
        maiorValor = findViewById(R.id.maiorValor);

        Button botaoIniciar = findViewById(R.id.btnIniciar);
        botaoIniciar.setOnClickListener(v -> {
            if (!realizandoLeitura) {
                botaoIniciar.setText("Pausar gravação");
                setLeituraComFuncionalidade(scannerBtle, gauge, tvMiolo);
            } else {
                botaoIniciar.setText("Iniciar gravação");
                setLeituraOnlyListener(scannerBtle, gauge, tvMiolo);
            }
            realizandoLeitura = !realizandoLeitura;
        });

        findViewById(R.id.btnReiniciar).setOnClickListener(v -> {
            botaoIniciar.setText("Iniciar gravação");
            setLeituraOnlyListener(scannerBtle, gauge, tvMiolo);
            menorAngulo = Float.MAX_VALUE;
            menorValor.setText("Maior ângulo: --");

            maiorAngulo = Float.MIN_VALUE;
            maiorValor.setText("Maior ângulo: --");
        });

        findViewById(R.id.btnFinalizar).setOnClickListener(v -> {
            if (menorAngulo == 0f || maiorAngulo == 0f) {
                Toast.makeText(this, "Realize uma gravação de angulos primeiro!", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(MensuracaoCreateActivity.this, ConclusaoActivity.class);
            intent.putExtra("ID_PACIENTE", Long.valueOf(idPaciente));
            intent.putExtra("NOME_PACIENTE", nomePaciente.toString());
            intent.putExtra("MAIOR_ANGULO", (int) maiorAngulo);
            intent.putExtra("MENOR_ANGULO", (int) menorAngulo);
            intent.putExtra("ARTICULACAO", spinnerArticulacao.getSelectedItem().toString());
            intent.putExtra("LADO", spinnerLado.getSelectedItem().toString());
            intent.putExtra("MOVIMENTO", spinnerMovimento.getSelectedItem().toString());
            startActivity(intent);
        });

        findViewById(R.id.btnVoltar).setOnClickListener(v -> {
            finish();
        });

        setupSpinners();
    }

    private static void setLeituraOnlyListener(ScannerBtle scannerBtle, AngleGaugeView gauge, TextView tvMiolo) {
        scannerBtle.setOnLeituraCallback(angulo -> {
            try {
                float anguloFloat = Float.parseFloat(angulo);
                updateGrafico(gauge, tvMiolo, anguloFloat);
            } catch (NumberFormatException e) {}
        });
    }

    private static void updateGrafico(AngleGaugeView gauge, TextView tvMiolo, float anguloFloat) {
        gauge.setAngle(anguloFloat);
        tvMiolo.setText(((int) anguloFloat) + "°");
    }

    private static void updateTela(float angulo) {
        if(angulo < menorAngulo) {
            menorAngulo = angulo;
            menorValor.setText("Menor ângulo: " + (int) angulo + "º");
        }
        if(angulo > maiorAngulo) {
            maiorAngulo = angulo;
            maiorValor.setText("Maior ângulo: " + (int) angulo + "º");
        }
    }

    private static void setLeituraComFuncionalidade(ScannerBtle scannerBtle, AngleGaugeView gauge, TextView tvMiolo) {
        scannerBtle.setOnLeituraCallback(angulo -> {
            try {
                float anguloFloat = Float.parseFloat(angulo);
                updateGrafico(gauge, tvMiolo, anguloFloat);
                updateTela(anguloFloat);
            } catch (NumberFormatException e) {

            }
        });
    }

    private void setupSpinners() {
        ArrayAdapter<String> articulacaoAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, new String[]{"Selecione a articulação", "Joelho", "Cotovelo"}) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }
        };
        articulacaoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerArticulacao.setAdapter(articulacaoAdapter);
        spinnerArticulacao.setSelection(0);

        ArrayAdapter<String> ladoAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, new String[]{"Selecione o lado", "Esquerdo", "Direto"}) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }
        };
        ladoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLado.setAdapter(ladoAdapter);
        spinnerLado.setSelection(0);

        ArrayAdapter<String> movimentoAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, new String[]{"Selecione o movimento","Flexão","Extensão","Pronação","Supinação"}) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }
        };
        movimentoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMovimento.setAdapter(movimentoAdapter);
        spinnerMovimento.setSelection(0);
    }

    /*
    private void criarMensuracao() {
        String articulacao = spinnerArticulacao.getSelectedItem().toString();
        String lado = spinnerLado.getSelectedItem().toString();
        String movimento = spinnerMovimento.getSelectedItem().toString();
        String posicao = edtPosicao.getText().toString().trim();

        if (pacienteId <= 0) {
            Toast.makeText(this, "Paciente inválido.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (articulacao.isEmpty() || lado.isEmpty() || movimento.isEmpty() || posicao.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos.", Toast.LENGTH_SHORT).show();
            return;
        }

        progress.setVisibility(View.VISIBLE);
        btnCriar.setEnabled(false);

        MensuracaoCreateDTO body = new MensuracaoCreateDTO();
        body.setArticulacao(articulacao);
        body.setLado(lado);
        body.setMovimento(movimento);
        body.setPosicao(posicao);
        body.setPacienteId(pacienteId);

        Call<MensuracaoDTO> call = api.createMensuracao("Bearer " + token, body);
        Log.e("debug", "Enviando post com o corpo: " + body);
        call.enqueue(new Callback<MensuracaoDTO>() {
            @Override
            public void onResponse(Call<MensuracaoDTO> call, Response<MensuracaoDTO> resp) {
                progress.setVisibility(View.GONE);
                btnCriar.setEnabled(true);

                if (!resp.isSuccessful() || resp.body() == null) {
                    Toast.makeText(MensuracaoCreateActivity.this,
                            "Falha ao criar mensuração (" + resp.code() + ")", Toast.LENGTH_SHORT).show();
                    return;
                }


                Intent it = new Intent(MensuracaoCreateActivity.this, MensuracaoRunActivity.class);
                it.putExtra("MENSURACAO_ID", resp.body().getId());
                it.putExtra("MENSURACAO_CRIADA", true);
                it.putExtra("PACIENTE_ID", pacienteId);
                it.putExtra("PACIENTE_NOME", pacienteNome);
                Log.e("Uaaaa", "PacienteId: " + pacienteId);
                // it.putExtra("MENSURACAO_ID", resp.body().getId()); // se houver
                startActivity(it);
            }

            @Override
            public void onFailure(Call<MensuracaoDTO> call, Throwable t) {
                progress.setVisibility(View.GONE);
                btnCriar.setEnabled(true);
                Toast.makeText(MensuracaoCreateActivity.this,
                        "Erro de rede: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
     */
}
