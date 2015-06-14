package ng.latitude.ui.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import ng.latitude.R;
import ng.latitude.support.conf.Constants;
import ng.latitude.support.conf.Latitude;
import ng.latitude.support.map.MapUnit;
import ng.latitude.support.ui.BottomButtons;
import ng.latitude.support.ui.BottomInfo;
import ng.latitude.support.ui.GravityInterpolator;
import ng.latitude.support.ui.SingleFragmentActivity;

/**
 * Created by Ng on 15/6/8.
 */
public class MapFragment extends Fragment implements MapUnit.OnMarkerAddedListener, MapUnit.OnLocationFixListener, SingleFragmentActivity.OnBackPressedListener {


    private SingleFragmentActivity activity;
    private View rootView;
    private ImageView ivSetPosition;
    private BottomButtons bBtn;
    private BottomInfo bInf;
    private MapUnit mapUtils;
    private SingleFragmentActivity.BackStatus currentBackStatus = SingleFragmentActivity.BackStatus.Normal;

    public static MapFragment newInstance() {
        return new MapFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.frag_main, container, false);

        findViews(rootView);

        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.tb_map);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));

        activity = (SingleFragmentActivity) getActivity();
        activity.setSupportActionBar(toolbar);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapUtils = new MapUnit(getActivity(), rootView.findViewById(R.id.map_main));
        mapUtils.onCreate(savedInstanceState);
        setListeners();

        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.tb_map);
        if (!Latitude.getUserInfo().getName().isEmpty())
            toolbar.setTitle(Latitude.getUserInfo().getName());
    }

    private void findViews(View v) {
        ivSetPosition = (ImageView) v.findViewById(R.id.iv_main_set_position);
        bBtn = (BottomButtons) v.findViewById(R.id.bbtn_main);
        bInf = (BottomInfo) v.findViewById(R.id.binf_main);
    }

    private void setListeners() {
        bBtn.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                addMarkersToMap(aMap.getCameraPosition().target);
                mapUtils.addMarkersToMap(null, null);
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPosition(false);
            }
        });
        mapUtils.setOnMarkerAddedListener(this);
        mapUtils.setOnLocationFixListener(this);
    }

    @Override
    public void onMarkerAdded() {
        setPosition(false);
    }

    @Override
    public void onMarkerFailed(String title, String snippet) {
        mapUtils.addMarkersToMap(title, snippet);
    }


    @Override
    public void startFixLocation() {
        bInf.show();
    }

    @Override
    public void stopFixLocation() {
        bInf.hide();
    }

    @Override
    public void lbsFixedLocation() {
        bInf.show();
        bInf.setMainText(R.string.widget_bottom_info_main_gps);
    }

    @Override
    public void gpsFixedLocation() {
        bInf.hide();
        bInf.reset();
    }

    @Override
    public void gpsStatus(boolean status) {
        if (status)
            bInf.reset();
        else
            bInf.setSubText(R.string.widget_bottom_info_sub_gps_not_available);
    }

    public void setPosition(boolean isSetting) {
        if (isSetting) {
            currentBackStatus = SingleFragmentActivity.BackStatus.SettingPosition;
            startSetPositionAnim(true);
            bBtn.show();
            activity.findViewById(R.id.action_add).setVisibility(View.GONE);
        } else {
            currentBackStatus = SingleFragmentActivity.BackStatus.Normal;
            startSetPositionAnim(false);
            bBtn.hide();
            activity.findViewById(R.id.action_add).setVisibility(View.VISIBLE);
        }
    }

    private void startSetPositionAnim(final boolean visible) {

        float transY = rootView.findViewById(R.id.rv_main).getHeight() - ivSetPosition.getY();
        ObjectAnimator oa = ObjectAnimator.ofFloat(ivSetPosition, Constants.OBJECT_ANIM_TRANSLATION_Y, visible ? transY : 0, visible ? 0 : 0).setDuration(Constants.ANIM_COMMON_DURATION);
        oa.setInterpolator(new GravityInterpolator(visible));
        oa.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (visible)
                    ivSetPosition.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!visible)
                    ivSetPosition.setVisibility(View.GONE);
            }
        });
        oa.start();
        ObjectAnimator.ofFloat(ivSetPosition, Constants.OBJECT_ANIM_ALPHA, visible ? 0f : 1f, visible ? 1f : 0f).setDuration(Constants.ANIM_COMMON_DURATION).start();
    }

    @Override
    public boolean onActivityBackPressed() {
        switch (currentBackStatus) {
            case SettingPosition:
                setPosition(false);
                return false;
            case Normal:
            default:
                return true;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_add:
                setPosition(true);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapUtils.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
        mapUtils.onPause();
        Log.e("onPause", "");
    }

    @Override
    public void onResume() {
        super.onResume();
        mapUtils.onResume();
        Log.e("onResume", "");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapUtils.onDestroy();
        Log.e("onDestroy", "");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e("onDestroyView", "");

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapUtils.onLowMemory();
    }


}
