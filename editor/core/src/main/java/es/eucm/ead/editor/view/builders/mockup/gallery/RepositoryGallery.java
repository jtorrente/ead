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
import java.util.Comparator;
import java.util.Properties;
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
import com.badlogic.gdx.utils.ObjectMap.Entry;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.editor.ChangeView;
import es.eucm.ead.editor.control.actions.model.AddSceneElement;
import es.eucm.ead.editor.model.Model;
import es.eucm.ead.editor.view.builders.mockup.edition.SceneEdition;
import es.eucm.ead.editor.view.builders.mockup.menu.InitialScreen;
import es.eucm.ead.editor.view.listeners.ActionOnClickListener;
import es.eucm.ead.editor.view.widgets.mockup.buttons.ElementButton;
import es.eucm.ead.editor.view.widgets.mockup.buttons.ToolbarButton;
import es.eucm.ead.engine.I18N;
import es.eucm.ead.engine.assets.Assets.AssetLoadedCallback;
import es.eucm.ead.schema.editor.components.Note;
import es.eucm.ead.schema.entities.ModelEntity;

/**
 * The gallery that will display our online elements. Has a top tool bar and a
 * gallery grid.
 */
public class RepositoryGallery extends BaseGallery<ElementButton> {

	public static final String NAME = "mockup_repository_gallery";

	private static final String IC_GO_BACK = "ic_goback";
	private static final String REPOSITORY_FOLDER = "/onlineRepository";
	private static final String THUMBNAILS_FOLDER = REPOSITORY_FOLDER
			+ "/thumbnails";
	private static final String THUMBNAIL_BINDINGS_FILE = "bindings.properties";
	private static final String REPOSITORY_INDEX_URL = "http://repo-justusevim.rhcloud.com/elements.json";
	private static final String REPOSITORY_THUMBNAILS_URL = "http://repo-justusevim.rhcloud.com/thumbnails.zip";

	private String previousElements;

	private final ObjectMap<String, ElementButton> onlineElements = new ObjectMap<String, ElementButton>();

	private TextButton updateButton;

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	protected WidgetGroup centerWidget(Vector2 viewport, I18N i18n, Skin skin,
			Controller controller) {
		setSelectable(false);
		return super.centerWidget(viewport, i18n, skin, controller);
	}

