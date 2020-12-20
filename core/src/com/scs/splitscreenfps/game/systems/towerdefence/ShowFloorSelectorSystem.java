package com.scs.splitscreenfps.game.systems.towerdefence;

import com.scs.basicecs.AbstractEntity;
import com.scs.basicecs.AbstractSystem;
import com.scs.basicecs.BasicECS;
import com.scs.splitscreenfps.game.components.HasModelComponent;
import com.scs.splitscreenfps.game.components.PositionComponent;
import com.scs.splitscreenfps.game.components.towerdefence.ShowFloorSelectorComponent;
import com.scs.splitscreenfps.game.entities.AbstractPlayersAvatar;
import com.scs.splitscreenfps.game.entities.towerdefence.FloorSelector;

public class ShowFloorSelectorSystem extends AbstractSystem {

	public ShowFloorSelectorSystem(BasicECS ecs) {
		super(ecs, ShowFloorSelectorComponent.class);
	}

	/*
	public void setVisible(AbstractEntity entity, boolean b) {
		ShowFloorSelectorComponent sfsc = (ShowFloorSelectorComponent)entity.getComponent(ShowFloorSelectorComponent.class);
		if (sfsc.actually_show == b) {
			return;
		}
		if (b) {
			sfsc.floor_selector.restoreComponent(HasModelComponent.class);
		} else {
			sfsc.floor_selector.hideComponent(HasModelComponent.class);
		}
	}
	 */

	@Override
	public void processEntity(AbstractEntity entity) {
		ShowFloorSelectorComponent sfsc = (ShowFloorSelectorComponent)entity.getComponent(ShowFloorSelectorComponent.class);
		AbstractPlayersAvatar player = (AbstractPlayersAvatar)entity;
		if (sfsc.actually_show) {
			if (sfsc.floor_selector == null) {
				sfsc.floor_selector = new FloorSelector(ecs, player.playerIdx);
			}
			sfsc.floor_selector.restoreComponent(HasModelComponent.class);

			PositionComponent entityPos = (PositionComponent)entity.getComponent(PositionComponent.class);
			PositionComponent selectorPos = (PositionComponent)sfsc.floor_selector.getComponent(PositionComponent.class);
			
			selectorPos.position.set((int)(entityPos.position.x)+1, 0, (int)(entityPos.position.z)+1);

			sfsc.pos.x = (int)(entityPos.position.x + player.camera.direction.x * 2);
			sfsc.pos.y = (int)(entityPos.position.z + player.camera.direction.z * 2);

			selectorPos.position.x = sfsc.pos.x;//-= player.camera.direction.x * 1;
			selectorPos.position.z = sfsc.pos.y;//-= player.camera.direction.z * 1;

		}

	}

}
