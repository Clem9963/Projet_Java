package com.projetisima;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Menu extends AppCompatActivity {
    Button score = null;
    Button play = null;
    EditText pseudo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        score = (Button) findViewById(R.id.score);
        play = (Button) findViewById(R.id.play);
        pseudo = (EditText) findViewById(R.id.pseudo);

        //recuperation du pseudo si celui ci existe
        SharedPreferences preferences = getSharedPreferences("parametres", 0);
        String p = preferences.getString("pseudo", "erreur");

        if(!p.equals("erreur"))
        {
            pseudo.setText(p);
        }
    
        play.setOnClickListener(playListener);
        score.setOnClickListener(scoreListener);
    }

    // pour le bouton jouer
    private View.OnClickListener playListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String text = pseudo.getText().toString();

            SharedPreferences preferences = getSharedPreferences("parametres", 0);
            String p = preferences.getString("pseudo", "");

            if(!text.equals("")) {
                if (!text.equals(p)) {
                    //on sauvegarde le pseudo et met le score à 0 dans les shared preferences
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("pseudo", text);
                    editor.putInt("score", 0);
                    editor.commit();
                }
            }

            Intent intent = new Intent(Menu.this, MainActivity.class);
            startActivity(intent);
        }
    };

    // pour le bouton high score
    private View.OnClickListener scoreListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Menu.this, HighScore.class);
            startActivity(intent);
        }
    };
}
