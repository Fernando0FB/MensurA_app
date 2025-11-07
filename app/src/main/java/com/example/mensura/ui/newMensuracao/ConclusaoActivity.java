package com.example.mensura.ui.newMensuracao;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.mensura.data.model.MensuracaoCreateDTO;
import com.example.mensura.data.model.MensuracaoDTO;
import com.example.mensura.data.network.ApiClient;
import com.example.mensura.data.network.ApiService;
import com.example.mensura.ui.base.BaseActivity;
import com.example.mensura.ui.main.MainActivity;
import com.example.mensura.util.Formatters.StringUtils;
import com.example.myapplication.R;
import com.google.android.material.textfield.TextInputEditText;

import java.time.LocalDateTime;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@RequiresApi(api = Build.VERSION_CODES.O)
public class ConclusaoActivity extends BaseActivity {

    private Button btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9, btn10;
    private TextInputEditText edtObs;
    private TextView tvContador;
    private int selectedValue = 0;
    private String observacoes = "", nomePaciente = "", articulacao = "", lado = "", movimento = "";
    long idPaciente = -1L, maiorAngulo = 0, menorAngulo = 0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.finalizar_mensuracao);

        FrameLayout overlay = findViewById(R.id.loadingOverlay);
        setLoadingOverlay(overlay);

        idPaciente = getIntent().getLongExtra("ID_PACIENTE", -1L);
        nomePaciente = getIntent().getStringExtra("NOME_PACIENTE");
        maiorAngulo = getIntent().getIntExtra("MAIOR_ANGULO", 0);
        menorAngulo = getIntent().getIntExtra("MENOR_ANGULO", 0);
        articulacao = getIntent().getStringExtra("ARTICULACAO");
        lado = getIntent().getStringExtra("LADO");
        movimento = getIntent().getStringExtra("MOVIMENTO");

        if (idPaciente == -1L) {
            finish();
        }

        ((TextView) findViewById(R.id.tvPaciente)).setText(nomePaciente);
        ((TextView) findViewById(R.id.tvArticulacao)).setText(StringUtils.capitalize(articulacao) + " - " + StringUtils.capitalize(lado));
        ((TextView) findViewById(R.id.tvTipo)).setText(movimento);
        ((TextView) findViewById(R.id.tvMenorAngulo)).setText(String.valueOf(menorAngulo));
        ((TextView) findViewById(R.id.tvMaiorAngulo)).setText(String.valueOf(maiorAngulo));
        ((TextView) findViewById(R.id.tvExcursao)).setText(String.valueOf(maiorAngulo - menorAngulo));

        edtObs = findViewById(R.id.edtObs);
        tvContador = findViewById(R.id.tvContador);
        edtObs.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvContador.setText(s.length() + " caracteres");
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);
        btn3 = findViewById(R.id.btn3);
        btn4 = findViewById(R.id.btn4);
        btn5 = findViewById(R.id.btn5);
        btn6 = findViewById(R.id.btn6);
        btn7 = findViewById(R.id.btn7);
        btn8 = findViewById(R.id.btn8);
        btn9 = findViewById(R.id.btn9);
        btn10 = findViewById(R.id.btn10);

        btn1.setOnClickListener(v-> { setBotaoDor(btn1); });
        btn2.setOnClickListener(v-> { setBotaoDor(btn2); });
        btn3.setOnClickListener(v-> { setBotaoDor(btn3); });
        btn4.setOnClickListener(v-> { setBotaoDor(btn4); });
        btn5.setOnClickListener(v-> { setBotaoDor(btn5); });
        btn6.setOnClickListener(v-> { setBotaoDor(btn6); });
        btn7.setOnClickListener(v-> { setBotaoDor(btn7); });
        btn8.setOnClickListener(v-> { setBotaoDor(btn8); });
        btn9.setOnClickListener(v-> { setBotaoDor(btn9); });
        btn10.setOnClickListener(v-> { setBotaoDor(btn10); });

        findViewById(R.id.btnSalvar).setOnClickListener(v -> {
            criarMensuracao();
        });

        findViewById(R.id.btnVoltar).setOnClickListener(v -> {
            finish();
        });
    }

    private void setBotaoDor(Button botaoSelecionado) {
        // Resetar todos os botões para o estado padrão
        btn1.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
        btn2.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
        btn3.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
        btn4.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
        btn5.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
        btn6.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
        btn7.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
        btn8.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
        btn9.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
        btn10.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));

        botaoSelecionado.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.cor_botoes2)));
        selectedValue = Integer.parseInt(botaoSelecionado.getText().toString());

        findViewById(R.id.btnSalvar).setEnabled(true);
        findViewById(R.id.btnSalvar).setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.cor_botoes2)));
        findViewById(R.id.tvAviso).setAlpha(0f);
    }

    private void criarMensuracao() {
        showLoading(ConclusaoActivity.this);
        MensuracaoCreateDTO mensuracaoCreate = new MensuracaoCreateDTO();
        mensuracaoCreate.setArticulacao(StringUtils.removeAccentsAndUpper(articulacao));
        mensuracaoCreate.setLado(StringUtils.removeAccentsAndUpper(lado));
        mensuracaoCreate.setMovimento(StringUtils.removeAccentsAndUpper(movimento));
        mensuracaoCreate.setPacienteId(idPaciente);
        mensuracaoCreate.setAnguloInicial(Integer.valueOf(String.valueOf(menorAngulo)));
        mensuracaoCreate.setAnguloFinal(Integer.valueOf(String.valueOf(maiorAngulo)));
        mensuracaoCreate.setExcursao(Integer.valueOf(String.valueOf((maiorAngulo - menorAngulo))));
        mensuracaoCreate.setDor(Integer.valueOf(String.valueOf(selectedValue)));

        SharedPreferences prefs = getSharedPreferences("APP_PREFS", MODE_PRIVATE);
        String token = prefs.getString("JWT_TOKEN", null);

        ApiService api = ApiClient.getClient().create(ApiService.class);

        Log.d("DEBUG", mensuracaoCreate.toString());
        api.createMensuracao("Bearer " + token, mensuracaoCreate).enqueue(new Callback<MensuracaoDTO>() {
            @Override
            public void onResponse(Call<MensuracaoDTO> call, Response<MensuracaoDTO> resp) {
                if (!resp.isSuccessful() || resp.body() == null) {
                    hideLoading();
                    Toast.makeText(ConclusaoActivity.this,
                            "Falha ao criar mensuração (" + resp.code() + ")", Toast.LENGTH_SHORT).show();

                    Log.e("DEBUG", "Erro: " + resp.code() + ". Message: " + resp.message());
                    return;
                }

                Toast.makeText(ConclusaoActivity.this,
                        "Mensuração criada com sucesso!", Toast.LENGTH_SHORT).show();
                hideLoading();
                Intent it = new Intent(ConclusaoActivity.this, MainActivity.class);
                startActivity(it);
            }

            @Override
            public void onFailure(Call<MensuracaoDTO> call, Throwable throwable) {
                Toast.makeText(ConclusaoActivity.this,
                        "Falha ao criar mensuração (" + throwable.getMessage() + ")", Toast.LENGTH_SHORT).show();
                hideLoading();
            }
        });
    }
}
