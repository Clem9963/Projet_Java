package com.projetisima;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public class BDD {
    public static final String URL = "https://projetisima.alwaysdata.net/";
    private static final String DOSSIER_URL = "script.php";

    //tous les scripts pour se connecter a la base de donnee et envoi de donnee se trouvent ci après
    public interface script {
        //pour demander la connexion du joueur
        @FormUrlEncoded
        @POST(DOSSIER_URL)
        Call<List<BDD>> connexion(@Field("parametre") String field1,
                                  @Field("pseudo") String field2,
                                  @Field("mdp") String field3);

        //inscription d'un nouveau membre
        @FormUrlEncoded
        @POST(DOSSIER_URL)
        Call<List<BDD>> inscription(@Field("parametre") String field1,
                                    @Field("pseudo") String field2,
                                    @Field("mdp") String field3);

        //envoi le  score
        @FormUrlEncoded
        @POST(DOSSIER_URL)
        Call<List<BDD>> sendScore(@Field("parametre") String field1,
                                  @Field("pseudo") String field2,
                                  @Field("mdp") String field3,
                                  @Field("score") String field4,
                                  @Field("date") String field5);

        //récupere les meilleures score du monde entier
        @FormUrlEncoded
        @POST(DOSSIER_URL)
        Call<List<BDD>> getHighScore(@Field("parametre") String field1);

    }

    //fonction pour vérifier si la connection internet est activée sur le téléphone
    public static boolean isConnectedInternet(Activity activity)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null) {
            NetworkInfo.State networkState = networkInfo.getState();
            if (networkState.compareTo(NetworkInfo.State.CONNECTED) == 0){
                return true;
            }
            else return false;
        }
        else return false;
    }

    @SerializedName("erreur")
    @Expose
    private String erreur;
    @SerializedName("pseudo")
    @Expose
    private String pseudo;
    @SerializedName("mot_de_passe")
    @Expose
    private String mot_de_passe;
    @SerializedName("nbScores")
    @Expose
    private String nbScores;
    @SerializedName("score")
    @Expose
    private String score;
    @SerializedName("date_score")
    @Expose
    private String dateScore;

    public String getErreur() { return erreur;}

    public String getNbScores() { return nbScores;}

    public String getScore() { return score; }

    public String getDateScore() {  return dateScore;}

    public String getPseudo() { return pseudo;}

    public String getMot_de_passe() {  return mot_de_passe;}
}
