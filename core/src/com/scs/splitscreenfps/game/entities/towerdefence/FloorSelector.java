package com.scs.splitscreenfps.game.entities.towerdefence;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix3;
import com.scs.basicecs.AbstractEntity;
import com.scs.basicecs.BasicECS;
import com.scs.splitscreenfps.game.components.HasModelComponent;
import com.scs.splitscreenfps.game.components.PositionComponent;

public class FloorSelector extends AbstractEntity {


	public FloorSelector(BasicECS ecs, int playerIdx) {
		super(ecs, FloorSelector.class.getSimpleName());

		this.addComponent(new PositionComponent());

		BlendingAttribute blendingAttribute = new BlendingAttribute();
		blendingAttribute.opacity = 1f;
		
		Texture tex = new Texture("towerdefence/crosshairs.png");
		//tex.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		Material white_material = new Material(TextureAttribute.createDiffuse(tex), blendingAttribute);

		float w = 1f;
		float d = 1f;

		ModelBuilder modelBuilder = new ModelBuilder();
		Model floor = modelBuilder.createRect(
				0f,0f, (float) d,
				(float)w, 0f, (float)d,
				(float)w, 0f, 0f,
				0f,0f,0f,
				1f,1f,1f,
				white_material,
				VertexAttributes.Usage.Position | VertexAttributes.Usage.TextureCoordinates);

		Matrix3 mat = new Matrix3();
		floor.meshes.get(0).transformUV(mat);

		ModelInstance instance = new ModelInstance(floor);

		HasModelComponent model = new HasModelComponent(this.name, instance);
		model.onlyDrawInViewId = playerIdx;
		this.addComponent(model);
		this.hideComponent(HasModelComponent.class); // Don't draw it just yet
	}


}
