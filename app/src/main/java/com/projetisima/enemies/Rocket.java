package com.projetisima.enemies;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.Log;

public class Rocket{
	//image de la fusée
	protected Bitmap img = null;
    protected Bitmap bmp;

    Directions direction;
	protected int x = 0, y = 0; //coordonnées de la fusée
	private int widthRocket, heightRocket; //taille de la fusée
	private int widthScreen, heightScreen; //taille de l'ecran

	protected int coefficientMouvement = 5; // coefficient pour choisir la vitesse de la fusée

	//contexte de l'application pour récuperer les images notamment
	private final Context mContext;

	// Constructeur de la fusée
	public Rocket(final Context c, Directions d, int XStart, int YStart, int widthScreen, int heightScreen, int pathRocketImage)
	{
		this.mContext = c;
		this.direction = d;
		this.x = XStart;
		this.y = YStart;
		this.widthScreen = widthScreen;
		this.heightScreen = heightScreen;
		this.bmp = BitmapFactory.decodeResource(c.getResources(), pathRocketImage);
		resize();
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	public int getWidthRocket() {
		return this.widthRocket;
	}

	public int getHeightRocket() {
		return this.heightRocket;
	}

	public Directions getDirection() {
		return this.direction;
	}

	//fonction qui ne fait rien mais permet l'accès aux méthodes move() des fusées filles
	public void move(){}

	//recupere les dimensions de l'ecran et redimensionnne la fusée
	public void resize() {
		//met l'image dans le bon sens en fonction de la direction de la fusée
		//par defaut l'image de la fusée est dirigée vers le haut
		Matrix mat = new Matrix();

		switch (this.direction) {
			case RIGHT:
				mat.postRotate(90);
				break;
			case BOTTOM:
				mat.postRotate(180);
				break;
			case LEFT:
				mat.postRotate(-90);
				break;
		}
		img = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), mat, true);

		// definition de la taille de la fusée
		widthRocket = bmp.getWidth();
		heightRocket = bmp.getHeight();
	}

	//verifie si la fusée a touché un coté de l'ecran
	public boolean outScreen(){
		if(x > widthScreen || x + widthRocket <= 0) {
			return true;
		}

		if(y > heightScreen || y + heightRocket <= 0) {
			return true;
		}
		return false;
	}

	// on dessine la fusée
	public void draw(Canvas canvas)
	{
		if(img == null) {return;}
		canvas.drawBitmap(img, this.x, this.y, null);
	}
}
