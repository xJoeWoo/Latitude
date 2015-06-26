package ng.latitude.support.ui;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import ng.latitude.R;

/**
 * Created by Ng on 15/6/8
 *
 * All Rights Reserved by Ng
 * Copyright © 2015
 */
public abstract class SingleFragmentActivity extends AppCompatActivity {

    private FragmentManager fm = getFragmentManager();
    private Fragment currentFragment;
    private OnBackPressedListener backPressedListener;

    protected Fragment getCurrentFragment() {
        return currentFragment;
    }

    protected void setOnBackPressedListener(OnBackPressedListener listener) {
        backPressedListener = listener;
    }

    protected abstract Fragment inflateFragment();

    protected void replaceFragment(Fragment fragment) {
        currentFragment = fragment;
        fm.beginTransaction().replace(R.id.rl_fragment_container, currentFragment).commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_single_fragment);

        currentFragment = fm.findFragmentById(R.id.rl_fragment_container);

        if (currentFragment == null) {
            currentFragment = inflateFragment();
            fm.beginTransaction().add(R.id.rl_fragment_container, currentFragment).commit();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.exit(0);
    }

    @Override
    public void onBackPressed() {
        if (backPressedListener != null)
            if (backPressedListener.onActivityBackPressed())
                super.onBackPressed();
    }

    public enum BackStatus {Normal, SettingPosition}

    public interface OnBackPressedListener {

        /**
         *
         * @return 返回<code>true</code>将调用 {@link Activity#onBackPressed()}，<code>false</code>将截留响应
         */
        boolean onActivityBackPressed();
    }
}
