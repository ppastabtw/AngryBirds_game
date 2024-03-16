/*
File Name: MyInputProcessor.java
Author: Kevin Yan

this was not used at all... it's supposed to listen to all user inputs but wasn't useful for my application
 */

package com.mygdx.angrybirds.utils;

import com.badlogic.gdx.InputProcessor;

public class MyInputProcessor implements InputProcessor {
    private float mx, my;
//    public MyInputProcessor(){
//        mx = 0;
//        my = 0;
//    }
//    public float getMx(){
//        return mx;
//    }
//
//    public float getMy(){
//        return my;
//    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
//        if (button == Input.Buttons.LEFT){
//            boxes.add(createBox(800/2, 250/2, 32, 32, false, false));
//        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        mx = screenX  / 2;
        my = Math.abs(screenY - 800)  / 2;
//
//        System.out.println(mx + " " + my);
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}
