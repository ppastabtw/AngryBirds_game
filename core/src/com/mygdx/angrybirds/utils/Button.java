/*
File Name:
Author: Kevin Yan

This class creates and stores all the information for a button.
 */
package com.mygdx.angrybirds.utils;

import com.badlogic.gdx.graphics.Texture;

import java.awt.*;

public class Button {
    private float cx, cy, w, h, x, y;
    private boolean isHover;
    private Rectangle rect;
    private Texture img;
    public Button(float cxx, float cyy, float ww, float hh){
        cx = cxx; // center x of button
        cy = cyy; // center y of button
        w = ww; // width of button
        h = hh; // height of button
        x = cx-w/2; // x
        y = cy-h/2; // y
        rect = new Rectangle((int) x, (int) y, (int) w, (int) h); // button as a rectangle
        isHover = false; // if mouse is hovering over
    }

    public Rectangle getRect(){return rect;}
    public float getX(){return x;}
    public float getY(){return y;}
    public float getWidth(){return w;}
    public float getHeight(){return h;}
    public float getCenterX(){return cx;}
    public float getCenterY(){return cy;}
    public boolean getIsHover(){return isHover;}
    public void setIsHover(boolean b){isHover = b;}
    public boolean checkIsHover(float mx, float my){
        isHover = rect.contains(mx, my);
        return isHover;
    }
    public void setTexture(Texture t){img = t;}
    public Texture getTexture(){return img;}

}
