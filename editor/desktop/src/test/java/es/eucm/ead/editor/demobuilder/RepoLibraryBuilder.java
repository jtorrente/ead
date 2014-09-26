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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import es.eucm.ead.editor.utils.ZipUtils;
import es.eucm.ead.schema.components.ModelComponent;
import es.eucm.ead.schema.data.Dimension;
import es.eucm.ead.schema.editor.components.repo.I18NString;
import es.eucm.ead.schema.editor.components.repo.I18NStrings;
import es.eucm.ead.schema.editor.components.repo.RepoAuthor;
import es.eucm.ead.schema.editor.components.repo.RepoElement;
import es.eucm.ead.schema.editor.components.repo.RepoLibrary;
import es.eucm.ead.schema.editor.components.repo.RepoLicense;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schema.renderers.Frame;
import es.eucm.ead.schema.renderers.Frames;
import es.eucm.ead.schema.renderers.Image;
import es.eucm.ead.schema.renderers.Renderer;
import es.eucm.ead.schema.renderers.State;
import es.eucm.ead.schema.renderers.States;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by Javier Torrente on 23/09/14.
 */
public abstract class RepoLibraryBuilder extends EditorDemoBuilder {

	private static final String DEFAULT_THUMBNAILS_FOLDER = "thumbnails/";

	private static final String DEFAULT_RESOURCES_FOLDER = "resources/";

	private static final String ENTITIES_JSON = "entities.json";

    public static final String THUMBNAILS = "thumbnails";
    public static final String RESOURCES = "resources";

	public static final String TAGS = "Tags";

	public static final String LICENSE = "License";

	public static final String AUTHOR_NAME = "aname";

	public static final String AUTHOR_URL = "aurl";

	public static final String MAX_WIDTH = "mwidth";

	public static final String MAX_HEIGHT = "mheight";

	public static final String DEFAULT = "default";

	private Map<String, String> properties = new HashMap<String, String>();

	private RepoLibrary lastLibrary;

	private RepoElement lastElement;

	private List<ModelEntity> repoEntities = new ArrayList<ModelEntity>();

	/**
	 * Creates the object but does not actually build the game. Just creates the
	 * temp folder and unzips the the contents of the file specified by the
	 * relative path {@code root}
	 * 
	 * @param root
	 */
	public RepoLibraryBuilder(String root) {
		super(root);
        setCommonProperty(THUMBNAILS, DEFAULT_THUMBNAILS_FOLDER);
        setCommonProperty(RESOURCES, DEFAULT_RESOURCES_FOLDER);
	}

    public RepoLibraryBuilder frame(String frameUri, float duration) {
        return (RepoLibraryBuilder)super.frame(properties.get(RESOURCES)+frameUri,duration);
    }

	public void export(String outputDir) {
		createOutputFolder();

		doBuild();

		// Save entities.json
		FileHandle fh = rootFolder.child(ENTITIES_JSON);
		Gdx.app.debug(LOG_TAG, "Saving " + ENTITIES_JSON + " to: "
				+ fh.file().getAbsolutePath());
		gameAssets.toJson(repoEntities, null, fh);

		// Zip entities.json (+resources & thumbnails) to output folder
		FileHandle outputZip = new FileHandle(outputDir);
		outputZip.mkdirs();
		outputZip = outputZip.child(root + ".zip");
		ZipUtils.zip(rootFolder, outputZip);

		// Update Mb
		float sizeInMb = outputZip.length() / 1048576F;
		if (sizeInMb > 0) {
			lastLibrary.setSize(sizeInMb);
		} else {
			lastLibrary.setSize(-1F);
		}

		// Create json for library
		FileHandle outputJson = new FileHandle(outputDir);
		outputJson = outputJson.child(root + ".json");
		gameAssets.toJson(lastLibrary, null, outputJson);
	}

	public RepoLibraryBuilder setCommonProperty(String property, String value) {
		properties.put(property, value);
		return this;
	}

	public RepoLibraryBuilder repoLib(String nameEn, String nameEs,
			String descriptionEn, String descriptionEs, String thumbnail) {
		lastLibrary = makeRepoLibrary(nameEn, nameEs, descriptionEn,
				descriptionEs, thumbnail);
		return this;
	}

