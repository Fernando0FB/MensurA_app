package com.example.mensura.ui.mensuracoes;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.example.mensura.data.model.AnaliseResponse;
import com.example.mensura.data.network.ApiClient;
import com.example.mensura.data.network.ApiService;
import com.example.mensura.ui.base.BaseActivity;
import com.example.mensura.util.Formatters.CpfFormat;
import com.example.mensura.util.Formatters.StringUtils;
import com.example.myapplication.R;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@RequiresApi(api = Build.VERSION_CODES.O)
public class ResultadoMensuracaoActivity extends BaseActivity {

    String token = "";
    TextView tvNome, tvCpf, tvNascimento, tvDataHora, tvArticulacao, tvTipo, tvAngInicial, tvAngFinal, tvExcursao, tvAmplitudePct, tvDorBadge, tvDescricaoDor, tvObservacoes;

    LinearProgressIndicator progAmplitude, progDor;


    AnaliseResponse analise = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analise_mensuracao);

        setLoadingOverlay(findViewById(R.id.loadingOverlay));

        showLoading(ResultadoMensuracaoActivity.this);

        findViewById(R.id.btnVoltar).setOnClickListener(v -> finish());

        SharedPreferences prefs = getSharedPreferences("APP_PREFS", MODE_PRIVATE);
        token = prefs.getString("JWT_TOKEN", null);

        Integer idMensuracao = getIntent().getIntExtra("ID_MENSURACAO", 0);

        if (idMensuracao == 0) {
            finish();
        }

        showLoading(ResultadoMensuracaoActivity.this);
        ApiService api = ApiClient.getClient().create(ApiService.class);
        api.getAnaliseMensuracao(idMensuracao, "Bearer " + token)
                .enqueue(new Callback<AnaliseResponse>() {
                    @Override
                    public void onResponse(Call<AnaliseResponse> call, Response<AnaliseResponse> response) {
                        hideLoading();

                        if (response.isSuccessful() && response.body() != null) {
                            analise = response.body();
                            loadCampos();
                            loadInformacoes(analise);
                        } else {
                            Toast.makeText(ResultadoMensuracaoActivity.this, "Erro ao buscar análise", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<AnaliseResponse> call, Throwable t) {
                        hideLoading();
                        Toast.makeText(ResultadoMensuracaoActivity.this, "Falha: " + t.getMessage(), Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
    }

    public void loadCampos() {
        tvNome = findViewById(R.id.tvNome);
        tvCpf = findViewById(R.id.tvCpf);
        tvNascimento = findViewById(R.id.tvNascimento);
        tvDataHora = findViewById(R.id.tvDataHora);

        tvArticulacao = findViewById(R.id.tvArticulacao);
        tvTipo = findViewById(R.id.tvTipo);
        tvAngInicial = findViewById(R.id.tvAngInicial);
        tvAngFinal = findViewById(R.id.tvAngFinal);
        tvExcursao = findViewById(R.id.tvExcursao);

        tvAmplitudePct = findViewById(R.id.tvAmplitudePct);
        tvDorBadge = findViewById(R.id.tvDorBadge);
        tvDescricaoDor = findViewById(R.id.tvDescricaoDor);
        tvObservacoes = findViewById(R.id.tvObservacoes);

        progAmplitude = findViewById(R.id.progAmplitude);
        progDor = findViewById(R.id.progDor);
    }

    public void loadInformacoes(AnaliseResponse analise) {
        Integer excursao = analise.getAvaliacao().getExcursao();
        Integer dor = analise.getAvaliacao().getDor();

        tvNome.setText(analise.getPaciente().getNome());
        tvCpf.setText(CpfFormat.mask(analise.getPaciente().getCpf()));
        tvNascimento.setText(analise.getPaciente().getDataNascimento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        tvDataHora.setText(analise.getAvaliacao().getDataHora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm")));

        tvArticulacao.setText(StringUtils.capitalize(analise.getAvaliacao().getArticulacao()) + " " + StringUtils.capitalize(analise.getAvaliacao().getLado()));
        tvTipo.setText(StringUtils.capitalize(analise.getAvaliacao().getMovimento()));
        tvAngInicial.setText(analise.getAvaliacao().getAnguloInicial() + "º");
        tvAngFinal.setText(analise.getAvaliacao().getAnguloFinal() + "º");
        tvExcursao.setText(excursao + "º");

        tvAmplitudePct.setText(String.format("%d%%", (excursao * 100) / 180));

        setInformacoesDor(dor);
    }

    public void setInformacoesDor(Integer dor) {
        tvDorBadge.setText(dor + "/10");
        progDor.setProgress(dor);

        if (dor == 0) {
            tvDorBadge.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_pill_grey));
            tvDescricaoDor.setText("Sem dor");
        } else if (dor >= 1 && dor <= 3) {
            tvDorBadge.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_pill_green));
            tvDescricaoDor.setText("Dor leve");
        } else if (dor >= 4 && dor <= 6) {
            tvDorBadge.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_pill_yellow));
            tvDescricaoDor.setText("Dor moderada");
        } else if (dor >= 7 && dor <= 9) {
            tvDorBadge.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_pill_orange));
            tvDescricaoDor.setText("Dor intensa");
        } else if (dor == 10) {
            tvDorBadge.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_pill_red));
            tvDescricaoDor.setText("Dor insuportável");
        }
    }
}
