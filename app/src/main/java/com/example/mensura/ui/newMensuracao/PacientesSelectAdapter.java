package com.example.mensura.ui.newMensuracao;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mensura.data.model.PacienteDTO;
import com.example.mensura.util.Formatters.CpfFormat;
import com.example.myapplication.R;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PacientesSelectAdapter extends RecyclerView.Adapter<PacientesSelectAdapter.PacienteViewHolder> {
    private final List<PacienteDTO> pacientes;

    private OnPacienteClickListener listener;

    public interface OnPacienteClickListener { void onPacienteClick(PacienteDTO pacienteDTO); }

    public PacientesSelectAdapter(List<PacienteDTO> pacientes, OnPacienteClickListener listener) {
        this.pacientes = pacientes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PacientesSelectAdapter.PacienteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_paciente, parent, false);
        return new PacientesSelectAdapter.PacienteViewHolder(v);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull PacientesSelectAdapter.PacienteViewHolder holder, int position) {
        PacienteDTO paciente = pacientes.get(position);

        holder.nomePaciente.setText(
                (paciente.getNome()!=null && !paciente.getNome().isEmpty()) ? paciente.getNome() : "Paciente"
        );

        holder.cpfPaciente.setText(CpfFormat.mask(paciente.getCpf()));

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

        holder.layoutItemPaciente.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPacienteClick(paciente);
            }
        });
    }

    @Override
    public int getItemCount() {
        return pacientes.size();
    }

    static class PacienteViewHolder extends RecyclerView.ViewHolder {
        TextView nomePaciente, cpfPaciente, dataNascimentoPaciente, idadePaciente;
        LinearLayout layoutItemPaciente;

        public PacienteViewHolder(@NonNull View itemView) {
            super(itemView);
            nomePaciente = itemView.findViewById(R.id.tvNomePaciente);
            cpfPaciente = itemView.findViewById(R.id.tvCpfPasciente);
            dataNascimentoPaciente = itemView.findViewById(R.id.tvDataNascimento);
            idadePaciente = itemView.findViewById(R.id.tvIdadePaciente);
            layoutItemPaciente = itemView.findViewById(R.id.layoutItemPaciente);
        }
    }
}
