package com.projetisima;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class ScoreLocauxActivity extends AppCompatActivity{

    //pour le recycler view
    private RecyclerView highScore;
    private List listScoreLocaux = new ArrayList<ScoreLocal>();
    private AdapterScoreLocaux mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_locaux);

        // afficher fleche de retour
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // On récupère toutes les vues dont on a besoin
        highScore = (RecyclerView) findViewById(R.id.highScore);

        //affichage de l'adapter
        mAdapter = new AdapterScoreLocaux(listScoreLocaux);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        highScore.setLayoutManager(mLayoutManager);
        highScore.setItemAnimator(new DefaultItemAnimator());
        highScore.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        // set the adapter
        highScore.setAdapter(mAdapter);

        //recupere les high score de la base de donnée
        loadHighScore();

        mAdapter.notifyDataSetChanged();
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

    private void loadHighScore(){
        ScoreLocalManager m = new ScoreLocalManager(this); // gestionnaire de la table "animal"
        m.open(); // ouverture de la table en lecture/écriture

        // Listing des enregistrements de la table
        Cursor c = m.getAllScoreLocal();
        int i = 1;
        if (c.moveToFirst())
        {
            do {
                listScoreLocaux.add(new ScoreLocal(i,
                        c.getLong(c.getColumnIndex(ScoreLocalManager.KEY_DATE)),
                        c.getInt(c.getColumnIndex(ScoreLocalManager.KEY_SCORE))));
                i++;
            }
            while (c.moveToNext());
        }
        c.close(); // fermeture du curseur

        // fermeture du gestionnaire
        m.close();
    }
}
