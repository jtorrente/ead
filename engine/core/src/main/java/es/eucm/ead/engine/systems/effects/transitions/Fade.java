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
package es.eucm.ead.engine.systems.effects.transitions;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;

import es.eucm.ead.engine.systems.effects.transitions.TransitionManager.Transition;

/**
 * Fade transition between the current screen and the next screen.
 */
public class Fade implements Transition {

	private static Fade instance;

	private float duration;
	private boolean fadeCurrentScreen;

	public static Fade init(float duration, boolean fadeCurrentScreen) {
		if (instance == null) {
			instance = new Fade(duration, fadeCurrentScreen);
		} else {
			instance.initialize(duration, fadeCurrentScreen);
		}
		return instance;
	}

	public Fade() {
		this(MathUtils.random(.4f, .6f), MathUtils.randomBoolean());
	}

	public Fade(float duration, boolean fadeCurrentScreen) {
		initialize(duration, fadeCurrentScreen);
	}

	private void initialize(float duration, boolean fadeCurrentScreen) {
		this.fadeCurrentScreen = fadeCurrentScreen;
		this.duration = duration;
	}

	@Override
	public float getDuration() {
		return duration;
	}

	@Override
	public void render(Batch batch, TextureRegion currScreen,
			Region currScreenRegion, TextureRegion nextScreen,
			Region nextScreenRegion, float alpha) {
		alpha = Interpolation.fade.apply(alpha);
		Color color = batch.getColor();
		if (fadeCurrentScreen)
			batch.setColor(1, 1, 1, 1 - alpha);
		batch.draw(currScreen, currScreenRegion.x, currScreenRegion.y,
				currScreenRegion.w, currScreenRegion.h);
		batch.setColor(1, 1, 1, alpha);
		batch.draw(nextScreen, nextScreenRegion.x, nextScreenRegion.y,
				nextScreenRegion.w, nextScreenRegion.h);
		batch.setColor(color);
	}

	@Override
	public void end() {

	}
}