package com.reddit.programming.mario;

import ch.idsia.mario.engine.Art;
import ch.idsia.mario.engine.LevelScene;
import ch.idsia.mario.engine.sprites.Sprite;


public class StaticMario extends Sprite
{
    public StaticMario(LevelScene world, int x, int y, int transparency, int marioMode)
    {
    	kind = KIND_MARIO;
        this.transparency = transparency / 100.0f;
        
    	this.x = x;
        this.y = y;
        xPicO = 8;
        yPicO = 15;

        xPic = 1;
        yPic = 0;
    	
        int size = 32;
        if (marioMode == 0) {
        	sheet = Art.smallMario;
        	size = 16;
            xPicO = 8;
            yPicO = 15;
        }
        	
        else {
        	sheet = marioMode > 1 ? Art.fireMario : Art.mario;
        	size = 32;
        	xPicO = 16;
            yPicO = 31;
        }

        wPic = hPic = size;
    }

    public void setMode(int marioMode)
    {
        int size = 32;
        if (marioMode == 0)
        {
        	sheet = Art.smallMario;
        	size = 16;
            xPicO = 8;
            yPicO = 15;
        }
        else
        {
        	sheet = marioMode > 1 ? Art.fireMario : Art.mario;
        	size = 32;
        	xPicO = 16;
            yPicO = 31;
        }

        wPic = hPic = size;
    }
}