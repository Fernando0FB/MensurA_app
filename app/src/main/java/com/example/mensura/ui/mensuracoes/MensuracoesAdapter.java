package com.example.mensura.ui.mensuracoes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mensura.data.model.MensuracaoDTO;
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
        MensuracaoDTO m = mensuracoes.get(position);

        String nomePac = (m.getPacienteMensuracaoDTO() != null && m.getPacienteMensuracaoDTO().getNome() != null)
                ? m.getPacienteMensuracaoDTO().getNome() : "Paciente";
        h.tvPaciente.setText(nomePac);

        String articulacao = safe(m.getArticulacao());
        String movimento   = safe(m.getMovimento());
        h.tvArticulacaoMov.setText(articulacao.isEmpty() && movimento.isEmpty()
                ? "—"
                : String.format("%s%s%s",
                articulacao,
                (!articulacao.isEmpty() && !movimento.isEmpty()) ? " (" : "",
                movimento + (!movimento.isEmpty() ? ")" : "")
        ).replace("()", ""));

        String lado = safe(m.getLado());
        String ladoAbrev = abreviaLado(lado);
        h.chipLado.setText(ladoAbrev.isEmpty() ? (lado.isEmpty() ? "—" : lado) : ladoAbrev);

        String pos = safe(m.getPosicao());
        h.chipPosicao.setText(pos.isEmpty() ? "Posição: —" : "Posição: " + pos);

        int reps = (m.getRepeticoes() == null) ? 0 : m.getRepeticoes().size();
        h.tvMeta.setText(" • Repetições: " + reps);


        h.btnAnalise.setOnClickListener(v -> listener.onAnaliseClick(m.getId()));
    }

    @Override
    public int getItemCount() { return mensuracoes.size(); }

    static class MensuracaoViewHolder extends RecyclerView.ViewHolder {
        TextView tvPaciente, tvArticulacaoMov, tvMeta;
        com.google.android.material.chip.Chip chipPosicao, chipLado;
        com.google.android.material.button.MaterialButton btnAnalise;

        MensuracaoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPaciente      = itemView.findViewById(R.id.tvPaciente);
            tvArticulacaoMov= itemView.findViewById(R.id.tvArticulacaoMov);
            tvMeta          = itemView.findViewById(R.id.tvMeta);
            chipPosicao     = itemView.findViewById(R.id.chipPosicao);
            chipLado        = itemView.findViewById(R.id.chipLado);
            btnAnalise      = itemView.findViewById(R.id.btnAnalise);
        }
    }

    private static String safe(String s){ return s == null ? "" : s.trim(); }

    private static String abreviaLado(String lado) {
        if (lado == null) return "";
        String l = lado.trim().toLowerCase();
        if (l.startsWith("esq")) return "E";
        if (l.startsWith("dir")) return "D";
        if (l.equals("esquerdo")) return "E";
        if (l.equals("direito"))  return "D";
        return "";
    }
}
