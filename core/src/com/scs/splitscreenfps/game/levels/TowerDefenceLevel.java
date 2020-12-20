package com.scs.splitscreenfps.game.levels;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.scs.basicecs.AbstractEntity;
import com.scs.basicecs.BasicECS;
import com.scs.splitscreenfps.game.Game;
import com.scs.splitscreenfps.game.MapData;
import com.scs.splitscreenfps.game.components.towerdefence.CanBuildComponent;
import com.scs.splitscreenfps.game.components.towerdefence.CanBuildOnComponent;
import com.scs.splitscreenfps.game.components.towerdefence.ShowFloorSelectorComponent;
import com.scs.splitscreenfps.game.components.towerdefence.TowerDefencePlayerData;
import com.scs.splitscreenfps.game.data.MapSquare;
import com.scs.splitscreenfps.game.entities.Floor;
import com.scs.splitscreenfps.game.entities.Wall;
import com.scs.splitscreenfps.game.entities.towerdefence.TowerDefenceEntityFactory;
import com.scs.splitscreenfps.game.input.ControllerInputMethod;
import com.scs.splitscreenfps.game.input.IInputMethod;
import com.scs.splitscreenfps.game.input.MouseAndKeyboardInputMethod;
import com.scs.splitscreenfps.game.input.NoInputMethod;
import com.scs.splitscreenfps.game.systems.towerdefence.BuildDefenceSystem;
import com.scs.splitscreenfps.game.systems.towerdefence.TowerDefenceBulletSystem;
import com.scs.splitscreenfps.game.systems.towerdefence.CheckAltarSystem;
import com.scs.splitscreenfps.game.systems.towerdefence.CollectCoinsSystem;
import com.scs.splitscreenfps.game.systems.towerdefence.ShowFloorSelectorSystem;
import com.scs.splitscreenfps.game.systems.towerdefence.TowerDefenceEnemySpawnSystem;
import com.scs.splitscreenfps.game.systems.towerdefence.TowerDefenceEnemySystem;
import com.scs.splitscreenfps.game.systems.towerdefence.TowerDefencePhaseSystem;
import com.scs.splitscreenfps.game.systems.towerdefence.TurretSystem;

import ssmith.lang.NumberFunctions;
import ssmith.libgdx.GridPoint2Static;

public final class TowerDefenceLevel extends AbstractLevel {

	public static Properties prop;

	public int levelNum = 1;
	public TowerDefenceEnemySpawnSystem spawnEnemiesSystem; // Gets process by the TowerDefenceLevelSystem
	private GridPoint2Static targetPos;
	private List<String> instructions = new ArrayList<String>(); 
	private CheckAltarSystem checkAltarSystem;
	private TowerDefencePhaseSystem towerDefencePhaseSystem;

