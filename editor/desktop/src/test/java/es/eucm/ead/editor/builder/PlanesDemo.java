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
package es.eucm.ead.editor.builder;

import es.eucm.ead.schema.components.Chase;
import es.eucm.ead.schema.components.Parallax;
import es.eucm.ead.schema.components.Tags;
import es.eucm.ead.schema.components.TrackEntity;
import es.eucm.ead.schema.components.tweens.AlphaTween;
import es.eucm.ead.schema.components.tweens.MoveTween;
import es.eucm.ead.schema.components.tweens.ScaleTween;
import es.eucm.ead.schema.data.Parameter;
import es.eucm.ead.schema.data.Script;
import es.eucm.ead.schema.effects.AddComponent;
import es.eucm.ead.schema.effects.AddEntity;
import es.eucm.ead.schema.effects.ChangeVar;
import es.eucm.ead.schema.effects.controlstructures.If;
import es.eucm.ead.schema.effects.controlstructures.IfThenElseIf;
import es.eucm.ead.schema.effects.controlstructures.ScriptCall;
import es.eucm.ead.schema.entities.ModelEntity;

/**
 * Created by Javier Torrente on 2/07/14.
 */
public class PlanesDemo extends GameBuilder{

    public PlanesDemo(){
        super("planes-demo");
    }

    @Override
    public void doBuild() {
        ModelEntity firstScene = singleSceneGame("images/background.png").getLastScene();
        parallax(firstScene.getChildren().get(0), 0F);
        entity("images/rocks_down.png", VerticalAlign.DOWN, HorizontalAlign.LEFT, firstScene);
        parallax(0.5f);
        entity("images/rocks_up.png", VerticalAlign.UP, HorizontalAlign.LEFT, firstScene);
        parallax(0.5f);
        MoveTween moveTween = new MoveTween();
        moveTween.setX(-3000);
        moveTween.setDuration(30);

        ChangeVar changeVar = new ChangeVar();
        changeVar.setVariable("chasedEntity");
        changeVar.setExpression("$_newest");
        changeVar.setContext(ChangeVar.Context.GLOBAL);

        AddComponent addComponent = new AddComponent();
        Chase chase = new Chase();
        chase.setSpeedX(-1.0f);
        chase.setSpeedY(0.0f);
        chase.setRelativeSpeed(true);
        chase.setCenterDistance(true);
        chase.setMinDistance(0);
        chase.setMaxDistance(0);
        chase.setTarget("$chasedEntity");
        //addComponent.setComponent(moveTween);
        TrackEntity trackEntity = new TrackEntity();
        trackEntity.setTarget("$chasedEntity");
        trackEntity.setSpeedX(-1.0F);
        trackEntity.setSpeedY(0.0F);
        addComponent.setComponent(trackEntity);
        addComponent.setTarget("(layer scamera)");

        String redUri = spaceship("Red");
        String blueUri = spaceship("Blue");
        String greenUri = spaceship("Green");
        String yellowUri = spaceship("Yellow");

        // Create the star
        ModelEntity star = entity("images/starGold.png",20, 400, firstScene).getLastEntity();
        Tags tags = new Tags();
        tags.getTags().add("star");
        star.getComponents().add(tags);

        initBehavior(firstScene, changeVar, addComponent);
        infiniteTimer(firstScene, 1, scriptCall(redUri, blueUri, greenUri, yellowUri));
    }

    private ScriptCall scriptCall(String... uris){
        ScriptCall scriptCall = new ScriptCall();
        scriptCall.getInputArgumentValues().add("(rand i0 i4)");
        scriptCall.getInputArgumentValues().add("(rand i0 i2)");
        scriptCall.getInputArgumentValues().add("(rand i80 i270)");
        scriptCall.getInputArgumentValues().add("(rand i15 i30)");
        scriptCall.setScript(script(uris));
        return scriptCall;
    }

    private Script script (String... uris){
       Script script = new Script();
        script.getInputArguments().add("color");
        script.getInputArguments().add("coin");
        script.getInputArguments().add("y");
        script.getInputArguments().add("duration");

        IfThenElseIf ifThenElseIf = new IfThenElseIf();
        addSpaceships(ifThenElseIf, uris);
        script.getEffects().add(ifThenElseIf);
        return script;
    }

