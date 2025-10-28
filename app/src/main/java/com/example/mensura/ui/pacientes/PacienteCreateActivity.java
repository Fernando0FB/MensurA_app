package com.example.mensura.ui.pacientes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.mensura.data.model.PacienteDTO;
import com.example.mensura.data.network.ApiClient;
import com.example.mensura.data.network.ApiService;
import com.example.mensura.ui.base.BaseActivity;
import com.example.mensura.util.Formatters.CPFMaskTextWatcher;
import com.example.myapplication.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PacienteCreateActivity extends BaseActivity {

    private TextInputEditText edtNome, edtCpf, edtEmail, edtIdade, edtObs, edtDataNasc;
    private TextInputLayout tilCpf, tilDataNasc;
    private AutoCompleteTextView edtSexo;
    private String token;

    private final String[] SEXOS = new String[]{"Masculino","Feminino","Outro","Prefiro não informar"};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paciente_create);


        // Overlay
        setLoadingOverlay(findViewById(R.id.loadingOverlay));

        // Token
        SharedPreferences prefs = getSharedPreferences("APP_PREFS", MODE_PRIVATE);
        token = prefs.getString("JWT_TOKEN", null);

        // Views
        edtNome     = findViewById(R.id.edtNome);
        edtCpf      = findViewById(R.id.edtCpf);
        edtEmail    = findViewById(R.id.edtEmail);
        edtIdade    = findViewById(R.id.edtIdade);
        edtObs      = findViewById(R.id.edtObs);
        edtDataNasc = findViewById(R.id.edtDataNasc);
        tilCpf      = findViewById(R.id.tilCpf);     // 👈 certifique-se que o TextInputLayout do CPF tem esse id
        tilDataNasc = findViewById(R.id.tilDataNasc);
        edtSexo     = findViewById(R.id.edtSexo);

        // Máscara/validação do CPF (mostra erro se < 11 enquanto digita)
        if (edtCpf != null) {
            edtCpf.addTextChangedListener(new CPFMaskTextWatcher(edtCpf, tilCpf));
        }

        // Dropdown de sexo
        android.widget.ArrayAdapter<String> sexoAdapter =
                new android.widget.ArrayAdapter<>(this,
                        android.R.layout.simple_dropdown_item_1line, SEXOS);
        edtSexo.setAdapter(sexoAdapter);

        // DatePicker → yyyy-MM-dd
        MaterialDatePicker<Long> datePicker =
                MaterialDatePicker.Builder.datePicker()
                        .setTitleText("Data de nascimento")
                        .build();

        edtDataNasc.setOnClickListener(v -> datePicker.show(getSupportFragmentManager(), "DP"));
        if (tilDataNasc != null) {
            tilDataNasc.setEndIconOnClickListener(v -> datePicker.show(getSupportFragmentManager(), "DP"));
        }

        datePicker.addOnPositiveButtonClickListener(selection -> {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            edtDataNasc.setText(sdf.format(new Date(selection)));
        });

        // Botões
        findViewById(R.id.btnCancelar).setOnClickListener(v -> finish());
        findViewById(R.id.btnSalvar).setOnClickListener(v -> salvar());
    }

    private void salvar() {
        // Validações mínimas
        String nome = getTrim(edtNome);
        if (TextUtils.isEmpty(nome)) {
            edtNome.setError("Informe o nome");
            edtNome.requestFocus();
            return;
        }

        // pega só dígitos do CPF pra validar/enviar
        String cpfDigits = CPFMaskTextWatcher.unmask(getTrim(edtCpf));
        if (!TextUtils.isEmpty(cpfDigits) && cpfDigits.length() != 11) {
            if (tilCpf != null) tilCpf.setError("CPF deve ter 11 dígitos");
            edtCpf.requestFocus();
            return;
        } else if (tilCpf != null) {
            tilCpf.setError(null);
        }

        PacienteDTO p = new PacienteDTO();
        p.setNome(nome);
        p.setCpf(cpfDigits.isEmpty() ? null : cpfDigits); // 👈 envia sem máscara
        p.setEmail(emptyToNull(getTrim(edtEmail)));

        String dn = getTrim(edtDataNasc); // yyyy-MM-dd
        p.setDataNascimento(emptyToNull(dn));

        String idadeStr = getTrim(edtIdade);
        if (!TextUtils.isEmpty(idadeStr)) {
            try { p.setIdade(Integer.parseInt(idadeStr)); }
            catch (NumberFormatException ignored) { /* deixa null */ }
        }

        p.setSexo(emptyToNull(getTrim(edtSexo)));
        p.setObservacoes(emptyToNull(getTrim(edtObs)));

        // Chamada API
        showLoading();
        ApiService api = ApiClient.getClient().create(ApiService.class);
        api.createPaciente("Bearer " + token, p).enqueue(new Callback<PacienteDTO>() {
            @Override
            public void onResponse(Call<PacienteDTO> call, Response<PacienteDTO> response) {
                hideLoading();
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(PacienteCreateActivity.this, "Paciente criado!", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    startActivity(new Intent(PacienteCreateActivity.this, PacienteListActivity.class));
                    finish();
                } else {
                    String msg = "Erro ao criar paciente (" + response.code() + ")";
                    try { if (response.errorBody() != null) msg += ": " + response.errorBody().string(); }
                    catch (Exception ignored) {}
                    Log.e("API", msg);
                    Toast.makeText(PacienteCreateActivity.this, msg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<PacienteDTO> call, Throwable t) {
                hideLoading();
                Log.e("API", "Falha", t);
                Toast.makeText(PacienteCreateActivity.this, "Falha: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private static String getTrim(TextInputEditText e){
        CharSequence cs = e.getText();
        return cs == null ? "" : cs.toString().trim();
    }
    private static String getTrim(AutoCompleteTextView e){
        CharSequence cs = e.getText();
        return cs == null ? "" : cs.toString().trim();
    }
    private static String emptyToNull(String s){ return (s == null || s.isEmpty()) ? null : s; }
}