	@Override
	protected void addActorToHide(Actor actorToHide) {
		// Do nothing because this gallery cannot be selected.
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
	protected void addSortingsAndComparators(Array<String> shortings,
			ObjectMap<String, Comparator<ElementButton>> comparators, I18N i18n) {
		// Do nothing since we won't have additional sorting methods in
		// RepositoryGallery
	}

	@Override
	protected boolean updateGalleryElements(Controller controller,
			Array<ElementButton> elements, Vector2 viewport, I18N i18n,
			Skin skin) {
		elements.clear();
		for (Entry<String, ElementButton> elem : onlineElements.entries()) {
			elements.add(elem.value);
		}
		return true;
	}

	@Override
	protected void entityClicked(InputEvent event, ElementButton target,
			Controller controller, I18N i18n) {

	}

	@Override
	protected void entityDeleted(ElementButton entity, Controller controller) {

	}

	@Override
	public void initialize(Controller controller) {
		update(controller);
	}

	@Override
	public void release(Controller controller) {
		super.release(controller);
	}

	private void update(final Controller controller) {
		setButtonDisabled(true, updateButton);
		String url;
		String httpMethod = Net.HttpMethods.GET;
		String requestContent = null;
		url = REPOSITORY_INDEX_URL;
		HttpRequest httpRequest = new HttpRequest(httpMethod);
		httpRequest.setUrl(url);
		httpRequest.setContent(requestContent);
		Gdx.net.sendHttpRequest(httpRequest, new HttpResponseListener() {

			@Override
			public void handleHttpResponse(final HttpResponse httpResponse) {
				final int statusCode = httpResponse.getStatus().getStatusCode();
				// We are not in main thread right now so we
				// need to post to main thread for UI updates

				if (statusCode != 200) {
					Gdx.app.log("NetAPITest",
							"An error ocurred since statusCode is not OK, "
									+ httpResponse);
					return;
				}

				final String res = httpResponse.getResultAsString();
				Gdx.app.log("NetAPITest", "Success ~> " + httpResponse + ", "
						+ res);

				if (previousElements == null || !previousElements.equals(res)) {
					Gdx.app.postRunnable(new Runnable() {
						public void run() {
							@SuppressWarnings("unchecked")
							ArrayList<ModelEntity> elems = controller
									.getEditorGameAssets().fromJson(
											ArrayList.class, res);
							onlineElements.clear();
							for (ModelEntity elem : elems) {
								onlineElements.put(
										Model.getComponent(elem, Note.class)
												.getTitle(), new ElementButton(
												controller.getPlatform()
														.getSize(), controller
														.getApplicationAssets()
														.getI18N(), elem, null,
												controller
														.getApplicationAssets()
														.getSkin(), controller,
												AddSceneElement.class, elem));
								// TODO download element resources and, when
								// it's clicked, copy them to /images folder if
								// they weren't previously there
							}
							RepositoryGallery.super.initialize(controller);
						}
					});

					sendDownloadRequest(controller);
				} else {
					setButtonDisabled(false, updateButton);
				}

				previousElements = res;
			}

			@Override
			public void failed(Throwable t) {
				Gdx.app.log("NetAPITest",
						"Failed to perform the HTTP Request: ", t);

			}

			@Override
			public void cancelled() {
				Gdx.app.log("NetAPITest", "HTTP request cancelled");

			}
		});
	}

	private void sendDownloadRequest(final Controller controller) {
		String url;
		String httpMethod = Net.HttpMethods.GET;
		String requestContent = null;
		url = REPOSITORY_THUMBNAILS_URL;
		HttpRequest httpRequest = new HttpRequest(httpMethod);
		httpRequest.setUrl(url);
		httpRequest.setContent(requestContent);
		Gdx.net.sendHttpRequest(httpRequest, new HttpResponseListener() {

			@Override
			public void handleHttpResponse(final HttpResponse httpResponse) {
				final int statusCode = httpResponse.getStatus().getStatusCode();
				// We are not in main thread right now so we
				// need to post to main thread for UI updates

				if (statusCode != 200) {
					Gdx.app.log("NetAPITest",
							"An error ocurred since statusCode is not OK, "
									+ httpResponse);
					return;
				}

				download(controller, httpResponse);
			}

			@Override
			public void failed(Throwable t) {
				Gdx.app.log("NetAPITest",
						"Failed to perform the HTTP Request: ", t);

			}

			@Override
			public void cancelled() {
				Gdx.app.log("NetAPITest", "HTTP request cancelled");

			}
		});
	}

	private void download(Controller controller, HttpResponse httpResponse) {
		InputStream input = null;
		OutputStream output = null;
		try {
			input = httpResponse.getResultAsStream();
			FileHandle outFile = Gdx.files
					.absolute(InitialScreen.MOCKUP_PROJECT_FILE.file()
							.getAbsolutePath()
							+ REPOSITORY_FOLDER
							+ "/thumbnails.zip");
			output = outFile.write(false);

			byte data[] = new byte[4096];
			// long total = 0;
			int count;
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

			FileHandle unzippedThumbnails = Gdx.files
					.absolute(InitialScreen.MOCKUP_PROJECT_FILE.file()
							.getAbsolutePath() + THUMBNAILS_FOLDER);
			if (!unzippedThumbnails.exists()) {
				unzippedThumbnails.mkdirs();
			}
			ZipInputStream zin = null;
			FileOutputStream fout = null;
			try {
				zin = new ZipInputStream(outFile.read());

				ZipEntry ze = null;
				while ((ze = zin.getNextEntry()) != null) {
					fout = new FileOutputStream(unzippedThumbnails.file()
							.getAbsolutePath() + "/" + ze.getName());
					while ((count = zin.read(data)) != -1) {
						fout.write(data, 0, count);
					}

					zin.closeEntry();
					fout.close();
					fout = null;
				}
			} catch (Exception e) {
				Gdx.app.error("NetAPITest", "Exception", e);
			} finally {
				try {
					if (fout != null)
						fout.close();
					if (zin != null)
						zin.close();
				} catch (IOException ignored) {
					Gdx.app.error("NetAPITest",
							"This exception should be ignored", ignored);
				}
			}
			outFile.delete();

			FileHandle bindings = unzippedThumbnails
					.child(THUMBNAIL_BINDINGS_FILE);
			InputStream is = null;
			Properties props = null;
			try {
				props = new Properties();

				props.load(is = bindings.read());
			} catch (IOException ioe) {
				Gdx.app.error("NetAPITest",
						"Error loading bindings properties", ioe);
			} finally {
				try {
					if (is != null)
						is.close();
				} catch (IOException ignored) {
					Gdx.app.error("NetAPITest",
							"This exception should be ignored", ignored);
				}
			}

			for (java.util.Map.Entry<Object, Object> prop : props.entrySet()) {
				controller.getEditorGameAssets().get(
						".." + THUMBNAILS_FOLDER + "/"
								+ prop.getValue().toString(), Texture.class,
						new ThumbnailLoadedListener(prop.getKey().toString()));
			}

		} catch (Exception e) {
			Gdx.app.error("NetAPITest", "Exception", e);
		} finally {
			try {
				if (output != null)
					output.close();
				if (input != null)
					input.close();
			} catch (IOException ignored) {
				Gdx.app.error("NetAPITest", "This exception should be ignored",
						ignored);
			}
		}

		setButtonDisabled(false, updateButton);
	}

	private class ThumbnailLoadedListener implements
			AssetLoadedCallback<Texture> {
		private String title;

		public ThumbnailLoadedListener(String title) {
			this.title = title;
		}

		@Override
		public void loaded(String fileName, Texture asset) {
			onlineElements.get(title).setIcon(asset);
		}
	}
}