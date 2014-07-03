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
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pools;
import es.eucm.ead.engine.components.positiontracking.ParallaxComponent;
import es.eucm.ead.engine.entities.EngineEntity;
import es.eucm.ead.engine.variables.VariablesManager;

/**
 * Created by Javier Torrente on 2/07/14.
 */
public class ParallaxSystem extends PositionTrackerSystem{
    public ParallaxSystem(VariablesManager variablesManager) {
        super(Family.getFamilyFor(ParallaxComponent.class), variablesManager);
    }

    @Override
    public void processEntity(Entity entity, float deltaTime) {
        ParallaxComponent parallaxComponent = entity.getComponent(ParallaxComponent.class);
        if (parallaxComponent.getTrackedEntity() == null){
            updateTarget(parallaxComponent);
        }

        // Check target entity actually moved
        Vector2 targetVelocity = Pools.obtain(Vector2.class);
        getSpeedOfTrackedEntity(parallaxComponent, targetVelocity);
        if (!MathUtils.isEqual(targetVelocity.x, 0, 0.01F)){
            // Calculate how much this entity's position must be adjusted
            float speed = -targetVelocity.x * (1-parallaxComponent.getD());
            ((EngineEntity)entity).getGroup().moveBy(speed, 0);
        }

        // Update position
        parallaxComponent.rememberPosition();
    }
}
