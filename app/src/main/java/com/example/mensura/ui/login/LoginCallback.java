package com.example.mensura.ui.login;

public interface LoginCallback {
    void onLoginSuccess(String token, String nomeCompleto);
    void onLoginFailure(String errorMessage);
}