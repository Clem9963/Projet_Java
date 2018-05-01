package com.projetisima.gui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Random;
import java.lang.Math;

import com.projetisima.player.*;
import com.projetisima.enemies.*;
import com.projetisima.game_system.*;
import com.projetisima.scores.*;
import com.projetisima.R;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
	private ArrayList<Integer> startPossibleX = new ArrayList<>();
	private ArrayList<Integer> startPossibleY = new ArrayList<>();

    //image dde fond
    private BitmapDrawable background = null;

	private int widthScreen;
	private int heightScreen;

	private Context context;
	private GameLoop gameLoopThread;
	private Ball player;
	private Border border;
	private ArrayList<Rocket> rockets; //tableau des fusées
	private Score score; // pour la gestion du temps

	private final static int widthRocket = 30;
	private final static int heightRocket = 40;

    Random r = new Random();

	public static double distance(int aX, int aY, int bX, int bY) {
		return Math.sqrt(Math.pow((double)(aX-bX), 2.) + Math.pow((double)(aY-bY), 2.));
	}

	// création de la surface de dessin
	public GameView(Context context, int width, int height) {
		super(context);
		getHolder().addCallback(this);

		this.context = context;
		this.widthScreen = width;
		this.heightScreen = height;

		player = new Ball(this.getContext(), width, height);
		border = new Border(this.getContext(), widthScreen, heightScreen);
		rockets = new ArrayList();

		//ajout des coordonnées possibles pour les fusées en x et en y suivant la taille de l'écran
		for(int i = 0; i < width; i = i + this.widthRocket){
			this.startPossibleX.add(i);
		}

		for(int i = 0; i < height; i = i + this.heightRocket){
			this.startPossibleY.add(i);
		}

		this.score = new Score();

        //charge l'image de fond
        this.background = setImage(this.context, R.drawable.footballground, this.widthScreen, this.heightScreen);
	}

	//recupere la balle pour pouvoir ensuite changer ses coordonnées et ainsi la déplacer
	public Ball getBall(){
		return this.player;
	}

	//fonction qui permet de stopper le thread ou de le relancer
	public void setRunningGameLoop(boolean b){
		if(b == false) {
			this.gameLoopThread.setRunning(false);
		}
		else
		{
            gameLoopThread = new GameLoop(this);
            gameLoopThread.setRunning(true);
            gameLoopThread.start();
		}
	}

	//ajoute une fusée de type A a la liste des fusées
	public void addRocketA(){
		Directions d = Directions.values()[r.nextInt(4)];
		addRocket(d, RocketType.A);
	}

	public void addRocketA(Directions d) {
	    addRocket(d, RocketType.A);
    }

    public void addRocketA(Directions d1, Directions d2){
        if(r.nextInt(2) == 0){
            addRocket(d1, RocketType.A);
        }
        else {
            addRocket(d2, RocketType.A);
        }
    }

    private void addRocket(Directions d, RocketType typeRocket){
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

        //ajoute la fusée suivant son type
        Log.d("rocketA param", "direction : " + d + " x = " + x + ", y : " + y);
        switch (typeRocket){
            case A:
                this.rockets.add(new RocketA(this.getContext(), d, x, y, widthScreen, heightScreen));
                break;
            case B:
                this.rockets.add(new RocketB(this.getContext(), d, x, y, widthScreen, heightScreen));
                break;
            case C:
                this.rockets.add(new RocketC(this.getContext(), d, x, y, widthScreen, heightScreen));
                break;
        }
    }

	//ajoute une fusée de type B a la liste des fusées
	public void addRocketB(){
		Directions d = Directions.values()[r.nextInt(4)];
		addRocket(d, RocketType.B);
	}

	//ajoute une fusée de type C a la liste des fusées
	public void addRocketC(){
		Directions d = Directions.values()[r.nextInt(4)];
		addRocket(d, RocketType.C);
	}

	public void addRocketC(Directions d){
        addRocket(d, RocketType.C);
    }

	//verifie si la bille a touché une fusée
	public boolean collision() {
	    //verifie si la balle touche une bordure
        if(player.getX() + player.getWidth() + border.getXBorder() > this.widthScreen || player.getX() <= border.getXBorder()) {
            return true;
        }
        if(player.getY() + player.getHeight() + border.getYBorder() > this.heightScreen || player.getY() <= border.getYBorder()) {
            return true;
        }

	    //verifie si la balle touche une fusée
		int topEdgeBallX = player.getX() + player.getRadiusBall();
		int topEdgeBallY = player.getY();

		int bottomEdgeBallX = player.getX() + player.getRadiusBall();
		int bottomEdgeBallY = player.getY() + player.getHeight();

		int leftEdgeBallX = player.getX();
		int leftEdgeBallY = player.getY() + player.getRadiusBall();

		int rightEdgeBallX = player.getX() + player.getWidth();
		int rightEdgeBallY = player.getY() + player.getRadiusBall();

		int centerBallX = player.getX() + player.getRadiusBall();
		int centerBallY = player.getY() + player.getRadiusBall();

		for(int i = 0; i < rockets.size(); i++) {
			int topLeftCornerRocketX = rockets.get(i).getX();
			int topLeftCornerRocketY = rockets.get(i).getY();

			int bottomLeftCornerRocketX = 0;
			int bottomLeftCornerRocketY = 0;

			int topRightCornerRocketX = 0;
			int topRightCornerRocketY = 0;

			int bottomRightCornerRocketX = 0;
			int bottomRightCornerRocketY = 0;

			bottomLeftCornerRocketX = rockets.get(i).getX();
			bottomLeftCornerRocketY = rockets.get(i).getY() + rockets.get(i).getHeightRocket();

			topRightCornerRocketX = rockets.get(i).getX() + rockets.get(i).getWidthRocket();
			topRightCornerRocketY = rockets.get(i).getY();

			bottomRightCornerRocketX = rockets.get(i).getX() + rockets.get(i).getWidthRocket();
			bottomRightCornerRocketY = rockets.get(i).getY() + rockets.get(i).getHeightRocket();

			if (Math.sqrt(Math.pow(centerBallX - topLeftCornerRocketX + rockets.get(i).getWidthRocket() / 2, 2) + Math.pow(centerBallY - topLeftCornerRocketY + rockets.get(i).getHeightRocket() / 2, 2)) < player.getRadiusBall() + rockets.get(i).getWidthRocket() + rockets.get(i).getHeightRocket()) {
				/* Traitement de la collision de la balle avec les bords de la bounding box de la fusée */

				if (rightEdgeBallX > topLeftCornerRocketX && leftEdgeBallX < topLeftCornerRocketX && rightEdgeBallY > topLeftCornerRocketY && rightEdgeBallY < bottomLeftCornerRocketY) {
					Log.d("collision", "bord droit balle");
					return true;
				} else if (leftEdgeBallX < topRightCornerRocketX && rightEdgeBallX > topRightCornerRocketX && leftEdgeBallY > topRightCornerRocketY && rightEdgeBallY < bottomRightCornerRocketY) {
					Log.d("collision", "bord gauche balle");
					return true;
				} else if (topEdgeBallY < bottomRightCornerRocketY && bottomEdgeBallY > bottomRightCornerRocketY && topEdgeBallX > bottomLeftCornerRocketX && topEdgeBallX < bottomRightCornerRocketX) {
					Log.d("collision", "bord haut balle");
					return true;
				} else if (bottomEdgeBallY > topRightCornerRocketY && topEdgeBallY < topRightCornerRocketY && bottomEdgeBallX > topLeftCornerRocketX && bottomEdgeBallX < topRightCornerRocketX) {
					Log.d("collision", "bord bas balle");
					return true;
				}

				/* Traitement de la collision de la balle avec les coins de la bounding box de la fusée */
				if (distance(centerBallX, centerBallY, bottomLeftCornerRocketX, bottomLeftCornerRocketY) < player.getRadiusBall()) {
					Log.d("collision", "coin bas gauche bounding box fusée");
					return true;
				} else if (distance(centerBallX, centerBallY, bottomRightCornerRocketX, bottomRightCornerRocketY) < player.getRadiusBall()) {
					Log.d("collision", "coin bas droit bounding box fusée");
					return true;
				}
				if (distance(centerBallX, centerBallY, topLeftCornerRocketX, topLeftCornerRocketY) < player.getRadiusBall()) {
					Log.d("collision", "coin haut gauche bounding box fusée");
					return true;
				} else if (distance(centerBallX, centerBallY, topRightCornerRocketX, topRightCornerRocketY) < player.getRadiusBall()) {
					Log.d("collision", "coin haut droit bounding box fusée");
					return true;
				}
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
		//affiche l'image de fond
        canvas.drawBitmap(this.background.getBitmap(), 0, 0, null);

		// on dessine la balle et la bordure
		player.draw(canvas);
		border.draw(canvas);

		//on dessine les fusées
		for(int i = 0; i < rockets.size(); i++){
			rockets.get(i).draw(canvas);
		}

		//on affiche le timer
		Paint paint = new Paint();
		paint.setColor(Color.BLACK);
		paint.setTextSize(60);

		//calcul la position du texte pour que celui ci soit centrer au milieu
        int xPos = widthScreen / 2 - (int)(paint.measureText("Score : " + this.score.getScore())/2);
        int yPos = (int) (border.getXBorder() / 2 - ((paint.descent() + paint.ascent()) / 2)) ;

		canvas.drawText("Score : " + this.score.getScore(), xPos, yPos, paint);
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
				Log.d("fusee draw", "la fusee est supprimée");
			}
			else{
				rockets.get(i).move();
			}
		}
	}

	public void removeAllRockets(){
        rockets.clear();
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
		player.resize(width, height); // on définit la taille de la balle selon la taille de l'écran
		border.resize();
		for(int i = 0; i < rockets.size(); i++){
			rockets.get(i).resize();
		}
	}

    public BitmapDrawable setImage(final Context c, final int ressource, final int w, final int h) {
        Drawable dr = c.getResources().getDrawable(ressource);
        Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
        return new BitmapDrawable(c.getResources(), Bitmap.createScaledBitmap(bitmap, w, h, true));
    }
}
