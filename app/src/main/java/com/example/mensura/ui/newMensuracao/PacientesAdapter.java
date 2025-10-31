package com.example.mensura.ui.newMensuracao;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mensura.data.model.PacienteDTO;
// ATENÇÃO: garanta que este R é do SEU módulo/app.
// Se o teu pacote do R é com.example.mensura.R, troque a linha abaixo:
import com.example.myapplication.R;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PacientesAdapter extends RecyclerView.Adapter<PacientesAdapter.VH> {

    public interface OnPacienteClick { void onClick(PacienteDTO p); }

    private final List<PacienteDTO> data = new ArrayList<>();
    private final OnPacienteClick onClick;

    public PacientesAdapter(OnPacienteClick onClick) {
        this.onClick = onClick;
    }

    public void replaceAll(List<PacienteDTO> novos) {
        data.clear();
        if (novos != null) data.addAll(novos);
        notifyDataSetChanged();
    }

    public void addAll(List<PacienteDTO> novos) {
        if (novos == null || novos.isEmpty()) return;
        int start = data.size();
        data.addAll(novos);
        notifyItemRangeInserted(start, novos.size());
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_paciente, parent, false);
        return new VH(v);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        PacienteDTO p = data.get(position);

        // Nome
        String nome = (p.getNome() != null && !p.getNome().isEmpty()) ? p.getNome() : "Paciente";
        h.nomePaciente.setText(nome);

        // CPF (mascarado / "Não informado")
        h.cpfPaciente.setText(cpfFormat(p.getCpf()));

        // Data de nascimento
        String dn = p.getDataNascimento().toString();
        if (dn != null && !dn.isEmpty()) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    DateTimeFormatter F = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    h.dataNascimentoPaciente.setText("Nasc.: " + LocalDate.parse(dn).format(F));
                } else {
                    // Fallback simples: mostra bruto ou adapta se vier AAAA-MM-DD
                    String exib = dn;
                    if (dn.matches("\\d{4}-\\d{2}-\\d{2}")) {
                        exib = dn.substring(8,10) + "/" + dn.substring(5,7) + "/" + dn.substring(0,4);
                    }
                    h.dataNascimentoPaciente.setText("Nasc.: " + exib);
                }
            } catch (Exception e) {
                h.dataNascimentoPaciente.setText("Nasc.: " + dn);
            }
        } else {
            h.dataNascimentoPaciente.setText("Nasc.: Não informado");
        }

        // Idade
        h.idadePaciente.setText(p.getIdade() != null ? (p.getIdade() + " anos") : "—");

        // Clique
        h.itemView.setOnClickListener(v -> {
            if (onClick != null) onClick.onClick(p);
        });
    }

    @Override
    public int getItemCount() { return data.size(); }

    private String cpfFormat(String cpf) {
        if (cpf == null || cpf.length() != 11) return "Não informado";
        return "***." + cpf.substring(3,6) + "." + cpf.substring(6,9) + "-**";
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView nomePaciente, cpfPaciente, dataNascimentoPaciente, idadePaciente;

        VH(@NonNull View itemView) {
            super(itemView);
            //TODO fazer os dados aqui

            // Ajuda a detectar R/layout errado na hora:
            if (nomePaciente == null || cpfPaciente == null ||
                    dataNascimentoPaciente == null || idadePaciente == null) {
                Log.e("PacientesAdapter", "IDs do item_paciente não encontrados. " +
                        "Verifique o import do R e se o XML contém esses ids.");
            }
        }
    }
}
