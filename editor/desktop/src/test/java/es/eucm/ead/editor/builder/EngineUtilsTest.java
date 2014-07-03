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
package es.eucm.ead.editor.builder;

import es.eucm.ead.engine.EngineTest;
import es.eucm.ead.engine.entities.EngineEntity;
import es.eucm.ead.engine.processors.renderers.ShapeRendererProcessor;
import es.eucm.ead.engine.utils.EngineUtils;
import es.eucm.ead.schema.data.shape.Rectangle;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schema.renderers.ShapeRenderer;
import es.eucm.ead.schemax.Layer;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by Javier Torrente on 1/07/14.
 */
public class EngineUtilsTest extends EngineTest {

	@Before
	public void setup() {
		componentLoader.registerComponentProcessor(ShapeRenderer.class,
				new ShapeRendererProcessor(gameLoop));
	}

	@Test
	public void testDistances() {
		// Vertical rectangles
		EngineEntity rect1a = buildRectEntity(10, 10, 100, 100);
		EngineEntity rect1b = buildRectEntity(10, 310, 100, 100);
		assertEquals(200, EngineUtils.getFloatBorderDistance(rect1a, rect1b), 0.00F);

		// Horizontal rectangles
		EngineEntity rect2a = buildRectEntity(10, 10, 100, 100);
		EngineEntity rect2b = buildRectEntity(310, 10, 100, 100);
		assertEquals(200, EngineUtils.getFloatBorderDistance(rect2a, rect2b), 0.00F);

		// Tangential rectangles
		EngineEntity rect3a = buildRectEntity(10, 50, 50, 50);
		EngineEntity rect3b = buildRectEntity(60, 0, 50, 50);
		assertEquals(0, EngineUtils.getFloatBorderDistance(rect3a, rect3b), 0.00F);

		// Internal rectangle
		EngineEntity rect4a = buildRectEntity(0, 0, 100, 100);
		EngineEntity rect4b = buildRectEntity(10, 10, 80, 80);
		assertEquals(-1, EngineUtils.getFloatBorderDistance(rect4a, rect4b), 0.00F);
	}

	private EngineEntity buildRectEntity(float x, float y, float width,
			float height) {
		ModelEntity modelEntity = new ModelEntity();
		modelEntity.setX(x);
		modelEntity.setY(y);
		Rectangle rectangle = new Rectangle();
		rectangle.setWidth((int) width);
		rectangle.setHeight((int) height);

		ShapeRenderer shapeRenderer = new ShapeRenderer();
		shapeRenderer.setShape(rectangle);
		shapeRenderer.setPaint("FFFFFF");
		modelEntity.getComponents().add(shapeRenderer);
		EngineEntity entity = entitiesLoader.toEngineEntity(modelEntity);
		gameView.addEntityToLayer(Layer.SCENE_CONTENT, entity);
		return entity;
	}
}
