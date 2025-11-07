package com.example.mensura.data.model;

import java.time.LocalDateTime;

public class MensuracaoCreateDTO {
    private Long pacienteId;
    private String articulacao;
    private String lado;
    private String movimento;
    private String posicao;
    private Integer anguloInicial;
    private Integer anguloFinal;
    private Integer excursao;
    private Integer dor;
    private String observacoes;
    private LocalDateTime dataHora;

    public Long getPacienteId() {
        return pacienteId;
    }

    public void setPacienteId(Long pacienteId) {
        this.pacienteId = pacienteId;
    }

    public String getArticulacao() {
        return articulacao;
    }

    public void setArticulacao(String articulacao) {
        this.articulacao = articulacao;
    }

    public String getLado() {
        return lado;
    }

    public void setLado(String lado) {
        this.lado = lado;
    }

    public String getMovimento() {
        return movimento;
    }

    public void setMovimento(String movimento) {
        this.movimento = movimento;
    }

    public String getPosicao() {
        return posicao;
    }

    public void setPosicao(String posicao) {
        this.posicao = posicao;
    }

    public Integer getAnguloInicial() {
        return anguloInicial;
    }

    public void setAnguloInicial(Integer anguloInicial) {
        this.anguloInicial = anguloInicial;
    }

    public Integer getAnguloFinal() {
        return anguloFinal;
    }

    public void setAnguloFinal(Integer anguloFinal) {
        this.anguloFinal = anguloFinal;
    }

    public Integer getExcursao() {
        return excursao;
    }

    public void setExcursao(Integer excursao) {
        this.excursao = excursao;
    }

    public Integer getDor() {
        return dor;
    }

    public void setDor(Integer dor) {
        this.dor = dor;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    @Override
    public String toString() {
        return "MensuracaoCreateDTO{" +
                "pacienteId=" + pacienteId +
                ", articulacao='" + articulacao + '\'' +
                ", lado='" + lado + '\'' +
                ", movimento='" + movimento + '\'' +
                ", posicao='" + posicao + '\'' +
                ", anguloInicial=" + anguloInicial +
                ", anguloFinal=" + anguloFinal +
                ", excursao=" + excursao +
                ", dor=" + dor +
                ", observacoes='" + observacoes + '\'' +
                ", dataHora=" + dataHora +
                '}';
    }
}
