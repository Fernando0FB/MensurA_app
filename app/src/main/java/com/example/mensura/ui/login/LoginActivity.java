package com.example.mensura.ui.login;

// LoginActivity.java
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.mensura.ui.main.MainActivity;
import com.example.mensura.data.model.LoginRequest;
import com.example.mensura.data.model.LoginResponse;
import com.example.mensura.data.network.ApiClient;
import com.example.mensura.data.network.ApiService;
import com.example.mensura.ui.base.BaseActivity;
import com.example.myapplication.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends BaseActivity {

    private EditText edtUser, edtPass;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtUser = findViewById(R.id.edtUser);
        edtPass = findViewById(R.id.edtPass);
        btnLogin = findViewById(R.id.btnLogin);

        FrameLayout overlay = findViewById(R.id.loadingOverlay);
        setLoadingOverlay(overlay);

        btnLogin.setOnClickListener(v -> doLogin());
    }

    private void doLogin() {
        String login = edtUser.getText().toString();
        String senha = edtPass.getText().toString();

        LoginRequest request = new LoginRequest(login, senha);

        showLoading();
        btnLogin.setEnabled(false);

        ApiService api = ApiClient.getClient().create(ApiService.class);
        api.login(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                hideLoading();
                btnLogin.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    String token = response.body().getToken();
                    String nomeCompleto = response.body().getNomeCompleto();

                    SharedPreferences prefs = getSharedPreferences("APP_PREFS", MODE_PRIVATE);
                    prefs.edit().putString("JWT_TOKEN", token).apply();
                    prefs.edit().putString("USER_NAME", nomeCompleto).apply();

                    Toast.makeText(LoginActivity.this, "Login OK!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Login inválido!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                hideLoading();
                btnLogin.setEnabled(true);

                Toast.makeText(LoginActivity.this, "Erro: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

}
