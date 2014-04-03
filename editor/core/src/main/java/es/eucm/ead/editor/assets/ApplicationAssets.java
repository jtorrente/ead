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
package es.eucm.ead.editor.assets;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetLoaderParameters.LoadedCallback;
import com.badlogic.gdx.assets.loaders.SkinLoader.SkinParameter;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.SerializationException;
import es.eucm.ead.editor.control.Preferences;
import es.eucm.ead.editor.control.appdata.ReleaseInfo;
import es.eucm.ead.engine.Assets;

/**
 * This asset manager is meant to deal with the editor's own assets. That is,
 * for example, the preferences, the skin and the i18n files for the
 * application.
 * 
 * For managing the game's assets, use {@link EditorGameAssets} instead.
 */
public class ApplicationAssets extends Assets {

	/**
	 * Default name for the editor's default preferences. These preferences are
	 * loaded and added to the {@link es.eucm.ead.editor.control.Preferences}
	 * object in case they were not stored persistently
	 */
	private static final String DEFAULT_PREFERENCES_FILE = "preferences.json";

	/**
	 * Id to let libgdx identify the user-generated prefs to be loaded. In
	 * desktop, this usually matches the name of the preferences file which is
	 * stored in user_folder/.prefs
	 */
	public static final String PREFERENCES_NAME = "eadeditor";

	/**
	 * Location of the file with the
	 * {@link es.eucm.ead.editor.control.appdata.ReleaseInfo} object used to
	 * identify the version of the editor
	 */
	public static final String RELEASE_FILE = "appdata/release.json";

	public static final String SKINS_PATH = "skins/";

	public static final String SKIN_FILE = "/skin.json";

	public static final String SKIN_ATLAS = "/skin.atlas";

	public static final String DEFAULT_SKIN = "default";

	/**
	 * Current UI for the editor
	 */
	private Skin skin;

	/**
	 * This field serves a similar purpose to static field {@link #RELEASE_FILE}
	 * . Both fields are kept for two reasons: 1) [Futurible] the actual path of
	 * this file may change depending on the platform and therefore it may be
	 * needed to assign the path of the release file dynamically from a set of
	 * static options (e.g. MAC OSX, WINDOWS, LINUX)
	 * 
	 * 2) [Testing] This allows modifying the field by reflection in tests.
	 */
	private String releaseFile;

	private LoadedCallback callback = new LoadedCallback() {
		@Override
		public void finishedLoading(
				com.badlogic.gdx.assets.AssetManager assetManager,
				String fileName, Class type) {
			if (type == Skin.class) {
				skin = assetManager.get(fileName, Skin.class);
			}
		}
	};

	/**
	 * Creates an assets handler
	 * 
	 * @param files
	 *            object granting access to files
	 */
	public ApplicationAssets(Files files) {
		this(files, DEFAULT_SKIN);
	}

	/**
	 * Creates an assets handler
	 * 
	 * @param files
	 *            object granting access to files
	 * @param skin
	 *            the Skin name you want to be loaded initially
	 */
	public ApplicationAssets(Files files, String skin) {
		super(files);
		setSkin(skin);
		releaseFile = RELEASE_FILE;
	}

	/**
	 * 
	 * @return returns the current skin for the UI
	 */
	public Skin getSkin() {
		return skin;
	}

	/**
	 * Loads the skin with the given name. It will be necessary to rebuild the
	 * UI to see changes reflected
	 * 
	 * @param skinName
	 *            the skin name
	 */
	public void setSkin(String skinName) {
		String pathName = convertNameToPath(skinName + SKIN_FILE, SKINS_PATH,
				false, false);
		if (isLoaded(pathName, Skin.class)) {
			skin = get(pathName, Skin.class);
		} else {
			SkinParameter skinParameter = new SkinParameter(convertNameToPath(
					skinName + SKIN_ATLAS, SKINS_PATH, false, false));
			skinParameter.loadedCallback = callback;
			load(pathName, Skin.class, skinParameter);
		}
	}

	@Override
	public FileHandle resolve(String path) {
		return files.internal(path);
	}

