package com.example.mensura.ui.pacientes;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mensura.data.model.PacienteDTO;
import com.example.myapplication.R;
import com.google.android.material.button.MaterialButton;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull PacienteViewHolder holder, int position) {
        PacienteDTO paciente = pacientes.get(position);

        holder.nomePaciente.setText(
                (paciente.getNome()!=null && !paciente.getNome().isEmpty()) ? paciente.getNome() : "Paciente"
        );

        holder.cpfPaciente.setText(cpfFormat(paciente.getCpf()));

        LocalDate dn = paciente.getDataNascimento();
        if (dn != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                holder.dataNascimentoPaciente.setText("Data nascimento: " + dn.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            }
        }

        holder.idadePaciente.setText(
            paciente.getIdade() != null ?
                String.valueOf(paciente.getIdade()) + " anos" : "Idade não informada"
        );

        holder.itemView.setOnClickListener(v -> listener.onPacienteClick(paciente.getId()));

        holder.quantidadeMensuracoes.setText(
                paciente.getQuantidadeMensuracoes() != null ?
                        String.valueOf(paciente.getQuantidadeMensuracoes()) + " medições" : "0 medições"
        );
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
        TextView nomePaciente, cpfPaciente, dataNascimentoPaciente, idadePaciente, quantidadeMensuracoes;

        public PacienteViewHolder(@NonNull View itemView) {
            super(itemView);
            nomePaciente = itemView.findViewById(R.id.tvNomePaciente);
            cpfPaciente = itemView.findViewById(R.id.tvCpfPasciente);
            dataNascimentoPaciente = itemView.findViewById(R.id.tvDataNascimento);
            idadePaciente = itemView.findViewById(R.id.tvIdadePaciente);
            quantidadeMensuracoes = itemView.findViewById(R.id.tvQtdMedicoes);
        }
    }
}
