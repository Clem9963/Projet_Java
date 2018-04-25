package com.projetisima.gui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.projetisima.BDD;
import com.projetisima.R;
import com.projetisima.game_system.MainActivity;
import com.projetisima.scores.ScoreLocauxActivity;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Inscription extends AppCompatActivity {
    EditText pseudo = null;
    EditText mdp = null;
    EditText mdp2 = null;
    Button inscription = null;
    Button connexion = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inscription);

        pseudo = findViewById(R.id.pseudo);
        mdp = findViewById(R.id.mdp);
        mdp2 = findViewById(R.id.mdp2);
        inscription =  findViewById(R.id.inscription);
        connexion = findViewById(R.id.connexion);

        inscription.setOnClickListener(inscriptionListener);
        connexion.setOnClickListener(connexionListener);
    }

    private View.OnClickListener inscriptionListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //vérifie les champs
            String p = pseudo.getText().toString();
            String m = mdp.getText().toString();
            String m2 = mdp2.getText().toString();

            // On verifie le pseudo
            if (p.equals("")) {
                Toast.makeText(getApplicationContext(), "Veuillez rentrer un pseudo" , Toast.LENGTH_LONG).show();
            }
            // On verifie le mdp
            else if (m.equals("")) {
                Toast.makeText(getApplicationContext(), "Veuillez rentrer un mot de passe" , Toast.LENGTH_LONG).show();
            }
            else if (m2.equals("")) {
                Toast.makeText(getApplicationContext(), "Veuillez rentrer le second mot de passe" , Toast.LENGTH_LONG).show();
            }
            else if(!m.equals(m2))
            {
                Toast.makeText(getApplicationContext(), "Les mots de passe sont différents" , Toast.LENGTH_LONG).show();
            }
            else if (!BDD.isConnectedInternet(Inscription.this)) {
                Toast.makeText(getApplicationContext(), "Veuillez activer votre connexion Internet" , Toast.LENGTH_LONG).show();
            }
            else {
                RetrofitInscription(p, m);
            }
        }
    };
    private View.OnClickListener connexionListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Inscription.this, Connexion.class);
            Inscription.this.finish();
            startActivity(intent);
        }
    };

    private void RetrofitInscription(String pseudo, String mdp) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BDD.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mdp = hash256(mdp);

        BDD.script service = retrofit.create(BDD.script.class);
        service.inscription("inscription", pseudo, mdp).enqueue(new Callback<List<BDD>>() {
            @Override
            public void onResponse(Call<List<BDD>> call, Response<List<BDD>> response) {
                if (response.body().get(0).getErreur().equals("pseudoExisteDeja")) {
                    Toast.makeText(getApplicationContext(), "Le pseudo existe déjà", Toast.LENGTH_LONG).show();
                } else {

                    //ecrit pseudo et mdp dans preference pour eviter de rerentrer cela a la connexion
                    SharedPreferences preferences = getSharedPreferences("membres", 0);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("pseudo", response.body().get(0).getPseudo());
                    editor.putString("mdp", response.body().get(0).getMot_de_passe());
                    editor.commit();
                    Intent intent = new Intent(Inscription.this, Menu.class);
                    //suppression de l'activité
                    Inscription.this.finish();
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<List<BDD>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Connexion au serveur impossible", Toast.LENGTH_LONG).show();
            }
        });
    }

    private static String hash256(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance("SHA-256");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++)
            {
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}















