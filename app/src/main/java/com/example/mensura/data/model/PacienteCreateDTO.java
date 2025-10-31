package com.example.mensura.data.model;

public class PacienteCreateDTO {
    private int id;
    private String nome;
    private String cpf;
    private String email;
    private String dataNascimento;
    private String sexo;
    private String observacoes;
    private Long quantidadeMensuracoes;

    public static PacienteCreateDTO from(PacienteDTO paciente) {
        PacienteCreateDTO dto = new PacienteCreateDTO();
        dto.id = paciente.getId();
        dto.nome = paciente.getNome();
        dto.cpf = paciente.getCpf();
        dto.email = paciente.getEmail();
        dto.dataNascimento = paciente.getDataNascimento().toString();
        dto.sexo = paciente.getSexo().toUpperCase();
        dto.observacoes = paciente.getObservacoes();
        dto.quantidadeMensuracoes = paciente.getQuantidadeMensuracoes();
        return dto;
    }
}
