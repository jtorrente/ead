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
package es.eucm.ead.engine.tests.systems.behaviors;

import es.eucm.ead.engine.EntitiesLoader;
import es.eucm.ead.engine.GameLoop;
import es.eucm.ead.engine.entities.ActorEntity;
import es.eucm.ead.engine.mock.schema.MockEffect;
import es.eucm.ead.engine.mock.schema.MockEffectExecutor;
import es.eucm.ead.engine.processors.ComponentProcessor;
import es.eucm.ead.engine.systems.EffectsSystem;
import es.eucm.ead.schema.entities.ModelEntity;
import org.junit.Before;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public abstract class BehaviorTest {

	protected GameLoop gameLoop;

	private EntitiesLoader entitiesLoader;

	@Before
	public void setUp() {
		gameLoop = new GameLoop();
		entitiesLoader = new EntitiesLoader(null, gameLoop, null);
		addSystems(gameLoop);

		EffectsSystem effectsSystem = new EffectsSystem(gameLoop);
		gameLoop.addSystem(effectsSystem);
		effectsSystem.registerEffectExecutor(MockEffect.class,
				new MockEffectExecutor());

		Map<Class, ComponentProcessor> componentProcessors = new HashMap<Class, ComponentProcessor>();
		registerComponentProcessors(gameLoop, componentProcessors);
		for (Entry<Class, ComponentProcessor> e : componentProcessors
				.entrySet()) {
			entitiesLoader.registerComponentProcessor(e.getKey(), e.getValue());
		}
	}

	/**
	 * Converts a model entity into an engine entity, and adds it to the game
	 * loop
	 * 
	 * @return the engine entity
	 */
	protected ActorEntity addEntity(ModelEntity modelEntity) {
		return entitiesLoader.addEntity(modelEntity);
	}

	/**
	 * Adds the require component processors for the test
	 */
	protected abstract void registerComponentProcessors(GameLoop gameLoop,
			Map<Class, ComponentProcessor> componentProcessors);

	/**
	 * Adds the require systems for the test
	 */
	public abstract void addSystems(GameLoop gameLoop);

}