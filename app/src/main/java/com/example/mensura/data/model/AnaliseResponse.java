package com.example.mensura.data.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class AnaliseResponse {
    private PacienteAnalise paciente;
    private AvaliacaoAnalise avaliacao;

    public PacienteAnalise getPaciente() { return paciente; }
    public AvaliacaoAnalise getAvaliacao() { return avaliacao; }

    public static class PacienteAnalise {
        private String nome;
        private String sexo;
        private LocalDate dataNascimento;
        private String cpf;

        public String getNome() { return nome; }
        public LocalDate getDataNascimento() { return dataNascimento; }
        public String getSexo() { return sexo; }
        public String getCpf() { return cpf; }

        @Override
        public String toString() {
            return "PacienteAnalise{" +
                    "nome='" + nome + '\'' +
                    ", sexo='" + sexo + '\'' +
                    ", dataNascimento=" + dataNascimento +
                    ", cpf='" + cpf + '\'' +
                    '}';
        }
    }

    public static class AvaliacaoAnalise {
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

        public Integer getAnguloInicial() {
            return anguloInicial;
        }
        public Integer getAnguloFinal() {
            return anguloFinal;
        }
        public Integer getExcursao() {
            return excursao;
        }
        public Integer getDor() {
            return dor;
        }
        public String getObservacao() {
            return observacao;
        }

        public String getArticulacao() { return articulacao; }
        public String getLado() { return lado; }
        public String getMovimento() { return movimento; }
        public String getPosicao() { return posicao; }
        public LocalDateTime getDataHora() { return dataHora; }

        @Override
        public String toString() {
            return "AvaliacaoAnalise{" +
                    "articulacao='" + articulacao + '\'' +
                    ", lado='" + lado + '\'' +
                    ", movimento='" + movimento + '\'' +
                    ", posicao='" + posicao + '\'' +
                    ", anguloInicial=" + anguloInicial +
                    ", anguloFinal=" + anguloFinal +
                    ", excursao=" + excursao +
                    ", dor=" + dor +
                    ", observacao='" + observacao + '\'' +
                    ", dataHora=" + dataHora +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "AnaliseResponse{" +
                "paciente=" + paciente.toString() +
                ", avaliacao=" + avaliacao.toString() +
                '}';
    }

}
