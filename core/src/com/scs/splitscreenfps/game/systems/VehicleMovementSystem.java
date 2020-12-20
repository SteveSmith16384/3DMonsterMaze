package com.scs.splitscreenfps.game.systems;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.scs.basicecs.AbstractEntity;
import com.scs.basicecs.AbstractEvent;
import com.scs.basicecs.AbstractSystem;
import com.scs.basicecs.BasicECS;
import com.scs.splitscreenfps.game.EventCollision;
import com.scs.splitscreenfps.game.Game;
import com.scs.splitscreenfps.game.components.MovementData;
import com.scs.splitscreenfps.game.components.PositionComponent;
import com.scs.splitscreenfps.game.components.VehicleComponent;
import com.scs.splitscreenfps.game.entities.PlayersAvatar_Car;
import com.scs.splitscreenfps.game.entities.stockcar.TrackComponent;

public class VehicleMovementSystem extends AbstractSystem {

	private Game game;
	private Vector3 tmpTargetMomentum = new Vector3();

	public VehicleMovementSystem(BasicECS ecs, Game _game) {
		super(ecs, VehicleComponent.class);

		game = _game;
	}


	@Override
	public void processEntity(AbstractEntity entity) {
		VehicleComponent veh = (VehicleComponent)entity.getComponent(VehicleComponent.class);

		// wrap angle
		float MAX = (float)Math.PI*2;
		if (veh.angle_rads > MAX) {
			veh.angle_rads -= MAX;
		} else if (veh.angle_rads < 0) {
			veh.angle_rads += MAX;
		}

		List<AbstractEvent> it = ecs.getEvents(EventCollision.class);
		for (AbstractEvent e : it) {
			EventCollision evt = (EventCollision)e;
			if (evt.movingEntity == entity) {
				veh.current_speed = 0;
				return;
			}
		}

		PositionComponent pos = (PositionComponent)entity.getComponent(PositionComponent.class);
		TrackComponent track = (TrackComponent)game.mapData.map[(int)pos.position.x][(int)pos.position.z].entity.getComponent(TrackComponent.class);
		float this_max_speed = veh.max_speed * track.max_speed;
		float this_traction = veh.traction * track.traction * Gdx.graphics.getDeltaTime();

		if (veh.current_speed > this_max_speed) { // todo - check momentum instead
			veh.current_speed = this_max_speed;
		} else if (veh.current_speed < -this_max_speed) {
			veh.current_speed = -this_max_speed;
		} else {
			// set speed to 0 if close enough
			if (Math.abs(veh.current_speed) < PlayersAvatar_Car.ACC * .5f * Gdx.graphics.getDeltaTime()) {
				veh.current_speed = 0;
			}			
		}

		MovementData movementData = (MovementData)entity.getComponent(MovementData.class);
		tmpTargetMomentum.set((float)Math.sin(veh.angle_rads), 0, (float)Math.cos(veh.angle_rads));
		tmpTargetMomentum.nor().scl(veh.current_speed);

		// MODE 1 veh.momentum.lerp(tmpTargetMomentum, traction);

		// MODE 2
		Vector3 diff = new Vector3(tmpTargetMomentum);
		diff.sub(veh.momentum);
		float length = diff.len();
		if (length != 0) {
			//Settings.p("Diff=" + length);
			//Settings.p("this_traction=" + this_traction);
			/*if (length < this_traction/200) {
				veh.momentum.set(tmpTargetMomentum);
				Settings.p("SET!");
			} else {*/
			veh.momentum.add(diff.nor().scl(this_traction));
			//}
		} else {
			veh.momentum.set(0, 0, 0);
		}
		movementData.offset.add(veh.momentum);

	}

}
