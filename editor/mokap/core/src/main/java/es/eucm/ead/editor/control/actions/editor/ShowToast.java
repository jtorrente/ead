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

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import es.eucm.ead.editor.control.Controller;
import es.eucm.ead.editor.control.actions.EditorAction;
import es.eucm.ead.editor.view.SkinConstants;
import es.eucm.ead.editor.view.widgets.Toast;
import es.eucm.ead.editor.view.widgets.WidgetBuilder;

/**
 * <p>
 * Shows a toast with the given text
 * </p>
 * <dl>
 * <dt><strong>Arguments</strong></dt>
 * <dd><strong>args[0]</strong> <em>String</em> The toast text</dd>
 * <dd><strong>args[1]</strong>
 * <em>{@link Boolean} (Optional, default = false)</em> If undo option must
 * appear in the toast</dd>
 * </dl>
 */
public class ShowToast extends EditorAction {

	private static final float TOAST_TIME = 1.0f;

	private Toast toast;

	private Label undo;

	public ShowToast() {
		super(true, true, new Class[] { String.class }, new Class[] {
				String.class, Boolean.class });
	}

	@Override
	public void initialize(Controller controller) {
		super.initialize(controller);
		Skin skin = controller.getApplicationAssets().getSkin();
		toast = new Toast(skin);
		toast.setTouchable(Touchable.childrenOnly);
		toast.pad(WidgetBuilder.dpToPixels(8));
		toast.setComputeInvisibles(false);

		undo = WidgetBuilder.label(controller.getApplicationAssets().getI18N()
				.m("undo").toUpperCase(), SkinConstants.STYLE_TOAST_ACTION);
		undo.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				ShowToast.this.controller.action(Undo.class);
				toast.remove();
			}
		});
		toast.add(undo).margin(
				WidgetBuilder.dpToPixels(WidgetBuilder.UNIT_SIZE), 0, 0, 0);
	}

	@Override
	public void perform(Object... args) {
		toast.setText((String) args[0]);

		boolean showUndo = args.length == 2 ? (Boolean) args[1] : false;
		undo.setVisible(showUndo);

		toast.pack();
		Group modalsContainer = controller.getViews().getViewsContainer();
		float x = modalsContainer.getWidth() / 2.0f - toast.getWidth() / 2.0f;
		float y = modalsContainer.getHeight() / 10.0f;
		toast.setPosition(x, y);
		toast.clearActions();
		toast.addAction(Actions.sequence(Actions.alpha(0.0f),
				Actions.alpha(1.0f, TOAST_TIME, Interpolation.exp5Out),
				Actions.delay(TOAST_TIME * (showUndo ? 10 : 1)),
				Actions.alpha(0.0f, TOAST_TIME, Interpolation.exp5Out),
				Actions.removeActor()));
		controller.getViews().addToModalsContainer(toast);
	}
}
