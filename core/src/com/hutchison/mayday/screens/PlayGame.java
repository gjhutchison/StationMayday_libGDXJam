package com.hutchison.mayday.screens;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.utils.Align;
import com.hutchison.mayday.crew.CrewManager;
import com.hutchison.mayday.station.Station;
import com.hutchison.mayday.tween.SpriteAccessor;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;

public class PlayGame implements Screen {

	// Game Vars
	private int day;

	// Game Flag Bools
	private Boolean gameOver;

	private Boolean rationScreen;

	private Boolean disableControls;

	private Boolean fading;

	private Boolean crewSelected;

	private Boolean help;

	private int idSelected;

	// Graphics Vars
	private Sprite bg;
	private Sprite bg2;
	private Sprite bg3;

	private Sprite helpSprite;

	private Sprite nDaySplash;

	private Sprite tags[];
	private Sprite ports[];
	private Sprite jobSymbs[];

	private Sprite tagSelection;

	private Sprite logSprite;
	private Sprite logStatic;

	private Sprite nextButton;
	private Sprite finishButton;
	private Sprite checkBox[];

	private Sprite cross;

	private SpriteBatch batch;

	private BitmapFont font;

	private Sprite statusIcon[];

	private TextureAtlas tagAtlas;
	private TextureAtlas portAtlas;
	private TextureAtlas jobAtlas;

	private TextureAtlas miscAtlas;

	private Label log;
	private Label helpLabel;

	private CrewManager crewManager;
	private Station station;

	private TweenManager tweenManager;

	private Sound click;

	private Rectangle nDayRect;
	private Rectangle doneRect;
	private Rectangle crewRects[] = { new Rectangle(890, 0, 390, 144), new Rectangle(890, 144, 390, 144),
			new Rectangle(890, 288, 390, 144), new Rectangle(890, 432, 390, 144), new Rectangle(890, 576, 390, 144), };

	// Areas to click for the jobs
	private Rectangle jobRects[] = { new Rectangle(440, 0, 400, 144), new Rectangle(440, 144, 400, 144),
			new Rectangle(440, 288, 400, 144), new Rectangle(440, 432, 400, 144), new Rectangle(440, 576, 400, 144), };

	private Rectangle eatRects[] = { new Rectangle(732, 10, 126, 124), new Rectangle(732, 154, 126, 124),
			new Rectangle(732, 298, 126, 124), new Rectangle(732, 442, 126, 124), new Rectangle(732, 586, 126, 124), };

	private final long session = System.currentTimeMillis() / 60000;

