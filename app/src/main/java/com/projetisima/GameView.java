package com.projetisima;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Random;
import java.lang.Math;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
	private ArrayList<Integer> startPossibleX = new ArrayList<>();
	private ArrayList<Integer> startPossibleY = new ArrayList<>();

	private int widthScreen;
	private int heightScreen;

	private GameLoop gameLoopThread;
	private Ball balle;
	private ArrayList<Rocket> rockets; //tableau des fusées
	private Score score; // pour la gestion du temps

	private final static int widthRocket = 30;
	private final static int heightRocket = 40;

	public static double distance(int aX, int aY, int bX, int bY) {
		return Math.sqrt(Math.pow((double)(aX-bX), 2.) + Math.pow((double)(aY-bY), 2.));
	}

	// création de la surface de dessin
	public GameView(Context context, int width, int height) {
		super(context);
		getHolder().addCallback(this);

		balle = new Ball(this.getContext());
		rockets = new ArrayList();
		this.widthScreen = width;
		this.heightScreen = height;

		//ajout des coordonnées possibles pour les fusées en x et en y suivant la taille de l'écran
		for(int i = 0; i < width; i = i + this.widthRocket){
			this.startPossibleX.add(i);
		}

		for(int i = 0; i < height; i = i + this.heightRocket){
			this.startPossibleY.add(i);
		}

		this.score = new Score();
	}

	//recupere la balle pour pouvoir ensuite changer ses coordonnées et ainsi la déplacer
	public Ball getBall(){
		return this.balle;
	}

	//fonction qui permet de stopper le thread
	public void setRunningGameLoop(boolean b){
		if(b == false) {
			this.gameLoopThread.interrupt();
		}
		else
		{
			this.gameLoopThread.setRunning(true);
		}
	}

	//ajoute une fusée a la liste des fusées
	public void addRocket(){
		//gestion de la direction aléatoire
		Random r = new Random();
		Directions d = Directions.values()[r.nextInt(4)];
		int x = 0, y = 0;

		//recuperation des coordonnées de départ suivant la direction
		switch (d){
			case RIGHT:
				x = 0;
				y = startPossibleY.get(r.nextInt(startPossibleY.size()));
				break;
			case BOTTOM:
				x = startPossibleX.get(r.nextInt(startPossibleX.size()));
				y = 0;
				break;
			case LEFT:
				x = widthScreen;
				y = startPossibleY.get(r.nextInt(startPossibleY.size()));
				break;
			case TOP:
				x = startPossibleX.get(r.nextInt(startPossibleX.size()));
				y = heightScreen;
				break;
		}

		Log.d("rocket param", "direction : " + d + " x = " + x + ", y : " + y);
		this.rockets.add(new Rocket(this.getContext(), d, x, y, widthScreen, heightScreen));
	}

	//verifie si la bille a touché une fusée
	public boolean collision() {
		int topEdgeBallX = balle.getX() + balle.getRadiusBall();
		int topEdgeBallY = balle.getY();

		int bottomEdgeBallX = balle.getX() + balle.getRadiusBall();
		int bottomEdgeBallY = balle.getY() + balle.getHeight();

		int leftEdgeBallX = balle.getX();
		int leftEdgeBallY = balle.getY() + balle.getRadiusBall();

		int rightEdgeBallX = balle.getX() + balle.getWidth();
		int rightEdgeBallY = balle.getY() + balle.getRadiusBall();

		int centerBallX = balle.getX() + balle.getRadiusBall();
		int centerBallY = balle.getY() + balle.getRadiusBall();

		for (int i = 0; i < rockets.size(); i++) {
			int topLeftCornerRocketX = rockets.get(i).getX();
			int topLeftCornerRocketY = rockets.get(i).getY();

			int bottomLeftCornerRocketX = 0;
			int bottomLeftCornerRocketY = 0;

			int topRightCornerRocketX = 0;
			int topRightCornerRocketY = 0;

			int bottomRightCornerRocketX = 0;
			int bottomRightCornerRocketY = 0;

			if (rockets.get(i).getDirection() == Directions.TOP || rockets.get(i).getDirection() == Directions.BOTTOM) {
				bottomLeftCornerRocketX = rockets.get(i).getX();
				bottomLeftCornerRocketY = rockets.get(i).getY() + rockets.get(i).getHeightRocket();

				topRightCornerRocketX = rockets.get(i).getX() + rockets.get(i).getWidthRocket();
				topRightCornerRocketY = rockets.get(i).getY();

				bottomRightCornerRocketX = rockets.get(i).getX() + rockets.get(i).getWidthRocket();
				bottomRightCornerRocketY = rockets.get(i).getY() + rockets.get(i).getHeightRocket();
			}
			else {
				bottomLeftCornerRocketX = rockets.get(i).getX();
				bottomLeftCornerRocketY = rockets.get(i).getY() + rockets.get(i).getWidthRocket();

				topRightCornerRocketX = rockets.get(i).getX() + rockets.get(i).getHeightRocket();
				topRightCornerRocketY = rockets.get(i).getY();

				bottomRightCornerRocketX = rockets.get(i).getX() + rockets.get(i).getHeightRocket();
				bottomRightCornerRocketY = rockets.get(i).getY() + rockets.get(i).getWidthRocket();
			}

			/* Traitement de la collision de la balle avec les bords de la bounding box de la fusée */

			if (rightEdgeBallX > topLeftCornerRocketX && leftEdgeBallX < topLeftCornerRocketX && rightEdgeBallY > topLeftCornerRocketY && rightEdgeBallY < bottomLeftCornerRocketY) {
				Log.d("collision", "bord droit balle");
				return true;
			}
			else if (leftEdgeBallX < topRightCornerRocketX && rightEdgeBallX > topRightCornerRocketX && leftEdgeBallY > topRightCornerRocketY && rightEdgeBallY < bottomRightCornerRocketY) {
				Log.d("collision", "bord gauche balle");
				return true;
			}
			else if (topEdgeBallY < bottomRightCornerRocketY && bottomEdgeBallY > bottomRightCornerRocketY && topEdgeBallX > bottomLeftCornerRocketX && topEdgeBallX < bottomRightCornerRocketX) {
				Log.d("collision", "bord haut balle");
				return true;
			}
			else if (bottomEdgeBallY > topRightCornerRocketY && topEdgeBallY < topRightCornerRocketY && bottomEdgeBallX > topLeftCornerRocketX && bottomEdgeBallX < topRightCornerRocketX) {
				Log.d("collision", "bord bas balle");
				return true;
			}

			/* Traitement de la collision de la balle avec les coins de la bounding box de la fusée */
			if (distance(centerBallX, centerBallY, bottomLeftCornerRocketX, bottomLeftCornerRocketY) < balle.getRadiusBall()) {
				Log.d("collision", "coin bas gauche bounding box fusée");
				return true;
			}
			else if (distance(centerBallX, centerBallY, bottomRightCornerRocketX, bottomRightCornerRocketY) < balle.getRadiusBall()) {
				Log.d("collision", "coin bas droit bounding box fusée");
				return true;
			}
			if (distance(centerBallX, centerBallY, topLeftCornerRocketX, topLeftCornerRocketY) < balle.getRadiusBall()) {
				Log.d("collision", "coin haut gauche bounding box fusée");
				return true;
			}
			else if (distance(centerBallX, centerBallY, topRightCornerRocketX, topRightCornerRocketY) < balle.getRadiusBall()) {
				Log.d("collision", "coin haut droit bounding box fusée");
				return true;
			}
		}
		return false;
	}

	//dessine les elements
	public void doDraw(Canvas canvas) {
		if(canvas == null) {
			return;
		}

		// on efface l'écran, en blanc
		canvas.drawColor(Color.WHITE);

		// on dessine la balle
		balle.draw(canvas);

		//on dessine les fusées
		for(int i = 0; i < rockets.size(); i++){
			rockets.get(i).draw(canvas);
		}

		//on affiche le timer
		Paint paint = new Paint();
		paint.setColor(Color.BLACK);
		paint.setTextSize(60);
		canvas.drawText("Score : " + this.score.getScore(), 20, 50, paint);
	}

	public Score getScore(){
		return this.score;
	}

	// Fonction appelée par la gameLoopThread pour gerer les fusees
	public void update() {
		//on deplace les fusées si elles sont toujours dans l'ecran
		for(int i = 0; i < rockets.size(); i++) {
			if(rockets.get(i).outScreen()){
				rockets.remove(i);
				Log.d("fusee draw", "la fusee supprime");
			}
			else{
				rockets.get(i).move();
			}
		}
	}

	// Fonction obligatoire de l'objet SurfaceView qui va lancer le thread du jeu
	@Override
	public void surfaceCreated(SurfaceHolder surfaceHolder) {
		// création du processus GameLoopThread si cela n'est pas fait
		gameLoopThread = new GameLoop(this);
		gameLoopThread.setRunning(true);
		gameLoopThread.start();
	}

	// Fonction obligatoire de l'objet SurfaceView qui va détruire le thread du jeu
	@Override
	public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
		boolean retry = true;
		gameLoopThread.setRunning(false);
		while (retry) {
			try {
				gameLoopThread.join();
				retry = false;
			}
			catch (InterruptedException e) {}
		}
	}

	//definit la taille de la bille, des fusées et les dimensions de l'écran
	@Override
	public void surfaceChanged(SurfaceHolder surfaceHolder, int indice, int width, int height) {
		balle.resize(width, height); // on définit la taille de la balle selon la taille de l'écran
		for(int i = 0; i < rockets.size(); i++){
			rockets.get(i).resize();
		}
	}
}