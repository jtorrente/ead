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
package es.eucm.ead.engine.systems.variables;

import ashley.core.Entity;
import ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import es.eucm.ead.engine.expressions.Expression;
import es.eucm.ead.engine.expressions.ExpressionEvaluationException;
import es.eucm.ead.engine.expressions.Parser;
import es.eucm.ead.engine.expressions.operators.OperatorFactory;
import es.eucm.ead.engine.systems.variables.VarsContext.Variable;
import es.eucm.ead.schema.components.VariableDef;

import java.util.List;
import java.util.Map.Entry;

/**
 * Deals with variables and expressions. Can set values and handles
 * {@link es.eucm.ead.engine.systems.variables.VariablesSystem.VariableListener}
 * . It also can evaluate expressions.
 */
public class VariablesSystem extends EntitySystem {

	/**
	 * Reserved word for identifying the entity that holds an expression that is
	 * being evaluated
	 */
	public static final String THIS = "this";

	/**
	 * Also reserved word for referring to "another" entity
	 */
	public static final String OTHER_ENTITY = "entity";

	private Array<VariableListener> listeners;

	private VarsContext varsContext;

	private ObjectMap<String, Expression> expressionMap;

	private Array<String> pendingToNotify;

	private OperatorFactory operatorFactory;

	public VariablesSystem() {
		this.operatorFactory = new OperatorFactory();
		this.varsContext = new VarsContext();
		this.expressionMap = new ObjectMap<String, Expression>();
		this.pendingToNotify = new Array<String>();
		this.listeners = new Array<VariableListener>();
		this.sleeping = true;
	}

	/**
	 * Updates the context with the given reserved variable entity (
	 * {@link #THIS} or {@link #OTHER_ENTITY}).
	 * 
	 * @param dis
	 *            New entity context. If {@code null}, nothing is done
	 */
	private void setReservedVariable(String varName, Entity dis) {
		if (dis == null)
			return;
		if (!varsContext.hasVariable(varName)) {
			varsContext.registerVariable(varName, dis, Entity.class);
		} else {
			varsContext.setValue(varName, dis);
		}
	}

	/**
	 * Clears entity context once it is not accessible. {@code varName} must be
	 * either {@link #OTHER_ENTITY} or {@link #THIS}.
	 */
	private void clearReservedVariable(String varName) {
		if (varsContext.hasVariable(varName)) {
			varsContext.setValue(varName, null);
		}
	}

	/**
	 * Adds a variable listener. Will be notified of variables changes when
	 * method
	 * {@link es.eucm.ead.engine.systems.variables.VariablesSystem.VariableListener#listensTo(String)}
	 * returns true
	 * 
	 * @param variableListener
	 *            the listener
	 */
	public void addListener(VariableListener variableListener) {
		listeners.add(variableListener);
	}

	/**
	 * @return the variable value.If variable does not exist, returns
	 *         {@code null}
	 */
	public Object getValue(String variable) {
		return varsContext.getValue(variable);
	}

	/**
	 * Equivalent to {@link #setValue(String, String, Entity, Entity)} where
	 * {@code otherEntity} is set to {@code null}. Convenient as most of the
	 * times {@code otherEntity} is not needed.
	 */
	public void setValue(String variable, String expression, Entity dis) {
		setValue(variable, expression, dis, null);
	}

	/**
	 * Evaluates the given {@code expression} and assigns the resulting value to
	 * the given {@code variable}.
	 * 
	 * @param variable
	 *            the variable name. Cannot be {@code null}.
	 * @param expression
	 *            a valid expression. Cannot be {@code null}.
	 * @param dis
	 *            The entity that owns the expression (can be {@code null}).
	 *            Passing a not {@code null} entity allows the expression to
	 *            resolve entity's properties (e.g. a given tag).
	 * @param otherEntity
	 *            An additional entity to be added to the context. Can be
	 *            {@code null}. Useful for expressions where it is necessary to
	 *            access other entities' properties beyond the owner.
	 */
	public void setValue(String variable, String expression, Entity dis,
			Entity otherEntity) {
		if (variable != null) {
			Object value = evaluateExpression(expression, dis, otherEntity);
			if (value != null) {
				Object oldValue = varsContext.getValue(variable);
				if (!value.equals(oldValue)) {
					varsContext.setValue(variable, value);
					// Add each variable pending of notification only once per
					// cycle
					if (!pendingToNotify.contains(variable, false)) {
						pendingToNotify.add(variable);
					}
					sleeping = false;
				}
			}
		}

		else {
			Gdx.app.error("VariablesSystem",
					"Error setting value for variable: It cannot be null");
		}
	}

	/**
	 * Equivalent to {@link #evaluateExpression(String, Entity, Entity)} where
	 * {@code otherEntity} is set to {@code null}. Convenient as most of the
	 * times {@code otherEntity} is not needed.
	 */
	public Object evaluateExpression(String expression, Entity dis) {
		return evaluateExpression(expression, dis, null);
	}

