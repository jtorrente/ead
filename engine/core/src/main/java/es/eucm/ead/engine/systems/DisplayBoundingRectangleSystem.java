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
package es.eucm.ead.engine.systems;

import ashley.core.Component;
import ashley.core.Entity;
import ashley.core.Family;
import ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Rectangle;
import es.eucm.ead.engine.ComponentLoader;
import es.eucm.ead.engine.GameLoop;
import es.eucm.ead.engine.GameView;
import es.eucm.ead.engine.components.DisplayBoundingRectangleComponent;
import es.eucm.ead.engine.components.TagsComponent;
import es.eucm.ead.engine.entities.EngineEntity;
import es.eucm.ead.engine.utils.EngineUtils;
import es.eucm.ead.schema.components.Chase;
import es.eucm.ead.schema.renderers.ShapeRenderer;
import es.eucm.ead.schemax.Layer;

/**
 * Created by Javier Torrente on 30/06/14.
 */
public class DisplayBoundingRectangleSystem extends IteratingSystem {

	private static final String TAG_PREFIX = "refentitybbox";

	private int tagCount = 0;

	private GameLoop gameLoop;

	private ComponentLoader componentLoader;

	private GameView gameView;

	public DisplayBoundingRectangleSystem(GameLoop gameLoop, GameView gameView,
			ComponentLoader componentLoader) {
		super(Family.getFamilyFor(DisplayBoundingRectangleComponent.class));
		this.gameLoop = gameLoop;
		this.componentLoader = componentLoader;
		this.gameView = gameView;
	}

	@Override
	public void processEntity(Entity entity, float deltaTime) {
		Rectangle rectangle = EngineUtils
				.getBoundingRectangle((EngineEntity) entity);
		EngineEntity rectEntity = gameLoop.createEntity();
		es.eucm.ead.schema.data.shape.Rectangle r = new es.eucm.ead.schema.data.shape.Rectangle();
		r.setWidth((int) rectangle.width);
		r.setHeight((int) rectangle.height);
		ShapeRenderer shapeRenderer = new ShapeRenderer();
		shapeRenderer.setShape(r);
		shapeRenderer.setPaint("FF000077");
		Component shapeRendererComponent = componentLoader
				.toEngineComponent(shapeRenderer);
		componentLoader.addComponent(rectEntity, shapeRendererComponent);
		Chase chase = new Chase();
		TagsComponent tagsComponent = gameLoop.addAndGetComponent(entity,
				TagsComponent.class);
		tagsComponent.getTags().add(TAG_PREFIX + (tagCount++));
		chase.setTarget("(collection (hastag $entity s" + TAG_PREFIX
				+ (tagCount - 1) + "))");
		chase.setSpeedX(1.0F);
		chase.setSpeedY(1.0F);
		chase.setMaxDistance(0);
		chase.setMinDistance(Float.NEGATIVE_INFINITY);
		componentLoader.addComponent(rectEntity,
				componentLoader.toEngineComponent(chase));
		gameLoop.addEntity(rectEntity);
		gameView.addEntityToLayer(Layer.SCENE_CONTENT, rectEntity);
		rectEntity.getGroup().setX(rectangle.getX());
		rectEntity.getGroup().setY(rectangle.getY());

		entity.remove(DisplayBoundingRectangleComponent.class);
	}
}
