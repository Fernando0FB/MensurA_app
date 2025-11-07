package com.example.mensura.ui.mensuracoes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mensura.data.model.MensuracaoDTO;
import com.example.mensura.util.Formatters.StringUtils;
import com.example.myapplication.R;

import java.util.List;

public class MensuracoesAdapter extends RecyclerView.Adapter<MensuracoesAdapter.MensuracaoViewHolder> {

    private final List<MensuracaoDTO> mensuracoes;
    private final OnAnaliseClickListener listener;

    public interface OnAnaliseClickListener { void onAnaliseClick(int idMensuracao); }

    public MensuracoesAdapter(List<MensuracaoDTO> mensuracoes, OnAnaliseClickListener listener) {
        this.mensuracoes = mensuracoes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MensuracaoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_mensuracao, parent, false);
        return new MensuracaoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MensuracaoViewHolder h, int position) {
        MensuracaoDTO mensuracao = mensuracoes.get(position);

        String nomePac = (mensuracao.getPacienteMensuracaoDTO() != null && mensuracao.getPacienteMensuracaoDTO().getNome() != null)
                ? mensuracao.getPacienteMensuracaoDTO().getNome() : "Paciente";
        h.tvPaciente.setText(nomePac);

        h.tvArticulacaoLado.setText(StringUtils.capitalize(safe(mensuracao.getArticulacao())) + " " + StringUtils.capitalize(safe(mensuracao.getLado())));

        h.tvTipoMovimento.setText(StringUtils.capitalize(safe(mensuracao.getMovimento())));

        h.tvExcursaoMedia.setText(mensuracao.getExcursao() != null
                ? mensuracao.getExcursao().toString() + "°" : "N/A");

        h.containerItemMensuracao.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAnaliseClick(mensuracao.getId());
            }
        });
    }

    @Override
    public int getItemCount() { return mensuracoes.size(); }

    static class MensuracaoViewHolder extends RecyclerView.ViewHolder {
        TextView tvPaciente, tvArticulacaoLado, tvTipoMovimento, tvExcursaoMedia;

        RelativeLayout containerItemMensuracao;

        MensuracaoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPaciente      = itemView.findViewById(R.id.tvPaciente);
            tvArticulacaoLado = itemView.findViewById(R.id.tvArticulacaoLado);
            tvTipoMovimento = itemView.findViewById(R.id.tvTipoMovimento);
            tvExcursaoMedia = itemView.findViewById(R.id.tvExcursao);
            containerItemMensuracao = itemView.findViewById(R.id.containerItemMensuracao);
        }
    }

    private static String safe(String s){ return s == null ? "" : s.trim(); }


}