	@Override
	public void show() {

		click = Gdx.audio.newSound(Gdx.files.internal("sound/click.mp3"));

		// Graphics Setup
		font = new BitmapFont(Gdx.files.internal("font/white.fnt"));
		// font.setColor(Color.GREEN);

		batch = new SpriteBatch();

		tagAtlas = new TextureAtlas("img/tags/tags.pack");
		portAtlas = new TextureAtlas("img/portrait/ports.pack");
		jobAtlas = new TextureAtlas("img/jobSymbols/jobSymbs.pack");
		miscAtlas = new TextureAtlas("img/misc/misc.pack");

		Texture textureBG = new Texture("img/PlayBG.png");
		Texture textureBG2 = new Texture("img/PlayBGStars.png");
		Texture textureBG3 = new Texture("img/PlayBGStars_2.png");

		Texture logTexture = new Texture("img/eventLog.png");

		tagSelection = miscAtlas.createSprite("tagSelect");
		nextButton = miscAtlas.createSprite("nextButton_up");
		finishButton = miscAtlas.createSprite("finishButton");

		cross = new Sprite(new Texture("img/misc/cross.png"));

		checkBox = new Sprite[2];

		checkBox[0] = miscAtlas.createSprite("yesBox");
		checkBox[1] = miscAtlas.createSprite("noBox");

		logSprite = new Sprite(logTexture);
		logStatic = new Sprite(new Texture("img/eventStatic.png"));

		bg = new Sprite(textureBG);
		bg2 = new Sprite(textureBG2);
		bg3 = new Sprite(textureBG3);

		helpSprite = new Sprite(new Texture("img/helpScreen.png"));

		nDaySplash = new Sprite(new Texture("img/nextDaySplash.png"));

		tags = new Sprite[5];

		tags[0] = tagAtlas.createSprite("tagCap");
		tags[1] = tagAtlas.createSprite("tagDoc");
		tags[2] = tagAtlas.createSprite("tagEng");
		tags[3] = tagAtlas.createSprite("tagJan");
		tags[4] = tagAtlas.createSprite("tagSecurity");

		ports = new Sprite[7];

		ports[0] = portAtlas.createSprite("capPort");
		ports[1] = portAtlas.createSprite("docPort");
		ports[2] = portAtlas.createSprite("engPort");
		ports[3] = portAtlas.createSprite("janPort");
		ports[4] = portAtlas.createSprite("secPort");
		ports[5] = portAtlas.createSprite("deadPort");
		ports[6] = portAtlas.createSprite("snappedPort");

		jobSymbs = new Sprite[5];

		jobSymbs[0] = jobAtlas.createSprite("genJob");
		jobSymbs[1] = jobAtlas.createSprite("watJob");
		jobSymbs[2] = jobAtlas.createSprite("heatJob");
		jobSymbs[3] = jobAtlas.createSprite("radJob");
		jobSymbs[4] = jobAtlas.createSprite("scavJob");

		statusIcon = new Sprite[2];

		statusIcon[0] = miscAtlas.createSprite("sickIcon");
		statusIcon[1] = miscAtlas.createSprite("injuredIcon");

		bg.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		bg2.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		bg3.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		helpSprite.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		crewManager = new CrewManager();
		station = new Station(crewManager);

		// Set up the event log
		LabelStyle logStyle = new LabelStyle(font, Color.GREEN);
		log = new Label("", logStyle);
		log.setBounds(10, 0, 385, 680);
		log.setFontScale(0.48f);
		log.setWrap(true);
		log.setAlignment(Align.topLeft);

		helpLabel = new Label("F1 - Help", logStyle);
		helpLabel.setPosition(10, 5);
		helpLabel.setFontScale(0.48f);

		String logReport = crewManager.requestCrewReport() + station.requestStationStatus();
		log.setText(logReport);

		nDayRect = new Rectangle(440, 620, 400, 100);
		doneRect = new Rectangle(440, 620, 200, 100);

		// Areas for selecting a crew member

		// Starting day
		day = 1;

		gameOver = false;

		// Revokes controls
		disableControls = false;

		crewSelected = false;
		idSelected = -1;

		rationScreen = false;

		fading = false;
		help = false;

		// Set up mouse input handler
		Gdx.input.setInputProcessor(new InputAdapter() {
			public boolean touchDown(int screenX, int screenY, int pointer, int button) {

				if (!disableControls) {
					if (button == Buttons.LEFT) {
						handleClick(screenX, screenY);
						return true;
					} else if (button == Buttons.RIGHT) {
						crewSelected = false;
						idSelected = -1;
						rationScreen = false;

						return true;
					}
				} else if (button == Buttons.RIGHT) {
					if (help) {
						help = false;
						disableControls = false;

						return true;
					}
				}

				return false;
			}

			public boolean keyDown(int keycode) {

				if (keycode == Input.Keys.ESCAPE) {
					Gdx.app.exit();
				}

				else if (keycode == Input.Keys.F1) {
					if (!fading) {
						if (help) {
							help = false;
							disableControls = false;
						} else {
							help = true;
							disableControls = true;
						}

						return true;
					}

				}

				return false;
			}

		});

		// Sparkle the stars and fizzle wizzle the screen
		tweenManager = new TweenManager();
		Tween.registerAccessor(Sprite.class, new SpriteAccessor());

		Tween.set(bg2, SpriteAccessor.ALPHA).target(0).start(tweenManager);
		Tween.to(bg2, SpriteAccessor.ALPHA, 3f).target(1).repeatYoyo(Tween.INFINITY, 0.5f).start(tweenManager);

		Tween.set(bg3, SpriteAccessor.ALPHA).target(1).start(tweenManager);
		Tween.to(bg3, SpriteAccessor.ALPHA, 3f).target(0).repeatYoyo(Tween.INFINITY, 0.5f).start(tweenManager);

		Tween.set(logStatic, SpriteAccessor.ALPHA).target(0.05f).start(tweenManager);
		Tween.to(logStatic, SpriteAccessor.ALPHA, 0.80f).target(0.15f).repeatYoyo(Tween.INFINITY, 0.80f)
				.start(tweenManager);

	}