	public RepoLibraryBuilder repoEntity(String nameEn, String nameEs,
			String descriptionEn, String descriptionEs, String thumbnail,
			String image) {
		if (image != null
				&& !(image.toLowerCase().endsWith(".png")
						|| image.toLowerCase().endsWith(".jpg") || image
						.toLowerCase().endsWith(".jpeg"))) {
			image += ".png";
		}
		// Create holding entity and component
		ModelEntity parent = entity(null,
				image == null ? null : properties.get(RESOURCES) + image, 0, 0)
				.getLastEntity();
		repoEntities.add(parent);
		lastElement = makeRepoElement(nameEn, nameEs, descriptionEn,
				descriptionEs, thumbnail == null ? null : properties.get(THUMBNAILS)
						+ thumbnail);
		parent.getComponents().add(lastElement);
		adjustEntity(parent);
		return this;
	}

	public RepoLibraryBuilder frameState(int nTags, float duration,
			String... tagsAndUris) {
		for (int i = nTags; i < tagsAndUris.length; i++) {
			tagsAndUris[i] = properties.get(RESOURCES) + tagsAndUris[i];
		}
		super.frameState(getLastEntity(), nTags, duration, tagsAndUris);
		return this;
	}

	public RepoLibraryBuilder adjustEntity(ModelEntity parent) {
		// /////// Entity adjustments
		// Calculate current dimension
		Dimension actualDim = null;
		for (ModelComponent component : parent.getComponents()) {
			if (component instanceof Renderer) {
				actualDim = getRendererDimension((Renderer) component);
				break;
			}
		}

		if (actualDim == null || actualDim.getWidth() == 0
				|| actualDim.getHeight() == 0) {
			return this;
		}

		float actualHeight = actualDim.getHeight();
		float actualWidth = actualDim.getWidth();

		for (ModelComponent component : parent.getComponents()) {
			if (component instanceof RepoElement) {
				((RepoElement) component).setWidth(actualWidth);
				((RepoElement) component).setHeight(actualHeight);
				break;
			}
		}

		// Center origin
		parent.setOriginX(actualWidth / 2.0F);
		parent.setOriginY(actualHeight / 2.0F);

		// Update scale in case there is a max width or max height declared
		float sy = 1.0F, sx = 1.0F;
		if (properties.get(MAX_HEIGHT) != null) {
			try {
				float maxHeight = Float.parseFloat(properties.get(MAX_HEIGHT));
				if (actualHeight > maxHeight) {
					sy = maxHeight / actualHeight;
				}
			} catch (NumberFormatException e) {
				// Just log it
				Gdx.app.log("RepoLibrary", "could not parse MAX_HEIGHT="
						+ properties.get(MAX_HEIGHT));
			}
		}

		if (properties.get(MAX_WIDTH) != null) {
			try {
				float maxWidth = Float.parseFloat(properties.get(MAX_WIDTH));
				if (actualWidth > maxWidth) {
					sx = maxWidth / actualWidth;
				}
			} catch (NumberFormatException e) {
				// Just log it
				Gdx.app.log("RepoLibrary", "could not parse MAX_HEIGHT="
						+ properties.get(MAX_HEIGHT));
			}
		}

		sx = sy = Math.min(sx, sy);

		if (sx != 1.0) {
			parent.setScaleX(sx);
		}

		if (sy != 1.0) {
			parent.setScaleY(sy);
		}

		return this;
	}

	public RepoLibraryBuilder authorName(String name) {
		if (lastLibrary != null) {
			if (lastLibrary.getAuthor() == null) {
				lastLibrary.setAuthor(new RepoAuthor());
			}
			lastLibrary.getAuthor().setName(name);
		} else if (lastElement != null) {
			if (lastElement.getAuthor() == null) {
				lastElement.setAuthor(new RepoAuthor());
			}
			lastElement.getAuthor().setName(name);
		}
		return this;
	}

	public RepoLibraryBuilder authorUrl(String authorUrl) {
		if (lastLibrary != null) {
			if (lastLibrary.getAuthor() == null) {
				lastLibrary.setAuthor(new RepoAuthor());
			}
			lastLibrary.getAuthor().setUrl(authorUrl);
		} else if (lastElement != null) {
			if (lastElement.getAuthor() == null) {
				lastElement.setAuthor(new RepoAuthor());
			}
			lastElement.getAuthor().setUrl(authorUrl);
		}
		return this;
	}

