package ng.latitude.ui.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.amap.api.maps.MapView;
import com.amap.api.maps.model.Marker;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.HashMap;

import ng.latitude.R;
import ng.latitude.support.bean.GetScoreBean;
import ng.latitude.support.conf.Constants;
import ng.latitude.support.conf.Latitude;
import ng.latitude.support.map.MapUnit;
import ng.latitude.support.network.GsonRequest;
import ng.latitude.support.network.HttpUtils;
import ng.latitude.support.ui.AddMarkerDialog;
import ng.latitude.support.ui.BaseFragment;
import ng.latitude.support.ui.BottomInfo;
import ng.latitude.support.ui.GravityInterpolator;
import ng.latitude.support.ui.InterfaceUtils;
import ng.latitude.support.ui.ScoreView;
import ng.latitude.support.ui.SingleFragmentActivity;
import ng.latitude.ui.activity.MainActivity;

/**
 * Created by Ng on 15/6/8
 * <p>
 * All Rights Reserved by Ng
 * Copyright © 2015
 */
public class MapFragment extends BaseFragment implements MapUnit.OnMarkerAddedListener
        , MapUnit.OnLocationFixListener, SingleFragmentActivity.OnBackPressedListener
        , MapUnit.OnCaptureButtonClickedListener, MapUnit.OnMarkerLoadListener, MapUnit.OnSpotForceChangedListener {


    private Handler handler;
    private MainActivity activity;
    private ImageView ivSetPosition;
    private BottomInfo bInf;
    private MapUnit mapUtils;
    private ScoreView scoreView;
    private MainActivity.BackStatus currentBackStatus = SingleFragmentActivity.BackStatus.Normal;
    private CoordinatorLayout snackBarLayout;
    private Runnable refreshSpotsRunnable = new Runnable() {
        @Override
        public void run() {
            mapUtils.loadMarkers();
            handler.postDelayed(this, Constants.REFRESH_SPOTS_INTERVAL);
        }
    };
    private boolean isUpdatingScore = false;
    private Runnable refreshScoreRunnable = new Runnable() {
        @Override
        public void run() {
            updateScore(scoreView);
            handler.postDelayed(this, Constants.REFRESH_SCORE_INTERVAL);
        }
    };

    public static MapFragment newInstance() {
        MapFragment mapFragment = new MapFragment();
        mapFragment.setContentView(R.layout.frag_main);
        return mapFragment;
    }

    @Override
    protected void findViews(View v) {
        ivSetPosition = (ImageView) v.findViewById(R.id.iv_main_set_position);
        bInf = (BottomInfo) v.findViewById(R.id.binf_main);
        snackBarLayout = (CoordinatorLayout) v.findViewById(R.id.snb_main);
        mapUtils = new MapUnit(this, (MapView) v.findViewById(R.id.map_main));
    }

    @Override
    protected void setListeners() {
        mapUtils.setOnMarkerAddedListener(this);
        mapUtils.setOnLocationFixListener(this);
        mapUtils.setOnCaptureButtonClickedListener(this);
        mapUtils.setOnMarkerLoadListener(this);
        mapUtils.setOnSpotForceChangedListener(this);
    }

    @Override
    protected void showContent() {
        Toolbar toolbar = (Toolbar) getRootView().findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        scoreView = new ScoreView(toolbar);
        activity = (MainActivity) getActivity();
        activity.setSupportActionBar(toolbar);

        handler = activity.getHandler();

        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setBackgroundDrawable(new ColorDrawable(activity.getResources()
                    .getColor(Latitude.getUserInfo().getForce() == Constants.Force.ONE ? R.color.force_1 : R.color.force_2)));
            if (!Latitude.getUserInfo().getName().isEmpty())
                activity.getSupportActionBar().setTitle(Latitude.getUserInfo().getName());
        }

        scoreView.updateScore(); // 先显示登录时的分数
    }

    /**
     * 更新 {@link ScoreView} 上的分数
     *
     * @param scoreView 需要更新分数的 {@link ScoreView}
     */
    private void updateScore(final ScoreView scoreView) {

        if (!isUpdatingScore) {
            isUpdatingScore = true;

            HashMap<String, String> params = new HashMap<>();
            params.put(HttpUtils.Params.USER_ID, String.valueOf(Latitude.getUserInfo().getId()));
            params.put(HttpUtils.Params.FORCE, String.valueOf(Latitude.getUserInfo().getForce()));

            Log.e("TAG", String.valueOf(HttpUtils.Params.USER_ID) + " : " + String.valueOf(Latitude.getUserInfo().getId()));
            Log.e("TAG", String.valueOf(HttpUtils.Params.FORCE) + " : " + String.valueOf(Latitude.getUserInfo().getForce()));

            HttpUtils.getRequestQueue().add(new GsonRequest<>(Request.Method.POST, HttpUtils.Urls.GET_SCORE, params, GetScoreBean.class, new Response.Listener<GetScoreBean>() {
                @Override
                public void onResponse(GetScoreBean response) {
                    Latitude.getUserInfo().setPlayerScore(response.getPlayerScore());
                    Latitude.getUserInfo().setForceScore(response.getForceScore());

                    scoreView.updateScore();

                    isUpdatingScore = false;
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    isUpdatingScore = false;
                }
            }));
        }

    }

    /**
     * 弹出或因此显示添加据点对话框 {@link AddMarkerDialog}
     *
     * @param isSetting {@code true} 为弹出，{@code false} 为隐藏
     */
    public void setPosition(boolean isSetting) {
        if (isSetting) {
            currentBackStatus = SingleFragmentActivity.BackStatus.SettingPosition;
            activity.findViewById(R.id.action_add).setVisibility(View.GONE);
            startSetPositionAnim(true);
            mapUtils.addMarkerToMap();
        } else {
            currentBackStatus = SingleFragmentActivity.BackStatus.Normal;
            activity.findViewById(R.id.action_add).setVisibility(View.VISIBLE);
            startSetPositionAnim(false);
        }
    }

    /**
     * 弹出或隐藏添加据点对话框及“目标”图片时的动画
     *
     * @param visible {@code true} 为弹出，{@code false} 为隐藏
     */
    private void startSetPositionAnim(final boolean visible) {
        float transY = getRootView().findViewById(R.id.rv_main).getHeight() - ivSetPosition.getY();
        final ObjectAnimator oa = ObjectAnimator.ofFloat(ivSetPosition, InterfaceUtils.AnimPropertyName.TRANSLATION_Y, visible ? transY : 0, 0).setDuration(Constants.ANIM_SLOW_DURATION);
        oa.setInterpolator(GravityInterpolator.getInstance(visible));
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
                oa.removeAllListeners();
            }
        });
        oa.start();
        ObjectAnimator.ofFloat(ivSetPosition, InterfaceUtils.AnimPropertyName.ALPHA, visible ? 0f : 1f, visible ? 1f : 0f).setDuration(Constants.ANIM_SLOW_DURATION).start();
    }

    @Override
    public void onMarkerAdded() {
        setPosition(false);
        mapUtils.loadMarkers();
    }

    @Override
    public void onMarkerFailed(String title, String snippet, int state) {

        setPosition(false);

        switch (state) {
            case MapUnit.OnMarkerAddedListener.ERROR_UNKNOWN:
                Snackbar.make(snackBarLayout, R.string.toast_spot_create_failed, Snackbar.LENGTH_SHORT).show();
                mapUtils.addMarkerToMap(title, snippet);
                break;
            case MapUnit.OnMarkerAddedListener.ERROR_MORE_THAN_THREE_SPOTS:
                Snackbar.make(snackBarLayout, R.string.toast_spot_over_limit, Snackbar.LENGTH_LONG).show();
                break;
            case MapUnit.OnMarkerAddedListener.CANCELLED:
                break;
            default:
                Snackbar.make(snackBarLayout, R.string.toast_network_error, Snackbar.LENGTH_SHORT).show();
                mapUtils.addMarkerToMap(title, snippet);
                break;

        }
    }

    @Override
    public void onFixState(int state) {
        switch (state) {
            case STATE_START_FIX:
                bInf.show();
                Log.e("TAG", String.valueOf("startFix"));
                break;

            case STATE_STOP_FIX:
                bInf.hide();
                Log.e("TAG", String.valueOf("stopFix"));
                break;

            case STATE_LBS_FIXED: // 不显示精确定位中
//                bInf.show();
//                bInf.setMainText(R.string.widget_bottom_info_main_gps);
//                Log.e("TAG", String.valueOf("lbsFixed"));
//                break;

            case STATE_GPS_FIXED:
                bInf.hide();
                bInf.reset();
                Log.e("TAG", String.valueOf("gpsFixed"));
                break;
        }

    }

    @Override
    public void onGpsState(boolean state) {
        if (state)
            bInf.setSubText(R.string.widget_bottom_info_sub);
        else
            bInf.setSubText(R.string.widget_bottom_info_sub_gps_not_available);
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
    public void onMarkerLoaded(boolean success) {
        if (success) {
            Snackbar.make(snackBarLayout, R.string.toast_spot_loaded, Snackbar.LENGTH_SHORT).show();
        } else {
//            Snackbar.make(snackBarLayout, R.string.toast_network_error, Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMarkerStartLoading() {


    }

    @Override
    public void onCaptureButtonClicked(boolean inRange, Marker marker) {
        if (inRange) {
//            SpotActionDialog.getInstance(spotBean).setOnSpotForceChangedListener(this).show(getFragmentManager(), "SpotActionDialog");
            mapUtils.changeSpotForce(marker, Latitude.getUserInfo().getForce());
        } else {
            Snackbar.make(snackBarLayout, R.string.toast_move_to_range, Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSpotForceChanged(int state, Marker marker) {

        switch (state) {
            case MapUnit.OnSpotForceChangedListener.SUCCEEDED:
//                mapUtils.changeSpotForce(spotId, Latitude.getUserInfo().getForce());
                Snackbar.make(snackBarLayout, R.string.toast_spot_capture_succeed, Snackbar.LENGTH_LONG).show();
                updateScore(scoreView);
                break;
            case MapUnit.OnSpotForceChangedListener.FAILED:
                Snackbar.make(snackBarLayout, R.string.toast_spot_capture_failed, Snackbar.LENGTH_SHORT).show();
                break;
            case MapUnit.OnSpotForceChangedListener.ERROR_NETWORK:
                Snackbar.make(snackBarLayout, R.string.toast_network_error, Snackbar.LENGTH_SHORT).show();
                break;
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
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapUtils.onCreate(savedInstanceState);
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

//        handler.removeCallbacks(refreshScoreRunnable);
//        handler.removeCallbacks(refreshSpotsRunnable);
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapUtils.onResume();

        handler.postDelayed(refreshScoreRunnable, Constants.REFRESH_SCORE_INTERVAL);
        handler.post(refreshSpotsRunnable);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapUtils.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapUtils.onLowMemory();
    }


}
