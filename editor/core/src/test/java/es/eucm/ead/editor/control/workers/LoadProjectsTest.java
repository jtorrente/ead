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
package es.eucm.ead.editor.control.workers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import es.eucm.ead.editor.control.actions.editor.ExecuteWorker;
import es.eucm.ead.editor.control.workers.Worker.WorkerListener;
import es.eucm.ead.editor.model.Q;
import es.eucm.ead.editor.platform.MockPlatform;
import es.eucm.ead.schema.editor.components.GameData;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schemax.ModelStructure;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class LoadProjectsTest extends WorkerTest implements WorkerListener {

	private Array<String> paths = new Array<String>();

	@Override
	public void testWorker() {
		MockPlatform platform = (MockPlatform) controller.getPlatform();
		FileHandle temp = Gdx.files.absolute(platform.createTempFile(true)
				.getAbsolutePath());
		platform.setDefaultProjectsFolder(temp.path());
		for (int i = 0; i < 10; i++) {
			createGame(temp, i);
		}
		controller.action(ExecuteWorker.class, LoadProjects.class, this);
	}

	@Override
	public void asserts() {
		assertEquals(paths.size, 0);
	}

	@Override
	public void start() {

	}

	@Override
	public void result(Object... results) {
		assertEquals(paths.removeIndex(0), results[0]);
		assertEquals(paths.removeIndex(0), results[2]);
	}

	@Override
	public void done() {
	}

	@Override
	public void error(Throwable ex) {
		fail("Exception thrown" + ex);
	}

	@Override
	public void cancelled() {

	}

	private void createGame(FileHandle temp, int i) {
		String gamePath = "game" + i + Math.random();

		FileHandle gameFolder = temp.child(gamePath);
		gameFolder.mkdirs();

		ModelEntity game = new ModelEntity();
		Q.getComponent(game, GameData.class).setInitialScene("s.json");

		controller.getEditorGameAssets().toJson(game, null,
				gameFolder.child(ModelStructure.GAME_FILE));

		String thumbnailPath = Q.getThumbnailPath("s.json");
		ModelEntity scene = new ModelEntity();

		controller.getEditorGameAssets().toJson(scene, null,
				gameFolder.child("s.json"));

		paths.add(temp.child(gamePath).path() + "/" + ModelStructure.GAME_FILE);
		paths.add(temp.child(gamePath).child(thumbnailPath).path());
	}
}
