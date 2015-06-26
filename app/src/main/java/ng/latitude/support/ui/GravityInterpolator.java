package ng.latitude.support.ui;

import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

/**
 * Created by Ng on 15/5/26
 *
 * All Rights Reserved by Ng
 * Copyright © 2015
 */
public final class GravityInterpolator {

    private static final DecelerateInterpolator de = new DecelerateInterpolator();
    private static final AccelerateInterpolator ac = new AccelerateInterpolator();

    public static Interpolator getInstance(boolean visible) {
        return visible ? de : ac;
    }

}
