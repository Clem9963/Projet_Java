package com.projetisima;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import static java.lang.Math.*;

class Ball
{
	//image de la balle
	private BitmapDrawable img = null;

<<<<<<< HEAD
<<<<<<< HEAD
	private int x = 0, y = 0; 				// coordonnées du coin supérieur gauche la balle
=======
	private int x = 1, y = 1; //coordonnées de la balle
>>>>>>> 13cc31a... Correction du bug du recommencer lors du démarrage de jeu
	private int widthBall, heightBall, radiusBall; //taille de la balle
	private int widthScreen, heightScreen; //taille de l'ecran
=======
	private int x = 1, y = 1;						// Coordonnées de la balle
	private int previousX = 1, previousY = 1;		// Précédentes coordonnées de la balle
	private int inertiaX = 0, inertiaY = 0;			// Inertie de la bille
	private int widthBall, heightBall, radiusBall;	// Taille de la balle
	private int widthScreen, heightScreen;			// Taille de l'ecran
>>>>>>> c5d2926... Amélioration légère de la physique de la balle.

	private final int divisionBall = 10;						// Coefficient pour choisir la taille de la bille
	private final int sensoryCoefficient = 5;					// Coefficient pour appréhender les valeurs fournies par l'accéléromètre
	private final float inertiaCoefficient = 0.3f;				// Coefficient pour l'inertie
	private final float dispersionCoefficient = 1.5f;	// Coefficient pour la dispersion de l'inertie

	//contexte de l'application pour récuperer les images notamment
	private final Context mContext;

	// Constructeur de la balle
	public Ball(final Context c)
	{
		this.mContext = c;
	}

	//recupère l'image de la bille
	public BitmapDrawable setImage(final Context c, final int ressource, final int w, final int h) {
		Drawable dr = c.getResources().getDrawable(ressource);
		Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
		return new BitmapDrawable(c.getResources(), Bitmap.createScaledBitmap(bitmap, w, h, true));
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	public int getRadiusBall() {
		return this.radiusBall;
	}

	public int getWidth() {
		return this.widthBall;
	}

	public int getHeight() {
		return this.heightBall;
	}

	public void placeMiddle() {
	    Log.d("plcaemiddle", "oui");
		this.x = widthScreen / 2;
		this.y = heightScreen / 2;

		this.previousX = this.x;
		this.previousY = this.y;
		
		this.inertiaX = 0;
		this.inertiaY = 0;
	}

	//recupere les dimensions de l'ecran et redimensionnne la bille
	public void resize(int wScreen, int hScreen) {
		widthScreen = wScreen;
		heightScreen = hScreen;
		Log.d("rezise1", "x : " + x);
		x = wScreen/2;
        Log.d("rezise2", "x : " + x);
		y = hScreen/2;

		// definition de la taille de la bille
		widthBall = wScreen/divisionBall;
		heightBall = wScreen/divisionBall; // car balle est un cercle et non un ellipse d'ou le "wScreen" deux fois
		img = setImage(mContext, R.drawable.ball, widthBall, heightBall);

		//calcul du rayon de la bille
		radiusBall = widthBall/2;
	}

	//deplace la bille suivant la largeur
	public void moveX(float x){
		x = x * this.sensoryCoefficient;

        Log.d("moveX1", " x : " + this.x);
		this.x = round(this.x + x + this.inertiaX/dispersionCoefficient);

        Log.d("moveX2", " x : " + this.x);
		this.y = round(this.y + this.inertiaY/dispersionCoefficient);

		this.inertiaX = round((this.x - this.previousX) * this.inertiaCoefficient + this.inertiaX/dispersionCoefficient);
		this.inertiaY = round((this.y - this.previousY) * this.inertiaCoefficient + this.inertiaY/dispersionCoefficient);

		this.previousX = this.x;
		this.previousY = this.y;
	}

	//deplace la bille suivant la hauteur
	public void moveY(float y){
		y = y * this.sensoryCoefficient;

		this.y = round(this.y + y + this.inertiaY/dispersionCoefficient);
        Log.d("moveY1", " x : " + this.x);
		this.x = round(this.x + this.inertiaX/dispersionCoefficient);
        Log.d("moveY2", " x : " + this.x);

		this.inertiaX = round((this.x - this.previousX) * this.inertiaCoefficient + this.inertiaX/dispersionCoefficient);
		this.inertiaY = round((this.y - this.previousY) * this.inertiaCoefficient + this.inertiaY/dispersionCoefficient);

		this.previousX = this.x;
		this.previousY = this.y;
	}

	//verifie si la balle a touché un coté de l'ecran
	public boolean outScreen(){
		if(x + widthBall > widthScreen || x <= 0) {
			Log.d("test1","oui");
			Log.d("reponse", "x : " + x + " , widthball " + widthBall + " , widthscreen : " + widthScreen);
			return true;
		}

		if(y + heightBall > heightScreen || y <= 0) {
			Log.d("test2","oui");
			return true;
		}
		return false;
	}

	// on dessine la bille
	public void draw(Canvas canvas)
	{
		if(img == null) {return;}
		canvas.drawBitmap(img.getBitmap(), x, y, null);
	}
}