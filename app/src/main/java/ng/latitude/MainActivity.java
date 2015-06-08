package ng.latitude;

import android.app.Fragment;
import android.os.Bundle;

import ng.latitude.support.ui.SingleFragmentActivity;


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
