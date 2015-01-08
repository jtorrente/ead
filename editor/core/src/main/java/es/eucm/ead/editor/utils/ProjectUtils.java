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
package es.eucm.ead.editor.utils;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

import es.eucm.ead.editor.model.Model;
import es.eucm.ead.schemax.ModelStructure;

/**
 * Some useful methods to deal with file system and projects
 */
public class ProjectUtils {

	public static final String ZIP_EXTENSION = "zip";

	private static final Array<String> IMAGE_EXTENSIONS = new Array<String>(
			new String[] { "jpg", "jpeg", "png", "gif", "bmp" });

	/**
	 * @return an array with paths of all the projects inside the given folder
	 */
	public static Array<String> findProjects(FileHandle folder) {
		Array<String> projects = new Array<String>();
		findProjects(folder, projects);
		projects.sort();
		return projects;
	}

	private static void findProjects(FileHandle folder, Array<String> projects) {
		for (FileHandle child : folder.list()) {
			if (child.isDirectory()) {
				if (child.child("game.json").exists()) {
					projects.add(child.path());
				} else {
					findProjects(child, projects);
				}
			}
		}
	}

	/**
	 * 
	 * @return a valid project name.
	 */
	public static String createProjectName() {
		return "mokap" + System.currentTimeMillis() + MathUtils.random(100);
	}

	public static String newSceneId(Model model) {
		int count = 0;
		String prefix = ModelStructure.SCENES_PATH + "scene";
		String id;
		do {
			id = prefix + count++ + ".json";
		} while (model.getResource(id) != null);
		return id;
	}

	/**
	 * @param directory
	 *            the directory where we want to search the new file.
	 * @return the first {@link FileHandle} that doesn't exist. Note that the
	 *         result will probably end with an index. E.g. {@code file} name:
	 *         "image", extension: ".jpg" => result: "image4.jpg" if that is the
	 *         first found file that doesn't exist.
	 */
	public static FileHandle getNonExistentFile(FileHandle directory,
			String name, String extension) {

		if (!extension.isEmpty() && !extension.startsWith(".")) {
			extension = "." + extension;
		}

		FileHandle result;
		result = directory.child(name + extension);
		if (result.exists()) {
			int count = 2;
			do {
				result = directory.child(name + count++ + extension);
			} while (result.exists());
		}

		return result;
	}

	/**
	 * 
	 * @return true if the file is a supported image that can be loaded.
	 */
	public static boolean isSupportedImage(FileHandle file) {
		if (file.isDirectory()) {
			return false;
		}
		String fileName = file.name();
		for (String imageExtension : IMAGE_EXTENSIONS) {
			if (fileName.endsWith(imageExtension)) {
				return true;
			}
		}
		return false;
	}
}
