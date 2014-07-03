package es.eucm.ead.engine.systems.positiontracking;

import ashley.core.Entity;
import ashley.core.Family;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pools;
import es.eucm.ead.engine.components.positiontracking.TrackEntityComponent;
import es.eucm.ead.engine.entities.EngineEntity;
import es.eucm.ead.engine.variables.VariablesManager;

/**
 * Created by Javier Torrente on 3/07/14.
 */
public class TrackEntitySystem extends PositionTrackerSystem{
    public TrackEntitySystem(VariablesManager variablesManager) {
        super(Family.getFamilyFor(TrackEntityComponent.class), variablesManager);
    }

    @Override
    public void processEntity(Entity entity, float deltaTime) {
        TrackEntityComponent trackEntityComponent = entity.getComponent(TrackEntityComponent.class);
        updateTarget(trackEntityComponent);

        // Check target entity actually moved
        Vector2 targetVelocity = Pools.obtain(Vector2.class);
        getSpeedOfTrackedEntity(trackEntityComponent, targetVelocity);
        if (!MathUtils.isEqual(targetVelocity.len(), 0, 0.1F)){
            // Calculate how much this entity's position must be adjusted
            float speedX = targetVelocity.x * trackEntityComponent.getSpeedX();
            float speedY = targetVelocity.y * trackEntityComponent.getSpeedY();
            ((EngineEntity)entity).getGroup().moveBy(speedX, speedY);
        }

        // Update position
        trackEntityComponent.rememberPosition();

    }
}
