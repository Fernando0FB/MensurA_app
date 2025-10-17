package com.example.mensura.ui.newMensuracao;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.mensura.data.model.PacienteDTO;
import com.example.mensura.data.model.PagedResponse; // <-- seu modelo já existente
import com.example.mensura.data.network.ApiClient;
import com.example.mensura.data.network.ApiService;
import com.example.myapplication.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PacienteSelectActivity extends AppCompatActivity {

    private ApiService api;
    private PacientesAdapter adapter;
    private RecyclerView rv;
    private SwipeRefreshLayout swipe;
    private android.widget.ProgressBar progress;

    private String token = "";
    private String query = null;

    // paginação
    private int page = 0;
    private final int size = 20;
    private boolean loading = false;
    private boolean last = false;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paciente_select);

        api = ApiClient.getClient().create(ApiService.class);
        rv = findViewById(R.id.rvPacientes);
        swipe = findViewById(R.id.swipe);
        progress = findViewById(R.id.progress);
        SearchView searchView = findViewById(R.id.searchView);

        adapter = new PacientesAdapter(p -> {
            // Ao selecionar, vai para a tela de medição
            Intent intent = new Intent(this, MensuracaoCreateActivity.class);
            intent.putExtra("NEW_MEASUREMENT", true);
            intent.putExtra("PACIENTE_ID", p.getId());
            intent.putExtra("PACIENTE_NOME", p.getNome());
            startActivity(intent);
        });

        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        // Scroll infinito
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override public void onScrolled(@Nullable RecyclerView recyclerView, int dx, int dy) {
                if (dy <= 0) return;
                LinearLayoutManager lm = (LinearLayoutManager) rv.getLayoutManager();
                if (lm == null) return;
                int visible = lm.getChildCount();
                int total = lm.getItemCount();
                int firstVisible = lm.findFirstVisibleItemPosition();
                if (!loading && !last && (visible + firstVisible) >= (total - 5)) {
                    loadPage(page + 1, false);
                }
            }
        });

        // Pull-to-refresh
        swipe.setOnRefreshListener(() -> {
            page = 0; last = false;
            loadPage(0, true);
        });

        // Busca por nome
        searchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String s) {
                query = (s == null || s.trim().isEmpty()) ? null : s.trim();
                page = 0; last = false;
                loadPage(0, true);
                searchView.clearFocus();
                return true;
            }
            @Override public boolean onQueryTextChange(String s) { return false; }
        });

        token = getTokenFromPrefs();
        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Token ausente. Faça login.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Carrega a primeira página
        loadPage(0, true);
    }

    private void loadPage(int pageToLoad, boolean clear) {
        if (loading) return;
        loading = true;
        if (!swipe.isRefreshing()) progress.setVisibility(android.view.View.VISIBLE);

        Call<PagedResponse<PacienteDTO>> call =
                api.getPacientes("Bearer " + token, query, pageToLoad, size);

        call.enqueue(new Callback<PagedResponse<PacienteDTO>>() {
            @Override
            public void onResponse(Call<PagedResponse<PacienteDTO>> call, Response<PagedResponse<PacienteDTO>> resp) {
                loading = false;
                progress.setVisibility(android.view.View.GONE);
                swipe.setRefreshing(false);

                if (!resp.isSuccessful() || resp.body() == null) {
                    Toast.makeText(PacienteSelectActivity.this, "Falha ao carregar pacientes (" + resp.code() + ")", Toast.LENGTH_SHORT).show();
                    return;
                }

                PagedResponse<PacienteDTO> body = resp.body();
                List<PacienteDTO> content = body.getContent();
                last = body.isLast();
                page = body.getNumber();

                if (clear) adapter.replaceAll(content);
                else adapter.addAll(content);
            }

            @Override
            public void onFailure(Call<PagedResponse<PacienteDTO>> call, Throwable t) {
                loading = false;
                progress.setVisibility(android.view.View.GONE);
                swipe.setRefreshing(false);
                Toast.makeText(PacienteSelectActivity.this, "Erro de rede: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getTokenFromPrefs() {
        // TODO: confirme NOME do arquivo e CHAVE do token
        SharedPreferences sp = getSharedPreferences("APP_PREFS", MODE_PRIVATE);
        return sp.getString("JWT_TOKEN", ""); // ex.: "eyJhbGciOi..."
    }
}
