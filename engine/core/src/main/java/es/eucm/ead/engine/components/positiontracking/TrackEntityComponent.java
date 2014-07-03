package es.eucm.ead.engine.components.positiontracking;

/**
 * Created by Javier Torrente on 3/07/14.
 */
public class TrackEntityComponent extends PositionTrackerComponent{

    /**
     * Amount of pixels the x coordinate must be increased when the target
     * entity moves along the x axis.
     *
     */
    private float speedX = 1.0F;
    /**
     * Amount of pixels the y coordinate must be increased when the target
     * entity moves along the y axis.
     *
     */
    private float speedY = 1.0F;

    /**
     * Amount of pixels the x coordinate must be increased when the target
     * entity moves along the x axis.
     *
     */
    public float getSpeedX() {
        return speedX;
    }

    /**
     * Amount of pixels the y coordinate must be increased when the target
     * entity moves along the y axis.
     *
     */
    public float getSpeedY() {
        return speedY;
    }

    public void set(String target, float speedX, float speedY){
        super.set(target);
        this.speedX = speedX;
        this.speedY = speedY;
    }
}
