package com.example.mensura.util.Formatters;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import com.google.android.material.textfield.TextInputLayout;

public class CPFMaskTextWatcher implements TextWatcher {

    private final EditText editText;
    private final TextInputLayout til; // pode ser null
    private boolean selfChange = false;
    private String lastDigits = "";

    public CPFMaskTextWatcher(EditText editText, TextInputLayout til) {
        this.editText = editText;
        this.til = til;
    }

    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

    @Override public void afterTextChanged(Editable s) {
        if (selfChange) return;

        String digits = onlyDigits(s.toString());
        if (digits.equals(lastDigits)) return;

        // limita a 11 dígitos
        if (digits.length() > 11) digits = digits.substring(0, 11);

        String masked = formatCpf(digits);

        selfChange = true;
        editText.setText(masked);
        editText.setSelection(masked.length());
        selfChange = false;

        lastDigits = digits;

        // validação simples (apenas tamanho)
        if (til != null) {
            if (digits.isEmpty()) {
                til.setError(null);
            } else if (digits.length() < 11) {
                til.setError("CPF deve ter 11 dígitos");
            } else {
                til.setError(null);
            }
        }
    }

    private static String onlyDigits(String s) {
        return s == null ? "" : s.replaceAll("\\D+", "");
    }

    private static String formatCpf(String digits) {
        int len = digits.length();
        StringBuilder out = new StringBuilder();

        for (int i = 0; i < len; i++) {
            out.append(digits.charAt(i));
            if (i == 2 && len > 3) out.append('.');
            if (i == 5 && len > 6) out.append('.');
            if (i == 8 && len > 9) out.append('-');
        }
        return out.toString();
    }

    /** Use isto para enviar ao backend (sem máscara) */
    public static String unmask(String s) {
        return onlyDigits(s);
    }
}