	public RepoLibraryBuilder tag(String tagEn, String tagEs) {
		if (lastLibrary != null) {
			lastLibrary.getTags().add(enEsString(tagEn, tagEs));
		} else if (lastElement != null) {
			lastElement.getTags().add(enEsString(tagEn, tagEs));
		}
		return this;
	}

	public RepoLibraryBuilder tagFullyAnimatedCharacter() {
		return tagAnimatedCharacter().tag("walk", "andar")
				.tag("grab", "agarrar").tag("talk", "hablar")
				.tag("use", "usar");
	}

	public RepoLibraryBuilder tagAnimatedCharacter() {
		return tagCharacter().tag("animated", "animado");
	}

	public RepoLibraryBuilder tagCharacter() {
		return tag("character", "personaje");
	}

	public RepoLibraryBuilder license(RepoLicense license) {
		if (lastLibrary != null) {
			lastLibrary.getLicenses().add(license);
		} else if (lastElement != null) {
			lastElement.setLicense(license);
		}
		return this;
	}

	public RepoElement makeRepoElement(String nameEn, String nameEs,
			String descriptionEn, String descriptionEs, String thumbnail) {
		// Infer w,h
		int width = 0, height = 0;
		ModelEntity modelEntity = getLastEntity();
		for (ModelComponent component : modelEntity.getComponents()) {
			if (component instanceof Renderer) {
				Dimension dimension = getRendererDimension((Renderer) component);
				width = Math.max(width, dimension.getWidth());
				height = Math.max(height, dimension.getHeight());
			}
		}

		return makeRepoElement(nameEn, nameEs, thumbnail, descriptionEn,
				descriptionEs, width, height);
	}

	public RepoLibrary makeRepoLibrary(String nameEn, String nameEs,
			String descriptionEn, String descriptionEs, String thumbnail) {
		RepoLibrary repoLibrary = new RepoLibrary();
		repoLibrary.setName(enEsString(nameEn, nameEs));
		repoLibrary.setPath(root);
		if (thumbnail != null) {
			repoLibrary.setThumbnail(thumbnail);
		} else {
			repoLibrary.setThumbnail(root + ".png");
		}
		repoLibrary.setDescription(enEsString(descriptionEn, descriptionEs));

		// Collect license & author info, number of elements
		NoRepetitionList<String> uniqueAuthors = new NoRepetitionList<String>();
		NoRepetitionList<RepoLicense> uniqueLicenses = new NoRepetitionList<RepoLicense>();
		int nItems = 0;
		for (ModelEntity entity : repoEntities) {
			for (ModelComponent component : entity.getComponents()) {
				if (component instanceof RepoElement) {
					nItems++;
					RepoElement repoElement = (RepoElement) component;
					if (repoElement.getAuthor() != null
							&& repoElement.getAuthor().getName() != null) {
						uniqueAuthors.add(repoElement.getAuthor().getName());
					}
					if (repoElement.getLicense() != null) {
						uniqueLicenses.add(repoElement.getLicense());
					}
					break;
				}
			}
		}
		String authorList = "";
		for (int i = 0; i < uniqueAuthors.size(); i++) {
			if (i < uniqueAuthors.size() - 1) {
				authorList += uniqueAuthors.get(i) + ", ";
			} else {
				authorList += uniqueAuthors.get(i);
			}
		}

		if (authorList != "") {
			if (repoLibrary.getAuthor() == null) {
				repoLibrary.setAuthor(new RepoAuthor());
			}
			repoLibrary.getAuthor().setName(authorList);
		}

		for (RepoLicense license : uniqueLicenses) {
			repoLibrary.getLicenses().add(license);
		}

		// Set number of elements
		repoLibrary.setNumberOfElements(nItems);
		// Set path (root)
		repoLibrary.setPath(root);
		// Set default tags, if any
		if (properties.get(TAGS) != null) {
			String commonTags = properties.get(TAGS);
			for (String tag : commonTags.split(",")) {
				String en = tag.split(";")[0];
				String es = tag.split(";").length > 1 ? tag.split(";")[1] : tag
						.split(";")[0];
				repoLibrary.getTags().add(enEsString(en, es));
			}
		}
		return repoLibrary;
	}

