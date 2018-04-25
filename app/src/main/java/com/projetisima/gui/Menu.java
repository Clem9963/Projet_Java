package com.projetisima.gui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.projetisima.scores.*;
import com.projetisima.game_system.*;
import com.projetisima.R;

public class Menu extends AppCompatActivity {
    Button deconnexion = null;
    LinearLayout scoreLocal = null;
    LinearLayout scoreMondial = null;
    Button play = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        deconnexion = findViewById(R.id.deconnexion);
        scoreLocal = findViewById(R.id.scoreLocal);
        scoreMondial = findViewById(R.id.scoreMondial);
        play =  findViewById(R.id.play);

        play.setOnClickListener(playListener);
        scoreLocal.setOnClickListener(scoreListener);
        deconnexion.setOnClickListener(deconnexionListner);
        scoreMondial.setOnClickListener(scoreMondialListener);
    }

    // pour le bouton jouer
    private View.OnClickListener playListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Menu.this, MainActivity.class);
            startActivity(intent);
        }
    };

    // pour le bouton high score
    private View.OnClickListener scoreListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Menu.this, ScoreLocauxActivity.class);
            startActivity(intent);
        }
    };

    //deconnexion
    private View.OnClickListener deconnexionListner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //suppression des preferences
            SharedPreferences membres = getSharedPreferences("membres", 0);
            membres.edit().clear().commit();

            //suppression des scores locaux
            ScoreLocalManager m = new ScoreLocalManager(Menu.this); // gestionnaire de la table "animal"
            m.open(); // ouverture de la table en lecture/écriture
            m.clearAllScores();
            m.close();

            //retour a la page de connexion
            Intent intent = new Intent(Menu.this, Connexion.class);
            Menu.this.finish();
            startActivity(intent);
        }
    };

    //acces aux score du monde entier
    private View.OnClickListener scoreMondialListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Menu.this, ScoreMondiauxActivity.class);
            startActivity(intent);
        }
    };
}
