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
package es.eucm.ead.editor.control.actions.editor;

import com.badlogic.gdx.files.FileHandle;
import es.eucm.ead.editor.control.actions.EditorAction;
import es.eucm.ead.editor.control.actions.EditorActionException;
import es.eucm.ead.editor.platform.Platform.FileChooserListener;

/**
 * Opens a game. Accepts one path (the path where the game is) as argument. If
 * no argument is passed along, the action uses
 * {@link es.eucm.ead.editor.control.actions.editor.ChooseFile} to ask user to
 * select a folder in the file system
 */
public class OpenGame extends EditorAction implements FileChooserListener {

	public OpenGame() {
		super(true, true, String.class);
	}

	@Override
	public boolean validate(Object... args) {
		return true;
	}

	@Override
	public void perform(Object... args) {
		if (args.length == 0) {
			controller.action(ChooseFile.class, true, this);
		} else {
			fileChosen(args[0].toString());
		}
	}

	@Override
	public void fileChosen(String path) {
		load(path);
	}

	private void load(String gamepath) {
		if (gamepath != null) {
			FileHandle fileHandle = controller.getEditorGameAssets().absolute(
					gamepath);
			if (fileHandle.exists()) {
				controller.loadGame(gamepath);
			} else {
				throw new EditorActionException("Invalid project folder: '"
						+ gamepath + "'");
			}
		}
	}
}