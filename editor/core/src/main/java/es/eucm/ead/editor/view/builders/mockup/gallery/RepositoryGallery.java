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
package es.eucm.ead.editor.view.builders.mockup.gallery;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Net.HttpRequest;
import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.Net.HttpResponseListener;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

import es.eucm.ead.editor.assets.ApplicationAssets;
import es.eucm.ead.editor.assets.EditorGameAssets;
import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.editor.ChangeView;
import es.eucm.ead.editor.control.actions.editor.CombinedAction;
import es.eucm.ead.editor.control.actions.model.AddSceneElement;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.model.Model.ModelListener;
import es.eucm.ead.editor.model.events.LoadEvent;
import es.eucm.ead.editor.view.builders.mockup.edition.SceneEdition;
import es.eucm.ead.editor.view.builders.mockup.menu.InitialScreen;
import es.eucm.ead.editor.view.listeners.ActionOnClickListener;
import es.eucm.ead.editor.view.widgets.mockup.buttons.ElementButton;
import es.eucm.ead.editor.view.widgets.mockup.buttons.ToolbarButton;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.engine.assets.Assets.AssetLoadedCallback;
import es.eucm.ead.schema.components.ModelComponent;
import es.eucm.ead.schema.editor.components.Note;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schema.renderers.Image;

/**
 * The gallery that will display our online elements. Has a top tool bar and a
 * gallery grid. This class handles the default client implementation.
 * <p>
 * The server's directory should have the following layout:
 * </p>
 * 
 * <pre>
 * |elements.json 	<- Correctly formated to parse a {@link List} of {@link ModelEntity ModelEntities}
 * |<strong>thumbnails.zip</strong>	<- All the thumbnails as .png, plus a bindings.properties file.
 * |
 * |resources/		<- Elements resources folder.
 * |	{NAME1}.zip	<- ZIP file containing the resources of the element who's title is "NAME1"(defined in elements.json).
 * |	{NAME2}.zip
 * |	{NAME3}.zip
 * </pre>
 * <p>
 * The <strong>thumbnails.zip</strong> contains the following structure:
 * </p>
 * 
 * <pre>
 * |bindings.properties	<- Java {@link Properties} file where is specified which element(key, title from elements.json) should display which thumbnail(value, e.g. file1.png, file2.png, file3.png).
 * |file1.png		<- This is a small image that will be displayed as a thumbnail of the element with title "X" if bindings.properties has the following line: X=file1.png.
 * |file2.png		<- As a thumbnail, it's size shoudn't be too high.
 * |file3.png
 * </pre>
 */
public class RepositoryGallery extends BaseGallery<ElementButton> {

	/**
	 * 25 s.
	 */
	private static final int TIMEOUT = 25000;

	public static final String NAME = "mockup_repository_gallery";

	private static final String IC_GO_BACK = "ic_goback";
	private static final String ONLINE_REPO_TAG = "RepositoryGallery";

	/*
	 * Client-side cached resources paths.
	 */

	private static final String MOCKUP_PROJECTS_PATH = InitialScreen.MOCKUP_PROJECT_FILE
			.file().getAbsolutePath();

	private static final String REPOSITORY_FOLDER_NAME = "/onlineRepository";
	private static final String REPOSITORY_FOLDER_PATH = MOCKUP_PROJECTS_PATH
			+ REPOSITORY_FOLDER_NAME;

	private static final String RESOURCES_FOLDER_NAME = "/resources";
	private static final String RESOURCES_FOLDER_PATH = REPOSITORY_FOLDER_PATH
			+ RESOURCES_FOLDER_NAME;

	private static final String THUMBNAILS_FOLDER_NAME = "/thumbnails";
	private static final String THUMBNAILS_FOLDER_PATH = REPOSITORY_FOLDER_PATH
			+ THUMBNAILS_FOLDER_NAME;

	private static final String THUMBNAIL_BINDINGS_FILE_NAME = "/bindings.properties";
	private static final String THUMBNAIL_BINDINGS_FILE_PATH = THUMBNAILS_FOLDER_PATH
			+ THUMBNAIL_BINDINGS_FILE_NAME;

