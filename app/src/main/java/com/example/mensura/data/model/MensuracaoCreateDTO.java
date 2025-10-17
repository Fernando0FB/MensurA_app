package com.example.mensura.data.model;

public class MensuracaoCreateDTO {
    private Long pacienteId;
    private String articulacao;
    private String lado;
    private String movimento;
    private String posicao;

    public Long getPacienteId() { return pacienteId; }
    public String getArticulacao() { return articulacao; }
    public String getLado() { return lado; }
    public String getMovimento() { return movimento; }
    public String getPosicao() { return posicao; }

    public void setPacienteId(Long pacienteId) {
        this.pacienteId = pacienteId;
    }

    public void setArticulacao(String articulacao) {
        this.articulacao = articulacao;
    }

    public void setLado(String lado) {
        this.lado = lado;
    }

    public void setMovimento(String movimento) {
        this.movimento = movimento;
    }

    public void setPosicao(String posicao) {
        this.posicao = posicao;
    }
}
