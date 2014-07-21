package es.eucm.ead.engine.demos;

import es.eucm.ead.editor.demobuilder.DemoBuilder;
import es.eucm.ead.schema.components.behaviors.Behavior;
import es.eucm.ead.schema.components.tweens.AlphaTween;
import es.eucm.ead.schema.components.tweens.MoveTween;
import es.eucm.ead.schema.components.tweens.RotateTween;
import es.eucm.ead.schema.components.tweens.ScaleTween;
import es.eucm.ead.schema.components.tweens.Timeline;
import es.eucm.ead.schema.components.tweens.Tween;
import es.eucm.ead.schema.data.Script;
import es.eucm.ead.schema.effects.ChangeVar;
import es.eucm.ead.schema.effects.SetCamera;
import es.eucm.ead.schema.effects.controlstructures.ScriptCall;
import es.eucm.ead.schema.entities.ModelEntity;

/**
 * Created by Javier Torrente on 9/07/14.
 */
public class CoolDemo2 extends DemoBuilder{

    public CoolDemo2() {
        super("cooldemo");
    }

    @Override
    protected void doBuild() {
        String initialScene = "scenes/menu.json";
        String hud = "huds/default.json";
        String mainScene = "scenes/scene2.json";
        String sceneBackground = "images/map.png";
        xGap = 10;
        yGap = 10;

        // Create the game file
        game(1066, 600, hud, initialScene).changeVar("showbee", "btrue", ChangeVar.Context.GLOBAL);

        // Initial scene. Just loads second scene
        scene(initialScene, sceneBackground).initBehavior().goScene(mainScene);

        // Create hud
        createMainHud(hud);


    }

    private void createMainScene(){

    }

    private void createMainHud(String hud){
        ModelEntity hudEntity = reusableEntity(hud, null, 0, 0).getLastEntity();
        // First child: animated label
        ModelEntity labelEntity = entity(hudEntity, null, 10, 550).label("welcome", "welcome").getLastEntity();
        // Create the animation
        Timeline timeline = new Timeline();
        timeline.setMode(Timeline.Mode.SEQUENCE);
        timeline.setDelay(2.0F);
        timeline.setYoyo(true);
        timeline.setRepeat(1);
        labelEntity.getComponents().add(timeline);
        tween(timeline, MoveTween.class, 200.0F, 500.0F, false, 1.5F, Tween.EaseEquation.BACK);
        tween(timeline, ScaleTween.class, 2.5F, 2.5F, false, 0.5F, Tween.EaseEquation.SINE);
        tween(timeline, RotateTween.class, 180.0F, null, true, 0.5F, Tween.EaseEquation.EXPO);
        tween(timeline, MoveTween.class, 500F, 600F, false, 1.0F, Tween.EaseEquation.CIRC);
        tween(timeline, RotateTween.class, 180F, null, true, 0.5F, Tween.EaseEquation.QUART);
        tween(timeline, ScaleTween.class, 1.5F, 1.5F, false, 1F, Tween.EaseEquation.QUAD);
        tween (timeline, AlphaTween.class, 0F, null, false, 0.5F, Tween.EaseEquation.LINEAR).getLastComponent(AlphaTween.class).setYoyo(true);
        getLastComponent(AlphaTween.class).setRepeat(21);

        // Spanish control button
        entity(hudEntity, null, 600F, 50F).textButton("spanish", "white").touchBehavior().changeVar("_lang", "s\"es\"").addComponent(null, makeSound("sounds/Rain_Inside_House-Mark_DiAngelo-323934112.mp3", null, null) );
        // English control button
        entity(hudEntity, null, 750, 50).textButton("english", "white").touchBehavior().changeVar("_lang", "s\"en\"").addComponent(null, makeSound("sounds/Blop-Mark_DiAngelo-79054334.mp3", null, null));
        // Exit control button
        entity(hudEntity, null, 900, 50).textButton("exit", "white").touchBehavior().endGame();
        // Button for showing/hiding the bee
        entity(hudEntity, null, 50, 50).imageButton("images/bee_showhide.png", "images/bee_showhide.png", "white").touchBehavior().changeVar("showbee","(not $showbee)");
        // Buttons for increasing/decreasing chameleon's alpha
        Behavior touch = entity(hudEntity, null, VerticalAlign.REL_TO_PREVIOUS, HorizontalAlign.LEFT).imageButton("images/chameleon_incalpha.png", "images/chameleon_incalpha.png", "white").touchBehavior().getLastComponent(Behavior.class);
        Script script =scriptCall(touch, "f0.05").script("alpha_inc").getLastModelPiece(Script.class);
        changeEntityProperty(script, "group.color.a", "(+ (prop $_target sgroup.color.a) $alpha_inc)").target(makeEntitiesWithTagExp("chameleon"));
        changeVar(touch, "_effects_volume", "(min f1.0 (+ $_effects_volume f0.2))");
        ScriptCall scriptCall2 = entity(hudEntity, null, VerticalAlign.REL_TO_PREVIOUS, HorizontalAlign.LEFT).imageButton("images/chameleon_decalpha.png", "images/chameleon_decalpha.png", "white").touchBehavior().scriptCall("f-0.05").getLastModelPiece(ScriptCall.class);
        scriptCall2.setScript(script);
        changeVar(touch, "_effects_volume", "(max f0.0 (- $_effects_volume f0.2))");
        // Button for zooming into alien
        SetCamera setCamera = new SetCamera();
        setCamera.setAnimationTime(2);
        setCamera.setCameraId("cameraAlien");
        entity(hudEntity, null, VerticalAlign.REL_TO_PREVIOUS, HorizontalAlign.LEFT).imageButton("images/alien_zoomin.png", "images/alien_zoomin.png", "white").touchBehavior(setCamera);
        // Button for zooming into chameleon
        setCamera = new SetCamera();
        setCamera.setAnimationTime(3);
        setCamera.setCameraId("cameraChameleon");
        entity(hudEntity, null, VerticalAlign.REL_TO_PREVIOUS, HorizontalAlign.LEFT).imageButton("images/chameleon_zoomin.png", "images/chameleon_zoomin.png", "white").touchBehavior(setCamera);
        // Button for zooming out
        setCamera = new SetCamera();
        setCamera.setAnimationTime(0);
        setCamera.setCameraId("defaultCamera");
        entity(hudEntity, null, VerticalAlign.REL_TO_PREVIOUS, HorizontalAlign.LEFT).imageButton("images/zoom_out.png", "images/zoom_out.png", "white").touchBehavior(setCamera);
    }

    protected <T extends Tween> DemoBuilder tween (Object container, Class<T> clazz, Float value1, Float value2, Boolean relative, Float duration, Tween.EaseEquation easeEquation){
        return tween(container, clazz, null, null, null, null, duration, relative, easeEquation, Tween.EaseType.OUT, value1, value2, null, null);
    }
}