	/**
	 * Loads and returns the Preferences object for the application. This method
	 * loads user-defined preferences by using libgdx's support. These prefs
	 * work across platforms. In desktop, these preferences are typically stored
	 * in a ".prefs" folder under the user's home dir (e.g.
	 * "C:/Users/Javier/.prefs/{@value #PREFERENCES_NAME}").
	 * 
	 * Before returning these preferences, a set of default properties stored in
	 * json format (in file {@link #DEFAULT_PREFERENCES_FILE}) are added, in
	 * case they are not present.
	 * 
	 * @return The {@link es.eucm.ead.editor.control.Preferences} object for the
	 *         controller.
	 */
	public Preferences loadPreferences() {
		// Load default preferences. The default preferences are stored in a
		// json file under the
		// path DEFAULT_PREFERENCES_FILE (e.g. "preferences.json"). This file
		// looks similar to this:
		/*
		 * { "windowMaximized": true, "windowWidth": 800, "windowHeight": 600 }
		 */
		// Where the part before : means the key for the preference and the part
		// after : is the value
		FileHandle preferencesFile = resolve(DEFAULT_PREFERENCES_FILE);
		ObjectMap<String, Object> defaultPreferences = new Json().fromJson(
				ObjectMap.class, preferencesFile);

		// Load user preferences. For this, libGDX's support is used. LibGDX
		// stores the preferences
		// persistently ina file called PREFERENCES_NAME under a folder ".prefs"
		// that is usually
		// located on the user's main folder
		com.badlogic.gdx.Preferences libGDXPreferences = Gdx.app
				.getPreferences(PREFERENCES_NAME);

		// Combine default and user-defined preferences. All default preferences
		// not present in
		// user-defined prefs are added to the libgdx's object
		for (ObjectMap.Entry<String, Object> e : defaultPreferences.entries()) {
			if (!libGDXPreferences.contains(e.key)) {
				if (e.value.getClass() == Boolean.class) {
					libGDXPreferences.putBoolean(e.key, (Boolean) e.value);
				} else if (e.value.getClass() == Integer.class) {
					libGDXPreferences.putInteger(e.key, (Integer) e.value);
				} else if (e.value.getClass() == Float.class) {
					libGDXPreferences.putFloat(e.key, (Float) e.value);
				} else {
					libGDXPreferences.putString(e.key, e.value.toString());
				}
			}
		}

		// Return the preferences object, a wrapper for libGDX's preferences
		// object
		return new Preferences(libGDXPreferences);

	}

	/**
	 * This method retrieves the release file from disk. If anything unexpected
	 * happens and the file cannot be loaded, it just initializes
	 * {@link es.eucm.ead.editor.control.appdata.ReleaseInfo} with a default
	 * one.
	 * 
	 * This method should be used only once, when
	 * {@link es.eucm.ead.editor.control.Controller} is initialized
	 * 
	 * @return The {@link es.eucm.ead.editor.control.appdata.ReleaseInfo} object
	 *         indicating the version of this application.
	 */
	public ReleaseInfo loadReleaseInfo() {
		ReleaseInfo releaseInfo = null;
		FileHandle releaseFH = this.resolve(releaseFile);
		if (releaseFH.exists()) {
			try {
				releaseInfo = this.fromJson(ReleaseInfo.class, releaseFH);
			} catch (SerializationException e) {
				// If this type of exception is thrown, that's because the file
				// was not defined in compliance with
				// the ReleaseInfo json-schema. Log it and create a default
				// releaseInfo later on
				Gdx.app.debug(
						this.getClass().getCanonicalName(),
						"Error parsing the release.json file. Default release object will be used.",
						e);
			}
		} else {
			Gdx.app.debug(this.getClass().getCanonicalName(),
					"release.json file not found. Default release object will be used.");
		}

		// Check if default releaseInfo must be used
		if (releaseInfo == null) {
			releaseInfo = new DefaultReleaseInfo();
		}

		// Check field validity. If appVersion is not found, use default "0.0.0"
		if (releaseInfo.getAppVersion() == null) {
			releaseInfo.setAppVersion("0.0.0");
		}

		// Check field validity. If modelVersion is not found, use default "1"
		if (releaseInfo.getModelVersion() == null) {
			releaseInfo.setModelVersion("1");
		}

		return releaseInfo;
	}

	private static class DefaultReleaseInfo extends ReleaseInfo {
		public DefaultReleaseInfo() {
			setAppVersion("0.0.0");
			setModelVersion("1");
			setReleaseType(ReleaseType.NIGHTLY);
			setDev(false);
		}
	}
}
