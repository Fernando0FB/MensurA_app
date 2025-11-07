package com.example.mensura.util.Formatters;

public class CpfFormat {
    public static String mask(String cpf) {
        if (cpf == null || cpf.length() != 11) return "Não informado";
        return "***." + cpf.substring(3,6) + "." + cpf.substring(6,9) + "-**";
    }
}
