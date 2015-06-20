package ng.latitude.support.ui;

import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

/**
 * Created by Joe on 2015/5/26.
 */
public class GravityInterpolator {

    private static DecelerateInterpolator de = new DecelerateInterpolator();
    private static AccelerateInterpolator ac = new AccelerateInterpolator();

    public static Interpolator getInstance(boolean visible) {
        return visible ? de : ac;
    }

}
