package com.example.mensura.ui.pacientes;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.mensura.data.model.PacienteCreateDTO;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@RequiresApi(api = Build.VERSION_CODES.O)
public class PacienteCreateActivity extends BaseActivity {

    private TextInputEditText edtNome, edtCpf, edtEmail, edtObs, edtDataNasc, edtTelefone;
    private Spinner edtSexo;
    private TextInputLayout tilCpf, tilSexo;
    private String token;

    private final String[] SEXOS = new String[]{"Selecione o Sexo", "Masculino","Feminino"};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paciente_create);

        SharedPreferences prefs = getSharedPreferences("APP_PREFS", MODE_PRIVATE);
        token = prefs.getString("JWT_TOKEN", null);

        // Suponho que você já tenha esses campos
        edtNome     = findViewById(R.id.edtNome);
        edtDataNasc = findViewById(R.id.edtDataNascimento);
        edtSexo     = findViewById(R.id.edtSexo); // Campo Sexo
        tilSexo     = findViewById(R.id.tilSexo);  // TextInputLayout para o Sexo
        edtCpf      = findViewById(R.id.edtCpf);
        tilCpf      = findViewById(R.id.tilCpf);
        edtEmail    = findViewById(R.id.edtEmail);
        edtObs      = findViewById(R.id.edtObs);

        ArrayAdapter<String> sexoAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, SEXOS) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }
        };
        sexoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        edtSexo.setAdapter(sexoAdapter);

        edtSexo.setSelection(0);

        edtDataNasc.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, year, monthOfYear, dayOfMonth) -> {
                        String selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%d", dayOfMonth, monthOfYear + 1, year);
                        edtDataNasc.setText(selectedDate);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });


        edtCpf.addTextChangedListener(new CPFMaskTextWatcher(edtCpf, tilCpf));

        // Configurando o click do botão de salvar
        findViewById(R.id.btnSalvar).setOnClickListener(v -> {

            salvar();
        });
    }


    public void salvar() {
        boolean temErro = false;
        String nome = getTrim(edtNome);
        if (TextUtils.isEmpty(nome)) {
            edtNome.setError("Nome é obrigatório");
            edtNome.requestFocus();
            temErro = true;
        }

        // Validação da Data de Nascimento
        String dataNasc = getTrim(edtDataNasc);
        if (TextUtils.isEmpty(dataNasc)) {
            edtDataNasc.setError("Data de Nascimento é obrigatória");
            edtDataNasc.requestFocus();
            temErro = true;
        }

        // Validação do CPF
        String cpfDigits = CPFMaskTextWatcher.unmask(getTrim(edtCpf));
        if (TextUtils.isEmpty(cpfDigits)) {
            if (tilCpf != null) tilCpf.setError("CPF é obrigatório");
            edtCpf.requestFocus();
            temErro = true;
        } else if (cpfDigits.length() != 11) {
            if (tilCpf != null) tilCpf.setError("CPF deve ter 11 dígitos");
            edtCpf.requestFocus();
            temErro = true;
        } else if (tilCpf != null) {
            tilCpf.setError(null);
        }

        // Validação do Email
        String email = getTrim(edtEmail);
        if (TextUtils.isEmpty(email)) {
            edtEmail.setError("Email é obrigatório");
            edtEmail.requestFocus();
            temErro = true;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.setError("Email inválido");
            edtEmail.requestFocus();
            temErro = true;
        }

        if (temErro) return;

        PacienteDTO novoPaciente = new PacienteDTO();
        novoPaciente.setNome(getTrim(edtNome));
        novoPaciente.setCpf(cpfDigits);
        novoPaciente.setSexo(edtSexo.getSelectedItem().toString().trim());
        novoPaciente.setEmail(emptyToNull(getTrim(edtEmail)));
        novoPaciente.setDataNascimento(LocalDate.parse(edtDataNasc.getText(), DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        novoPaciente.setObservacoes(emptyToNull(getTrim(edtObs)));

        showLoading();
        ApiService api = ApiClient.getClient().create(ApiService.class);
        api.createPaciente("Bearer " + token, PacienteCreateDTO.from(novoPaciente)).enqueue(new Callback<PacienteDTO>() {
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

    /*
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
        p.setCpf(cpfDigits.isEmpty() ? null : cpfDigits);
        p.setEmail(emptyToNull(getTrim(edtEmail)));

        LocalDate dataNasc = LocalDate.parse(edtDataNasc.getText(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        p.setDataNascimento(dataNasc);

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
     */
    private static String getTrim(TextInputEditText e){
        CharSequence cs = e.getText();
        return cs == null ? "" : cs.toString().trim();
    }
    private static String emptyToNull(String s){ return (s == null || s.isEmpty()) ? null : s; }
}
