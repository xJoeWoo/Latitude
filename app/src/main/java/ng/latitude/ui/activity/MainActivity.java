package ng.latitude.ui.activity;

import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;

import ng.latitude.R;
import ng.latitude.support.conf.Constants;
import ng.latitude.support.conf.Latitude;
import ng.latitude.support.ui.SingleFragmentActivity;
import ng.latitude.ui.fragment.MapFragment;


public class MainActivity extends SingleFragmentActivity {

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
