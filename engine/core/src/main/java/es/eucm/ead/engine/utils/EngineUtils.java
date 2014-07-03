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
package es.eucm.ead.engine.utils;

import ashley.core.Component;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Pools;
import es.eucm.ead.engine.components.renderers.RendererComponent;
import es.eucm.ead.engine.entities.EngineEntity;
import es.eucm.ead.engine.variables.VariablesManager;
import es.eucm.ead.schema.data.Parameter;

public class EngineUtils {

	public static void setParameters(VariablesManager variablesManager,
			Object object, Iterable<Parameter> expressionFields) {
		for (Parameter parameter : expressionFields) {
			Object value = variablesManager.evaluateExpression(parameter
					.getValue());
			variablesManager.getAccessor().set(object, parameter.getName(),
					value);
		}
	}

	private static boolean processRenderer(RendererComponent rendererComponent,
			Group group, Vector2 x, Vector2 y) {
		boolean hasRenderer = false;
		Vector2 tmp = Pools.obtain(Vector2.class);
		if (rendererComponent.getCollider() != null) {
			for (Polygon polygon : rendererComponent.getCollider()) {
				for (int i = 0; i < polygon.getVertices().length; i += 2) {
					tmp.set(polygon.getVertices()[i],
							polygon.getVertices()[i + 1]);
					group.localToStageCoordinates(tmp);
					x.set(Math.min(tmp.x, x.x), Math.max(tmp.x, x.y));
					y.set(Math.min(tmp.y, y.x), Math.max(tmp.y, y.y));
					hasRenderer = true;
				}
			}
		}
		Pools.free(tmp);
		return hasRenderer;
	}

	public static Rectangle getBoundingRectangle(EngineEntity entity) {
		Rectangle box = new Rectangle();
		Vector2 x = Pools.obtain(Vector2.class);
		Vector2 y = Pools.obtain(Vector2.class);
		x.set(Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY);
		y.set(Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY);
		boolean hasRenderer = false;
		for (Component component : entity.getComponents()) {
			if (component instanceof RendererComponent) {
				hasRenderer = processRenderer((RendererComponent) component,
						entity.getGroup(), x, y);
			}
		}

		Vector2 tmp = Pools.obtain(Vector2.class);
		if (!hasRenderer) {
			tmp.set(entity.getGroup().getX(), entity.getGroup().getY());
			entity.getGroup().localToStageCoordinates(tmp);
			tmp.set(entity.getGroup().getWidth() + entity.getGroup().getX(),
					entity.getGroup().getY() + entity.getGroup().getY());
			entity.getGroup().localToStageCoordinates(tmp);
			x.set(Math.min(tmp.x, x.x), Math.max(tmp.x, x.y));
			y.set(Math.min(tmp.y, y.x), Math.max(tmp.y, y.y));
		}

		for (Actor actor : entity.getGroup().getChildren()) {
			if (actor.getUserObject() != null
					&& actor.getUserObject() instanceof EngineEntity) {
				EngineEntity child = (EngineEntity) actor.getUserObject();
				Rectangle childRect = getBoundingRectangle(child);
				x.set(Math.min(childRect.x, x.x),
						Math.max(childRect.x + childRect.width, x.y));
				y.set(Math.min(childRect.y, y.x),
						Math.max(childRect.y + childRect.height, y.y));
			}
		}

		box.x = x.x;
		box.y = y.x;
		box.width = x.y - x.x;
		box.height = y.y - y.x;
		Pools.free(tmp);
		Pools.free(x);
		Pools.free(y);
		return box;
	}

    public static void getVectorCenterDistance(EngineEntity entity1,
                                                  EngineEntity entity2, Vector2 intersection) {
        Rectangle r1 = getBoundingRectangle(entity1);
        Rectangle r2 = getBoundingRectangle(entity2);

        Vector2 c1 = Pools.obtain(Vector2.class);
        Vector2 c2 = Pools.obtain(Vector2.class);
        r1.getCenter(c1);
        r2.getCenter(c2);

        intersection.set(c1.sub(c2));
        Pools.free(c1);
        Pools.free(c2);
    }

	public static boolean getVectorBorderDistance(EngineEntity entity1,
                                                  EngineEntity entity2, Vector2 intersection) {
		Rectangle r1 = getBoundingRectangle(entity1);
		Rectangle r2 = getBoundingRectangle(entity2);

		Vector2 c1 = Pools.obtain(Vector2.class);
		Vector2 c2 = Pools.obtain(Vector2.class);
		r1.getCenter(c1);
		r2.getCenter(c2);

		Vector2 i1 = Pools.obtain(Vector2.class);
		Vector2 i2 = Pools.obtain(Vector2.class);

		boolean intersected = intersectSegmentRectangle(r1, c1, c2, i1)
				&& intersectSegmentRectangle(r2, c1, c2, i2);

		i1.sub(i2);
		intersection.set(i1);
        Pools.free(c1);
        Pools.free(c2);
		Pools.free(i1);
		Pools.free(i2);
		return intersected;
	}

    public static float getFloatCenterDistance(EngineEntity entity1,
                                               EngineEntity entity2) {
        Vector2 intersection = Pools.obtain(Vector2.class);
        getVectorCenterDistance(entity1, entity2, intersection);
        float distance = intersection.len();
        Pools.free(intersection);
        return distance;
    }

	public static float getFloatBorderDistance(EngineEntity entity1,
                                               EngineEntity entity2) {
		Vector2 intersection = Pools.obtain(Vector2.class);
		boolean intersected = getVectorBorderDistance(entity1, entity2, intersection);
		float distance = intersected ? intersection.len() : -1;
		Pools.free(intersection);
		return distance;
	}

	private static boolean intersectSegmentRectangle(Rectangle rect,
			Vector2 start, Vector2 end, Vector2 intersection) {
		boolean intersected = false;
		Vector2 v1 = Pools.obtain(Vector2.class);
		Vector2 v2 = Pools.obtain(Vector2.class);
		Polygon rectPol = Pools.obtain(Polygon.class);
		fromRectangleToPolygon(rect, rectPol);
		for (int i = 0; i < 4 && !intersected; i++) {
			v1.set(rectPol.getVertices()[(2 * i) % 8],
					rectPol.getVertices()[(2 * i + 1) % 8]);
			v2.set(rectPol.getVertices()[(2 * i + 2) % 8],
					rectPol.getVertices()[(2 * i + 3) % 8]);
			intersected = Intersector.intersectSegments(v1, v2, start, end,
					intersection);
		}
		Pools.free(v1);
		Pools.free(v2);
		Pools.free(rectPol);
		return intersected;
	}

	private static void fromRectangleToPolygon(Rectangle rect, Polygon polygon) {
		if (polygon.getVertices().length != 8) {
			polygon.setVertices(new float[8]);
		}
		polygon.getVertices()[0] = rect.x;
		polygon.getVertices()[1] = rect.y;
		polygon.getVertices()[2] = rect.x + rect.width;
		polygon.getVertices()[3] = rect.y;
		polygon.getVertices()[4] = rect.x + rect.width;
		polygon.getVertices()[5] = rect.y + rect.height;
		polygon.getVertices()[6] = rect.x;
		polygon.getVertices()[7] = rect.y + rect.height;
	}
}
