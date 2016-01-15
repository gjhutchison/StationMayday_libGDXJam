package com.hutchison.mayday.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.hutchison.mayday.MaydayGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		
		cfg.title = MaydayGame.TITLE + " v"+MaydayGame.VERSION;
		cfg.width = 1280;
		cfg.height = 720;
		
		cfg.backgroundFPS = -1;
		
		
		
		cfg.resizable = false;
		
		
		
		new LwjglApplication(new MaydayGame(), cfg);
	}
}

