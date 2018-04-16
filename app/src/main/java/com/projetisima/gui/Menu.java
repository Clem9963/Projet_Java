package com.projetisima.gui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.projetisima.scores.*;
import com.projetisima.game_system.*;
import com.projetisima.R;

public class Menu extends AppCompatActivity {
    Button score = null;
    Button play = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        score =  findViewById(R.id.score);
        play =  findViewById(R.id.play);

        play.setOnClickListener(playListener);
        score.setOnClickListener(scoreListener);
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
}
