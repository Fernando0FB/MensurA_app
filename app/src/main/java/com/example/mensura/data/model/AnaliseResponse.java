package com.example.mensura.data.model;

import java.util.List;

public class AnaliseResponse {
    private PacienteAnalise paciente;
    private AvaliacaoAnalise avaliacao;
    private ResumoRepeticoes resumoRepeticoes;
    private List<String> observacoesClinicas;

    public PacienteAnalise getPaciente() { return paciente; }
    public AvaliacaoAnalise getAvaliacao() { return avaliacao; }
    public ResumoRepeticoes getResumoRepeticoes() { return resumoRepeticoes; }
    public List<String> getObservacoesClinicas() { return observacoesClinicas; }

    public static class PacienteAnalise {
        private String nome;
        private int idade;
        private String sexo;
        private String cpf;

        public String getNome() { return nome; }
        public int getIdade() { return idade; }
        public String getSexo() { return sexo; }
        public String getCpf() { return cpf; }
    }

    public static class AvaliacaoAnalise {
        private String articulacao;
        private String lado;
        private String movimento;
        private String posicao;

        public String getArticulacao() { return articulacao; }
        public String getLado() { return lado; }
        public String getMovimento() { return movimento; }
        public String getPosicao() { return posicao; }
    }

    public static class ResumoRepeticoes {
        private double anguloInicialMedio;
        private double anguloFinalMedio;
        private double excursaoMedia;
        private double dorMedia;
        private int melhorExcursao;
        private int piorExcursao;
        private int quantidadeExecucoes;

        public double getAnguloInicialMedio() { return anguloInicialMedio; }
        public double getAnguloFinalMedio() { return anguloFinalMedio; }
        public double getExcursaoMedia() { return excursaoMedia; }
        public double getDorMedia() { return dorMedia; }
        public int getMelhorExcursao() { return melhorExcursao; }
        public int getPiorExcursao() { return piorExcursao; }
        public int getQuantidadeExecucoes() { return quantidadeExecucoes; }
    }
}
