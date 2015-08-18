package ng.latitude.ui.activity;

import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import ng.latitude.R;
import ng.latitude.support.conf.Constants;
import ng.latitude.support.conf.Latitude;
import ng.latitude.support.ui.SingleFragmentActivity;
import ng.latitude.ui.fragment.MapFragment;

/**
 * Created by Ng on 15/5/24
 * <p>
 * All Rights Reserved by Ng
 * Copyright © 2015
 */

public class MainActivity extends SingleFragmentActivity {

    private static final Handler handler = new Handler();

    public Handler getHandler() {
        return handler;
    }

    @Override
    protected Fragment inflateFragment() {
        MapFragment mapFragment = MapFragment.newInstance();
        setOnBackPressedListener(mapFragment);
        return mapFragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            getWindow().setStatusBarColor(getResources().getColor(Latitude.getUserInfo().getForce() == Constants.Force.ONE ? R.color.force_1 : R.color.force_2));
    }
}
