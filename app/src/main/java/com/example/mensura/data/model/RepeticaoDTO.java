package com.example.mensura.data.model;

public class RepeticaoDTO {
    private int id;
    private int anguloInicial;
    private int anguloFinal;
    private int excursao;
    private int dor;
    private int serie;
    private String dataHoraRepeticao;
    private String observacoes;
    private int mensuracaoId;

    public int getId() { return id; }
    public int getAnguloInicial() { return anguloInicial; }
    public int getAnguloFinal() { return anguloFinal; }
    public int getExcursao() { return excursao; }
    public int getDor() { return dor; }
    public int getSerie() { return serie; }
    public String getDataHoraRepeticao() { return dataHoraRepeticao; }
    public String getObservacoes() { return observacoes; }

    public RepeticaoDTO(int anguloInicial, int anguloFinal, int excursao, int dor, int serie, String dataHoraRepeticao, String observacoes, int mensuracaoId) {
        this.anguloInicial = anguloInicial;
        this.anguloFinal = anguloFinal;
        this.excursao = excursao;
        this.dor = dor;
        this.serie = serie;
        this.dataHoraRepeticao = dataHoraRepeticao;
        this.observacoes = observacoes;
        this.mensuracaoId = mensuracaoId;
    }
}