    private void addSpaceships(IfThenElseIf ifThenElseIf, String... uris){
        int i=0;
        for (String uri: uris){
            If ifEffect = null;
            if (ifThenElseIf.getEffects().size() == 0){
                ifEffect = ifThenElseIf;
            } else {
                ifEffect = new If();
                ifThenElseIf.getElseIfList().add(ifEffect);
            }

            ifEffect.setCondition("(eq $color i0)".replace("0", ""+i));

            ChangeVar changeVar = new ChangeVar();
            changeVar.setVariable("left");
            changeVar.setExpression("(eq $coin i0)");
            ifEffect.getEffects().add(changeVar);

            AddEntity addEntity = new AddEntity();
            /*Parameter parameter = new Parameter();
            parameter.setName("duration");
            parameter.setValue("$duration");*/
            addEntity.setEntityUri(uri);
            //addEntity.getParameters().add(parameter);
            ifEffect.getEffects().add(addEntity);

            If flipIf = new If();
            flipIf.setCondition("(not $left)");
            //flipIf.setCondition("btrue");
            ScaleTween scaleTween = new ScaleTween();
            scaleTween.setScaleX(-2.0F);
            scaleTween.setRelative(true);
            scaleTween.setDuration(0);
            AddComponent addComponent = new AddComponent();
            addComponent.setComponent(scaleTween);
            addComponent.setTarget("$_newest");
            flipIf.getEffects().add(addComponent);
            ifEffect.getEffects().add(flipIf);

            MoveTween positionShip = new MoveTween();
            positionShip.setDuration(0);
            Parameter parameter = new Parameter();
            parameter.setName("x");
            parameter.setValue("(if $left i-100 i3700)");
            //parameter.setValue("i-100");
            //parameter.setValue("i200");
            positionShip.getParameters().add(parameter);
            parameter = new Parameter();
            parameter.setName("y");
            parameter.setValue("$y");
            positionShip.getParameters().add(parameter);
            addComponent = new AddComponent();
            addComponent.setComponent(positionShip);
            addComponent.setTarget("$_newest");
            ifEffect.getEffects().add(addComponent);

            MoveTween spaceMove = new MoveTween();
            spaceMove.setRelative(true);
            spaceMove.setDelay(0.1f);
            parameter = new Parameter();
            parameter.setName("x");
            parameter.setValue("(if $left i3000 i-3000)");
            //parameter.setValue("i3100");
            spaceMove.getParameters().add(parameter);
            parameter = new Parameter();
            parameter.setName("duration");
            parameter.setValue("$duration");
            spaceMove.getParameters().add(parameter);
            addComponent = new AddComponent();
            addComponent.setTarget("$_newest");
            addComponent.setComponent(spaceMove);
            ifEffect.getEffects().add(addComponent);

            AlphaTween alphaTween = new AlphaTween();
            alphaTween.setRelative(false);
            alphaTween.setAlpha(0.0F);
            alphaTween.setDuration(0.5f);
            parameter = new Parameter();
            parameter.setName("delay");
            parameter.setValue("$duration");
            alphaTween.getParameters().add(parameter);
            addComponent = new AddComponent();
            addComponent.setTarget("$_newest");
            addComponent.setComponent(alphaTween);
            ifEffect.getEffects().add(addComponent);

            i++;
        }
    }

    private String spaceship(String color){
        String uri = "spaceship/red.json".replace("red", color.toLowerCase());
        reusableEntity(uri, "images/planeRed1.png".replace("Red", color), -100, 0).frame("images/planeRed2.png".replace("Red", color), 0.2F).frame("images/planeRed3.png".replace("Red", color), 0.2F);

        /*RemoveComponent removeComponent = new RemoveComponent();
        removeComponent.setComponent(Parallax.class.getCanonicalName());
        removeComponent.setTarget("$chasedEntity");

        AddComponent addComponent = new AddComponent();
        Parallax parallax = new Parallax();
        parallax.setD(0);
        addComponent.setComponent(parallax);*/

        ChangeVar changeVar = new ChangeVar();
        changeVar.setVariable("chasedEntity");
        changeVar.setExpression("$_this");

        Chase chase = new Chase();
        chase.setRelativeSpeed(false);
        chase.setSpeedX(25);
        chase.setSpeedY(25);
        chase.setMinDistance(28);
        chase.setMaxDistance(30);
        chase.setTarget("$chasedEntity");

        AddComponent addComponent = new AddComponent();
        addComponent.setTarget("(collection (hastag $entity sstar))");
        addComponent.setComponent(chase);

        touchBehavior(changeVar, addComponent);
        return uri;
    }

    public static void main (String[]args){
        PlanesDemo planesDemo = new PlanesDemo();
        planesDemo.buildAndRun();
    }
}
