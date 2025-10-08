package com.example.mensura.ui.mensuracoes;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mensura.data.model.AnaliseResponse;
import com.example.mensura.data.model.MensuracaoDTO;
import com.example.mensura.data.model.PagedResponse;
import com.example.mensura.data.network.ApiClient;
import com.example.mensura.data.network.ApiService;
import com.example.mensura.ui.base.BaseActivity;
import com.example.myapplication.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MensuracoesActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mensuracoes);

        recyclerView = findViewById(R.id.recyclerMensuracoes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        setLoadingOverlay(findViewById(R.id.loadingOverlay));

        // Pega o token salvo no login
        SharedPreferences prefs = getSharedPreferences("APP_PREFS", MODE_PRIVATE);
        token = prefs.getString("JWT_TOKEN", null);

        loadMensuracoes();
    }

    private void loadMensuracoes() {
        showLoading();

        ApiService api = ApiClient.getClient().create(ApiService.class);
        api.getMensuracoes("Bearer " + token).enqueue(new Callback<PagedResponse<MensuracaoDTO>>() {
            @Override
            public void onResponse(Call<PagedResponse<MensuracaoDTO>> call, Response<PagedResponse<MensuracaoDTO>> response) {
                hideLoading();

                if (response.isSuccessful() && response.body() != null) {
                    List<MensuracaoDTO> lista = response.body().getContent();

                    // Passa o callback de clique no botão "Analisar"
                    MensuracoesAdapter adapter = new MensuracoesAdapter(lista,  id -> buscarAnalise(id));
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(MensuracoesActivity.this, "Erro ao carregar mensurações", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PagedResponse<MensuracaoDTO>> call, Throwable t) {
                hideLoading();
                Toast.makeText(MensuracoesActivity.this, "Falha: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void buscarAnalise(int idMensuracao) {
        showLoading();

        ApiService api = ApiClient.getClient().create(ApiService.class);
        api.getAnaliseMensuracao(idMensuracao, "Bearer " + token)
                .enqueue(new Callback<AnaliseResponse>() {
                    @Override
                    public void onResponse(Call<AnaliseResponse> call, Response<AnaliseResponse> response) {
                        hideLoading();

                        if (response.isSuccessful() && response.body() != null) {
                            AnaliseResponse analise = response.body();
                            mostrarDialogAnalise(analise);
                        } else {
                            Toast.makeText(MensuracoesActivity.this, "Erro ao buscar análise", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<AnaliseResponse> call, Throwable t) {
                        hideLoading();
                        Toast.makeText(MensuracoesActivity.this, "Falha: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void mostrarDialogAnalise(AnaliseResponse analise) {
        String resumo =
                "👤 Paciente: " + analise.getPaciente().getNome() + "\n" +
                        "Idade: " + analise.getPaciente().getIdade() + " anos\n" +
                        "Sexo: " + analise.getPaciente().getSexo() + "\n" +
                        "CPF: " + analise.getPaciente().getCpf() + "\n\n" +
                        "🦵 Avaliação: " + analise.getAvaliacao().getArticulacao() + " - " +
                        analise.getAvaliacao().getLado() + " (" + analise.getAvaliacao().getMovimento() + ")\n" +
                        "Posição: " + analise.getAvaliacao().getPosicao() + "\n\n" +
                        "📊 Excursão média: " + analise.getResumoRepeticoes().getExcursaoMedia() + "\n" +
                        "Dor média: " + analise.getResumoRepeticoes().getDorMedia() + "\n" +
                        "Melhor excursão: " + analise.getResumoRepeticoes().getMelhorExcursao() + "\n" +
                        "Pior excursão: " + analise.getResumoRepeticoes().getPiorExcursao() + "\n" +
                        "Execuções: " + analise.getResumoRepeticoes().getQuantidadeExecucoes() + "\n\n" +
                        "📝 Observações: " + String.join(", ", analise.getObservacoesClinicas());

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Análise da Mensuração")
                .setMessage(resumo)
                .setPositiveButton("Fechar", null)
                .show();
    }
}