	@Override
	public void render(float delta) {

		// Graphics Stuffs
		tweenManager.update(delta);

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.begin();

		batch.draw(bg, 0, 0);

		bg2.draw(batch);
		bg3.draw(batch);

		if (help) {
			batch.draw(helpSprite, 0, 0);
		} else {

			float tagX = Gdx.graphics.getWidth() - tags[0].getWidth();

			logStatic.draw(batch);
			batch.draw(logSprite, 0, 0);

			font.draw(batch, "Event Log Day:" + day, 10, 710);

			float x = Gdx.input.getX();
			float y = Gdx.input.getY();

			// Drawing crew portraits and tags
			for (int i = 0; i < 5; i++) {
				if (crewManager.isDead(4 - i)) {
					batch.draw(ports[5], tagX + 10, 10 + (144 * (i)));

				} else if (crewManager.isSnapped(4 - i)) {
					batch.draw(ports[6], tagX + 10, 10 + (144 * (i)));
				} else {
					batch.draw(ports[4 - i], tagX + 10, 10 + (144 * (i)));

					if (crewManager.isSick(4 - i)) {
						batch.draw(statusIcon[0], tagX + 10, 10 + (144 * (i)));
					}

					if (crewManager.isInjured(4 - i)) {
						batch.draw(statusIcon[1], tagX + 10 + 96, 10 + (144 * (i)));
					}

				}
				batch.draw(tags[i], tagX, Gdx.graphics.getHeight() - tags[0].getHeight() * (i + 1));

				if (crewManager.isDead(i)) {
					font.draw(batch, "Dead", tagX + 150, 620 - (144 * i));
				} else if (crewManager.isSnapped(i)) {
					font.draw(batch, "Snapped", tagX + 150, 620 - (144 * i));
				} else {
					font.draw(batch, crewManager.getJobDesc(i), tagX + 150, 620 - (144 * i));
				}

				font.draw(batch, crewManager.getName(i), tagX + 150, 700 - (144 * (i)));

			}
			// Drawing job menu
			if (crewSelected) {
				batch.draw(tagSelection, tagX, 576 - 144 * idSelected);
				for (int i = 0; i < jobSymbs.length; i++) {

					if (crewManager.jobFree(i + 1)) {
						batch.draw(jobSymbs[i], 465, Gdx.graphics.getHeight() - jobSymbs[0].getHeight() * (i + 1));
					} else {
						jobSymbs[i].setPosition(465, Gdx.graphics.getHeight() - jobSymbs[0].getHeight() * (i + 1));
						jobSymbs[i].setColor(Color.GRAY);
						jobSymbs[i].draw(batch);

					}

				}
				if (idSelected == 1) {
					for (int i = 0; i < 5; i++) {
						if (crewRects[i].contains(x, y) && crewManager.isGone(i) == false && i != 1) {
							cross.setPosition(tagX + 10, 10 + (144 * (4 - i)));
							cross.draw(batch);
							break;
						}
					}
				}

			}
			// Draw ration menu
			else if (rationScreen) {
				for (int i = 0; i < 5; i++) {
					if (!crewManager.isGone(4 - i)) {
						batch.draw(checkBox[crewManager.getEat(4 - i)], tagX - checkBox[0].getWidth() - 20,
								10 + (144 * (i)));
					}

				}

				font.draw(batch, "Who Will Eat?", 420, 710);
				batch.draw(finishButton, 440, 0);

			} else {
				batch.draw(nextButton, 440, 0);

				for (int i = 0; i < 5; i++) {
					if (crewRects[i].contains(x, y) && crewManager.isGone(i) == false) {
						tagSelection.setColor(Color.CORAL);
						tagSelection.setPosition(tagX, 576 - 144 * i);
						tagSelection.draw(batch);
						break;
					}
				}

			}

			log.draw(batch, 1);

			helpLabel.draw(batch, 1);

			if (fading) {
				nDaySplash.draw(batch);
			}

		}

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
		// Dispose Batch
		batch.dispose();

		// Dispose Font
		font.dispose();

		// Dispose Textures
		bg.getTexture().dispose();
		bg2.getTexture().dispose();
		bg3.getTexture().dispose();
		helpSprite.getTexture().dispose();

		nDaySplash.getTexture().dispose();

		logSprite.getTexture().dispose();
		logStatic.getTexture().dispose();

		// Dispose atlasessssssassss
		tagAtlas.dispose();
		portAtlas.dispose();
		jobAtlas.dispose();
		miscAtlas.dispose();

		cross.getTexture().dispose();

		click.dispose();

		crewManager = null;
		station = null;

		tweenManager = null;

		nDayRect = null;
		doneRect = null;
		crewRects = null;
		jobRects = null;
		eatRects = null;

		System.gc();
	}

