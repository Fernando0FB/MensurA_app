package com.example.mensura.data.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MensuracaoDTO {
    private int id;
    private PacienteMensuracaoDTO pacienteMensuracaoDTO;
    private String articulacao;
    private String lado;
    private String movimento;
    private String posicao;
    private Integer anguloInicial;
    private Integer anguloFinal;
    private Integer excursao;
    private Integer dor;
    private String observacao;
    private LocalDateTime dataHora;

    public int getId() { return id; }
    public PacienteMensuracaoDTO getPacienteMensuracaoDTO() { return pacienteMensuracaoDTO; }
    public String getArticulacao() { return articulacao; }
    public String getLado() { return lado; }
    public String getMovimento() { return movimento; }
    public String getPosicao() { return posicao; }

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

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
    public LocalDateTime getDataHora() {
        return dataHora;
    }
    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }
}