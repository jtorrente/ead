/**
 * eAdventure is a research project of the
 *    e-UCM research group.
 *
 *    Copyright 2005-2014 e-UCM research group.
 *
 *    You can access a list of all the contributors to eAdventure at:
 *          http://e-adventure.e-ucm.es/contributors
 *
 *    e-UCM is a research group of the Department of Software Engineering
 *          and Artificial Intelligence at the Complutense University of Madrid
 *          (School of Computer Science).
 *
 *          CL Profesor Jose Garcia Santesmases 9,
 *          28040 Madrid (Madrid), Spain.
 *
 *          For more info please visit:  <http://e-adventure.e-ucm.es> or
 *          <http://www.e-ucm.es>
 *
 * ****************************************************************************
 *
 *  This file is part of eAdventure
 *
 *      eAdventure is free software: you can redistribute it and/or modify
 *      it under the terms of the GNU Lesser General Public License as published by
 *      the Free Software Foundation, either version 3 of the License, or
 *      (at your option) any later version.
 *
 *      eAdventure is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU Lesser General Public License for more details.
 *
 *      You should have received a copy of the GNU Lesser General Public License
 *      along with eAdventure.  If not, see <http://www.gnu.org/licenses/>.
 */
package es.eucm.ead.engine.systems.positiontracking;

import ashley.core.Entity;
import ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pools;
import es.eucm.ead.engine.components.positiontracking.ChaseComponent;
import es.eucm.ead.engine.entities.EngineEntity;
import es.eucm.ead.engine.utils.EngineUtils;
import es.eucm.ead.engine.variables.VariablesManager;

/**
 * Created by Javier Torrente on 30/06/14.
 */
public class ChaseSystem extends PositionTrackerSystem {

	private static final float CONSTANT = 1.05f;

	public ChaseSystem(VariablesManager variablesManager) {
		super(Family.getFamilyFor(ChaseComponent.class), variablesManager);
	}

	@Override
	public void processEntity(Entity entity, float deltaTime) {
		ChaseComponent chaseComponent = entity
				.getComponent(ChaseComponent.class);
		updateTarget(chaseComponent);
        if (chaseComponent.getTrackedEntity() == null) {
            Gdx.app.debug(
                    "ChaseSystem",
                    "Invalid entity target defined for chase component. Entity will not be processed.");
            return;
        }

		float d = getFloatDistance(chaseComponent, (EngineEntity) entity);//EngineUtils.getFloatBorderDistance(chaseComponent.getTrackedEntity(), (EngineEntity) entity);
        float dif = d-chaseComponent.getMaxDistance();
		if (dif > 0) {
			moveTowards(chaseComponent, (EngineEntity) entity);
            // Recalculate distance after moving
            //d = EngineUtils.getFloatBorderDistance(chaseComponent.getTrackedEntity(), (EngineEntity) entity);
            d = getFloatDistance(chaseComponent, (EngineEntity)entity);
		}
        if (d < chaseComponent.getMinDistance()){
            moveAwayFrom(chaseComponent, (EngineEntity) entity);
            d = getFloatDistance(chaseComponent, (EngineEntity)entity);
        }

        chaseComponent.rememberPosition();
	}

    private void moveTowards(ChaseComponent chaseComponent, EngineEntity entity) {
        /* Calculate target's velocity. Needed just in case the chase effect has relative speed configured. */
        Vector2 targetVelocity = Pools.obtain(Vector2.class);
        getSpeedOfTrackedEntity(chaseComponent, targetVelocity);

        // Calculate scalar speed
        float relSpeedX = targetVelocity.x * chaseComponent.getSpeedX();
        float relSpeedY = targetVelocity.y * chaseComponent.getSpeedY();
        Pools.free(targetVelocity);
        boolean isRelative = chaseComponent.isRelativeSpeed()
                && (!MathUtils.isEqual(relSpeedX, 0, 0.05f) || !MathUtils
                .isEqual(relSpeedY, 0, 0.05f));
        float speedX = Math.abs(isRelative ? relSpeedX : chaseComponent
                .getSpeedX());
        float speedY = Math.abs(isRelative ? relSpeedY : chaseComponent
                .getSpeedY());

        // Move
        move(entity, chaseComponent.getTrackedEntity(), speedX, speedY);
    }

	private void moveAwayFrom(ChaseComponent chaseComponent, EngineEntity entity) {
        float d = getFloatDistance(chaseComponent, entity);
        float speedX =  (d-chaseComponent.getMinDistance())*CONSTANT;
        float speedY =  speedX;

        move(entity, chaseComponent.getTrackedEntity(), speedX, speedY);
	}

    private float getFloatDistance(ChaseComponent chaseComponent, EngineEntity entity){
        if (chaseComponent.isCenterDistance()){
            return EngineUtils.getFloatCenterDistance(chaseComponent.getTrackedEntity(), entity);
        } else {
            return EngineUtils.getFloatBorderDistance(chaseComponent.getTrackedEntity(), entity);
        }
    } 
}
