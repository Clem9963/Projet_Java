package com.projetisima;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

	private SensorManager mSensorManager; // pour recuperer les changements de l'acceleromètre

	float x, y; // les variables pour l'accéléromètre

	Timer t;
	TimerTask task;
	int secondes = 0;
	int secondesTotal = 0;

	private GameView gameView;
	private Ball player;
	private Score score;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

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

			//si la bille est au bord de l'ecran alors le joueur a perdu
			if(player.outScreen() || gameView.collision())
			{
				//on arrete le temps
				t.cancel();
				task.cancel();
				mSensorManager.unregisterListener(mSensorListener);//on bloque la reception de l'acceleromètre
				gameView.setRunningGameLoop(true); //stoppe le thread = empeche les elements de se déplacer
				dialogBox(); //on affiche la boite de dialogue
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
						score.setScore(secondesTotal); // affiche le temps de jeu

						//si l'activité est au premier plan
						if(MainActivity.this.getWindow().getDecorView().getRootView().isShown()) {

                            //l'augmentation de la difficulté ce fait dans le for
						    for(int i = 0; i < secondesTotal; i = i + 5)
                            {
                                gameView.addRocket();
                            }

							if (secondes == 5) {
								Log.d("test", "lancement fusée");
								gameView.addRocket();
								t.cancel();
								task.cancel();
								secondes = 0;
								startTimer();
							} else {
								Log.d("test", "t = " + secondes);
								secondes++;
								secondesTotal++;
							}
						}
						//sinon on arrete le temps de jeu
						else
						{
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
				secondesTotal = 0;
				score.setScore(0);
				startTimer();
			}
		});

        //si c'est le meilleur temps du joueur on affiche le troisième choix
        SharedPreferences preferences = getSharedPreferences("parametres", 0);
        int scoreJoueur = preferences.getInt("score", 0);
        Log.d("score : ", "scoreNow  :" + score.getScore() + " scoreJoueur :  " + scoreJoueur);
        if(score.getScore() > scoreJoueur)
        {
            builder.setNeutralButton("Enregistrer le score", new DialogInterface.OnClickListener() { // définition de la callback pour la réponse Oui
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    envoiScoreBDD();
                    MainActivity.this.finish();
                }
            });
        }
		builder.show();
	}

	private void envoiScoreBDD(){
        SharedPreferences preferences = getSharedPreferences("parametres", 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("score", score.getScore());
        editor.commit();
        Toast.makeText(getApplicationContext(), "L'envoi de votre score a réussi", Toast.LENGTH_LONG).show();
    }
}