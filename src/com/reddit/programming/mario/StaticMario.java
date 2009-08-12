package com.reddit.programming.mario;

import ch.idsia.mario.engine.Art;
import ch.idsia.mario.engine.LevelScene;
import ch.idsia.mario.engine.sprites.Sprite;


public class StaticMario extends Sprite
{
    private int width = 4;
    int height = 24;

    private LevelScene world;
    public int facing;

    public boolean avoidCliffs = false;
    private int life;

    public StaticMario(LevelScene world, int x, int y)
    {
        kind = KIND_MARIO;
        sheet = Art.smallMario;

        xPicO = 8;
        yPicO = 15;
        wPic = hPic = 16;

        this.x = x;
        this.y = y;
        this.world = world;
        xPicO = 8;
        yPicO = 15;

        xPic = 1;
        yPic = 0;
        height = 12;
        facing = 1;
        wPic  = hPic = 16;
        life = 0;
    }
}