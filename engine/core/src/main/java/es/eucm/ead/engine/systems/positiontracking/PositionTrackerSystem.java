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

import ashley.core.Family;
import ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;
import es.eucm.ead.engine.components.positiontracking.PositionTrackerComponent;
import es.eucm.ead.engine.entities.EngineEntity;
import es.eucm.ead.engine.utils.EngineUtils;
import es.eucm.ead.engine.variables.VariablesManager;

/**
 * Created by Javier Torrente on 2/07/14.
 */
public abstract class PositionTrackerSystem extends IteratingSystem{

    protected VariablesManager variablesManager;

    public PositionTrackerSystem(Family family, VariablesManager variablesManager) {
        super(family);
        this.variablesManager = variablesManager;
    }

    protected void updateTarget(PositionTrackerComponent positionTracker) {
        // Reevaluate target
        Object result = variablesManager.evaluateExpression(positionTracker
                .getTarget());
        EngineEntity trackedEntity = null;

        if (result instanceof EngineEntity) {
            trackedEntity = (EngineEntity) result;
        } else if (result instanceof Array
                && ((Array) result).get(0) instanceof EngineEntity) {
            trackedEntity = (EngineEntity) (((Array) result).get(0));
        }

        if (trackedEntity != positionTracker.getTrackedEntity()) {
            positionTracker.updateTarget(trackedEntity);
        }
    }

    protected void move(EngineEntity chasing, EngineEntity target, float speedX, float speedY){
        /* Calculate distance vector. It is a vector that has the direction of distance vector between target and chasing entities, and module equals to the speed calculated*/
        Vector2 distanceVector = Pools.obtain(Vector2.class);
        EngineUtils.getVectorCenterDistance(target, chasing,
                distanceVector);
        distanceVector.scl(speedX / distanceVector.len(), speedY
                / distanceVector.len());

        // Move
        chasing.getGroup().moveBy(distanceVector.x, distanceVector.y);
        Pools.free(distanceVector);
    }

    protected void getSpeedOfTrackedEntity(PositionTrackerComponent positionTrackerComponent, Vector2 trackedEntitySpeed){
        trackedEntitySpeed.set(positionTrackerComponent.getTrackedEntity().getGroup().getX(),
                positionTrackerComponent.getTrackedEntity().getGroup().getY()).sub(positionTrackerComponent.getLastX(), positionTrackerComponent.getLastY());
    }
}
