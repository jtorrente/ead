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

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglNativesLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.vividsolutions.jts.geom.Geometry;
import es.eucm.ead.editor.assets.EditorGameAssets;
import es.eucm.ead.editor.utils.GeometryUtils;
import es.eucm.ead.engine.EngineDesktop;
import es.eucm.ead.engine.assets.GameAssets;
import es.eucm.ead.engine.mock.MockApplication;
import es.eucm.ead.engine.mock.MockFiles;
import es.eucm.ead.schema.components.ModelComponent;
import es.eucm.ead.schema.components.Parallax;
import es.eucm.ead.schema.components.behaviors.Behavior;
import es.eucm.ead.schema.components.behaviors.events.Init;
import es.eucm.ead.schema.components.behaviors.events.Timer;
import es.eucm.ead.schema.components.behaviors.events.Touch;
import es.eucm.ead.schema.data.Dimension;
import es.eucm.ead.schema.data.shape.Polygon;
import es.eucm.ead.schema.effects.AddEntity;
import es.eucm.ead.schema.effects.ChangeVar;
import es.eucm.ead.schema.effects.Effect;
import es.eucm.ead.schema.effects.SetViewport;
import es.eucm.ead.schema.entities.ModelEntity;
import es.eucm.ead.schema.renderers.Frame;
import es.eucm.ead.schema.renderers.Frames;
import es.eucm.ead.schema.renderers.Image;
import es.eucm.ead.schema.renderers.Renderer;
import es.eucm.ead.schema.renderers.Sequence;
import es.eucm.ead.schemax.GameStructure;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Javier Torrente on 30/06/14.
 */
public abstract class GameBuilder {

    public static final String DEFAULT_SCENE_PREF="scenes/s";
    public static final String JSON=".json";

    private static boolean init = false;

    protected HashMap<String, ModelEntity> entities;

    protected ModelEntity lastEntity;
    protected ModelEntity lastScene;

    protected int sceneCount;

    protected float gameWidth;

    protected float gameHeight;

    protected String root;

    protected GameAssets gameAssets;

    protected Json json;

    public GameBuilder(String root){
        init();
        json = new Json();
        this.root = root;
        entities = new HashMap<String, ModelEntity>();
        gameAssets = new GameAssets(Gdx.files);
        gameAssets.setLoadingPath(root, true);
        FileHandle fileHandle = gameAssets.resolve("images/");
        System.out.println(fileHandle.file().getAbsolutePath());
        FileHandle background = fileHandle.child("background.png");
        System.out.println(fileHandle.exists());
        for (FileHandle child: fileHandle.list()){
            System.out.println(child.name());
        }
        System.out.println(background.exists());

        sceneCount = 0;
    }

    private void init(){
        if (!init){
            LwjglNativesLoader.load();
            MockApplication.initStatics();
        }
    }

    public void buildAndRun(){
        build();
        save();
        EngineDesktop engine = new EngineDesktop((int)gameWidth, (int)gameHeight);
        engine.run(root, true);
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
    }

    public void save(){
        for (Map.Entry<String, ModelEntity> entry:entities.entrySet()){
            FileHandle fh =  gameAssets.absolute(root+"/"+entry.getKey());
            if (fh.exists()){
                fh.delete();
            }
            System.out.println("Saving to: "+fh.file().getAbsolutePath());
            json.toJson(entry.getValue(),fh);
        }
    }

    /*public InputStream resolve(String resource){
        FileHandle handle = gameAssets.resolve(resource);
        if (handle!=null) {
            System.out.println(handle.path());
            try {
                return new FileInputStream(handle.file());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        URL url = GameBuilder.class.getResource(resource);
        if (url!=null){
            try {
                return url.openStream();
            } catch (IOException e) {

            }
        }

        File folder = new File(root);
        try {
            return new FileInputStream(new File(folder, resource));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }*/

    public ModelEntity getLastEntity(){
        return lastEntity;
    }

    public ModelEntity getLastScene(){
        return lastScene;
    }

    public HashMap<String, ModelEntity> build(){
        doBuild();
        return entities;
    }

    public abstract void doBuild();

