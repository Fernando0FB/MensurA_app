package com.example.mensura.util.Formatters;

public class StringUtils {

    public static String capitalize(String s) {
        if (s == null || s.isEmpty()) return "";
        String[] words = s.split("\\s+");
        StringBuilder capitalized = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                capitalized.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1).toLowerCase())
                        .append(" ");
            }
        }
        return capitalized.toString().trim();
    }

    public static String removeAccentsAndUpper(String str) {
        if (str == null || str.isEmpty()) return "";
        String normalized = java.text.Normalizer.normalize(str, java.text.Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{M}", "").toUpperCase();
    }

}
