package com.example.mensura.data.model;

import java.util.List;

public class MensuracaoDTO {
    private int id;
    private PacienteMensuracaoDTO pacienteMensuracaoDTO;
    private String articulacao;
    private String lado;
    private String movimento;
    private String posicao;
    private List<RepeticaoDTO> repeticoes;

    public int getId() { return id; }
    public PacienteMensuracaoDTO getPacienteMensuracaoDTO() { return pacienteMensuracaoDTO; }
    public String getArticulacao() { return articulacao; }
    public String getLado() { return lado; }
    public String getMovimento() { return movimento; }
    public String getPosicao() { return posicao; }
    public List<RepeticaoDTO> getRepeticoes() { return repeticoes; }
}