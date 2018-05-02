package com.projetisima.scores;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.projetisima.R;

import java.util.List;

class AdapterScoreMondiaux extends RecyclerView.Adapter<AdapterScoreMondiaux.MyViewHolder> {

    private List<ScoreMondial> listScoreMondial;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView date;
        public TextView score;
        public TextView pseudo;
        public TextView rank;

        public MyViewHolder(View view) {
            super(view);
            score = view.findViewById(R.id.score);
            pseudo = view.findViewById(R.id.pseudo);
            rank = view.findViewById(R.id.rank);
            date = view.findViewById(R.id.date);
        }
    }

    public AdapterScoreMondiaux(List<ScoreMondial> listScoreMondiaux) {
        this.listScoreMondial = listScoreMondiaux;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.line_score_mondiaux, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.rank.setText(listScoreMondial.get(position).getId() + "");
        holder.score.setText(listScoreMondial.get(position).getScore() + "");
        holder.date.setText(listScoreMondial.get(position).getDate());
        holder.pseudo.setText(listScoreMondial.get(position).getPseudo());
    }

    @Override
    public int getItemCount() {
        return listScoreMondial.size();
    }
}
