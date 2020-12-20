package com.scs.splitscreenfps.game.entities.monstermaze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.scs.basicecs.AbstractEntity;
import com.scs.basicecs.BasicECS;
import com.scs.splitscreenfps.game.components.CollidesComponent;
import com.scs.splitscreenfps.game.components.HasDecal;
import com.scs.splitscreenfps.game.components.HasDecalCycle;
import com.scs.splitscreenfps.game.components.PositionComponent;
import com.scs.splitscreenfps.game.components.monstermaze.MonsterMazeExitComponent;

import ssmith.libgdx.GraphicsHelper;

public class MonsterMazeExit extends AbstractEntity {

	public MonsterMazeExit(BasicECS ecs, int map_x, int map_y) {
		super(ecs, MonsterMazeExit.class.getSimpleName());
		
		//PositionData posData = new PositionData((map_x*Game.UNIT)-(Game.UNIT/2), (map_y*Game.UNIT)-(Game.UNIT/2));
		PositionComponent posData = new PositionComponent((map_x)+(0.5f), (map_y)+(0.5f));
		this.addComponent(posData);

		HasDecal hasDecal = new HasDecal();
		Texture tex = new Texture(Gdx.files.internal("monstermaze/exit1.png"));
		TextureRegion tr = new TextureRegion(tex, 0, 0, tex.getWidth(), tex.getHeight());
        hasDecal.decal = Decal.newDecal(tr, true);
        hasDecal.decal.setScale(1f / tr.getRegionWidth());
        hasDecal.decal.setPosition(posData.position);
        hasDecal.faceCamera = true;
        hasDecal.dontLockYAxis = true;        
        this.addComponent(hasDecal);

        HasDecalCycle cycle = new HasDecalCycle(.5f, 2);
        cycle.decals[0] = hasDecal.decal;
        cycle.decals[1] = GraphicsHelper.DecalHelper("monstermaze/exit2.png", 1f);
        this.addComponent(cycle);
        
		this.addComponent(new MonsterMazeExitComponent());

        this.addComponent(new CollidesComponent(false, .3f));//new MyBoundingBox(posData.position, .3f, .3f, .3f)));
	}

}
