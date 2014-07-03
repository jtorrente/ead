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
package es.eucm.ead.engine.components.positiontracking;

/**
 * Created by Javier Torrente on 29/06/14.
 */
public class ChaseComponent extends TrackEntityComponent {

    /**
     * This entity must always be between maxDistance and minDistance.
     *
     */
    private float minDistance = 0;
    /**
     * This entity must always be between maxDistance and minDistance.
     *
     */
    private float maxDistance = 0;
    /**
     * This entity must always be between maxDistance and minDistance.
     *
     */
    private boolean relativeSpeed = true;

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
	public float getMaxDistance() {
		return maxDistance;
	}

	/**
	 * This entity must always be between maxDistance and minDistance.
	 * 
	 */
	public boolean isRelativeSpeed() {
		return relativeSpeed;
	}

    public boolean isCenterDistance() {
        return centerDistance;
    }

    public void set(String target, float speedX, float speedY,
                    boolean isRelativeSpeed, boolean isCenterDistance, float minDistance, float maxDistance) {
        super.set(target, speedX, speedY);
        this.relativeSpeed = isRelativeSpeed;
        this.centerDistance = isCenterDistance;
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
    }
}
