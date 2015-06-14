package ng.latitude.ui.activity;

import android.app.Fragment;
import android.os.Bundle;

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
    }
}
