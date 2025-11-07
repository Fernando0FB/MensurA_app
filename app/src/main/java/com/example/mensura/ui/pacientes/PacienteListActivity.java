package com.example.mensura.ui.pacientes;

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

import com.example.mensura.data.model.PacienteDTO;
import com.example.mensura.data.model.PagedResponse;
import com.example.mensura.data.network.ApiClient;
import com.example.mensura.data.network.ApiService;
import com.example.mensura.ui.base.BaseActivity;
import com.example.myapplication.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@RequiresApi(api = Build.VERSION_CODES.O)
public class PacienteListActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private String token;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pacienteslist);

        recyclerView = findViewById(R.id.rvPacientes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        findViewById(R.id.btnNovoPaciente).setOnClickListener(v -> {
            startActivity(new Intent(this, PacienteCreateActivity.class));
        });

        findViewById(R.id.btnVoltar).setOnClickListener(v -> {
            finish();
        });

        setLoadingOverlay(findViewById(R.id.loadingOverlay));

        SharedPreferences prefs = getSharedPreferences("APP_PREFS", MODE_PRIVATE);
        token = prefs.getString("JWT_TOKEN", null);

        findViewById(R.id.btnBuscar).setOnClickListener(v -> {
            EditText etNomePaciente = findViewById(R.id.edtBuscarPaciente);
            String nomePaciente = etNomePaciente.getText().toString();
            loadPacientes(nomePaciente, 0, 20);
        });

        loadPacientes(null, 0, 20);
    }

    private void loadPacientes(String pacienteNome, Integer page, Integer size) {
        showLoading(PacienteListActivity.this);

        ApiService api = ApiClient.getClient().create(ApiService.class);
        api.getPacientes("Bearer " + token, pacienteNome, page, size)
                .enqueue(new Callback<PagedResponse<PacienteDTO>>() {
                    @Override
                    public void onResponse(Call<PagedResponse<PacienteDTO>> call,
                                           Response<PagedResponse<PacienteDTO>> response) {
                        hideLoading();
                        if (response.isSuccessful() && response.body() != null) {
                            List<PacienteDTO> lista = response.body().getContent();
                            recyclerView.setAdapter(new PacienteListAdapter(lista));
                        } else {
                            Toast.makeText(PacienteListActivity.this, "Erro ao carregar pacientes", Toast.LENGTH_SHORT).show();
                            Log.e("DEBUG", "Erro: " + response.code() + " - " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<PagedResponse<PacienteDTO>> call, Throwable throwable) {
                        hideLoading();
                        Toast.makeText(PacienteListActivity.this, "Erro ao carregar pacientes", Toast.LENGTH_SHORT).show();
                        Log.e("DEBUG", "Erro: " + throwable.getMessage());
                    }
                });
    }

    public void editarPaciente(int id) {
        Intent intent = new Intent(this, PacienteCreateActivity.class);
        intent.putExtra("PACIENTE_ID", id);
        startActivity(intent);
    }
}
