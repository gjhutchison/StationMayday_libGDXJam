package com.hutchison.mayday.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.hutchison.mayday.tween.SpriteAccessor;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;

public class Splash implements Screen {

	private Sprite splash;
	private Sprite splash2;
	private SpriteBatch batch;

	private TweenManager tweenManager;

	private Boolean skip;

	public Splash(Boolean skipMe) {
		skip = skipMe;
	}

	@Override
	public void show() {
		batch = new SpriteBatch();
		tweenManager = new TweenManager();

		Tween.registerAccessor(Sprite.class, new SpriteAccessor());

		Texture splashTexture = new Texture("img/splashScreen.png");
		Texture splashTexture2 = new Texture("img/spaceStationSplash.png");
		splash = new Sprite(splashTexture);
		splash2 = new Sprite(splashTexture2);
		splash.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		splash2.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		splash2.setAlpha(0);

		if (skip) {
			Tween.set(splash2, SpriteAccessor.ALPHA).target(0).start(tweenManager);
			Tween.to(splash2, SpriteAccessor.ALPHA, 1.5f).target(1).repeatYoyo(1, 2).setCallback(new TweenCallback() {
				@Override
				public void onEvent(int type, BaseTween<?> source) {
					((Game) Gdx.app.getApplicationListener()).setScreen(new PlayGame());

				}
			}).start(tweenManager);
		} else {
			Tween.set(splash, SpriteAccessor.ALPHA).target(0).start(tweenManager);
			Tween.to(splash, SpriteAccessor.ALPHA, 0.8f).target(1).repeatYoyo(1, 1.5f).setCallback(new TweenCallback() {
				@Override
				public void onEvent(int type, BaseTween<?> source) {
					Tween.set(splash2, SpriteAccessor.ALPHA).target(0).start(tweenManager);
					Tween.to(splash2, SpriteAccessor.ALPHA, 1.5f).target(1).repeatYoyo(1, 2)
							.setCallback(new TweenCallback() {
						@Override
						public void onEvent(int type, BaseTween<?> source) {
							((Game) Gdx.app.getApplicationListener()).setScreen(new PlayGame());

						}
					}).start(tweenManager);

				}
			}).start(tweenManager);
		}
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		tweenManager.update(delta);

		batch.begin();

		if (!skip) {
			splash.draw(batch);
		}
		splash2.draw(batch);

		batch.end();

	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void hide() {

	}

	@Override
	public void dispose() {
		batch.dispose();
		splash.getTexture().dispose();
		splash2.getTexture().dispose();
	}

}
