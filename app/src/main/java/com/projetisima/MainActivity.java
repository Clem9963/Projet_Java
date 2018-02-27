package com.projetisima;

import android.app.Activity;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

	private SensorManager mSensorManager; // pour recuperer les changements de l'acceleromètre

	int x, y; // les variables pour l'accéléromètre

	Timer t;
	TimerTask task;
	int secondes = 0;
	int secondesTotal = 0;

	private GameView gameView;
	private Ball player;
	private Time time;

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
		time = gameView.getTime();

		// afichage du gameView
		setContentView(gameView);

		//recupère l'accéléromètre
		mSensorManager = (SensorManager)getSystemService(this.SENSOR_SERVICE);
		mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_GAME); // changer sensor_delay_game suivant la vitesse que l'ont veut

		//lancement du timer
		startTimer();
	}

	//boucle qui détecte les changements de l'accéléromètre
	private final SensorEventListener mSensorListener = new SensorEventListener() {

		public void onSensorChanged(SensorEvent se)
		{
			x = (int)se.values[0];
			y = (int)se.values[1];

			//si la bille est au bord de l'ecran alors le joueur a perdu
			if(player.outScreen() || gameView.collision())
			{
				//on arrete le temps
				t.cancel();
				task.cancel();
				mSensorManager.unregisterListener(mSensorListener);//on bloque la reception de l'acceleromètre
				gameView.setRunningGameLoop(false); //stoppe le thread = empeche les elements de se déplacer
				dialogBox(); //on affiche la boite de dialogue
			}
			//sinon la bille se déplace
			else {
				//déplacement de la bille en largeur
				if (x != 0) {
					player.moveOnX(-1 * x);
				}
				//déplacement de la bille en hauteur
				if (y != 0) {
					player.moveOnY(y);
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
						time.setTime(secondesTotal); // affiche le temps de jeu

						if(secondes == 5)
						{
							Log.d("test", "lancement fusée");
							gameView.addRocket();
							t.cancel();
							task.cancel();
							secondes = 0;
							startTimer();
						}
						else
						{
							Log.d("test", "t = " + secondes);
							secondes++;
							secondesTotal++;
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
		builder.setPositiveButton("Quitter", new DialogInterface.OnClickListener() { // définition de la callback pour la réponse Oui
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
				gameView.setRunningGameLoop(true); //on redemarre le thread

				//met les temps à 0
				secondes = 0;
				secondesTotal = 0;
				time.setTime(0);
				startTimer();

				player.placeMiddle(); //replace la balle au centre
			}
		});
		builder.show();
	}
}