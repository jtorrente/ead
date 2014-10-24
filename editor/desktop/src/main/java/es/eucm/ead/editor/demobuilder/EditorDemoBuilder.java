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
package es.eucm.ead.editor.demobuilder;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.vividsolutions.jts.geom.Geometry;
import es.eucm.ead.editor.DesktopPlatform;
import es.eucm.ead.editor.utils.GeometryUtils;
import es.eucm.ead.editor.utils.ZipUtils;
import es.eucm.ead.engine.EngineDesktop;
import es.eucm.ead.engine.assets.GameAssets;
import es.eucm.ead.engine.demobuilder.DemoBuilder;
import es.eucm.ead.schema.data.Dimension;
import es.eucm.ead.schema.data.shape.Polygon;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schema.renderers.Image;
import org.im4java.core.ConvertCmd;
import org.im4java.core.IM4JavaException;
import org.im4java.core.IMOperation;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * DemoBuilder is a tool for helping make simple demo games quickly. Games
 * created with the tool can be saved automatically to a temp location on disk
 * and run by the engine. Use {@link #build()}, {@link #save()}, {@link #run()}
 * and {@link #clean()} for more info.
 * 
 * Usage: To create a new demo with the tool, create a class that extends
 * DemoBuilder. Then implement {@link #doBuild()} and, perhaps,
 * {@link #assetPaths()}, just in case you want to have all your asset paths
 * defined in one place. You may also consider overriding
 * {@link #getDescription()} and {@link #getSnapshotUri()} if you plan to add
 * the demo to DemoLauncher.
 * 
 * All the resources of the demo (images, sounds, etc.) must be zipped into a
 * file that should be available to DemoBuilder at runtime. DemoBuilder will
 * unzip these files to the temp location of the game. The information required
 * for resolving the location of the zipFile is placed at construction time (see
 * {@link #EditorDemoBuilder(String)}).
 */
public abstract class EditorDemoBuilder extends DemoBuilder {

	/*
	 * Static stuff needed to use some stuff, like Gdx.app or Pixmap. Should be
	 * used just once
	 */
	private static boolean init = false;

	private static void init() {
		/*
		 * if (!init) { LwjglNativesLoader.load(); Gdx.app = new
		 * LwjglApplication(new ApplicationListener() {
		 * 
		 * @Override public void create() {
		 * 
		 * }
		 * 
		 * @Override public void resize(int width, int height) {
		 * 
		 * }
		 * 
		 * @Override public void render() {
		 * 
		 * }
		 * 
		 * @Override public void pause() {
		 * 
		 * }
		 * 
		 * @Override public void resume() {
		 * 
		 * }
		 * 
		 * @Override public void dispose() {
		 * 
		 * } }); Gdx.files = new EDBFiles(); //MockApplication.initStatics(); }
		 */
	}

	protected GameAssets gameAssets;

	/* To avoid building entities more than once */
	protected boolean built = false;

	/* Convenient container of asset paths - no actual need to use it */
	protected String[] assets;

	/* points to the temp folder where the game is saved */
	protected FileHandle rootFolder;
	/*
	 * relative path of zip file with resources for this game (with no
	 * extension)
	 */
	protected String root;

	// To determine image dimensions
	protected DesktopPlatform platform;

    protected boolean convertInterlacedPngs=false;

	/**
	 * Creates the object but does not actually build the game. Just creates the
	 * temp folder and unzips the the contents of the file specified by the
	 * relative path {@code root}
	 * 
	 * @param root
	 */
	public EditorDemoBuilder(String root) {
		init();
		this.gameAssets = new GameAssets(Gdx.files);
		this.root = root;
		platform = new DesktopPlatform();
	}

	// ///////////////////////////////////////////////////
	// Private methods
	// //////////////////////////////////////////////////
	/**
	 * Creates a model collider for the given image
	 */
	protected Array<Polygon> createSchemaCollider(String imageUri) {
		Array<Polygon> collider = new Array<Polygon>();
		Pixmap pixmap = new Pixmap(gameAssets.resolve(imageUri));
		Array<Geometry> geometryArray = GeometryUtils
				.findBorders(pixmap, .1, 2);
		for (Geometry geometry : geometryArray) {
			collider.add(GeometryUtils.jtsToSchemaPolygon(geometry));
		}
		pixmap.dispose();
		return collider;
	}

	protected Dimension getImageDimension(String imageUri) {
		return platform.getImageDimension(gameAssets.resolve(imageUri).read());
	}

	// ////////////////////////////////////////////////////
	// Public methods for building, saving, and running the game
	// ////////////////////////////////////////////////////

	/**
	 * Builds the game, saves it to disk, and runs it.
	 */
	public void run() {
		if (!built) {
			build();
		}
		save();
		EngineDesktop engine = new EngineDesktop((int) gameWidth,
				(int) gameHeight) {
			@Override
			protected void dispose() {
				EditorDemoBuilder.this.clean();
				super.dispose();
			}
		};
		engine.run(rootFolder.file().getAbsolutePath(), false, false);
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
	}

	/**
	 * Builds all the entities of the game, and sets up assets, but does not
	 * save the game to disk or run the game. Use {@link #save()} or
	 * {@link #run()} instead.
	 * 
	 * @return The hashmap with all the entities of the game
	 */
	public HashMap<String, ModelEntity> build() {
		createOutputFolder();
		assets = assetPaths();
		doBuild();
		built = true;
		return entities;
	}

	/**
	 * Saves all entities to the temp folder ({@link #rootFolder}). Should be
	 * invoked always after {@link #build()}.
	 * 
	 * After this method is invoked, {@link #getRootFolder()} can be used to
	 * determine the location of the folder this game was saved to.
	 */
	public void save() {
		for (Map.Entry<String, ModelEntity> entry : entities.entrySet()) {
			FileHandle fh = rootFolder.child(entry.getKey());
			if (fh.isDirectory()) {
				fh.mkdirs();
			} else if (!fh.parent().exists()) {
				fh.parent().mkdirs();
			}
			Gdx.app.debug(LOG_TAG, "Saving to: " + fh.file().getAbsolutePath());
			gameAssets.toJson(entry.getValue(), null, fh);
		}
	}

	/*
	 * Creates the output folder and extracts contents from the zip. Needed
	 * before building
	 */
	protected void createOutputFolder() {
		rootFolder = FileHandle.tempDirectory(root);
		rootFolder.mkdirs();

		gameAssets.setLoadingPath("", true);
		ZipUtils.unzip(gameAssets.resolve(root + ".zip"), rootFolder);

        if (convertInterlacedPngs){
		    convertInterlacedPNGs(null, rootFolder);
        }

		gameAssets.setLoadingPath(rootFolder.file().getAbsolutePath(), false);
	}

	protected void convertInterlacedPNGs(ConvertCmd cmd, FileHandle dir) {
        if (cmd == null){
            cmd = new ConvertCmd();
        }

		for (FileHandle child : dir.list()) {
			if (child.isDirectory()) {
				convertInterlacedPNGs(cmd, child);
			} else if (child.extension().toLowerCase().endsWith("png")) {
                IMOperation op = new IMOperation();
                op.addImage(child.path());
                op.interlace("None");
                op.format("png");
                try {
                    cmd.run(op);
                } catch (IOException e) {
                    e.printStackTrace();
                    System.err.println("[Error] Could not convert PNG image "
                            + child.name());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    System.err.println("[Error] Could not convert PNG image "
                            + child.name());
                } catch (IM4JavaException e) {
                    e.printStackTrace();
                    System.err.println("[Error] Could not convert PNG image "
                            + child.name());
                }
			}
		}
	}

	/**
	 * @return An input stream ready for reading a snapshot image of the game
	 *         for preview, or null if no image is available. By default, it is
	 *         assumed that an image with name {@code root} and png extension
	 *         will be available. Demos can define their snapshot to be in
	 *         another location or with another name by overriding
	 *         {@link #getSnapshotUri()}.
	 */
	public InputStream getSnapshotInputStream() {
		FileHandle imageFileHandle = gameAssets.resolve(getSnapshotUri());
		if (imageFileHandle.exists() && imageFileHandle != null) {
			return imageFileHandle.read();
		}
		return null;
	}

	/**
	 * @return An external FileHandle pointing to the temp folder where the game
	 *         was saved
	 */
	public FileHandle getRootFolder() {
		return rootFolder;
	}

	/**
	 * Deletes the whole temp folder the game was saved to. Should be called
	 * once the game is not planned to be run anymore.
	 */
	public void clean() {
		if (built && rootFolder != null) {
			rootFolder.deleteDirectory();
			built = false;
			entities.clear();
			rootFolder = null;
			lastEntity = lastScene = null;
			lastComponent = null;
			sceneCount = 0;
		}
	}

	// /////////////////////////////////////////////////////////////////////////////
	// Protected methods that subclasses may want or need to implement or
	// override
	// /////////////////////////////////////////////////////////////////////////////

	/**
	 * Sets up {@link #assets}. Useful to encapsulate asset paths all in one
	 * place
	 */
	protected String[] assetPaths() {
		return new String[] {};
	}

	/**
	 * @return A textual description of the demo (for DemoLauncher). By default,
	 *         "No description available is returned". Is strongly encouraged
	 *         for subclasses to override this method.
	 */
	public String getDescription() {
		return "No description available";
	}

	/**
	 * @return The path of the snapshot image for the demo.
	 */
	protected String getSnapshotUri() {
		return root + ".png";
	}

	/**
	 * @return A name of the demo, for the DemoLauncher. By default returns the
	 *         relative path ({@link #root}).
	 */
	public String getName() {
		return root;
	}

	// //////////////////////////////////////////////////////////
	// Methods for accessing last entities and component
	// /////////////////////////////////////////////////////////

	/**
	 * Creates a game with just one scene configured with the given
	 * {@code backgroundUri} image. The size of the screen (game width and
	 * height) is determined automatically from the size of the image.
	 * 
	 * @param backgroundUri
	 *            Relative uri of the background image of the scene
	 */
	public EditorDemoBuilder singleSceneGame(String backgroundUri) {
		Dimension backgroundDim = getImageDimension(backgroundUri);
		super.singleSceneGame(backgroundUri, backgroundDim.getWidth(),
				backgroundDim.getHeight());
		return this;
	}

	/**
	 * Creates a new entity with the given screen alignment as a child of the
	 * {@code parent} entity provided.
	 * 
	 * @param parent
	 *            The entity to add this entity to to
	 * @param imageUri
	 *            The relative uri of the image to serve as renderer
	 * @param verticalAlign
	 *            Can be TOP (sticks the entity to screen top), BOTTOM (sticks
	 *            the entity to screen bottom) or CENTER (places the entity
	 *            vertically centered on the screen).
	 * @param horizontalAlign
	 *            Can be LEFT(sticks the entity to the left of the screen),
	 *            RIGHT (sticks the entity to the right of the screen) or CENTER
	 *            (places the entity horizontally on the screen).
	 */
	public EditorDemoBuilder entity(ModelEntity parent, String imageUri,
			VerticalAlign verticalAlign, HorizontalAlign horizontalAlign) {
		Dimension imageDim = getImageDimension(imageUri);
		super.entity(parent, imageUri, verticalAlign, horizontalAlign,
				(float) imageDim.getWidth(), (float) imageDim.getHeight());
		return this;
	}

	@Override
	protected Image createImage(String uri) {
		Image image = super.createImage(uri);
		image.setCollider(createSchemaCollider(uri));
		return image;
	}

	private static class EDBFileHandle extends FileHandle {
		public EDBFileHandle(String fileName, Files.FileType type) {
			super(fileName, type);
		}

		public EDBFileHandle(File file, Files.FileType type) {
			super(file, type);
		}

		public FileHandle child(String name) {
			if (file.getPath().length() == 0)
				return new EDBFileHandle(new File(name), type);
			return new EDBFileHandle(new File(file, name), type);
		}

		public FileHandle sibling(String name) {
			if (file.getPath().length() == 0)
				throw new GdxRuntimeException(
						"Cannot get the sibling of the root.");
			return new EDBFileHandle(new File(file.getParent(), name), type);
		}

		public FileHandle parent() {
			File parent = file.getParentFile();
			if (parent == null) {
				if (type == Files.FileType.Absolute)
					parent = new File("/");
				else
					parent = new File("");
			}
			return new EDBFileHandle(parent, type);
		}

		public File file() {
			if (type == Files.FileType.External)
				return new File(EDBFiles.externalPath, file.getPath());
			return file;
		}
	}

	private static class EDBFiles implements Files {
		static public final String externalPath = System
				.getProperty("user.home") + "/";

		@Override
		public FileHandle getFileHandle(String fileName, FileType type) {
			return new EDBFileHandle(fileName, type);
		}

		@Override
		public FileHandle classpath(String path) {
			return new EDBFileHandle(path, FileType.Classpath);
		}

		@Override
		public FileHandle internal(String path) {
			return new EDBFileHandle(path, FileType.Internal);
		}

		@Override
		public FileHandle external(String path) {
			return new EDBFileHandle(path, FileType.External);
		}

		@Override
		public FileHandle absolute(String path) {
			return new EDBFileHandle(path, FileType.Absolute);
		}

		@Override
		public FileHandle local(String path) {
			return new EDBFileHandle(path, FileType.Local);
		}

		@Override
		public String getExternalStoragePath() {
			return externalPath;
		}

		@Override
		public boolean isExternalStorageAvailable() {
			return true;
		}

		@Override
		public String getLocalStoragePath() {
			return "";
		}

		@Override
		public boolean isLocalStorageAvailable() {
			return true;
		}
	}

}