    public Dimension getImageDimension(String imageUri){
        InputStream inputStream = gameAssets.resolve(imageUri).read();
        ImageInputStream in = null;
        try {
            in = ImageIO.createImageInputStream(inputStream);
            final Iterator<ImageReader> readers = ImageIO.getImageReaders(in);
            if (readers.hasNext()) {
                ImageReader reader = readers.next();
                try {
                    reader.setInput(in);
                    Dimension dimension = new Dimension();
                    dimension.setWidth(reader.getWidth(0));
                    dimension.setHeight(reader.getHeight(0));
                    return dimension;
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    reader.dispose();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public GameBuilder game(int width, int height){
        gameWidth = width;
        gameHeight = height;

        ModelEntity game = entity().getLastEntity();
        Behavior init = new Behavior();
        init.setEvent(new Init());
        AddEntity loadScene = new AddEntity();
        loadScene.setEntityUri(DEFAULT_SCENE_PREF+sceneCount+JSON);
        loadScene.setTarget("(layer sscene_content)");
        init.getEffects().add(loadScene);
        SetViewport viewport = new SetViewport();
        viewport.setWidth(width);
        viewport.setHeight(height);
        init.getEffects().add(viewport);
        game.getComponents().add(init);
        entities.put(GameStructure.GAME_FILE, game);
        return this;
    }

    public GameBuilder singleSceneGame(String backgroundUri){
        Dimension backgroundDim = getImageDimension(backgroundUri);
        game(backgroundDim.getWidth(), backgroundDim.getHeight()).scene(backgroundUri);
        return this;
    }

    public GameBuilder entity(){
        lastEntity = new ModelEntity();
        return this;
    }


    public GameBuilder reusableEntity(String entityUri, String imageUri, float x, float y){
        ModelEntity modelEntity = entity(imageUri, x, y).getLastEntity();
        entities.put(entityUri, modelEntity);
        return this;
    }


    public GameBuilder entity(String imageUri, float x, float y){
        ModelEntity modelEntity = entity().getLastEntity();
        Image image = new Image();
        image.setUri(imageUri);
        image.setCollider(createSchemaCollider(imageUri));
        modelEntity.getComponents().add(image);
        modelEntity.setX(x);
        modelEntity.setY(y);
        return this;
    }

    public GameBuilder frame(String frameUri, float duration){
        return frame (getLastEntity(), frameUri, duration);
    }

    public GameBuilder frame(ModelEntity modelEntity, String frameUri, float duration){
        Frames frames = null;

        Frame frame = new Frame();
        frame.setTime(duration);
        Image image = new Image();
        image.setCollider(createSchemaCollider(frameUri));
        image.setUri(frameUri);
        frame.setRenderer(image);

        for (ModelComponent modelComponent: modelEntity.getComponents()){
            if (modelComponent instanceof Frames){
                frames = ((Frames)modelComponent);
            } else if (modelComponent instanceof Renderer){
                frames = new Frames();
                frames.setSequence(Sequence.LINEAR);
                Frame prevFrame = new Frame();
                prevFrame.setRenderer((Renderer)modelComponent);
                prevFrame.setTime(duration);
                frames.getFrames().add(prevFrame);
                modelEntity.getComponents().remove(modelComponent);
                modelEntity.getComponents().add(frames);
                break;
            }
        }

        if (frames == null){
            frames = new Frames();
            frames.setSequence(Sequence.LINEAR);
            modelEntity.getComponents().add(frames);
        }

        frames.getFrames().add(frame);
        return this;
    }

    public ChangeVar changeVar(String variable, String expression){
        ChangeVar changeVar = new ChangeVar();
        changeVar.setVariable(variable);
        changeVar.setExpression(expression);
        return changeVar;
    }

    public GameBuilder entity(String imageUri, float x, float y, ModelEntity parent){
        parent.getChildren().add(entity(imageUri, x, y).getLastEntity());
        return this;
    }

    public GameBuilder entity(String imageUri, VerticalAlign verticalAlign, HorizontalAlign horizontalAlign, ModelEntity parent){
        Dimension imageDim = getImageDimension(imageUri);
        float x = horizontalAlign==HorizontalAlign.LEFT?0:(horizontalAlign==HorizontalAlign.RIGHT?gameWidth-imageDim.getWidth():(gameWidth-imageDim.getWidth())/2.0F);
        float y = verticalAlign==VerticalAlign.DOWN?0:(verticalAlign==VerticalAlign.UP?gameHeight-imageDim.getHeight():(gameHeight-imageDim.getHeight())/2.0F);
        return entity (imageUri, x, y, parent);
    }

    public GameBuilder scene(String imageUri){
        lastScene = entity().getLastEntity();
        lastScene.getChildren().add(entity(imageUri, 0 , 0).getLastEntity());
        String sceneId = DEFAULT_SCENE_PREF+(sceneCount++)+ JSON;
        entities.put(sceneId, lastScene);
        return this;
    }

    public GameBuilder parallax(float d){
        return parallax(getLastEntity(), d);
    }

    public GameBuilder parallax(ModelEntity parent, float d){
        Parallax parallax = new Parallax();
        parallax.setD(d);
        parent.getComponents().add(parallax);
        return this;
    }

    public GameBuilder infiniteTimer(ModelEntity parent, int time, Effect...effects){
        Behavior behavior = new Behavior();
        Timer timer = new Timer();
        timer.setTime(time);
        timer.setRepeat(-1);
        behavior.setEvent(timer);
        for (Effect effect: effects){
            behavior.getEffects().add(effect);
        }
        parent.getComponents().add(behavior);
        return this;
    }

    public GameBuilder touchBehavior(Effect...effects){
        return touchBehavior(getLastEntity(), effects);
    }

    public GameBuilder touchBehavior(ModelEntity parent, Effect...effects){
        Behavior behavior = new Behavior();
        behavior.setEvent(new Touch());
        parent.getComponents().add(behavior);
        for (Effect effect: effects){
            behavior.getEffects().add(effect);
        }
        return this;
    }

    public GameBuilder initBehavior(Effect...effects){
        return initBehavior(getLastEntity(), effects);
    }

    public GameBuilder initBehavior(ModelEntity parent, Effect...effects){
        Behavior behavior = new Behavior();
        behavior.setEvent(new Init());
        parent.getComponents().add(behavior);
        for (Effect effect: effects){
            behavior.getEffects().add(effect);
        }
        return this;
    }

    public List<Polygon> createSchemaCollider(String imageUri) {
		List<Polygon> collider = new ArrayList<Polygon>();
		Pixmap pixmap = new Pixmap(gameAssets.resolve(imageUri));
		Array<Geometry> geometryArray = GeometryUtils
				.findBorders(pixmap, .1, 2);
		for (Geometry geometry : geometryArray) {
			collider.add(GeometryUtils.jtsToSchemaPolygon(geometry));
		}
		pixmap.dispose();
		return collider;
	}

	public Array<com.badlogic.gdx.math.Polygon> createEngineCollider(
			String imageUri) {
		Array<com.badlogic.gdx.math.Polygon> collider = new Array<com.badlogic.gdx.math.Polygon>();
		for (Polygon polygon : createSchemaCollider(imageUri)) {
			float[] points = new float[polygon.getPoints().size()];
			for (int i = 0; i < polygon.getPoints().size(); i++) {
				points[i] = polygon.getPoints().get(i);
			}
			com.badlogic.gdx.math.Polygon contour = new com.badlogic.gdx.math.Polygon(
					points);
			collider.add(contour);
		}
		return collider;
	}

    public String collidersToString(FileHandle folder){
        String colliders="";
        for (FileHandle child : folder.list("png")) {
            String collider = "\"collider\": [\n";
            for (Polygon polygon : createSchemaCollider(child.path())) {
                collider += "{ \"points\":[\n";
                for (float f : polygon.getPoints()) {
                    collider += f + ", ";
                }
                collider = collider.substring(0, collider.length() - 2);
                collider += "\n]},\n";
            }
            collider = collider.substring(0, collider.length() - 2);
            collider += "\n]\n";

            colliders+="****************************";
            colliders+=child.name();
            colliders+="****************************";
            colliders+=collider;
        }
        return colliders;
    }

    public enum HorizontalAlign{
        LEFT, CENTER, RIGHT;
    }
    public enum VerticalAlign{
        UP, CENTER, DOWN;
    }

}
