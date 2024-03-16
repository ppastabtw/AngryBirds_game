/*
File Name:
Author: Kevin Yan

This class extends Game from box2D, this allows me to switch screens from the game and the mainmenus.
 */
package com.mygdx.angrybirds;

import com.badlogic.gdx.Game;


public class Drop extends Game {

//    public SpriteBatch batch;
//    public BitmapFont font;

    public void create() {
//        batch = new SpriteBatch();
        this.setScreen(new mainMenu(this, "mainmenu"));
    }

    public void render() {
        super.render(); // important!
    }

    public void dispose() {
//        batch.dispose();
//        font.dispose();
    }

}
