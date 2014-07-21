package es.eucm.ead.editor.demobuilder;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import es.eucm.ead.schema.data.shape.Circle;
import es.eucm.ead.schema.data.shape.Polygon;
import es.eucm.ead.schema.renderers.Frame;
import es.eucm.ead.schema.renderers.Frames;
import es.eucm.ead.schema.renderers.Image;
import es.eucm.ead.schema.renderers.Renderer;
import es.eucm.ead.schema.renderers.ShapeRenderer;
import es.eucm.ead.schema.renderers.State;
import es.eucm.ead.schema.renderers.States;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Javier Torrente on 9/07/14.
 */
public class EntityDimensionBuilder {

    private Map<Class<? extends Renderer>, RendererDimensionBuilder> rendererDimensionBuilders = new HashMap<Class<? extends Renderer>, RendererDimensionBuilder>();

    public EntityDimensionBuilder (){
        rendererDimensionBuilders.put(ShapeRenderer.class, new ShapeDimensionBuilder());
        rendererDimensionBuilders.put(Frames.class, new FramesDimensionBuilder());
        rendererDimensionBuilders.put(States.class, new StatesDimensionBuilder());
        rendererDimensionBuilders.put(Image.class, new ImageDimensionBuilder());
    }

    public Rectangle getDimension(Renderer renderer){
        RendererDimensionBuilder builder = rendererDimensionBuilders.get(renderer);
        if (builder!=null){
            return builder.rendererDimension(renderer);
        }
        return null;
    }

    private class ShapeDimensionBuilder extends RendererDimensionBuilder<ShapeRenderer>{

        @Override
        public Rectangle rendererDimension(ShapeRenderer renderer) {
            if (renderer.getShape() instanceof es.eucm.ead.schema.data.shape.Rectangle){
                es.eucm.ead.schema.data.shape.Rectangle rect = (es.eucm.ead.schema.data.shape.Rectangle)renderer.getShape();
                return new Rectangle(0,0, rect.getWidth(), rect.getHeight());
            } else if (renderer.getShape() instanceof Circle){
                Circle circle = (Circle)renderer.getShape();
                return new Rectangle(0,0,circle.getRadius()*2, circle.getRadius()*2);
            } else if (renderer.getShape() instanceof Polygon){
                Polygon polygon = (Polygon)renderer.getShape();
                Vector2 x = new Vector2();
                Vector2 y = new Vector2();
                processPolygon(polygon, x, y);
            }
            return null;
        }
    }

    private class StatesDimensionBuilder extends RendererDimensionBuilder<States>{

        @Override
        public Rectangle rendererDimension(States renderer) {
            Vector2 x = new Vector2();
            Vector2 y = new Vector2();
            x.set(Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY);
            y.set(Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY);
            for (State state: renderer.getStates()){
                Rectangle frameDimension = rendererDimensionBuilders.get(state.getRenderer().getClass()).rendererDimension(state.getRenderer());
                x.set(Math.min(frameDimension.x, x.x), Math.max(frameDimension.x+frameDimension.width, x.y));
                y.set(Math.min(frameDimension.y, y.x), Math.max(frameDimension.y + frameDimension.height, y.y));
            }
            return new Rectangle(x.x, y.x, x.y-x.x, y.y-y.x);
        }
    }

    private class FramesDimensionBuilder extends RendererDimensionBuilder<Frames>{

        @Override
        public Rectangle rendererDimension(Frames renderer) {
            Vector2 x = new Vector2();
            Vector2 y = new Vector2();
            x.set(Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY);
            y.set(Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY);
            for (Frame frame: renderer.getFrames()){
                Rectangle frameDimension = rendererDimensionBuilders.get(frame.getRenderer().getClass()).rendererDimension(frame.getRenderer());
                x.set(Math.min(frameDimension.x, x.x), Math.max(frameDimension.x+frameDimension.width, x.y));
                y.set(Math.min(frameDimension.y, y.x), Math.max(frameDimension.y + frameDimension.height, y.y));
            }
            return new Rectangle(x.x, y.x, x.y-x.x, y.y-y.x);
        }
    }

    private class ImageDimensionBuilder extends RendererDimensionBuilder<Image>{

        @Override
        public Rectangle rendererDimension(Image renderer) {
            return colliderDimension(renderer.getCollider());
        }
    }

    private abstract class RendererDimensionBuilder<T extends Renderer>{
        /**
         * @return  A minimum rectangle that contains the given renderer. Given on the entity's coordinate system.
         */
        public abstract Rectangle rendererDimension(T renderer);

        protected Rectangle colliderDimension(Array<Polygon> collider){
            Vector2 x = new Vector2();
            Vector2 y = new Vector2();
            x.set(Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY);
            y.set(Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY);
            if (collider != null) {
                for (Polygon polygon : collider) {
                    processPolygon(polygon, x, y);
                }
                return new Rectangle(x.x, y.x, x.y-x.x, y.y-y.x);
            }
            return null;
        }

        protected void processPolygon(Polygon polygon, Vector2 x, Vector2 y){
            Vector2 tmp = new Vector2();
            for (int i = 0; i < polygon.getPoints().size; i += 2) {
                tmp.set(polygon.getPoints().get(i),
                        polygon.getPoints().get(i + 1));
                x.set(Math.min(tmp.x, x.x), Math.max(tmp.x, x.y));
                y.set(Math.min(tmp.y, y.x), Math.max(tmp.y, y.y));
            }
        }
    }
}
