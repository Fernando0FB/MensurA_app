package com.example.mensura.ui.mensuracoes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mensura.data.model.AnaliseResponse;
import com.example.mensura.data.model.MensuracaoDTO;
import com.example.mensura.data.model.PagedResponse;
import com.example.mensura.data.network.ApiClient;
import com.example.mensura.data.network.ApiService;
import com.example.mensura.ui.base.BaseActivity;
import com.example.myapplication.R;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@RequiresApi(api = Build.VERSION_CODES.O)
public class MensuracoesActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private EditText valorBusca;
    private String token;

    private MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mensuracoes);

        recyclerView = findViewById(R.id. rvMensuracoes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        valorBusca = findViewById(R.id.edtBuscarMensuracao);

        findViewById(R.id.btnBuscar).setOnClickListener(v -> {
            loadMensuracoes(emptyToNull(getTextTrim(valorBusca)), 0, 20); // page,size se quiser
        });

        setLoadingOverlay(findViewById(R.id.loadingOverlay));

        SharedPreferences prefs = getSharedPreferences("APP_PREFS", MODE_PRIVATE);
        token = prefs.getString("JWT_TOKEN", null);

        findViewById(R.id.btnVoltar).setOnClickListener(v -> finish());

        loadMensuracoes(null, null, 0);

    }

    private static String getTextTrim(EditText e) {
        CharSequence cs = e.getText();
        return cs == null ? "" : cs.toString().trim();
    }

    private static String emptyToNull(String s) { return (s == null || s.isEmpty()) ? null : s; }

    private void loadMensuracoes(String valorBusca, Integer page, Integer size) {
        showLoading(MensuracoesActivity.this);

        ApiService api = ApiClient.getClient().create(ApiService.class);
        api.getMensuracoes("Bearer " + token, valorBusca, page, size)
                .enqueue(new Callback<PagedResponse<MensuracaoDTO>>() {
                    @Override
                    public void onResponse(Call<PagedResponse<MensuracaoDTO>> call,
                                           Response<PagedResponse<MensuracaoDTO>> response) {
                        hideLoading();
                        if (response.isSuccessful() && response.body() != null) {
                            List<MensuracaoDTO> lista = response.body().getContent();
                            recyclerView.setAdapter(new MensuracoesAdapter(lista, id -> {
                                Intent intent = new Intent(MensuracoesActivity.this, ResultadoMensuracaoActivity.class);
                                intent.putExtra("ID_MENSURACAO", id);
                                startActivity(intent);
                            }));
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
}
