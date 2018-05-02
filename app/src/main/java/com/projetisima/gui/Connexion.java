package com.projetisima.gui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.projetisima.BDD;
import com.projetisima.R;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Connexion extends AppCompatActivity {
    EditText pseudo = null;
    EditText mdp = null;
    Button inscription = null;
    Button connexion = null;

    SharedPreferences preferences;
    String pseudoMembre;
    String mdpMembre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connexion);

        pseudo = findViewById(R.id.pseudo);
        mdp = findViewById(R.id.mdp);
        inscription =  findViewById(R.id.inscription);
        connexion = findViewById(R.id.connexion);

        inscription.setOnClickListener(inscriptionListener);
        connexion.setOnClickListener(connexionListener);

        //verifie les shared preferences
        //si ell sont deja rentrée et juste on va sur menu sinon on affiche la page connexion
        preferences = getSharedPreferences("membres", 0);
        pseudoMembre = preferences.getString("pseudo", "");
        mdpMembre = preferences.getString("mdp", "");

        if(!pseudoMembre.equals("") && !mdpMembre.equals(""))
        {
            Intent intent = new Intent(Connexion.this, Menu.class);
            Connexion.this.finish();
            startActivity(intent);
        }
    }

    private View.OnClickListener inscriptionListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Connexion.this, Inscription.class);
            Connexion.this.finish();
            startActivity(intent);
        }
    };
    private View.OnClickListener connexionListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String p = pseudo.getText().toString();
            String m = mdp.getText().toString();

            // On verifie le pseudo
            if (p.equals("")) {
                Toast.makeText(getApplicationContext(), "Veuillez rentrer un pseudo" , Toast.LENGTH_LONG).show();
            }
            // On verifie le mdp
            else if (m.equals("")) {
                Toast.makeText(getApplicationContext(), "Veuillez rentrer un mot de passe" , Toast.LENGTH_LONG).show();
            }
            else if (!BDD.isConnectedInternet(Connexion.this)) {
                Toast.makeText(getApplicationContext(), "Veuillez activer votre connexion Internet" , Toast.LENGTH_LONG).show();
            }
            else {
                RetrofitConnexion(p, m);
            }
        }
    };

    private void RetrofitConnexion(String pseudo, String mdp) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BDD.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mdp = hash256(mdp);

        BDD.script service = retrofit.create(BDD.script.class);
        service.connexion("connexion", pseudo, mdp).enqueue(new Callback<List<BDD>>() {
            @Override
            public void onResponse(Call<List<BDD>> call, Response<List<BDD>> response) {
                if (response.body().get(0).getErreur().equals("existePas")) {
                    Toast.makeText(getApplicationContext(), "Le pseudo ou le mot de passe est incorrect", Toast.LENGTH_LONG).show();
                }
                else {

                    //ecrit pseudo et mdp dans preference pour eviter de rerentrer cela a la connexion
                    SharedPreferences preferences = getSharedPreferences("membres", 0);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("pseudo", response.body().get(0).getPseudo());
                    editor.putString("mdp", response.body().get(0).getMot_de_passe());
                    editor.commit();
                    Intent intent = new Intent(Connexion.this, Menu.class);
                    //suppression de l'activité
                    Connexion.this.finish();
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











