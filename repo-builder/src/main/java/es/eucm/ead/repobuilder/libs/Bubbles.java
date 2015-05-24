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
package es.eucm.ead.repobuilder.libs;

import es.eucm.ead.repobuilder.BuildRepoLibs;
import es.eucm.ead.repobuilder.RepoTags;
import es.eucm.ead.schema.components.controls.Label;
import es.eucm.ead.schema.components.tweens.AlphaTween;
import es.eucm.ead.schema.components.tweens.ScaleTween;
import es.eucm.ead.schema.components.tweens.Timeline;
import es.eucm.ead.schema.components.tweens.Tween;
import es.eucm.ead.schema.data.Color;
import es.eucm.ead.schema.editor.components.repo.RepoCategories;
import es.eucm.ead.schema.effects.ChangeEntityProperty;
import es.eucm.ead.schema.entities.ModelEntity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by jtorrente on 23/05/2015.
 */
public class Bubbles extends PlaygroundLibrary {

	public Bubbles() {
		super("bubbles");
	}

	@Override
	protected void doBuildImpl() {
		generate(false);
		generate(true);
	}

	private void generate(boolean flip) {
		Color white10 = makeColor(1.0F, 1.0F, 1.0F, 1.0F);
		Color black09 = makeColor(0.0F, 0.0F, 0.0F, 0.9F);
		for (int i = 1; i <= 9; i++) {
			if (flip && (i == 7 || i == 9)) {
				continue;
			}
			try {
				Method method = Bubbles.class.getDeclaredMethod("b0" + i,
						Color.class, Color.class, Color.class, Boolean.class);
				method.invoke(this, white10, black09, white10, flip);
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	private void b01(Color text, Color foreground, Color background,
			Boolean flipVertical) {
		bubble("Normal dialogue", "Diálogo normal", "01", 36F, 0F, text,
				flipVertical ? 140 : 60, 120, 0.4F, foreground, background,
				false, flipVertical);
	}

	private void b02(Color text, Color foreground, Color background,
			Boolean flipVertical) {
		bubble("Normal round dialogue", "Diálogo normal redondo", "02", 268F,
				0F, text, flipVertical ? 590 : 40, 120, 0.4F, foreground,
				background, false, flipVertical);
	}

	private void b03(Color text, Color foreground, Color background,
			Boolean flipVertical) {
		bubble("Exclamation dialogue", "Diálogo de exclamación", "03", 159F,
				0F, text, flipVertical ? 380 : 60, 130, 0.4F, foreground,
				background, false, flipVertical);
	}

	private void b04(Color text, Color foreground, Color background,
			Boolean flipVertical) {
		bubble("Normal dialogue 2", "Diálogo normal 2", "04", 84F, 0F, text,
				flipVertical ? 225 : 55, 140, 0.4F, foreground, background,
				false, flipVertical);
	}

	private void b05(Color text, Color foreground, Color background,
			Boolean flipVertical) {
		bubble("Thinking dialogue", "Diálogo de pensar", "05", 282F, 0F, text,
				flipVertical ? 625 : 60, 120, 0.4F, foreground, background,
				false, flipVertical);
	}

	private void b06(Color text, Color foreground, Color background,
			Boolean flipVertical) {
		bubble("Exclamation dialogue 2", "Diálogo de exclamación 2", "06",
				143F, 0F, text, flipVertical ? 380 : 85, 125, 0.4F, foreground,
				background, false, flipVertical);
	}

	private void b07(Color text, Color foreground, Color background,
			Boolean flipVertical) {
		bubble("Narrator exclamation", "Exclamación del narrador", "07", 197F,
				142F, text, flipVertical ? 440 : 75, 60, 0.4F, foreground,
				background, false, flipVertical);
	}

	private void b08(Color text, Color foreground, Color background,
			Boolean flipVertical) {
		bubble("Normal round dialogue 2", "Diálogo normal redondo 2", "08",
				264F, 0F, text, flipVertical ? 625 : 80, 90, 0.4F, foreground,
				background, false, flipVertical);
	}

	private void b09(Color text, Color foreground, Color background,
			Boolean flipVertical) {
		bubble("Narrator note", "Aclaración del narrador", "09", 342F, 107F,
				text, flipVertical ? 940 : 250, 50, 0.4F, foreground,
				background, false, flipVertical);
	}

	private void bubble(String typeEn, String typeEs, String imgPrefix,
			float originX, float originY, Color text, float textX, float textY,
			float scaleText, Color foreground, Color background,
			boolean flipHorizontal, boolean flipVertical) {
		ModelEntity main = repoEntity(
				"Bubble (" + typeEn
						+ (flipVertical || flipHorizontal ? " - Reversed" : "")
						+ ")",
				"Viñeta comic ("
						+ typeEs
						+ (flipVertical || flipHorizontal ? " - Revertido" : "")
						+ ")", buildDescriptionEn(""), buildDescriptionEs(""),
				imgPrefix + "t.png",
				RepoCategories.ELEMENTS_OBJECTS.toString(), (String) null)
				.getLastEntity();
		editable(true);
		tag(RepoTags.DESIGN_FLAT).tag(RepoTags.TYPE_GUI)
				.tag("bubble", "viñeta").tag("comic", "cómic").tag("tebeo");

		ModelEntity bubble = entity(main, 0, 0).getLastEntity();
		ModelEntity backgroundEnt = entity(bubble, imgPrefix + "b.png", 0, 0)
				.getLastEntity();
		backgroundEnt.setColor(background);
		ModelEntity foregroundEnt = entity(bubble, imgPrefix + "f.png", 0, 0)
				.getLastEntity();
		foregroundEnt.setColor(foreground);
		bubble.setOriginX(originX);
		bubble.setOriginY(originY);

		// Text
		ModelEntity textEntity = textEntity(main, "[YOUR TEXT\nHERE]", text,
				textX, textY).getLastEntity();
		scale(scaleText);
		adjustEntity(textEntity);

		// Entrance animation
		if (flipVertical) {
			bubble.setX(getImageDimension(imgPrefix + "b.png").getWidth() + 5);
			bubble.setScaleX(-1);
		}
		if (flipHorizontal) {
			bubble.setY(getImageDimension(imgPrefix + "b.png").getHeight() + 5);
			bubble.setScaleY(-1);
		}
		scaleUpAlphaEntranceAnimation(bubble, true, 0.3F, 0.1F, 0F, 0F, 0F, 1F,
				flipVertical ? -1.0F : 1.0F, flipHorizontal ? -1.0F : 1.0F);
		scaleUpAlphaEntranceAnimation(textEntity, true, 0.3F, 0.1F, 0F, 0F, 0F,
				1F, scaleText, scaleText);
	}

	@Override
	protected String buildDescriptionEn(String descriptionEn) {
		return descriptionEn + "\n\nBubble designed by " + mainPublisher()
				+ "/" + authorName()
				+ " and animated and assembled by the Mokap team.";
	}

	@Override
	protected String buildDescriptionEs(String descriptionEs) {
		return descriptionEs + "\n\nViñeta diseñado por " + mainPublisher()
				+ "/" + authorName()
				+ " y animada y ensamblada por el equipo de Mokap.";
	}

	@Override
	protected String libName() {
		return "Comic bubbles";
	}

	@Override
	protected String libDescriptionEn() {
		return "A library with comic bubbles to make interactive stories/comics";
	}

	@Override
	protected String libDescriptionEs() {
		return "Una librería con diálogos de comic para crear historias interactivas/cómics";
	}

	@Override
	protected String mainPublisher() {
		return "Vector4Free";
	}

	@Override
	protected RepoTags mainSource() {
		return RepoTags.SOURCE_VECTOR4FREE;
	}

	@Override
	protected String additionalTags() {
		return RepoTags.appendTags("", "comic", "tebeo", "dialogue", "diálogo",
				"speech", "talk", "hablar", "conversar", "conversation",
				"bubble");
	}

	@Override
	protected String authorName() {
		return "Kidkie";
	}

	@Override
	protected String authorUrl() {
		return "http://vector4free.com/vectors/author/298/";
	}

	public static void main(String[] args) {
		String[] argsForBuilder = new String[] { "-out", args[0], "-libs",
				Bubbles.class.getName(), "-imagemagick", args[1],
				"-engine-lib", args[2] };
		BuildRepoLibs.main(argsForBuilder);
	}
}
