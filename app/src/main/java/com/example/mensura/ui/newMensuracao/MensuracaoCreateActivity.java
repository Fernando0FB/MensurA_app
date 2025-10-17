package com.example.mensura.ui.newMensuracao;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mensura.data.model.MensuracaoCreateDTO;  // resposta
import com.example.mensura.data.model.MensuracaoDTO;
import com.example.mensura.data.network.ApiClient;
import com.example.mensura.data.network.ApiService;
// troque para o seu R real:
import com.example.myapplication.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MensuracaoCreateActivity extends AppCompatActivity {

    private long pacienteId = -1L;
    private String pacienteNome = "";

    private TextView tvPaciente;
    private EditText edtPosicao;
    private Spinner spinnerArticulacao, spinnerLado, spinnerMovimento;

    private Button btnCriar;
    private ProgressBar progress;

    private ApiService api;
    private String token;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mensuracao_create);

        tvPaciente = findViewById(R.id.tvPaciente);
        spinnerArticulacao = findViewById(R.id.spinnerArticulacao);
        spinnerLado = findViewById(R.id.spinnerLado);
        spinnerMovimento = findViewById(R.id.spinnerMovimento);
        edtPosicao = findViewById(R.id.edtPosicao);
        btnCriar = findViewById(R.id.btnCriarMensuracao);
        progress = findViewById(R.id.progress);

        pacienteId = getIntent().getIntExtra("PACIENTE_ID", -1);
        pacienteNome = getIntent().getStringExtra("PACIENTE_NOME");
        tvPaciente.setText("Paciente: " + (pacienteNome != null ? pacienteNome : ("#" + pacienteId)));
        Log.e("pacienteIdCreate", "Paciente no create: " + pacienteId);
        Log.e("pacienteIdCreate", "extras: " + getIntent().getExtras().toString());
        token = getTokenFromPrefs();
        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Token ausente. Faça login.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        api = ApiClient.getClient().create(ApiService.class);

        // Preenche os Spinners
        setupSpinners();

        btnCriar.setOnClickListener(v -> criarMensuracao());
    }

    private void setupSpinners() {
        // Articulação: JOELHO ou COTOVELO
        ArrayAdapter<CharSequence> articulacaoAdapter = ArrayAdapter.createFromResource(this,
                R.array.articulacao_options, android.R.layout.simple_spinner_item);
        articulacaoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerArticulacao.setAdapter(articulacaoAdapter);

        // Lado: DIREITO ou ESQUERDO
        ArrayAdapter<CharSequence> ladoAdapter = ArrayAdapter.createFromResource(this,
                R.array.lado_options, android.R.layout.simple_spinner_item);
        ladoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLado.setAdapter(ladoAdapter);

        // Movimento: FLEXAO, EXTENSAO, PRONACAO, SUPINACAO
        ArrayAdapter<CharSequence> movimentoAdapter = ArrayAdapter.createFromResource(this,
                R.array.movimento_options, android.R.layout.simple_spinner_item);
        movimentoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMovimento.setAdapter(movimentoAdapter);
    }

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

    private String getTokenFromPrefs() {
        // CONFIRME chave/arquivo
        SharedPreferences sp = getSharedPreferences("APP_PREFS", MODE_PRIVATE);
        return sp.getString("JWT_TOKEN", "");
    }
}
