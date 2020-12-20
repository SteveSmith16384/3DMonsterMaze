package com.scs.splitscreenfps.game.entities.deathchase;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.scs.basicecs.AbstractEntity;
import com.scs.splitscreenfps.game.Game;
import com.scs.splitscreenfps.game.components.CollidesComponent;
import com.scs.splitscreenfps.game.components.HasDecal;
import com.scs.splitscreenfps.game.components.PositionComponent;

import ssmith.lang.NumberFunctions;
import ssmith.libgdx.GraphicsHelper;

public class DeathchaseEntityFactory {

	public DeathchaseEntityFactory() {
	}


	public static AbstractEntity createTree(Game game, float map_x, float map_z) {
		AbstractEntity plant = new AbstractEntity(game.ecs, "Tree");
		
		PositionComponent posData = new PositionComponent((map_x)+(0.5f), (map_z)+(0.5f));
		plant.addComponent(posData);

		HasDecal hasDecal = new HasDecal();
		//TextureRegion[][] trs = GraphicsHelper.createSheet("deathchase/SimpleTrees_By_Jestan/Crop_Spritesheet.png", 16, 16);
		hasDecal.decal = GraphicsHelper.DecalHelper("deathchase/SimpleTrees_By_Jestan/tree1.png", 1);
		hasDecal.faceCamera = true;
		hasDecal.dontLockYAxis = true;
		hasDecal.decal.transformationOffset = new Vector2(0, -.35f);
		plant.addComponent(hasDecal);

		CollidesComponent cc = new CollidesComponent(true, 0.1f);
		plant.addComponent(cc);
		
		return plant;

	}


	public static AbstractEntity createWeed(Game game, float map_x, float map_z) {
		AbstractEntity plant = new AbstractEntity(game.ecs, "Weed");
		
		PositionComponent posData = new PositionComponent((map_x)+(0.5f), (map_z)+(0.5f));
		plant.addComponent(posData);

		HasDecal hasDecal = new HasDecal();
		int num = 936/24;
		TextureRegion[][] trs = GraphicsHelper.createSheet("deathchase/plants.png", num, 1);
		int idx = NumberFunctions.rnd(0, num-1);
		hasDecal.decal = GraphicsHelper.DecalHelper(trs[idx][0], 1);
		hasDecal.faceCamera = true;
		hasDecal.dontLockYAxis = true;
		//hasDecal.decal.transformationOffset = new Vector2(0, -.35f);
		plant.addComponent(hasDecal);

		return plant;

	}


}