	public TowerDefenceLevel(Game _game) {
		super(_game);

		prop = new Properties();
		try {
			prop.load(new FileInputStream("towerdefence/td_config.txt"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		instructions.add("Keyboard:");
		instructions.add("W: Build Tower");
		instructions.add("B: Build Wall");
		instructions.add("");
		instructions.add("Controllers:");
		instructions.add("Todo: Build Tower");
		instructions.add("Todo: Build Wall");

		spawnEnemiesSystem = new TowerDefenceEnemySpawnSystem(game.ecs, game, this);
		this.towerDefencePhaseSystem = new TowerDefencePhaseSystem(this);
	}


	@Override
	public void setupAvatars(AbstractEntity player, int playerIdx) {
		player.addComponent(new ShowFloorSelectorComponent());
		player.addComponent(new TowerDefencePlayerData());
		player.addComponent(new CanBuildComponent());
	}


	@Override
	public void setBackgroundColour() {
		if (towerDefencePhaseSystem.spawn_phase) {
			Gdx.gl.glClearColor(0, 0, 0, 1);
		} else {
			Gdx.gl.glClearColor(0.4f, 0.4f, 0.4f, 1);
		}
	}


	@Override
	public void load() {
		loadMapFromFile("towerdefence/map1.csv");
	}


	private void loadMapFromFile(String file) {
		checkAltarSystem = new CheckAltarSystem(game.ecs, game);

		String str = Gdx.files.internal(file).readString();
		String[] str2 = str.split("\n");

		this.map_width = str2[0].split("\t").length;
		this.map_height = str2.length;

		game.mapData = new MapData(map_width, map_height);

		int row = 0;
		for (String s : str2) {
			s = s.trim();
			if (s.length() > 0 && s.startsWith("#") == false) {
				String cells[] = s.split("\t");
				for (int col=0 ; col<cells.length ; col++) {
					game.mapData.map[col][row] = new MapSquare(game.ecs);

					String cell = cells[col];
					String tokens[] = cell.split(Pattern.quote("+"));
					for (String token : tokens) {
						if (token.equals("P")) { // Start pos
							this.startPositions.add(new GridPoint2Static(col, row));
							Floor floor = new Floor(game.ecs, "towerdefence/textures/corridor.jpg", col, row, 1, 1, false);
							game.ecs.addEntity(floor);
							game.mapData.map[col][row].entity.addComponent(new CanBuildOnComponent());
						} else if (token.equals("W")) { // Wall
							game.mapData.map[col][row].blocked = true;
							Wall wall = new Wall(game.ecs, "towerdefence/textures/ufo2_03.png", col, 0, row, false);
							game.ecs.addEntity(wall);
						} else if (token.equals("C")) { // Chasm
							game.mapData.map[col][row].blocked = true;
						} else if (token.equals("F")) { // Floor - can build
							Floor floor = new Floor(game.ecs, "towerdefence/textures/corridor.jpg", col, row, 1, 1, false);
							game.ecs.addEntity(floor);
							game.mapData.map[col][row].entity.addComponent(new CanBuildOnComponent());
						} else if (token.equals("E")) { // Empty floor - cannot build
							Floor floor = new Floor(game.ecs, "towerdefence/textures/wall2.jpg", col, row, 1, 1, false);
							game.ecs.addEntity(floor);
							if (NumberFunctions.rnd(1,  5) == 1) {
								AbstractEntity coin = TowerDefenceEntityFactory.createCoin(game.ecs, col+.5f, row+.5f);
								game.ecs.addEntity(coin);
							}
						} else if (token.equals("D")) { // Centre for defending!
							targetPos = new GridPoint2Static(col, row);
							Floor floor = new Floor(game.ecs, "towerdefence/textures/wall2.jpg", col, row, 1, 1, false);
							game.ecs.addEntity(floor);							
							AbstractEntity altar = TowerDefenceEntityFactory.createAltar(game.ecs, col, row);
							game.ecs.addEntity(altar);
							checkAltarSystem.altars.add(altar);
						} else if (token.equals("S")) { // Spawn point
							Floor floor = new Floor(game.ecs, "towerdefence/textures/wall2.jpg", col, row, 1, 1, false);
							game.ecs.addEntity(floor);
							spawnEnemiesSystem.enemy_spawn_points.add(new GridPoint2Static(col, row));
						} else {
							throw new RuntimeException("Unknown cell type: " + token);
						}
					}
				}
				row++;
			}
		}
	}


	@Override
	public void addSystems(BasicECS ecs) {
		ecs.addSystem(new ShowFloorSelectorSystem(ecs));
		ecs.addSystem(new BuildDefenceSystem(ecs, game));
		ecs.addSystem(new TurretSystem(ecs, game));
		ecs.addSystem(new TowerDefenceEnemySystem(ecs, game, targetPos));
		ecs.addSystem(new CollectCoinsSystem(ecs, game));
		ecs.addSystem(new TowerDefenceBulletSystem(ecs));
		ecs.addSystem(checkAltarSystem);
		ecs.addSystem(this.towerDefencePhaseSystem);
	}


	@Override
	public void update() {
		game.ecs.processSystem(ShowFloorSelectorSystem.class);
		//spawnEnemiesSystem.process();
		game.ecs.processSystem(BuildDefenceSystem.class);
		game.ecs.processSystem(TurretSystem.class);
		game.ecs.processSystem(TowerDefenceEnemySystem.class);
		game.ecs.processSystem(CollectCoinsSystem.class);
		game.ecs.processSystem(TowerDefenceBulletSystem.class);
		//game.ecs.processSystem(CheckAltarSystem.class);
		this.checkAltarSystem.process();
		//game.ecs.processSystem(TowerDefencePhaseSystem.class);
		this.towerDefencePhaseSystem.process();
	}


	public void renderUI(SpriteBatch batch2d, int viewIndex) {
		AbstractEntity playerAvatar = game.players[viewIndex];
		TowerDefencePlayerData tc = (TowerDefencePlayerData)playerAvatar.getComponent(TowerDefencePlayerData.class);
		if (tc != null) {
			game.font_med.setColor(1, 1, 1, 1);
			game.font_med.draw(batch2d, "Coins: " + tc.coins, 10, game.font_med.getLineHeight()*2);
			game.font_med.draw(batch2d, "Level: " + levelNum, 10, game.font_med.getLineHeight()*3);
		}
	}


	@Override
	public void renderHelp(SpriteBatch batch2d, int viewIndex) {
		game.font_med.setColor(1, 1, 1, 1);
		int x = (int)(Gdx.graphics.getWidth()*0.4);
		int y = (int)(Gdx.graphics.getHeight()*0.8);
		for (String s : this.instructions) {
			game.font_med.draw(batch2d, s, x, y);
			y -= this.game.font_med.getLineHeight();
		}
	}



	public boolean isDismanstlePressed(IInputMethod input) {
		if (input instanceof MouseAndKeyboardInputMethod) {
			return input.isKeyJustPressed(Keys.X);
		} else if (input instanceof ControllerInputMethod) {
			return input.isSquarePressed();
		} else if (input instanceof NoInputMethod) {
			return false;
		} else {
			throw new RuntimeException("Unknown input type");
		}

	}
}