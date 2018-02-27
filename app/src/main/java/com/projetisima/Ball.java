package com.projetisima;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

class Ball
{
	//image de la balle
	private BitmapDrawable img = null;

<<<<<<< HEAD
	private int x = 0, y = 0; 				// coordonnées du coin supérieur gauche la balle
=======
	private int x = 1, y = 1; //coordonnées de la balle
>>>>>>> 13cc31a... Correction du bug du recommencer lors du démarrage de jeu
	private int widthBall, heightBall, radiusBall; //taille de la balle
	private int widthScreen, heightScreen; //taille de l'ecran

	private final int divisionBall = 10; // coefficient pour choisir la taille de la bille
	private final int coefficientMouvement = 5; // coefficient pour choisir la vitesse de la bille

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
		x = widthScreen/2;
		y = heightScreen/2;
	}


	//recupere les dimensions de l'ecran et redimensionnne la bille
	public void resize(int wScreen, int hScreen) {
		widthScreen = wScreen;
		heightScreen = hScreen;
		x = wScreen/2;
		y = hScreen/2;

		// definition de la taille de la bille
		widthBall = wScreen/divisionBall;
		heightBall = wScreen/divisionBall; // car balle est un cercle et non un ellipse d'ou le "wScreen" deux fois
		img = setImage(mContext, R.drawable.ball, widthBall, heightBall);

		//calcul du rayon de la bille
		radiusBall = widthBall/2;
	}

	//deplace la bille suivant la largeur
	public void moveOnX(int x){
		this.x = (x * coefficientMouvement) + this.x;
	}

	//deplace la bille suivant la hauteur
	public void moveOnY(int y){
		this.y = (y * coefficientMouvement) + this.y;
	}

	//verifie si la balle a touché un coté de l'ecran
	public boolean outScreen(){
		if(x + widthBall > widthScreen || x <= 0) {
			return true;
		}

		if(y + heightBall > heightScreen || y <= 0) {
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