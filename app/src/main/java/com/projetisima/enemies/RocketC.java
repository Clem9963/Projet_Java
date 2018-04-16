package com.projetisima.enemies;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;

import com.projetisima.R;

public class RocketC extends Rocket {
    private Directions directionFinale;
    private int pallier = 0;
    private int nbPallier = 100;
    private int diviseur = 30;
    private int multiplicateur = 2;

    //pour modifier l'angle de la fuséé
    Matrix mat = new Matrix();
    private int angle = 0;


    public RocketC(final Context c, Directions d, int XStart, int YStart, int widthScreen, int heightScreen){
        super(c, d, XStart, YStart, widthScreen, heightScreen, R.drawable.rocket_c);

        //calcul le bord d'arrive de la fusée suivant la direction de départ et sa position sur le bord
        switch (d){
            case RIGHT:
                if(YStart < heightScreen / multiplicateur){
                    this.directionFinale = Directions.BOTTOM;
                }
                else{
                    this.directionFinale = Directions.TOP;
                }
                break;
            case LEFT:
                if(YStart < heightScreen / multiplicateur){
                    this.directionFinale = Directions.BOTTOM;
                }
                else{
                    this.directionFinale = Directions.TOP;
                }
                break;

            case BOTTOM:
                if(XStart < widthScreen / multiplicateur){
                    this.directionFinale = Directions.RIGHT;
                }
                else{
                    this.directionFinale = Directions.LEFT;
                }
                break;

            case TOP:
                if(XStart < widthScreen / multiplicateur){
                    this.directionFinale = Directions.RIGHT;
                }
                else{
                    this.directionFinale = Directions.LEFT;
                }
                break;
        }
    }

