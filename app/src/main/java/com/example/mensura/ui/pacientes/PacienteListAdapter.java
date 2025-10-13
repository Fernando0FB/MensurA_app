package com.example.mensura.ui.pacientes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mensura.data.model.PacienteDTO;
import com.example.myapplication.R;
import com.google.android.material.button.MaterialButton;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PacienteListAdapter extends RecyclerView.Adapter<PacienteListAdapter.PacienteViewHolder> {

    private final List<PacienteDTO> pacientes;
    private OnPacienteClickListener listener;

    public interface OnPacienteClickListener { void onPacienteClick(int idPaciente); }

    public PacienteListAdapter(List<PacienteDTO> pacientes, OnPacienteClickListener listener) {
        this.pacientes = pacientes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PacienteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_paciente, parent, false);
        return new PacienteViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PacienteViewHolder h, int position) {
        PacienteDTO p = pacientes.get(position);

        h.nomePaciente.setText(
                (p.getNome()!=null && !p.getNome().isEmpty()) ? p.getNome() : "Paciente"
        );

        h.cpfPaciente.setText(cpfFormat(p.getCpf())); // já trata null/tamanho

        // Data nascimento (LocalDate -> dd/MM/yyyy)
        String dn = p.getDataNascimento();
        if (dn != null) {
            DateTimeFormatter F = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                F = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                h.dataNascimentoPaciente.setText("Nasc.: " + LocalDate.parse(dn).format(F));
            }
        } else {
            h.dataNascimentoPaciente.setText("Nasc.: Não informado");
        }

        h.idadePaciente.setText(
                p.getIdade() != null ? p.getIdade() + " anos" : "—"
        );

        h.itemView.setOnClickListener(v -> listener.onPacienteClick(p.getId()));
    }

    @Override
    public int getItemCount() {
        return pacientes.size();
    }

    public String cpfFormat(String cpf) {
        if (cpf == null || cpf.length() != 11) return "Não informado";
        return "***." + cpf.substring(3,6) + "." + cpf.substring(6,9) + "-**";
    }

    static class PacienteViewHolder extends RecyclerView.ViewHolder {
        TextView nomePaciente, cpfPaciente, dataNascimentoPaciente, idadePaciente;

        public PacienteViewHolder(@NonNull View itemView) {
            super(itemView);
            nomePaciente = itemView.findViewById(R.id.nomePaciente);
            cpfPaciente = itemView.findViewById(R.id.cpfPaciente);
            dataNascimentoPaciente = itemView.findViewById(R.id.dataNascimentoPaciente);
            idadePaciente = itemView.findViewById(R.id.idadePaciente);
        }
    }
}