	public RepoElement makeRepoElement(String nameEn, String nameEs,
			String thumbnail, String descriptionEn, String descriptionEs,
			int width, int height) {
		// Create repo element with default options, if necessary
		RepoElement repoElement = lastElement = new RepoElement();
		repoElement.setName(enEsString(nameEn, nameEs));
		if (thumbnail != null) {
			repoElement.setThumbnail(thumbnail);
		}
		repoElement.setDescription(enEsString(descriptionEn, descriptionEs));
		repoElement.setWidth(width);
		repoElement.setHeight(height);

		// Add default author, if any
		if (properties.get(AUTHOR_NAME) != null) {
			if (repoElement.getAuthor() == null) {
				repoElement.setAuthor(new RepoAuthor());
			}
			repoElement.getAuthor().setName(properties.get(AUTHOR_NAME));
		}

		if (properties.get(AUTHOR_URL) != null) {
			if (repoElement.getAuthor() == null) {
				repoElement.setAuthor(new RepoAuthor());
			}
			repoElement.getAuthor().setUrl(properties.get(AUTHOR_URL));
		}

		// Add default tags, if any
		if (properties.get(TAGS) != null) {
			String commonTags = properties.get(TAGS);
			for (String tag : commonTags.split(",")) {
				String en = tag.split(";")[0];
				String es = tag.split(";").length > 1 ? tag.split(";")[1] : tag
						.split(";")[0];
				repoElement.getTags().add(enEsString(en, es));
			}
		}

		// Add default license, if any
		if (properties.get(LICENSE) != null) {
			for (String commonLicense : properties.get(LICENSE).split(",")) {
				license(RepoLicense.fromValue(commonLicense));
			}
		}
		return repoElement;
	}

	private Dimension getRendererDimension(Renderer component) {
		int width = 0, height = 0;
		if (component instanceof Image) {
			Image image = (Image) component;
			Dimension dim = getImageDimension(image.getUri());
			width = dim.getWidth();
			height = dim.getHeight();
		} else if (component instanceof Frames) {
			Frames frames = (Frames) component;
			for (Frame frame : frames.getFrames()) {
				Dimension frameDim = getRendererDimension(frame.getRenderer());
				width = Math.max(width, frameDim.getWidth());
				height = Math.max(height, frameDim.getHeight());
			}
		} else if (component instanceof States) {
			States states = (States) component;
			for (State state : states.getStates()) {
				Dimension stateDim = getRendererDimension(state.getRenderer());
				width = Math.max(width, stateDim.getWidth());
				height = Math.max(height, stateDim.getHeight());
			}
		}
		Dimension dimension = new Dimension();
		dimension.setWidth(width);
		dimension.setHeight(height);
		return dimension;
	}

	private String nameToPath(String name) {
		if (name == null || name.equals("")) {
			return "undef" + new Random().nextInt(100000000);
		}

		String path = "";
		for (int i = 0; i < name.length(); i++) {
			if (Character.isLetterOrDigit(name.charAt(i))) {
				path += name.charAt(i);
			} else {
				path += "_";
			}
		}

		return path;
	}

	private String[] enEquivalents = { "en", "EN", "en_EN", "en_US", "en_UK" };
	private String[] esEquivalents = { "es", "ES", "es_ES" };

	private I18NStrings enEsString(String en, String es) {
		I18NStrings i18NStrings = new I18NStrings();
		for (String enLang : enEquivalents) {
			I18NString enStr = new I18NString();
			enStr.setLang(enLang);
			enStr.setValue(en);
			i18NStrings.getStrings().add(enStr);
		}
		for (String esLang : esEquivalents) {
			I18NString esStr = new I18NString();
			esStr.setLang(esLang);
			esStr.setValue(es);
			i18NStrings.getStrings().add(esStr);
		}
		return i18NStrings;
	}

	private static class NoRepetitionList<T> extends ArrayList<T> {
		@Override
		public boolean add(T t) {
			boolean exists = false;
			for (int i = 0; i < size(); i++) {
				exists |= get(i) == null && t == null || get(i).equals(t);
			}
			if (!exists) {
				return super.add(t);
			}
			return false;
		}
	}
}