    //redefinition du déplacement de la fusée suivant son type
    //deplace la fusée
    @Override
    public void move(){
        switch (this.direction){
            case RIGHT:
                //si la fusée ne s'est pas assez deplacer sur l'axe de depart, on continuer a la deplacer
                if(pallier < nbPallier)
                {
                    this.x = this.x + coefficientMouvement;
                }
                else
                {
                    //suivant la direction d'arrivée
                    if(directionFinale == Directions.TOP) {
                        //si le changement de direction est fini, on la deplace seulement sur l'axe final
                        if(pallier > nbPallier * multiplicateur)
                        {
                            this.y = this.y - coefficientMouvement;
                        }
                        //sinon on continue de changer l'axe de la fusée
                        else
                        {
                            this.x = this.x + coefficientMouvement - pallier /diviseur;
                            this.y = this.y - coefficientMouvement - pallier /diviseur;
                            changeangle();
                        }
                    }
                    else {
                        //si le changement de direction est fini, on la deplace seulement sur l'axe final
                        if(pallier > nbPallier * multiplicateur)
                        {
                            this.y = this.y + coefficientMouvement;
                        }
                        //sinon on continue de changer l'axe de la fusée
                        else
                        {
                            this.x = this.x + coefficientMouvement - pallier /diviseur;
                            this.y = this.y + coefficientMouvement + pallier /diviseur;
                            changeangle();
                        }
                    }
                }
                break;
            case LEFT:
                //si la fusée ne s'est pas assez deplacer sur l'axe de depart, on continuer a la deplacer
                if(pallier < nbPallier)
                {
                    this.x = this.x - coefficientMouvement;
                }
                else
                {
                    //suivant la direction d'arrivée
                    if(directionFinale == Directions.TOP) {
                        //si le changement de direction est fini, on la deplace seulement sur l'axe final
                        if(pallier > nbPallier * multiplicateur)
                        {
                            this.y = this.y - coefficientMouvement;
                        }
                        //sinon on continue de changer l'axe de la fusée
                        else
                        {
                            this.x = this.x - coefficientMouvement + pallier /diviseur;
                            this.y = this.y - coefficientMouvement - pallier /diviseur;
                            changeangle();
                        }
                    }
                    else {
                        //si le changement de direction est fini, on la deplace seulement sur l'axe final
                        if(pallier > nbPallier * multiplicateur)
                        {
                            this.y = this.y + coefficientMouvement;
                        }
                        //sinon on continue de changer l'axe de la fusée
                        else
                        {
                            this.x = this.x - coefficientMouvement + pallier /diviseur;
                            this.y = this.y + coefficientMouvement + pallier /diviseur;
                            changeangle();
                        }
                    }
                }
                break;

            case BOTTOM:
                //si la fusée ne s'est pas assez deplacer sur l'axe de depart, on continuer a la deplacer
                if(pallier < nbPallier)
                {
                    this.y = this.y + coefficientMouvement;
                }
                else
                {
                    //suivant la direction d'arrivée
                    if(directionFinale == Directions.LEFT) {
                        //si le changement de direction est fini, on la deplace seulement sur l'axe final
                        if(pallier > nbPallier * multiplicateur)
                        {
                            this.x = this.x - coefficientMouvement;
                        }
                        //sinon on continue de changer l'axe de la fusée
                        else
                        {
                            this.y = this.y + coefficientMouvement - pallier /diviseur;
                            this.x = this.x - coefficientMouvement - pallier /diviseur;
                            changeangle();
                        }
                    }
                    else {
                        //si le changement de direction est fini, on la deplace seulement sur l'axe final
                        if(pallier > nbPallier * multiplicateur)
                        {
                            this.x = this.x + coefficientMouvement;
                        }
                        //sinon on continue de changer l'axe de la fusée
                        else
                        {
                            this.y = this.y + coefficientMouvement - pallier /diviseur;
                            this.x = this.x + coefficientMouvement + pallier /diviseur;
                            changeangle();
                        }
                    }
                }
                break;

            case TOP:
                //si la fusée ne s'est pas assez deplacer sur l'axe de depart, on continuer a la deplacer
                if(pallier < nbPallier)
                {
                    this.y = this.y - coefficientMouvement;
                }
                else
                {
                    //suivant la direction d'arrivée
                    if(directionFinale == Directions.LEFT) {
                        //si le changement de direction est fini, on la deplace seulement sur l'axe final
                        if(pallier > nbPallier * multiplicateur)
                        {
                            this.x = this.x - coefficientMouvement;
                        }
                        //sinon on continue de changer l'axe de la fusée
                        else
                        {
                            this.y = this.y - coefficientMouvement + pallier /diviseur;
                            this.x = this.x - coefficientMouvement - pallier /diviseur;
                            changeangle();
                        }
                    }
                    else {
                        //si le changement de direction est fini, on la deplace seulement sur l'axe final
                        if(pallier > nbPallier * multiplicateur)
                        {
                            this.x = this.x + coefficientMouvement;
                        }
                        //sinon on continue de changer l'axe de la fusée
                        else
                        {
                            this.y = this.y - coefficientMouvement + pallier /diviseur;
                            this.x = this.x + coefficientMouvement + pallier /diviseur;
                            changeangle();
                        }
                    }
                }
                break;
        }
        pallier++;
    }

    private void changeangle(){
        if(angle < 90) {
            angle += 5;
        }
        mat.reset();

        switch (this.direction) {
            case RIGHT:
                if(this.directionFinale == Directions.TOP)
                {
                    mat.postRotate(90 - angle);
                }
                else
                {
                    mat.postRotate(90 + angle);
                }
                break;
            case LEFT:
                if(this.directionFinale == Directions.TOP)
                {
                    mat.postRotate(-90 + angle);
                }
                else
                {
                    mat.postRotate(-90 - angle);
                }
                break;

            case BOTTOM:
                if(this.directionFinale == Directions.LEFT)
                {
                    mat.postRotate(180 + angle);
                }
                else
                {
                    mat.postRotate(180 - angle);
                }
                break;
            case TOP:
                if(this.directionFinale == Directions.LEFT)
                {
                    mat.postRotate(-1 * angle);
                }
                else
                {
                    mat.postRotate(angle);
                }
                break;
        }
        this.img = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), mat, true);
    }
}
