package com.projetisima;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.Log;

import static java.lang.Math.*;

public class Border{
	//images pour les bordures
	private Bitmap verticalImg = null;
	private Bitmap horizontalImg = null;

	private int xBorder, yBorder; 			//taille des bordures
	private int widthScreen, heightScreen; //taille de l'ecran

	//contexte de l'application pour récuperer les images notamment
	private final Context mContext;

	// Constructeur de la bordure
	public Border(final Context c, int widthScreen, int heightScreen)
	{
		this.mContext = c;
		this.widthScreen = widthScreen;
		this.heightScreen = heightScreen;
		this.resize();
	}

	/* Récupère les dimensions de l'ecran et redimensionnne les bordures en fonction */
	public void resize() {
		/* Chargement des images pour les bordures */
		/* Applications des traitements sur la bordure horizontale */
		Bitmap tmp = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.horizontal_border);

		int width = tmp.getWidth();
		int height = tmp.getHeight();
		float ratio = width/(float)height;
		int newWidth = widthScreen;
		int newHeight = round(newWidth/ratio);
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;

		/* Création d'une matrice pour la manipulation */
		Matrix matrix = new Matrix();

		/* Redimensionnement effectif de l'image pour la bordure horizontale */
		matrix.postScale(scaleWidth, scaleHeight);
		Log.d("newWidth", String.valueOf(newWidth));
		Log.d("newHeight", String.valueOf(newHeight));
		Log.d("widthScreen", String.valueOf(widthScreen));
		Log.d("heightScreen", String.valueOf(heightScreen));
		this.horizontalImg = Bitmap.createBitmap(tmp, 0, 0, width, height, matrix, false);
		this.yBorder = newHeight;

		/* Applications des traitements sur la bordure verticale */
		tmp = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.vertical_border);

		width = tmp.getWidth();
		height = tmp.getHeight();
		ratio = width/(float)height;
		newHeight = heightScreen - 152;			// Pas de superposition
		newWidth = round(newHeight * ratio);

		scaleWidth = ((float) newWidth) / width;
		scaleHeight = ((float) newHeight) / height;

		/* Création d'une matrice pour la manipulation */
		matrix = new Matrix();

		/* Redimensionnement effectif de l'image pour la bordure horizontale */
		matrix.postScale(scaleWidth, scaleHeight);
		this.verticalImg = Bitmap.createBitmap(tmp, 0, 0, width, height, matrix, false);
		this.xBorder = newWidth;
	}

	/* Dessin des bordures */
	public void draw(Canvas canvas)
	{
		if(this.horizontalImg != null) {
			canvas.drawBitmap(horizontalImg, 0, 0, null);
			canvas.drawBitmap(horizontalImg, 0, heightScreen - yBorder, null);
		}
		if(this.verticalImg != null) {
			canvas.drawBitmap(verticalImg, 0, yBorder, null);
			canvas.drawBitmap(verticalImg, widthScreen - xBorder, yBorder, null);
		}
	}

	public int getXBorder() {
		return xBorder;
	}

	public int getYBorder() {
		return yBorder;
	}

}
