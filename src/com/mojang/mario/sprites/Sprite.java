package com.mojang.mario.sprites;

import java.awt.Graphics;
import java.awt.Image;

import com.mojang.mario.level.SpriteTemplate;
import com.mojang.mario.GlobalOptions;

public class Sprite
{
    public static SpriteContext spriteContext;
    public byte kind = 120; //SK: undefined, if this is shown!
    
    public float xOld, yOld, x, y, xa, ya;
    public int mapX, mapY;
    
    public int xPic, yPic;
    public int wPic = 32;
    public int hPic = 32;
    public int xPicO, yPicO;
    public boolean xFlipPic = false;
    public boolean yFlipPic = false;
    public Image[][] sheet;
    public boolean visible = true;
    
    public int layer = 1;

    public SpriteTemplate spriteTemplate;
    
    public void move()
    {
        x+=xa;
        y+=ya;
    }
    
    public void render(Graphics og, float alpha)
    {
        if (!visible) return;
        
//        int xPixel = (int)(xOld+(x-xOld)*alpha)-xPicO;
//        int yPixel = (int)(yOld+(y-yOld)*alpha)-yPicO;

        int xPixel = (int)x-xPicO;
        int yPixel = (int)y-yPicO;


        og.drawImage(sheet[xPic][yPic], xPixel+(xFlipPic?wPic:0), yPixel+(yFlipPic?hPic:0), xFlipPic?-wPic:wPic, yFlipPic?-hPic:hPic, null);
        if (GlobalOptions.Labels)
            og.drawString("" + xPixel + "," + yPixel, xPixel, yPixel);
    }
    
    public final void tick()
    {
        xOld = x;
        yOld = y;
        mapX = (int)(xOld / 16);
        mapY = (int)(yOld / 16);
        move();
    }

    public final void tickNoMove()
    {
        xOld = x;
        yOld = y;
    }

//    public float getX(float alpha)
//    {
//        return (xOld+(x-xOld)*alpha)-xPicO;
//    }
//
//    public float getY(float alpha)
//    {
//        return (yOld+(y-yOld)*alpha)-yPicO;
//    }

    public void collideCheck()
    {
    }

    public void bumpCheck(int xTile, int yTile)
    {
    }

    public boolean shellCollideCheck(Shell shell)
    {
        return false;
    }

    public void release(Mario mario)
    {
    }

    public boolean fireballCollideCheck(Fireball fireball)
    {
        return false;
    }
}