	private static final String ELEMENTS_FILE = REPOSITORY_FOLDER_PATH
			+ "/elements.json";

	/*
	 * Server-side resources URLs.
	 */

	/**
	 * Used to download elements.json file that will be parsed as a {@link List}
	 * of {@link ModelEntity ModelEntities}.
	 */
	private static final String REPOSITORY_ELEMENTS_URL = "http://repo-justusevim.rhcloud.com/elements.json";
	/**
	 * Used to download the thumbnails.zip file.
	 */
	private static final String REPOSITORY_THUMBNAILS_URL = "http://repo-justusevim.rhcloud.com/thumbnails.zip";
	/**
	 * Used to download resources, if not aviable locally, when the user decides
	 * to import a {@link ModelEntity} into a scene. The {@link HttpRequest}
	 * will be sent to the following URL: {@value #REPOSITORY_RESOURCES_URL} +
	 * "ELEMENT_TITLE" + ".zip"
	 */
	private static final String REPOSITORY_RESOURCES_URL = "http://repo-justusevim.rhcloud.com/resources/";

	private String previousElements = "";

	/**
	 * Key {@link ModelEntity}'s title, value the {@link ElementButton} that
	 * displays the {@link ModelEntity}. Used to process correctly
	 * {@value #THUMBNAIL_BINDINGS_FILE_NAME}.
	 */
	private final ObjectMap<String, ElementButton> onlineElements = new ObjectMap<String, ElementButton>();

	private TextButton updateButton;
	private boolean listenerAdded = false;

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	protected WidgetGroup centerWidget(Vector2 viewport, I18N i18n, Skin skin,
			Controller controller) {
		setSelectable(false);
		if (!listenerAdded) {
			listenerAdded = true;
			controller.getModel().addLoadListener(
					new ModelListener<LoadEvent>() {

						@Override
						public void modelChanged(LoadEvent event) {
							previousElements = "";
						}

					});
		}
		return super.centerWidget(viewport, i18n, skin, controller);
	}

	@Override
	protected void addActorToHide(Actor actorToHide) {
		// Do nothing because this gallery doesn't have "selecting mode".
	}

	@Override
	protected Button topLeftButton(Vector2 viewport, Skin skin,
			Controller controller) {
		final Button backButton = new ToolbarButton(viewport, skin, IC_GO_BACK);
		backButton.addListener(new ActionOnClickListener(controller,
				ChangeView.class, SceneEdition.NAME));
		return backButton;
	}

