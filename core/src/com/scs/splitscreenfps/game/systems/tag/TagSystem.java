package com.scs.splitscreenfps.game.systems.tag;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.scs.basicecs.AbstractEntity;
import com.scs.basicecs.AbstractEvent;
import com.scs.basicecs.AbstractSystem;
import com.scs.basicecs.BasicECS;
import com.scs.splitscreenfps.BillBoardFPS_Main;
import com.scs.splitscreenfps.Settings;
import com.scs.splitscreenfps.game.EventCollision;
import com.scs.splitscreenfps.game.Game;
import com.scs.splitscreenfps.game.components.AnimatedComponent;
import com.scs.splitscreenfps.game.components.HasModelComponent;
import com.scs.splitscreenfps.game.components.MovementData;
import com.scs.splitscreenfps.game.components.tag.TagableComponent;
import com.scs.splitscreenfps.game.entities.TextEntity;

import ssmith.lang.NumberFunctions;

/**
 * System for checking if one player has "tagged" another player.
 * @author StephenCS
 *
 */
public class TagSystem extends AbstractSystem {

	public static final long TAG_INTERVAL = 4000;

	private Game game;

	public AbstractEntity currentIt;
	public long lastTagTime = System.currentTimeMillis(); // Can't move at start!

	public TagSystem(BasicECS ecs, Game _game) {
		super(ecs, TagableComponent.class);

		game = _game;
	}


	@Override
	public void processEntity(AbstractEntity it_entity) {
		if (currentIt == null) {
			// Choose random player
			this.currentIt = game.players[NumberFunctions.rnd(0, game.players.length-1)];
			TagableComponent it2 = (TagableComponent)currentIt.getComponent(TagableComponent.class);
			this.swapModel(it2);
		}

		if (it_entity != this.currentIt) {
			return;
		}

		if (System.currentTimeMillis() < this.lastTagTime + TAG_INTERVAL) {
			return;
		}

		// Entity must be the current "it" by this point
		TagableComponent it_tagable = (TagableComponent)it_entity.getComponent(TagableComponent.class);
		it_tagable.timeLeftAsIt -= Gdx.graphics.getDeltaTime();
		
		// Check for winner
		if (it_tagable.timeLeftAsIt <= 0) {
			this.ecs.removeSystem(TagSystem.class);
			game.playerHasLost(it_tagable.player);
		}

		// Check for collisions
		List<AbstractEvent> it = ecs.getEventsForEntity(EventCollision.class, currentIt);
		for (AbstractEvent e : it) {
			tagged((EventCollision)e);
			break;
		}
	}


	private void tagged(EventCollision evt) {
		/*
		CollidesComponent cc = (CollidesComponent)it_entity.getComponent(CollidesComponent.class);
		for (CollisionResult cr : cc.results) {
			AbstractEntity e = cr.collidedWith;*/
		AbstractEntity clean = null;
		AbstractEntity it = null;
		if (evt.movingEntity == currentIt) {
			clean = evt.hitEntity;
			it = evt.movingEntity;
		} else {
			clean = evt.movingEntity;
			it = evt.hitEntity;
		}
		if (clean != null) {
			TagableComponent clean_tagable = (TagableComponent)clean.getComponent(TagableComponent.class);
			if (clean_tagable != null) {
				TagableComponent it_tagable = (TagableComponent)it.getComponent(TagableComponent.class);
				if (it_tagable != null) {
					Settings.p("Player " + clean_tagable.player + " tagged!");

					BillBoardFPS_Main.audio.play("tag/sfx/impactsplat01.mp3");

					// Change "it" player to normal
					this.swapModel(it_tagable);
					// Change normal player to "it"
					this.swapModel(clean_tagable);

					this.currentIt = clean;
					lastTagTime = System.currentTimeMillis();

					game.ecs.addEntity(new TextEntity(ecs, "YOU HAVE BEEN INFECTED", 50, 2, new Color(1, 1f, 0, 1), clean_tagable.playerIdx, 2));

					MovementData movementData = (MovementData)this.currentIt.getComponent(MovementData.class);
					movementData.frozenUntil = System.currentTimeMillis() + TAG_INTERVAL;
				}
			}
		}
	}


	private void swapModel(TagableComponent it_tagable) {
		Settings.p("Swapping model of player #" + it_tagable.player);

		AbstractEntity it_entity = it_tagable.player;
		HasModelComponent hasModel = (HasModelComponent)it_entity.getComponent(HasModelComponent.class);
		//AnimatedForAvatarComponent animatedForAvatarComponent = (AnimatedForAvatarComponent)it_entity.getComponent(AnimatedForAvatarComponent.class);
		AnimatedComponent animatedComponent = (AnimatedComponent)it_entity.getComponent(AnimatedComponent.class);

		// Store current
		TagableComponent tmp = new TagableComponent(null, -1); // temp for swapping vars
		tmp.storedAnimated = animatedComponent;
		//tmp.storedAvatarAnim = animatedForAvatarComponent;
		tmp.storedHasModel = hasModel;

		// Remove current
		it_entity.removeComponent(HasModelComponent.class);
		//it_entity.removeComponent(AnimatedForAvatarComponent.class);
		it_entity.removeComponent(AnimatedComponent.class);

		// Put other ones in
		it_entity.addComponent(it_tagable.storedAnimated);
		//it_entity.addComponent(it_tagable.storedAvatarAnim);
		it_entity.addComponent(it_tagable.storedHasModel);

		it_tagable.storedAnimated = tmp.storedAnimated;
		//it_tagable.storedAvatarAnim = tmp.storedAvatarAnim;
		it_tagable.storedHasModel = tmp.storedHasModel;

		HasModelComponent xxx = (HasModelComponent)it_entity.getComponent(HasModelComponent.class);
		Settings.p("Model is now " + xxx.name);

	}

}
