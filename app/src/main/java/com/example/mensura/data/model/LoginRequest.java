package com.example.mensura.data.model;

public class LoginRequest {
    private String login;
    private String senha;

    public LoginRequest(String login, String senha) {
        this.login = login;
        this.senha = senha;
    }
}
