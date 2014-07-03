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
public class Chase extends TrackEntity {

	/**
	 * This entity must always be between maxDistance and minDistance.
	 * 
	 */
	private float minDistance = 0.0F;
	/**
	 * This entity must always be between maxDistance and minDistance.
	 * 
	 */
	private float maxDistance = 0.0F;
	/**
	 * This entity must always be between maxDistance and minDistance.
	 * 
	 */
	private boolean relativeSpeed = true;
	/**
	 * If true, distance between entities is measured between centers. If false,
	 * it's measured between borders.
	 * 
	 */
	private boolean centerDistance = false;

	/**
	 * This entity must always be between maxDistance and minDistance.
	 * 
	 */
	public float getMinDistance() {
		return minDistance;
	}

	/**
	 * This entity must always be between maxDistance and minDistance.
	 * 
	 */
	public void setMinDistance(float minDistance) {
		this.minDistance = minDistance;
	}

	/**
	 * This entity must always be between maxDistance and minDistance.
	 * 
	 */
	public float getMaxDistance() {
		return maxDistance;
	}

	/**
	 * This entity must always be between maxDistance and minDistance.
	 * 
	 */
	public void setMaxDistance(float maxDistance) {
		this.maxDistance = maxDistance;
	}

	/**
	 * This entity must always be between maxDistance and minDistance.
	 * 
	 */
	public boolean isRelativeSpeed() {
		return relativeSpeed;
	}

	/**
	 * This entity must always be between maxDistance and minDistance.
	 * 
	 */
	public void setRelativeSpeed(boolean relativeSpeed) {
		this.relativeSpeed = relativeSpeed;
	}

	/**
	 * If true, distance between entities is measured between centers. If false,
	 * it's measured between borders.
	 * 
	 */
	public boolean isCenterDistance() {
		return centerDistance;
	}

	/**
	 * If true, distance between entities is measured between centers. If false,
	 * it's measured between borders.
	 * 
	 */
	public void setCenterDistance(boolean centerDistance) {
		this.centerDistance = centerDistance;
	}

}
