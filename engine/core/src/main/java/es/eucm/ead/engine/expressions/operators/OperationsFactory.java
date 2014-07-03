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
package es.eucm.ead.engine.expressions.operators;

import es.eucm.ead.engine.Accessor;
import es.eucm.ead.engine.GameView;
import es.eucm.ead.engine.GameLoop;
import es.eucm.ead.engine.expressions.Operation;

/**
 * A factory class for all recognized operations.
 * 
 * @author mfreire
 */
public class OperationsFactory {

	private Accessor accessor;

	private GameView gameView;

	private GameLoop gameLoop;

	public OperationsFactory(GameLoop gameLoop, Accessor accessor,
			GameView gameView) {
		this.accessor = accessor;
		this.gameView = gameView;
		this.gameLoop = gameLoop;
	}

	public Operation createOperation(String name) {
		Operation op = null;
		if ("and".equals(name)) {
			op = new And();
		} else if ("or".equals(name)) {
			op = new Or();
		} else if ("not".equals(name)) {
			op = new Not();
		} else if ("xor".equals(name)) {
			op = new Xor();
		} else if ("+".equals(name)) {
			op = new Add();
		} else if ("-".equals(name)) {
			op = new Sub();
		} else if ("*".equals(name)) {
			op = new Mul();
		} else if ("/".equals(name)) {
			op = new Div();
		} else if ("%".equals(name)) {
			op = new Mod();
		} else if ("pow".equals(name)) {
			op = new Pow();
		} else if ("sqrt".equals(name)) {
			op = new Sqrt();
		} else if ("min".equals(name)) {
			op = new Min();
		} else if ("max".equals(name)) {
			op = new Max();
		} else if ("rand".equals(name)) {
			op = new Rand();
		} else if ("eq".equals(name)) {
			op = new EquivalenceOperation();
		} else if ("le".equals(name)) {
			op = new LowerEqual();
		} else if ("lt".equals(name)) {
			op = new LowerThan();
		} else if ("ge".equals(name)) {
			op = new GreaterEqual();
		} else if ("gt".equals(name)) {
			op = new GreaterThan();
		} else if ("int".equals(name)) {
			op = new AsInt();
		} else if ("f".equals(name)) {
			op = new AsFloat();
		} else if ("bool".equals(name)) {
			op = new AsBoolean();
		} else if ("string".equals(name)) {
			op = new AsString();
		} else if ("concat".equals(name)) {
			op = new Concat();
		} else if ("hastag".equals(name)) {
			op = new HasTag();
		} else if ("prop".equals(name)) {
			op = new AccessProperty(accessor);
		} else if ("layer".equals(name)) {
			op = new GetLayerOperation(gameView);
		} else if ("collection".equals(name)) {
			op = new EntityCollection(gameLoop);
		} else if ("get".equals(name)) {
			op = new GetFromCollection();
		} else if ("size".equals(name)) {
			op = new CollectionSize();
		} else if ("list".equals(name)) {
			op = new List();
		} else if ("randlist".equals(name)) {
			op = new RandomList();
		} else if ("if".equals(name)) {
            op = new IfOperator();
        }

		if (op != null) {
			op.setName(name);
			return op;
		} else {
			throw new IllegalArgumentException("No operation named '" + name
					+ "'");
		}
	}
}
