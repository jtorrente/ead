package es.eucm.ead.engine.processors.positiontracking;

import ashley.core.Component;
import es.eucm.ead.engine.GameLoop;
import es.eucm.ead.engine.components.positiontracking.TrackEntityComponent;
import es.eucm.ead.engine.processors.ComponentProcessor;
import es.eucm.ead.schema.components.TrackEntity;

/**
 * Created by Javier Torrente on 3/07/14.
 */
public class TrackEntityProcessor extends ComponentProcessor<TrackEntity>{
    public TrackEntityProcessor(GameLoop gameLoop) {
        super(gameLoop);
    }

    @Override
    public Component getComponent(TrackEntity component) {
        TrackEntityComponent trackEntityComponent = gameLoop.createComponent(TrackEntityComponent.class);
        trackEntityComponent.set(component.getTarget(), component.getSpeedX(), component.getSpeedY());
        return trackEntityComponent;
    }
}
