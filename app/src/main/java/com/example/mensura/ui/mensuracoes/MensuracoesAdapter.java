package com.example.mensura.ui.mensuracoes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mensura.data.model.MensuracaoDTO;
import com.example.myapplication.R;

import java.util.List;

public class MensuracoesAdapter extends RecyclerView.Adapter<MensuracoesAdapter.MensuracaoViewHolder> {

    private final List<MensuracaoDTO> mensuracoes;
    private final OnAnaliseClickListener listener;

    public interface OnAnaliseClickListener {
        void onAnaliseClick(int idMensuracao);
    }

    public MensuracoesAdapter(List<MensuracaoDTO> mensuracoes, OnAnaliseClickListener listener) {
        this.mensuracoes = mensuracoes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MensuracaoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_mensuracao, parent, false);
        return new MensuracaoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MensuracaoViewHolder holder, int position) {
        MensuracaoDTO m = mensuracoes.get(position);
        holder.tvPaciente.setText(m.getPacienteMensuracaoDTO().getNome());
        holder.tvDescricao.setText(m.getArticulacao() + " - " + m.getLado() + " (" + m.getMovimento() + ")");
        holder.tvPosicao.setText("Posição: " + m.getPosicao());

        holder.btnAnalise.setOnClickListener(v -> listener.onAnaliseClick(m.getId()));
    }

    @Override
    public int getItemCount() {
        return mensuracoes.size();
    }

    static class MensuracaoViewHolder extends RecyclerView.ViewHolder {
        TextView tvPaciente, tvDescricao, tvPosicao;
        Button btnAnalise;

        MensuracaoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPaciente = itemView.findViewById(R.id.tvPaciente);
            tvDescricao = itemView.findViewById(R.id.tvDescricao);
            tvPosicao = itemView.findViewById(R.id.tvPosicao);
            btnAnalise = itemView.findViewById(R.id.btnAnalise);
        }
    }
}
