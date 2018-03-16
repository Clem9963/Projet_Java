package com.projetisima;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

class AdapterScoreLocaux extends RecyclerView.Adapter<AdapterScoreLocaux.MyViewHolder> {

    private List<ScoreLocal> listScoreLocaux;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView date;
        public TextView score;
        public TextView rank;

        public MyViewHolder(View view) {
            super(view);
            score = view.findViewById(R.id.score);
            rank = view.findViewById(R.id.rank);
            date = view.findViewById(R.id.date);
        }
    }

    public AdapterScoreLocaux(List<ScoreLocal> listScoreLocaux) {
        this.listScoreLocaux = listScoreLocaux;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.line_score_locaux, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.rank.setText(listScoreLocaux.get(position).getId() + "");
        holder.score.setText(listScoreLocaux.get(position).getScore() + "");

        Date d = new Date(listScoreLocaux.get(position).getDate());
        String date = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).format(d);
        holder.date.setText(date);
    }

    @Override
    public int getItemCount() {
        return listScoreLocaux.size();
    }
}
