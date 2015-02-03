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
package es.eucm.ead.engine.tests.systems.effects;

import com.badlogic.gdx.scenes.scene2d.Group;
import es.eucm.ead.engine.systems.effects.GoSceneExecutor;
import es.eucm.ead.schema.effects.GoScene;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.fail;

public class GoSceneTest extends EffectTest {

	private GoSceneExecutor goSceneExecutor;

	@Before
	public void setUp() {
		super.setUp();
		effectsSystem.registerEffectExecutor(GoScene.class,
				goSceneExecutor = new GoSceneExecutor(entitiesLoader, gameView,
						gameAssets) {
					protected int getScreenWidth(Group layer) {
						return 0;
					}

					protected int getScreenHeight(Group layer) {
						return 0;
					}
				});
	}

	@Test
	public void goNonExistingScene() {
		try {
			GoScene goScene = new GoScene();
			goScene.setSceneId("ñor");
			goSceneExecutor.execute(null, goScene);
			gameAssets.getAssetManager().finishLoading();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Exception should not be thrown");
		}
	}
}
