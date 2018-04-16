package com.projetisima.enemies;

import android.content.Context;

import com.projetisima.R;

public class RocketA extends Rocket {

    public RocketA(final Context c, Directions d, int XStart, int YStart, int widthScreen, int heightScreen){
        super(c, d, XStart, YStart, widthScreen, heightScreen, R.drawable.rocket_a);
    }

    //redefinition du déplacement de la fusée suivant son type
    //deplace la fusée
    @Override
    public void move(){
        switch (this.direction){
            case RIGHT:
                this.x = this.x + coefficientMouvement;
                break;
            case BOTTOM:
                this.y = this.y + coefficientMouvement;
                break;
            case LEFT:
                this.x = this.x - coefficientMouvement;
                break;
            case TOP:
                this.y = this.y - coefficientMouvement;
                break;
        }
    }
}
