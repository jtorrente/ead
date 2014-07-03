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

package es.eucm.ead.schema.components;

import javax.annotation.Generated;

/**
 * Makes the visibility of the parent entity depend on a given condition. When
 * the condition is evaluated to true, the entity will be shown in screen and
 * touchable.
 * 
 */
@Generated("org.jsonschema2pojo")
public class TrackEntity extends ModelComponent {

	/**
	 * An expression that must return the entity to chase after. If multiple
	 * entities are returned, the first one will be selected. (Required)
	 * 
	 */
	private String target;
	/**
	 * Amount of pixels the x coordinate must be increased when the target
	 * entity moves along the x axis.
	 * 
	 */
	private float speedX = 1.0F;
	/**
	 * Amount of pixels the y coordinate must be increased when the target
	 * entity moves along the y axis.
	 * 
	 */
	private float speedY = 1.0F;

	/**
	 * An expression that must return the entity to chase after. If multiple
	 * entities are returned, the first one will be selected. (Required)
	 * 
	 */
	public String getTarget() {
		return target;
	}

	/**
	 * An expression that must return the entity to chase after. If multiple
	 * entities are returned, the first one will be selected. (Required)
	 * 
	 */
	public void setTarget(String target) {
		this.target = target;
	}

	/**
	 * Amount of pixels the x coordinate must be increased when the target
	 * entity moves along the x axis.
	 * 
	 */
	public float getSpeedX() {
		return speedX;
	}

	/**
	 * Amount of pixels the x coordinate must be increased when the target
	 * entity moves along the x axis.
	 * 
	 */
	public void setSpeedX(float speedX) {
		this.speedX = speedX;
	}

	/**
	 * Amount of pixels the y coordinate must be increased when the target
	 * entity moves along the y axis.
	 * 
	 */
	public float getSpeedY() {
		return speedY;
	}

	/**
	 * Amount of pixels the y coordinate must be increased when the target
	 * entity moves along the y axis.
	 * 
	 */
	public void setSpeedY(float speedY) {
		this.speedY = speedY;
	}

}
