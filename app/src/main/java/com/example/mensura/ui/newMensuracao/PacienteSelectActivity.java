package com.example.mensura.ui.newMensuracao;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.mensura.data.model.MensuracaoDTO;
import com.example.mensura.data.model.PacienteDTO;
import com.example.mensura.data.model.PagedResponse; // <-- seu modelo já existente
import com.example.mensura.data.network.ApiClient;
import com.example.mensura.data.network.ApiService;
import com.example.mensura.ui.base.BaseActivity;
import com.example.mensura.ui.mensuracoes.MensuracoesActivity;
import com.example.mensura.ui.mensuracoes.MensuracoesAdapter;
import com.example.mensura.ui.pacientes.PacienteListActivity;
import com.example.mensura.ui.pacientes.PacienteListAdapter;
import com.example.myapplication.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@RequiresApi(api = Build.VERSION_CODES.O)
public class PacienteSelectActivity extends BaseActivity {

    private RecyclerView recyclerView;

    private String token = "";

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paciente_select);

        recyclerView = findViewById(R.id.rvSelectPacientes);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        findViewById(R.id.btnVoltar).setOnClickListener(v -> {
            finish();
        });

        setLoadingOverlay(findViewById(R.id.loadingOverlay));
        SharedPreferences prefs = getSharedPreferences("APP_PREFS", MODE_PRIVATE);
        token = prefs.getString("JWT_TOKEN", null);

        findViewById(R.id.btnBuscar).setOnClickListener(v -> {
            EditText edtBuscarPaciente = findViewById(R.id.edtBuscarPaciente);
            String nomePaciente = edtBuscarPaciente.getText().toString();
            loadPacientes(nomePaciente, 0, 20);
        });

        loadPacientes(null, 0, 20);
    }

    private void loadPacientes(String pacienteNome, Integer page, Integer size) {
        showLoading(PacienteSelectActivity.this);

        ApiService api = ApiClient.getClient().create(ApiService.class);
        api.getPacientes("Bearer " + token, pacienteNome, page, size)
                .enqueue(new Callback<PagedResponse<PacienteDTO>>() {
                    @Override
                    public void onResponse(Call<PagedResponse<PacienteDTO>> call,
                                           Response<PagedResponse<PacienteDTO>> response) {
                        hideLoading();
                        if (response.isSuccessful() && response.body() != null) {
                            List<PacienteDTO> lista = response.body().getContent();
                            recyclerView.setAdapter(new PacientesSelectAdapter(lista, paciente -> {
                                Intent intent = new Intent(PacienteSelectActivity.this, MensuracaoCreateActivity.class);
                                intent.putExtra("ID_PACIENTE", Long.valueOf(paciente.getId()));
                                intent.putExtra("NOME_PACIENTE", paciente.getNome().toString());
                                startActivity(intent);
                            }));
                        } else {
                            Toast.makeText(PacienteSelectActivity.this, "Erro ao carregar pacientes", Toast.LENGTH_SHORT).show();
                            Log.e("DEBUG", "Erro: " + response.code() + " - " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<PagedResponse<PacienteDTO>> call, Throwable throwable) {
                        hideLoading();
                        Toast.makeText(PacienteSelectActivity.this, "Erro ao carregar pacientes", Toast.LENGTH_SHORT).show();
                        Log.e("DEBUG", "Erro: " + throwable.getMessage());
                    }
                });
    }
}