	@Override
	protected Button getFirstPositionActor(Vector2 viewport, I18N i18n,
			Skin skin, final Controller controller) {
		updateButton = new TextButton(i18n.m("update.repository"), skin);
		updateButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				update(controller);
			}
		});
		return updateButton;
	}

	private void setButtonDisabled(boolean disabled, Button button) {
		Touchable t = disabled ? Touchable.disabled : Touchable.enabled;

		button.setDisabled(disabled);
		button.setTouchable(t);
	}

	@Override
	protected String getTitle(I18N i18n) {
		return i18n.m("general.mockup.repository");
	}

	@Override
	protected boolean updateGalleryElements(Controller controller,
			Array<ElementButton> elements, Vector2 viewport, I18N i18n,
			Skin skin) {
		elements.clear();
		for (ElementButton elem : onlineElements.values()) {
			elements.add(elem);
		}
		return true;
	}

	@Override
	protected void entityClicked(InputEvent event, final ElementButton target,
			final Controller controller, I18N i18n) {
		// Start editing the clicked element

		final byte data[] = new byte[4096];
		final EditorGameAssets gameAssets = controller.getEditorGameAssets();
		final String elemTitle = target.getTitle();
		final String resourceElementPath = RESOURCES_FOLDER_PATH + "/"
				+ elemTitle;
		final FileHandle zipFile = gameAssets.absolute(resourceElementPath
				+ ".zip");
		sendDownloadRequest(
				REPOSITORY_RESOURCES_URL + elemTitle.replace(" ", "%20")
						+ ".zip", zipFile, controller, data,
				new OnDownloadFinishedListener() {

					@Override
					public void onDownlaodFinished() {
						FileHandle unzippedResource = gameAssets
								.absolute(resourceElementPath);

						unzipFile(zipFile, unzippedResource, data, true);

						// Take special care in order to import correctly the
						// elements
						// from the
						// "/onlineRepository/resource/{elemTitle}/{elem_image.png}"
						// to the project directory.
						ModelEntity elem = target.getSceneElement();
						String localPathToResources = RESOURCES_FOLDER_PATH
								+ "/" + elemTitle;
						List<ModelComponent> comps = elem.getComponents();
						for (int i = 0, length = comps.size(); i < length; ++i) {
							ModelComponent comp = comps.get(i);
							if (comp.getClass() == Image.class) {
								Image renderer = (Image) comp;
								String uri = renderer.getUri();
								uri = localPathToResources
										+ uri.substring(uri.lastIndexOf("/"));
								String newUri = gameAssets.copyToProject(uri,
										Texture.class);
								renderer.setUri(newUri == null ? uri : newUri);
							}
						}

						controller.action(CombinedAction.class,
								AddSceneElement.class,
								new Object[] { target.getSceneElement() },
								ChangeView.class,
								new Object[] { SceneEdition.NAME });
					}
				});
	}

	@Override
	public void initialize(Controller controller) {
		update(controller);
	}

	// ///////////////////////////
	// / CLIENT
	// ///////////////////////////

	/**
	 * Tries to update the repository either by downloading new information when
	 * changes are detected or by loading from local cache.
	 * 
	 * @param controller
	 */
	private void update(final Controller controller) {
		setButtonDisabled(true, updateButton);
		HttpRequest httpRequest = new HttpRequest(Net.HttpMethods.GET);
		httpRequest.setUrl(REPOSITORY_ELEMENTS_URL);
		httpRequest.setContent(null);
		httpRequest.setTimeOut(TIMEOUT);
		Gdx.net.sendHttpRequest(httpRequest, new HttpResponseListener() {

			@Override
			public void handleHttpResponse(final HttpResponse httpResponse) {
				final int statusCode = httpResponse.getStatus().getStatusCode();
				// We are not in main thread right now so we
				// need to post to main thread for UI updates

				if (statusCode != 200) {
					Gdx.app.log(ONLINE_REPO_TAG,
							"An error ocurred since statusCode is not OK, "
									+ httpResponse);
					return;
				}

				final String res = httpResponse.getResultAsString();
				Gdx.app.log(ONLINE_REPO_TAG, "Success");

				if (("".equals(previousElements) && !loadFromLocal(controller,
						res)) || !previousElements.equals(res)) {
					EditorGameAssets gameAssets = controller
							.getEditorGameAssets();
					gameAssets.absolute(ELEMENTS_FILE).writeString(res, false);
					createFromString(res, controller);

					final byte data[] = new byte[4096];
					final FileHandle zipFile = gameAssets
							.absolute(REPOSITORY_FOLDER_PATH
									+ "/thumbnails.zip");
					sendDownloadRequest(REPOSITORY_THUMBNAILS_URL, zipFile,
							controller, data, new OnDownloadFinishedListener() {

								@Override
								public void onDownlaodFinished() {
									FileHandle unzippedThumbnails = controller
											.getEditorGameAssets().absolute(
													THUMBNAILS_FOLDER_PATH);

									unzipFile(zipFile, unzippedThumbnails,
											data, true);

									FileHandle bindings = unzippedThumbnails
											.child(THUMBNAIL_BINDINGS_FILE_NAME);
									processBindings(bindings, controller);
								}
							});
				} else {
					setButtonDisabled(false, updateButton);
				}

				previousElements = res;
			}

			@Override
			public void failed(Throwable t) {
				Gdx.app.log(ONLINE_REPO_TAG,
						"Failed to perform the HTTP Request: ", t);

				if (!loadFromLocal(controller, previousElements)) {
					// TODO show notification to the user informing that we
					// couldn't refresh the elements
				}
			}

			@Override
			public void cancelled() {
				Gdx.app.log(ONLINE_REPO_TAG, "HTTP request cancelled");

			}
		});
	}

	/**
	 * 
	 * @param controller
	 * @param updatedJson
	 *            the most updated {@link #ELEMENTS_FILE} info. Usually the most
	 *            recently downloaded.
	 * @return true if we could load the elements from local path.
	 */
	private boolean loadFromLocal(Controller controller, String updatedJson) {
		EditorGameAssets gameAssets = controller.getEditorGameAssets();
		FileHandle elementsFile = gameAssets.absolute(ELEMENTS_FILE);
		if (elementsFile.exists()) {
			String localJson = elementsFile.readString();
			if (!"".equals(updatedJson) && !localJson.equals(updatedJson)) {
				return false;
			}
			createFromString(localJson, controller);
			processBindings(gameAssets.absolute(THUMBNAIL_BINDINGS_FILE_PATH),
					controller);
			previousElements = updatedJson;
			setButtonDisabled(false, updateButton);
			return true;
		}
		return false;
	}

	/**
	 * Tries to fill the {@link #onlineElements} by creating {@link ModelEntity
	 * ModelEntities} from the jsonString.
	 * 
	 * @param jsonString
	 *            must be correctly formated as a {@link List list of
	 *            ModelEntities}.
	 * @param controller
	 */
	private void createFromString(final String jsonString,
			final Controller controller) {
		EditorGameAssets gameAssets = controller.getEditorGameAssets();

		@SuppressWarnings("unchecked")
		ArrayList<ModelEntity> elems = gameAssets.fromJson(ArrayList.class,
				jsonString);

		onlineElements.clear();
		ApplicationAssets appAssets = controller.getApplicationAssets();
		I18N i18n = appAssets.getI18N();
		Skin skin = appAssets.getSkin();
		for (ModelEntity elem : elems) {
			onlineElements.put(Model.getComponent(elem, Note.class).getTitle(),
					new ElementButton(controller.getPlatform().getSize(), i18n,
							elem, null, skin, controller));
		}
		RepositoryGallery.super.initialize(controller);
	}

	/**
	 * Sends a download request from an URL, saving it to dstFile.
	 * 
	 * @param fromURL
	 * @param dstFile
	 * @param controller
	 * @param data
	 *            a temporal byte array used to write to disk efficiently.
	 * @param listener
	 *            may not be null.
	 */
	private void sendDownloadRequest(String fromURL, final FileHandle dstFile,
			final Controller controller, final byte[] data,
			final OnDownloadFinishedListener listener) {
		HttpRequest httpRequest = new HttpRequest(Net.HttpMethods.GET);
		httpRequest.setUrl(fromURL);
		httpRequest.setContent(null);
		httpRequest.setTimeOut(TIMEOUT);
		Gdx.net.sendHttpRequest(httpRequest, new HttpResponseListener() {

			@Override
			public void handleHttpResponse(final HttpResponse httpResponse) {
				final int statusCode = httpResponse.getStatus().getStatusCode();
				// We are not in main thread right now so we
				// need to post to main thread for UI updates

				if (statusCode != 200) {
					Gdx.app.log(ONLINE_REPO_TAG,
							"An error ocurred since statusCode(" + statusCode
									+ ") is not OK(200)");
					return;
				}

				download(dstFile, controller, httpResponse, data);

				listener.onDownlaodFinished();

				setButtonDisabled(false, updateButton);
			}

			@Override
			public void failed(Throwable t) {
				Gdx.app.log(ONLINE_REPO_TAG,
						"Failed to perform the HTTP Request: ", t);

			}

			@Override
			public void cancelled() {
				Gdx.app.log(ONLINE_REPO_TAG, "HTTP request cancelled");

			}
		});
	}

	private void download(FileHandle dstFile, Controller controller,
			HttpResponse httpResponse, byte[] data) {
		int count = -1;

		InputStream input = null;
		OutputStream output = null;
		try {
			input = httpResponse.getResultAsStream();
			output = dstFile.write(false);

			// long total = 0;
			while ((count = input.read(data)) != -1) {
				// allow canceling with back button
				/*
				 * if (isCancelled()) { input.close(); return; }
				 */
				// total += count;
				// publishing the progress....
				/*
				 * if (fileLength > 0) // only if total length is known
				 * publishProgress((int) (total * 100 / fileLength));
				 */
				output.write(data, 0, count);
			}

		} catch (Exception e) {
			Gdx.app.error(ONLINE_REPO_TAG, "Exception while downloading file "
					+ dstFile.toString(), e);
		} finally {
			try {
				if (output != null)
					output.close();
				if (input != null)
					input.close();
			} catch (IOException ignored) {
				Gdx.app.error(ONLINE_REPO_TAG,
						"This exception should be ignored", ignored);
			}
		}
	}

	/**
	 * Unzips a .zip file.
	 * 
	 * @param zipFile
	 * @param outDir
	 * @param data
	 * @param deleteZipFile
	 */
	private void unzipFile(FileHandle zipFile, FileHandle outDir, byte data[],
			boolean deleteZipFile) {
		if (!outDir.exists()) {
			outDir.mkdirs();
		}
		String outPath = outDir.file().getAbsolutePath() + "/";

		ZipEntry ze = null;
		ZipInputStream zin = null;
		FileOutputStream fout = null;
		int count = -1;
		try {
			zin = new ZipInputStream(zipFile.read());
			while ((ze = zin.getNextEntry()) != null) {
				fout = new FileOutputStream(outPath + ze.getName());
				while ((count = zin.read(data)) != -1) {
					fout.write(data, 0, count);
				}

				zin.closeEntry();
				fout.close();
				fout = null;
			}
		} catch (Exception e) {
			Gdx.app.error(ONLINE_REPO_TAG, "Exception while unzipping file "
					+ zipFile.toString(), e);
		} finally {
			try {
				if (fout != null)
					fout.close();
				if (zin != null)
					zin.close();
			} catch (IOException ignored) {
				Gdx.app.error(ONLINE_REPO_TAG,
						"This exception should be ignored", ignored);
			}
		}
		if (deleteZipFile)
			zipFile.delete();
	}

	/**
	 * Reads and loads bindingsFile to display the thumbnails.
	 * 
	 * @param bindingsFile
	 * @param controller
	 */
	private void processBindings(FileHandle bindingsFile, Controller controller) {
		InputStream is = null;
		Properties props = null;
		try {
			props = new Properties();

			props.load(is = bindingsFile.read());
		} catch (IOException ioe) {
			Gdx.app.error(ONLINE_REPO_TAG, "Error loading bindings properties",
					ioe);
		} finally {
			try {
				if (is != null)
					is.close();
			} catch (IOException ignored) {
				Gdx.app.error(ONLINE_REPO_TAG,
						"This exception should be ignored", ignored);
			}
		}

		String thumbnailsPath = ".." + REPOSITORY_FOLDER_NAME
				+ THUMBNAILS_FOLDER_NAME + "/";
		EditorGameAssets gameAssets = controller.getEditorGameAssets();
		Set<java.util.Map.Entry<Object, Object>> propsSet = props.entrySet();
		for (java.util.Map.Entry<Object, Object> prop : propsSet) {
			gameAssets.get(thumbnailsPath + prop.getValue().toString(),
					Texture.class, new ThumbnailLoadedListener(prop.getKey()
							.toString()));
		}
	}

	private interface OnDownloadFinishedListener {
		void onDownlaodFinished();
	}

	/**
	 * This listener sets the thumbnail icon to the linked {@link ElementButton}
	 * . The binding relation is defined via {@link #onlineElements}.
	 */
	private class ThumbnailLoadedListener implements
			AssetLoadedCallback<Texture> {
		private String title;

		/**
		 * This listener sets the thumbnail icon to the linked
		 * {@link ElementButton}. The binding relation is defined via
		 * {@link #onlineElements}.
		 */
		public ThumbnailLoadedListener(String title) {
			this.title = title;
		}

		@Override
		public void loaded(String fileName, Texture asset) {
			onlineElements.get(this.title).setIcon(asset);
		}
	}
}