	public void handleClick(float x, float y) {

		float clickVol = 0.2f;

		if (!rationScreen) {

			if (!gameOver) {
				for (int i = 0; i < 5; i++) {
					if (crewRects[i].contains(x, y) && crewManager.isGone(i) == false) {
						if (crewSelected && idSelected == 1 || crewSelected && idSelected == i) {
							crewManager.setJob(idSelected, i + 6);
							idSelected = -1;
							crewSelected = false;
							click.play(clickVol);
						} else if (!crewSelected) {
							idSelected = i;
							crewSelected = true;
							click.play(clickVol);
						}

						return;

					}
				}

				if (crewSelected) {
					for (int i = 0; i < 5; i++) {
						if (jobRects[i].contains(x, y)) {
							crewManager.setJob(idSelected, i + 1);

							idSelected = -1;
							crewSelected = false;

							click.play(clickVol);
							return;
						}
					}
				}
			}

			if (nDayRect.contains(x, y) && !crewSelected) {
				if (!gameOver) {

					click.play(clickVol);
					rationScreen = true;
				} else {
					endGame();
				}

			}
		} else if (rationScreen) {
			for (int i = 0; i < 5; i++) {
				if (eatRects[i].contains(x, y) && !crewManager.isGone(i)) {
					crewManager.toggleEat(i);
					click.play(clickVol);
				}
			}

			if (doneRect.contains(x, y)) {
				rationScreen = false;
				disableControls = true;

				fading = true;

				click.play(clickVol);

				Tween.set(nDaySplash, SpriteAccessor.ALPHA).target(0).start(tweenManager);
				Tween.to(nDaySplash, SpriteAccessor.ALPHA, 0.7f).target(1).setCallback(new TweenCallback() {
					@Override
					public void onEvent(int type, BaseTween<?> source) {
						advanceDay();
					}
				}).start(tweenManager);
				Tween.to(nDaySplash, SpriteAccessor.ALPHA, 1.5f).target(0).delay(2.5f).setCallback(new TweenCallback() {
					@Override
					public void onEvent(int type, BaseTween<?> source) {
						disableControls = false;
						fading = false;
					}
				}).start(tweenManager);

			}

		}

	}

	public void advanceDay() {
		if (!gameOver) {
			day++;
			String logReport = "";

			station.updateStation();

			logReport = logReport + crewManager.requestCrewReport() + station.requestStationStatus();
			log.setText(logReport);

			station.resetJobs();
			crewManager.resetJobs();

			// makeBugLog(logReport+"\n--------------------\n"+station.getStationStats()+"\n--------------------\n"+crewManager.getCrewStats()+"\n\n\n");

			if (station.getSaved()) {

				logReport = station.requestStationStatus();
				logReport = "The crew has successfully been saved! You WIN! Press NEXT DAY to play again or ESC to exit.";

				log.setText(logReport);

				gameOver = true;

			} else if (station.getRadiationLeak() || crewManager.crewAlive() == 0) {
				logReport = station.requestStationStatus();
				logReport += "\nThe crew have all died or snapped, you LOSE. Press NEXT DAY to play again or ESC to exit.";

				log.setText(logReport);

				gameOver = true;
			}
		}

	}

	// for dev use only
	public void makeBugLog(String s) {

		try {
			File file = new File(session + "-log.txt");

			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getName(), true);

			BufferedWriter bw = new BufferedWriter(fw);

			bw.write("DAY: " + day + "\n" + s);
			bw.close();

		} catch (Exception e) {
			System.out.println("Write Error");
		}

	}

	// reset the game
	public void endGame() {
		// disableControls = true;
		Gdx.input.setInputProcessor(null);
		((Game) Gdx.app.getApplicationListener()).setScreen(new Splash(true));
	}

}
