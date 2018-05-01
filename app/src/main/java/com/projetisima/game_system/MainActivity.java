package com.projetisima.game_system;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import com.projetisima.BDD;
import com.projetisima.enemies.*;
import com.projetisima.player.*;
import com.projetisima.gui.*;
import com.projetisima.scores.*;
import com.projetisima.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

	private SensorManager mSensorManager; // pour recuperer les changements de l'acceleromètre

	float x, y; // les variables pour l'accéléromètre

    //pour la musique
    MediaPlayer musicPlayer;
    MediaPlayer soundCollision;

	Timer t;
	TimerTask task;
	int secondes = 0;

	private GameView gameView;
	private Ball player;
	private Score score;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//met le jeu en plein écran
		int pleinEcran = WindowManager.LayoutParams.FLAG_FULLSCREEN ;
		getWindow().setFlags(pleinEcran,pleinEcran);

		//lancement de la musique
        musicPlayer = MediaPlayer.create(this, R.raw.musique);
        //si le joueur arrive au bout de la musique, o la relance
        musicPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                musicPlayer.seekTo(0);
                musicPlayer.start();
            }
        });
        musicPlayer.start(); // no need to call prepare(); create() does that for you

        //chargement du bruit de collision
        soundCollision = MediaPlayer.create(this, R.raw.explosion);
        soundCollision.setVolume(40,40);
        soundCollision.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                dialogBox(); //on affiche la boite de dialogue
                player.placeMiddle();
                gameView.removeAllRockets();
            }
        });

		//recuperation de la dimension de l'ecran pour la fournir au gameView
		DisplayMetrics dimensions;
		dimensions = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dimensions);

		//création de gameview qui est le code principal du jeu et qui gère les elements
		gameView = new GameView(this, dimensions.widthPixels, dimensions.heightPixels);
		player = gameView.getBall();
		score = gameView.getScore();

		// afichage du gameView
		setContentView(gameView);

		//recupère l'accéléromètre
		mSensorManager = (SensorManager)getSystemService(this.SENSOR_SERVICE);
		mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_GAME); // changer sensor_delay_game suivant la vitesse que l'ont veut

		//lancement du timer
		startTimer();
	}

	//si on appuie sur la touche retour pendant que l'on joue
	@Override
	public void onBackPressed() {
	    if(musicPlayer.isPlaying()) {
            musicPlayer.stop();
        }
        if(soundCollision.isPlaying()){
	        soundCollision.stop();
        }
	    t.cancel();
	    task.cancel();
        MainActivity.this.finish();
	}

	//boucle qui détecte les changements de l'accéléromètre
	private final SensorEventListener mSensorListener = new SensorEventListener() {

		public void onSensorChanged(SensorEvent se)
		{
			x = se.values[0];
			y = se.values[1];

			//si la bille génère une collision, alors le joueur a perdu
			if(gameView.collision())
			{
			    //lance le bruit de collision
                soundCollision.start();

                //on stop la musique
                musicPlayer.seekTo(0);
                musicPlayer.pause();

				//on arrete le temps
				t.cancel();
				task.cancel();
				mSensorManager.unregisterListener(mSensorListener);//on bloque la reception de l'acceleromètre
				gameView.setRunningGameLoop(false); //stoppe le thread = empeche les elements de se déplacer
			}
			//sinon la bille se déplace
			else {
				//déplacement de la bille en largeur
				if (x != 0) {
					player.moveX(-1 * x);		// Le -1 permet de se faire déplacer la bille dans le bon sens
				}
				//déplacement de la bille en hauteur
				if (y != 0) {
					player.moveY(y);
				}
			}
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int i) {}
	};

	@Override
	protected void onResume()
	{
		super.onResume();
		mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
	}

	@Override
	protected void onStop()
	{
		mSensorManager.unregisterListener(mSensorListener);
		super.onStop();
	}

	//gestion du temps
	public void startTimer(){
		t = new Timer();
		task = new TimerTask() {

			@Override
			public void run() {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						score.setScore(secondes); // affiche le score

						//si l'activité est au premier plan
						if(MainActivity.this.getWindow().getDecorView().getRootView().isShown()){
						    difficultyManager();
						    secondes++;
						}
						//sinon on arrete le temps de jeu
						else
						{
						    musicPlayer.stop();
							t.cancel();
							task.cancel();
							MainActivity.this.finish();
						}
					}
				});
			}
		};
		t.scheduleAtFixedRate(task, 0, 1000);
	}

	//boite de dialogue si le joueur a touche un cote de l'ecran  ou une fusée
	public void dialogBox () {
	    //enregistrement du score dans la base de donnée locale
        saveScoreLocal();

        //enregistrement du score dans la base de donnée distante si on peut
        saveScoreServer();

		Activity activity = this; // récupération de l'Activity courante
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setMessage("Vous avez perdu !!!");
		builder.setPositiveButton("Retour au menu", new DialogInterface.OnClickListener() { // définition de la callback pour la réponse Oui
			@Override
			public void onClick(DialogInterface dialog, int which) {
				MainActivity.this.finish();
			}
		});
		builder.setNegativeButton("Recommencer", new DialogInterface.OnClickListener() { // définition de la callback pour la réponse Oui
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//relance l'aceleromètre, le thread et le temps
				mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);

                player.placeMiddle(); //replace la balle au centre
				gameView.setRunningGameLoop(true); //on redemarre le thread

				//met les temps à 0
				secondes = 0;
				score.setScore(0);
				musicPlayer.start(); //relance la musique
				startTimer();
			}
		});

		builder.show();
	}

	private void saveScoreServer(){
        ScoreLocalManager m = new ScoreLocalManager(this); // gestionnaire de la table "animal"
        m.open(); // ouverture de la table en lecture/écriture

        //recuperation du score minimum
        ScoreLocal sc = m.getScoreMax();

        //si le meilleur score est celui qu'il vient de faire
        //alors on l'envoit au serveur si il y a de la connexion
        if(sc.getScore() == score.getScore())
        {
            if(BDD.isConnectedInternet(MainActivity.this))
            {
                sendScoreRetrofit(sc.getScore(), sc.getDate()/1000);
            }
        }
        m.close();
    }

    private void sendScoreRetrofit(int score, long date) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BDD.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //recuperation des elements à envoyer
        SharedPreferences preferences = getSharedPreferences("membres", 0);
        String pseudo = preferences.getString("pseudo", "");
        String mdp = preferences.getString("mdp", "");

        BDD.script service = retrofit.create(BDD.script.class);
        service.sendScore("sendScore", pseudo, mdp, String.valueOf(score), String.valueOf(date)).enqueue(new Callback<List<BDD>>() {
            @Override
            public void onResponse(Call<List<BDD>> call, Response<List<BDD>> response) {
                if (response.body().get(0).getErreur().equals("impossible")) {
                    Toast.makeText(getApplicationContext(), "Une erreur est survenue, le score n'a pas été enregistré", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<BDD>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Connexion au serveur impossible", Toast.LENGTH_LONG).show();
            }
        });
    }

	private void saveScoreLocal(){
        ScoreLocalManager m = new ScoreLocalManager(this); // gestionnaire de la table "animal"
        m.open(); // ouverture de la table en lecture/écriture

        Long millis = System.currentTimeMillis();

        //recuperation du score minimum
        ScoreLocal sc = m.getScoreLocalMin();

        //insertion si le score est superieur au score minimum
        if(score.getScore() > sc.getScore() || m.getNbTables() < MySQLite.sizeTable){
            m.addScoreLocal(new ScoreLocal(0, millis, score.getScore()));
        }

        //suppression du score minimum si la table est remplit
        if(m.getNbTables() > MySQLite.sizeTable)
        {
            m.removeScoreLocal(sc);
        }
        // fermeture du gestionnaire
        m.close();
    }

    //fonction qui gère la difficulté suivant le temps de jeu
    //todo : surement ameliorer la gestion de la difficulte :
    //car on a beaucoup de mal a atteindre le troisième type de fusée
    //la fonction fait exactement ce qui est décrit dans la time line de la difficulté
    private void difficultyManager(){
        if(secondes < 10){
            if(secondes % 2 == 0) {
                gameView.addRocketA(Directions.RIGHT); // definit arbitrairement
            }
            return;
        }
        else if(secondes < 20)
        {
            gameView.addRocketA(Directions.RIGHT, Directions.LEFT); // definit arbitrairement
            return;
        }
        else if (secondes < 30)
        {
            gameView.addRocketA();
            return;
        }
        else if(secondes < 40)
        {
            gameView.addRocketA();
            if(secondes % 2 == 0)
            {
                gameView.addRocketA();
            }
            return;
        }
        else if (secondes < 50)
        {
            gameView.addRocketA();
            gameView.addRocketA();
            return;
        }
        else if(secondes < 60)
        {
            gameView.addRocketA();
            gameView.addRocketA();

            if(secondes % 2 == 0)
            {
                gameView.addRocketB();
            }
            return;
        }
        else if (secondes < 70)
        {
            gameView.addRocketA();
            gameView.addRocketA();
            gameView.addRocketB();
            return;
        }
        else if (secondes < 80)
        {
            gameView.addRocketA();
            gameView.addRocketA();
            gameView.addRocketB();

            if(secondes % 2 == 0)
            {
                gameView.addRocketB();
            }
            return;
        }
        else if(secondes < 90)
        {
            gameView.addRocketA();
            gameView.addRocketA();
            gameView.addRocketA();
            gameView.addRocketB();
            gameView.addRocketB();
            return;
        }
        else if(secondes < 100)
        {
            gameView.addRocketA();
            gameView.addRocketA();
            gameView.addRocketA();
            gameView.addRocketB();
            gameView.addRocketB();
            gameView.addRocketC(Directions.LEFT);  //definit arbitrairement
            return;
        }
        else if(secondes < 120)
        {
            gameView.addRocketA();
            gameView.addRocketA();
            gameView.addRocketA();
            gameView.addRocketA();
            gameView.addRocketB();
            gameView.addRocketB();
            gameView.addRocketB();
            gameView.addRocketC();
            gameView.addRocketC();
            return;
        }
        else // le joueur tient plus de 2 min
        {
            gameView.addRocketA();
            gameView.addRocketA();
            gameView.addRocketA();
            gameView.addRocketA();
            gameView.addRocketB();
            gameView.addRocketB();
            gameView.addRocketB();
            gameView.addRocketC();
            gameView.addRocketC();
            for(int i = 20; i < secondes - 120; i = i + 20){
                gameView.addRocketA();
                gameView.addRocketB();
                gameView.addRocketC();
            }
            return;
        }
    }
}