	/**
	 * Schedules an anonymous expression for evaluation on the next
	 * {@link #update(float)}.
	 * 
	 * @param expression
	 *            A valid not-null expression (see the wiki for more details on
	 *            valid expressions).
	 * @param dis
	 *            The entity that owns the expression (can be {@code null}).
	 *            Passing a not {@code null} entity allows the expression to
	 *            resolve entity's properties (e.g. a given tag) by referring to
	 *            reserved variable "$this".
	 * @param otherEntity
	 *            An additional entity to be added to the context. Can be
	 *            {@code null}. Useful for expressions where it is necessary to
	 *            access other entities' properties beyond the owner.
	 */
	public Object evaluateExpression(String expression, Entity dis,
			Entity otherEntity) {
		if (expression != null) {
			// Variable assignation
			Expression e = expressionMap.get(expression);
			if (e == null) {
				e = Parser.parse(expression, operatorFactory);
				expressionMap.put(expression, e);
			}

			try {
				// Set reserved variables "this" and "other entity"
				setReservedVariable(THIS, dis);
				setReservedVariable(OTHER_ENTITY, otherEntity);
				Object value = e.evaluate(varsContext);
				clearReservedVariable(THIS);
				clearReservedVariable(OTHER_ENTITY);
				return value;
			} catch (ExpressionEvaluationException e1) {
				Gdx.app.error("VariablesSystem", "Error evaluating "
						+ expression, e1);
			}

		} else {
			Gdx.app.error(
					"VariablesSystem",
					"Error setting value for variable: Neither variable nor expression should be null");
		}
		return null;
	}

	/**
	 * Equivalent to {@link #evaluateCondition(String, Entity, Entity, boolean)}
	 * where {@code otherEntity} is set to {@code null}. Convenient as most of
	 * the times {@code otherEntity} is not needed.
	 */
	public boolean evaluateCondition(String expression, Entity dis,
			boolean defaultValue) {
		return evaluateCondition(expression, dis, null, defaultValue);
	}

	/**
	 * Convenient method for evaluating boolean expressions. Useful for checking
	 * conditions in {@link es.eucm.ead.engine.components.ConditionedComponent}
	 * s.
	 * 
	 * @param expression
	 *            The boolean expression
	 * @param dis
	 *            The entity that owns the expression (can be {@code null}).
	 *            Passing a not {@code null} entity allows the expression to
	 *            resolve the entity's properties (e.g. a given tag).
	 * @param otherEntity
	 *            An additional entity to be added to the context. Can be
	 *            {@code null}. Useful for expressions where it is necessary to
	 *            access other entities' properties beyond the owner.
	 * @param defaultValue
	 *            The value to be returned if the expression is null (usually
	 *            because it was not defined in the model) or if the expression
	 *            cannot be evaluated to a boolean
	 * @return The result of the evaluation
	 */
	public boolean evaluateCondition(String expression, Entity dis,
			Entity otherEntity, boolean defaultValue) {
		if (expression == null)
			return defaultValue;

		Object result = evaluateExpression(expression, dis, otherEntity);

		if (result == null)
			return defaultValue;

		if (result instanceof Boolean) {
			return ((Boolean) result).booleanValue();
		} else if (result instanceof Integer) {
			return ((Integer) result).intValue() > 0;
		} else {
			return defaultValue;
		}
	}

	@Override
	public void update(float deltaTime) {
		this.sleeping = true;
		while (pendingToNotify.size > 0) {
			String variable = pendingToNotify.removeIndex(0);
			notify(variable, varsContext.getValue(variable));
		}
	}

	/**
	 * Notifies listeners a change in a variable
	 */
	private void notify(String variable, Object value) {
		for (VariableListener listener : listeners) {
			if (listener.listensTo(variable)) {
				listener.variableChanged(variable, value);
			}
		}
	}

	/**
	 * Removes all variables from the system
	 */
	public void clear() {
		varsContext.clear();
	}

	/**
	 * Register a list of variables in the system
	 * 
	 * @param variablesDefinitions
	 *            a list with the variables definitions
	 */
	public void registerVariables(List<VariableDef> variablesDefinitions) {
		varsContext.registerVariables(variablesDefinitions);
		for (Entry<String, Variable> e : varsContext.getVariables().entrySet()) {
			notify(e.getKey(), e.getValue().getValue());
		}
	}

	/**
	 * Listener for changes in variables.
	 */
	public interface VariableListener {

		/**
		 * @return whether this listener is interested in the given variable
		 */
		boolean listensTo(String variableName);

		/**
		 * Notifies a variable change. Gets updated when the value for a
		 * variable changes.
		 * 
		 * @param variableName
		 *            the variable name
		 * @param value
		 *            the new value for the variable
		 */
		void variableChanged(String variableName, Object value);

	}
}
