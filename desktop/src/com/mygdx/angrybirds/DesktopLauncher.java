/*
File Name:
Author: Kevin Yan

this class starts up the application window with its default settings and starts the game.
 */
package com.mygdx.angrybirds;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setWindowedMode(1000, 800);
		config.setForegroundFPS(60);
		config.setTitle("AngryBirds");
		 config.setWindowIcon("assets/birds/red1.png");

		new Lwjgl3Application(new Drop(), config); // start game
//		new Lwjgl3Application(new angryBirds(), config);

	}

}
