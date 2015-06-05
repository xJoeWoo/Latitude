package ng.latitude.support.ui;

import android.view.animation.Interpolator;

/**
 * Created by Joe on 2015/5/26.
 */
public class GravityInterpolator implements Interpolator {

    private boolean visible;

    public GravityInterpolator(boolean visible) {
        this.visible = visible;
    }

    @Override
    public float getInterpolation(float input) {
        if (visible)
            return (float) Math.sqrt(input);
        else
            return input * input;
    }
}
