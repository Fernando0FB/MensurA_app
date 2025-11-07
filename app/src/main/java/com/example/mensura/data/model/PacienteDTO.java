package com.example.mensura.data.model;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalDate;

public class PacienteDTO {
    private int id;
    private String nome;
    private String cpf;
    private String email;
    private LocalDate dataNascimento;
    private String sexo;
    private Long quantidadeMensuracoes;
    private String observacoes;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public Long getQuantidadeMensuracoes() {
        return quantidadeMensuracoes;
    }
    public void setQuantidadeMensuracoes(Long quantidadeMensuracoes) {
        this.quantidadeMensuracoes = quantidadeMensuracoes;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Integer getIdade() {
        if (dataNascimento == null) return null;
        LocalDate hoje = LocalDate.now();
        return hoje.getYear() - dataNascimento.getYear() -
                (hoje.getDayOfYear() < dataNascimento.getDayOfYear() ? 1 : 0);
    }

    @Override
    public String toString() {
        return "PacienteDTO{" + "id=" + id + ", nome='" + nome + '"' + ", cpf='" + cpf + '"' + ", email='"
                + email + '"' + ", dataNascimento=" + dataNascimento + ", sexo='" + sexo + '"' + ", observacoes='"
                + observacoes + '"' + ", quantidadeMensuracoes=" + quantidadeMensuracoes + '}';
    }
}
