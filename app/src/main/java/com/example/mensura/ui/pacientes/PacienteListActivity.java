package com.example.mensura.ui.pacientes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mensura.data.model.MensuracaoDTO;
import com.example.mensura.data.model.PacienteDTO;
import com.example.mensura.data.model.PagedResponse;
import com.example.mensura.data.network.ApiClient;
import com.example.mensura.data.network.ApiService;
import com.example.mensura.ui.base.BaseActivity;
import com.example.mensura.ui.main.MainActivity;
import com.example.mensura.ui.mensuracoes.MensuracoesActivity;
import com.example.myapplication.R;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PacienteListActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private String token;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pacienteslist);

        //TODO ver para adicionar a filtragem com base no nome do paciente digitado
        recyclerView = findViewById(R.id.rvPacientes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //TODO validar o scroll infinito das infos. se caso tiver mais do que 20 infos, como que vai funcionar o scroll?

        //TODO Botão de voltar na toolbar

        findViewById(R.id.btnNovoPaciente).setOnClickListener(v -> {
            startActivity(new Intent(this, PacienteCreateActivity.class));
        });

        setLoadingOverlay(findViewById(R.id.loadingOverlay));

        SharedPreferences prefs = getSharedPreferences("APP_PREFS", MODE_PRIVATE);
        token = prefs.getString("JWT_TOKEN", null);

        loadPacientes(null, 0, 20);
    }

    private void loadPacientes(String pacienteNome, Integer page, Integer size) {
        showLoading();

        ApiService api = ApiClient.getClient().create(ApiService.class);
        api.getPacientes("Bearer " + token, pacienteNome, page, size)
                .enqueue(new Callback<PagedResponse<PacienteDTO>>() {
                    @Override
                    public void onResponse(Call<PagedResponse<PacienteDTO>> call,
                                           Response<PagedResponse<PacienteDTO>> response) {
                        hideLoading();
                        if (response.isSuccessful() && response.body() != null) {
                            Log.e("UAAAA", "DEU SUCESSO");
                            Log.e("UAAAA", String.valueOf(response.body().getContent().isEmpty()));
                            Log.e("UAAAA", String.valueOf(response.body().getContent().size()));
                            List<PacienteDTO> lista = response.body().getContent();
                            recyclerView.setAdapter(new PacienteListAdapter(lista, id -> editarPaciente(id)));
                        } else {
                            Log.e("UAAAA", "Erro: " + response.message());
                            Toast.makeText(PacienteListActivity.this, "Erro ao carregar pacientes", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<PagedResponse<PacienteDTO>> call, Throwable throwable) {
                        hideLoading();
                        Toast.makeText(PacienteListActivity.this, "Falha: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("UAAAA", throwable.getMessage());
                    }
                });
    }

    public void editarPaciente(int id) {
        Intent intent = new Intent(this, PacienteEditActivity.class);
        intent.putExtra("PACIENTE_ID", id);
        startActivity(intent);
    }
}
