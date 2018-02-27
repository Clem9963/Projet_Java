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

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
	private ArrayList<Integer> startPossibleX = new ArrayList<>();
	private ArrayList<Integer> startPossibleY = new ArrayList<>();

	private int widthScreen;
	private int heightScreen;

	private GameLoop gameLoopThread;
	private Ball balle;
	private ArrayList<Rocket> rockets; //tableau des fusées
	private Time time; // pour la gestion du temps

	private final static int widthRocket = 30;
	private final static int heightRocket = 40;

	// création de la surface de dessin
	public GameView(Context context, int width, int height) {
		super(context);
		getHolder().addCallback(this);

		gameLoopThread = new GameLoop(this);

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

		this.time = new Time();
	}

	//recupere la balle pour pouvoir ensuite changer ses coordonnées et ainsi la déplacer
	public Ball getBall(){
		return this.balle;
	}

	//fonction qui permet de bloquer le thread ou de le redémarrer
	public void setRunningGameLoop(boolean b){
		if(b == true) {
			this.gameLoopThread = new GameLoop(this);
		}
		this.gameLoopThread.setRunning(b);
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
	//todo ameliorer la gestion de collision ??
	public boolean collision(){
		// for(int i = 0; i < rockets.size(); i++)
		// {
		// 	int circleDistanceX = Math.abs(balle.getX() - rockets.get(i).getX() - rockets.get(i).getWidthRocket()/2);
		// 	int circleDistanceY = Math.abs(balle.getY() - rockets.get(i).getY() - rockets.get(i).getHeightRocket()/2);

		// 	if (circleDistanceX > (rockets.get(i).getHeightRocket()/2 + balle.getRadiusBall())) { return false; }
		// 	if (circleDistanceY > (rockets.get(i).getHeightRocket()/2 + balle.getRadiusBall())) { return false; }

		// 	if (circleDistanceX <= (rockets.get(i).getWidthRocket()/2)) { return true; }
		// 	if (circleDistanceY <= (rockets.get(i).getHeightRocket()/2)) { return true; }

		// 	int cornerDistance_sq = (circleDistanceX - rockets.get(i).getHeightRocket()/2)^2 +
		// 			(circleDistanceY - rockets.get(i).getHeightRocket()/2)^2;

		// 	return (cornerDistance_sq <= (Math.pow(balle.getRadiusBall(), 2)));
		// }
		// return false;
		int topEdgeBallX = balle.getX() + balle.getRadiusBall();
		int topEdgeBallY = balle.getY();

		int bottomEdgeBallX = balle.getX() + balle.getRadiusBall();
		int bottomEdgeBallY = balle.getY() + balle.getHeight();

		int leftEdgeBallX = balle.getX();
		int leftEdgeBallY = balle.getY() + balle.getRadiusBall();

		int rightEdgeBallX = balle.getX() + balle.getWidth();
		int rightEdgeBallY = balle.getY() + balle.getRadiusBall();

		for (int i = 0; i < rockets.size(); i++) {
			int topLeftCornerRocketX = rockets.get(i).getX();
			int topLeftCornerRocketY = rockets.get(i).getY();

			int bottomLeftCornerRocketX = rockets.get(i).getX();
			int bottomLeftCornerRocketY = rockets.get(i).getY() + rockets.get(i).getHeightRocket();

			int topRightCornerRocketX = rockets.get(i).getX() + rockets.get(i).getWidthRocket();
			int topRightCornerRocketY = rockets.get(i).getY();

			int bottomRightCornerRocketX = rockets.get(i).getX() + rockets.get(i).getWidthRocket();
			int bottomRightCornerRocketY = rockets.get(i).getY() + rockets.get(i).getHeightRocket();

			if (rightEdgeBallX > topLeftCornerRocketX && rightEdgeBallY > topLeftCornerRocketY && rightEdgeBallY < bottomLeftCornerRocketY) {
				return true;
			}
			else if (leftEdgeBallX < topRightCornerRocketX && leftEdgeBallY > topRightCornerRocketY && rightEdgeBallY < bottomRightCornerRocketY) {
				return true;
			}
			else if (topEdgeBallY < bottomRightCornerRocketX && topEdgeBallX > bottomRightCornerRocketX && topEdgeBallX < bottomLeftCornerRocketX) {
				return true;
			}
			else if (bottomEdgeBallY > topRightCornerRocketX && bottomEdgeBallY > topLeftCornerRocketX && bottomEdgeBallY < topRightCornerRocketX) {
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
		canvas.drawText("Time : " + this.time.getTime(), 20, 50, paint);
	}

	public Time getTime(){
		return this.time;
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
		if(gameLoopThread.getState()==Thread.State.TERMINATED) {
			gameLoopThread = new GameLoop(this);
		}
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