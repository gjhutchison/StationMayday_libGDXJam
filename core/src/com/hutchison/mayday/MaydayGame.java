package com.hutchison.mayday;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.hutchison.mayday.screens.Splash;

public class MaydayGame extends Game {
	Music music;

	public static final String TITLE = "Station Mayday", VERSION = "1.0.0";

	@Override
	public void create() {
		Gdx.app.log(TITLE, VERSION);
		Gdx.app.log(TITLE, "create()");

		music = Gdx.audio.newMusic(Gdx.files.internal("sound/Dark Fog.mp3"));

		music.setLooping(true);
		music.setVolume(0.2f);

		music.play();

		setScreen(new Splash(false));

	}

	@Override
	public void render() {
		super.render();
	}

	@Override
	public void dispose() {
		music.dispose();

	}
}
