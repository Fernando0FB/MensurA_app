package com.example.mensura.ui.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.mensura.data.model.LoginRequest;
import com.example.mensura.data.model.LoginResponse;
import com.example.mensura.data.network.ApiClient;
import com.example.mensura.data.network.ApiService;
import com.example.mensura.ui.base.BaseActivity;
import com.example.mensura.ui.main.MainActivity;
import com.example.myapplication.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


@RequiresApi(api = Build.VERSION_CODES.O)
public class LoginActivity extends BaseActivity {

    private EditText edtUser, edtPass;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        SharedPreferences prefs = getSharedPreferences("APP_PREFS", MODE_PRIVATE);

        FrameLayout overlay = findViewById(R.id.loadingOverlay);
        setLoadingOverlay(overlay);

        if (prefs.getString("LOGIN", null) != null && prefs.getString("SENHA", null) != null) {
            showLoading(LoginActivity.this);
            LoginRequest request = new LoginRequest(
                    prefs.getString("LOGIN", null),
                    prefs.getString("SENHA", null)
            );
            extractToken(request, new LoginCallback() {
                @Override
                public void onLoginSuccess(String token, String nomeCompleto) {
                    hideLoading();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                }

                @Override
                public void onLoginFailure(String errorMessage) {
                    hideLoading();
                }
            });
        }

        edtUser = findViewById(R.id.edtUsuario);
        edtPass = findViewById(R.id.edtSenha);
        btnLogin = findViewById(R.id.btnEntrar);

        btnLogin.setOnClickListener(v -> doLogin());
    }

    private void doLogin() {
        String login = edtUser.getText().toString();
        String senha = edtPass.getText().toString();

        LoginRequest request = new LoginRequest(login, senha);

        showLoading(LoginActivity.this);
        btnLogin.setEnabled(false);

        extractToken(request, new LoginCallback() {
            @Override
            public void onLoginSuccess(String token, String nomeCompleto) {
                hideLoading();
                btnLogin.setEnabled(true);
                Toast.makeText(LoginActivity.this, "Login OK!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }

            @Override
            public void onLoginFailure(String errorMessage) {
                hideLoading();
                btnLogin.setEnabled(true);
                Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void extractToken(LoginRequest request, LoginCallback callback) {
        ApiService api = ApiClient.getClient().create(ApiService.class);
        api.login(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String token = response.body().getToken();
                    String nomeCompleto = response.body().getNome();

                    SharedPreferences prefs = getSharedPreferences("APP_PREFS", MODE_PRIVATE);
                    prefs.edit().putString("LOGIN", request.getLogin()).apply();
                    prefs.edit().putString("SENHA", request.getSenha()).apply();
                    prefs.edit().putString("JWT_TOKEN", token).apply();
                    prefs.edit().putString("USER_NAME", nomeCompleto).apply();

                    callback.onLoginSuccess(token, nomeCompleto);
                } else {
                    callback.onLoginFailure("Login inválido!");
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                callback.onLoginFailure("Erro de conexão");
            }
        });
    }
}
