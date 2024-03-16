/*
File Name:
Author: Kevin Yan

This class is for storing x,y float values for a point.
 */
package com.mygdx.angrybirds.utils;

public class Point {
    private float x, y;
    public Point(float xx, float yy){
        x = xx;
        y = yy;
    }

    public float getX(){
        return x;
    }

    public float getY(){
        return y;
    }
}
