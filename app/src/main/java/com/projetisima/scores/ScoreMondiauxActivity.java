package com.projetisima.scores;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.projetisima.BDD;
import com.projetisima.R;
import com.projetisima.gui.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ScoreMondiauxActivity extends AppCompatActivity{

    //pour le recycler view
    private RecyclerView highScore;
    private List listScoreMondiaux = new ArrayList<ScoreMondial>();
    private AdapterScoreMondiaux mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_mondiaux);

        // afficher fleche de retour
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // On récupère toutes les vues dont on a besoin
        highScore = findViewById(R.id.highScore);

        //affichage de l'adapter
        mAdapter = new AdapterScoreMondiaux(listScoreMondiaux);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        highScore.setLayoutManager(mLayoutManager);
        highScore.setItemAnimator(new DefaultItemAnimator());
        highScore.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        // set the adapter
        highScore.setAdapter(mAdapter);

        //recupere les high score de la base de donnée
        loadHighScoreRetrofit();
    }

    //pour la clique sur la flèche retour
    public boolean onOptionsItemSelected(MenuItem item){
        back();
        return true;
    }

    @Override
    public void onBackPressed() {
        back();
    }

    private void back() {
        this.finish();
    }

    private void loadHighScoreRetrofit(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BDD.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        BDD.script service = retrofit.create(BDD.script.class);
        service.getHighScore("getHighScore").enqueue(new Callback<List<BDD>>() {
            @Override
            public void onResponse(Call<List<BDD>> call, Response<List<BDD>> response) {
                if (response.isSuccessful()) {
                    int nbScore = Integer.parseInt(response.body().get(0).getNbScores());

                    for(int i = 1; i <= nbScore; i++)
                    {
                        listScoreMondiaux.add(new ScoreMondial(i,
                                response.body().get(i).getDateScore(),
                                response.body().get(i).getScore(),
                                response.body().get(i).getPseudo()));
                    }
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<BDD>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Connexion au serveur impossible", Toast.LENGTH_LONG).show();
            }
        });
    }
}
