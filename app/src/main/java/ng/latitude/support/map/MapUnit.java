package ng.latitude.support.map;

import android.app.Fragment;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.OnCameraChangeListener;
import com.amap.api.maps.AMap.OnMapClickListener;
import com.amap.api.maps.AMap.OnMapLoadedListener;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.HashMap;

import ng.latitude.R;
import ng.latitude.support.bean.CaptureSpotBean;
import ng.latitude.support.bean.SetSpotBean;
import ng.latitude.support.bean.SpotBean;
import ng.latitude.support.conf.Constants;
import ng.latitude.support.conf.Latitude;
import ng.latitude.support.conf.PreferenceUtils;
import ng.latitude.support.network.GsonRequest;
import ng.latitude.support.network.HttpUtils;
import ng.latitude.support.ui.AddMarketDialog;
import ng.latitude.support.ui.InterfaceUtils;
import ng.latitude.support.ui.LatitudeProgressDialog;

/**
 * Created by Ng on 15/6/13
 * <p>
 * All Rights Reserved by Ng
 * Copyright © 2015
 */
public class MapUnit implements SensorUnit.OnHeadingChangedListener, InfoWindowAdapter.OnInfoWindowButtonClickedListener {

    private final MapView mapView;
    private final Fragment fragment;
    private AMap aMap;
    private double[] latestLatLng = new double[2];
    private LatLngBounds initBounds;
    private LocationSource.OnLocationChangedListener onLocationChangedListener;
    private LocationManagerProxy locationManagerProxy;
    private OnLocationFixListener onLocationFixListener;
    private OnMarkerAddedListener onMarkerAddedListener;
    private OnCaptureButtonClickedListener onCaptureButtonClickedListener;
    private OnMarkerLoadListener onMarkerLoadListener;
    private OnSpotForceChangedListener onSpotForceChangedListener;
    private SensorUnit sensorUnit;
    private LatLng lastFix = new LatLng(0d, 0d);
    private float lastZoom = 99;
    private int heading;
    private int headingGap;
    private Marker currentMarker;
    private Circle myLocationCenterCircle;
    private Circle myLocationRangeCircle;
    private boolean isLoadingMarkers = false;
    private boolean isRanBefore = false;
    private boolean isAddingMarker = false;
    private boolean isFirstFix = true;

    /**
     * 设备位置改变监听
     */
    private AMapLocationListener aMapLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aLocation) {
            if (onLocationFixListener != null)
                onLocationFixListener.gpsStatus(isGPSEnabled());

            LatLng currentFix = new LatLng(aLocation.getLatitude(), aLocation.getLongitude());

            if (onLocationChangedListener != null && aLocation.hasAccuracy() && !isAddingMarker
                    && Math.abs(aLocation.getLatitude()) > 0.1 && Math.abs(aLocation.getLongitude()) > 0.1
                    && AMapUtils.calculateLineDistance(lastFix, currentFix) >= Constants.LOCATION_UPDATE_ACCURATE) {

                lastFix = currentFix;
                final String provider = aLocation.getProvider();

                if (provider.equals(Constants.LOCATION_PROVIDER_GPS)) {// GPS定位
                    if (onLocationFixListener != null)
                        onLocationFixListener.gpsFixedLocation();
                } else if (provider.equals(Constants.LOCATION_PROVIDER_LBS)) {// 网络定位
                    if (onLocationFixListener != null)
                        onLocationFixListener.lbsFixedLocation();
                }

//            aLocation.setBearing(Math.abs(heading - 90) % 360); // 总之要减90


//            aLocation.setBearing(360 - heading); // ALocation为顺时针计数
//                aLocation.setAccuracy(Constants.GAMING_CAPTURE_RANGE); // 范围圈

                if (isFirstFix) {
                    aMap.animateCamera(CameraUpdateFactory.changeLatLng(currentFix), Constants.ANIM_SLOW_DURATION, null);
                    isFirstFix = false;
                }

                Log.e("onLocationChanged", String.format("%s: %f %f\tbearing: %f", aLocation.getProvider(), aLocation.getLatitude(), aLocation.getLongitude(), aLocation.getBearing()));

                if (myLocationCenterCircle != null) {
                    myLocationCenterCircle.setCenter(currentFix);
                    myLocationRangeCircle.setCenter(currentFix);
                } else {
                    myLocationCenterCircle = aMap.addCircle(new CircleOptions().center(currentFix).radius(1.5d).fillColor(fragment.getResources().getColor(R.color.green_primary)).zIndex(999).strokeWidth(0f));
                    myLocationRangeCircle = aMap.addCircle(new CircleOptions().center(currentFix).radius(Constants.GAMING_CAPTURE_RANGE).fillColor(fragment.getResources().getColor(R.color.location_green_primary))
                            .strokeColor(fragment.getResources().getColor(R.color.location_green_primary_light)).strokeWidth(4f));
                }

                onLocationChangedListener.onLocationChanged(aLocation);// 显示系统小蓝点
//            aMap.setMyLocationRotateAngle(heading);// 设置小蓝点旋转角度

                if (!isRanBefore) {
                    isRanBefore = true;
                    PreferenceUtils.savePreference(PreferenceUtils.KEY_RAN_BEFORE, true);
                    aMap.moveCamera(CameraUpdateFactory.zoomTo(Constants.MAP_INIT_ZOOM_LEVEL));
                }

                aMap.moveCamera(CameraUpdateFactory.changeTilt(Constants.MAP_INIT_TILT));
                latestLatLng[0] = aLocation.getLatitude();
                latestLatLng[1] = aLocation.getLongitude();
            }
        }

        @Override
        public void onLocationChanged(Location location) {

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    /**
     * 管理 {@code GPS} 开关
     */
    private LocationSource locationSource = new LocationSource() {
        @Override
        public void activate(OnLocationChangedListener listener) {
            if (onLocationChangedListener == null)
                onLocationChangedListener = listener;
        }

        @Override
        public void deactivate() {
            if (locationManagerProxy != null) {
                locationManagerProxy.removeUpdates(aMapLocationListener);
                locationManagerProxy.destory();
            }
            locationManagerProxy = null;

            if (onLocationFixListener != null)
                onLocationFixListener.stopFixLocation();
        }
    };

    /**
     * 初始化 {@link MapUnit}
     *
     * @param fragment 拥有 {@link MapView} 的 {@link Fragment}
     * @param view     需要控制的 {@link MapView}
     */
    public MapUnit(Fragment fragment, MapView view) {

        this.mapView = view;
        this.fragment = fragment;

        sensorUnit = new SensorUnit(this.fragment.getActivity());
//        sensorUnit.setOnHeadingChangedListener(this);
//        sensorUnit.bind();

        latestLatLng[0] = PreferenceUtils.getFloat(PreferenceUtils.KEY_LATITUDE);
        if (latestLatLng[0] != PreferenceUtils.FLOAT_NOT_EXIST) {
            latestLatLng[1] = PreferenceUtils.getFloat(PreferenceUtils.KEY_LONGITUDE);
            initBounds = new LatLngBounds.Builder().include(new LatLng(latestLatLng[0], latestLatLng[1])).build();
        }

        if (aMap == null) {
            aMap = mapView.getMap();


            /**
             * 地图加载成功监听
             */
            aMap.setOnMapLoadedListener(new OnMapLoadedListener() { // 设置amap加载成功事件监听器
                @Override
                public void onMapLoaded() {
                    if (initBounds != null) {
                        aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(initBounds, 0));
                        aMap.moveCamera(CameraUpdateFactory.zoomTo(Constants.MAP_INIT_ZOOM_LEVEL));
                    }
                }
            });


            /**
             * Marker点击监听
             */
            aMap.setOnMarkerClickListener(new OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) { // 设置点击marker事件监听器
                    currentMarker = marker;
                    marker.showInfoWindow();
                    return true;
                }
            });


            /**
             * 地图点击监听
             */
            aMap.setOnMapClickListener(new OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    currentMarker.hideInfoWindow();
                }
            });

            /**
             * 视图改变监听
             */
            aMap.setOnCameraChangeListener(new OnCameraChangeListener() {
                @Override
                public void onCameraChange(CameraPosition cameraPosition) {
                    //        heading = (headingGap + (int) cameraPosition.bearing) % 360;
                    //        aMap.setMyLocationRotateAngle(heading);

                }

                @Override
                public void onCameraChangeFinish(CameraPosition cameraPosition) {
                    //        if (lastZoom > cameraPosition.zoom && aMap.getMyLocation() != null && aMap.getMyLocation().hasAccuracy()) {
                    ////            loadMarkers();
                    //            lastZoom = cameraPosition.zoom;
                    //        }
                    if (aMap.getCameraPosition().tilt != Constants.MAP_INIT_TILT) {
                        aMap.moveCamera(CameraUpdateFactory.changeTilt(Constants.MAP_INIT_TILT));
                    }
                }
            });

            /**
             * 定位监听
             */
            aMap.setLocationSource(locationSource);

            /**
             * 设置显示样式
             */
            aMap.setInfoWindowAdapter(new InfoWindowAdapter().setOnInfoWindowButtonClickedListener(this));// 设置自定义InfoWindow样式
            MyLocationStyle myLocationStyle = new MyLocationStyle();
            myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.transperant));// 设置小蓝点的图标
            // myLocationStyle.anchor(int,int)//设置小蓝点的锚点
            myLocationStyle.radiusFillColor(this.fragment.getResources().getColor(android.R.color.transparent));// 设置圆形的填充颜色
            myLocationStyle.strokeWidth(0);// 设置圆形的边框粗细
            aMap.setMyLocationStyle(myLocationStyle);
            aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
            aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);//设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
            aMap.getUiSettings().setZoomPosition(AMapOptions.ZOOM_POSITION_RIGHT_CENTER);// 缩放按钮位置
            aMap.getUiSettings().setTiltGesturesEnabled(false); // 设置是否开启倾斜地图手势
            aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
            aMap.getUiSettings().setCompassEnabled(true);// 设置是否显示指南针
            aMap.showMapText(false); // 设置是否显示地图路名等信息
//            aMap.setMapType(AMap.MAP_TYPE_SATELLITE);// 设置地图底图

        }


    }

    /**
     * 设置设备位置改变事件监听
     *
     * @param onLocationFixListener 位置改变事件监听器
     */
    public void setOnLocationFixListener(OnLocationFixListener onLocationFixListener) {
        this.onLocationFixListener = onLocationFixListener;
    }

    /**
     * 设置 {@link Marker} 添加事件监听
     *
     * @param onMarkerAddedListener {@link Marker} 添加事件监听器
     */
    public void setOnMarkerAddedListener(OnMarkerAddedListener onMarkerAddedListener) {
        this.onMarkerAddedListener = onMarkerAddedListener;
    }

    /**
     * 设置据点“占领”按钮点击事件监听
     *
     * @param onCaptureButtonClickedListener “占领”按钮点击事件监听器
     */
    public void setOnCaptureButtonClickedListener(OnCaptureButtonClickedListener onCaptureButtonClickedListener) {
        this.onCaptureButtonClickedListener = onCaptureButtonClickedListener;
    }

    /**
     * 设置据点加载完成事件监听
     *
     * @param onMarkerLoadListener 据点加载完成事件监听器
     */
    public void setOnMarkerLoadListener(OnMarkerLoadListener onMarkerLoadListener) {
        this.onMarkerLoadListener = onMarkerLoadListener;
    }

    /**
     * 设置据点阵营改变事件监听
     *
     * @param onSpotForceChangedListener 据点阵营改变事件监听器
     */
    public void setOnSpotForceChangedListener(OnSpotForceChangedListener onSpotForceChangedListener) {
        this.onSpotForceChangedListener = onSpotForceChangedListener;
    }

    /**
     * 更变特定 {@link Marker} 的阵营
     *
     * @param marker 需要改变阵营的 {@link Marker}
     * @param force  需要改变的阵营，从 {@link ng.latitude.support.conf.Constants.Force} 中选择
     */
    public void changeSpotForce(final Marker marker, final int force) {

        final SpotBean spotBean = (SpotBean) marker.getObject();

        final LatitudeProgressDialog progressDialog = new LatitudeProgressDialog(fragment.getActivity(), fragment.getActivity().getString(R.string.dialog_spot_action_capturing));
        progressDialog.show();

        HashMap<String, String> params = new HashMap<>();
        params.put(HttpUtils.Params.ID, String.valueOf(spotBean.getId()));
        params.put(HttpUtils.Params.USER_ID, String.valueOf(Latitude.getUserInfo().getId()));
        params.put(HttpUtils.Params.FORCE, String.valueOf(Latitude.getUserInfo().getForce()));

        HttpUtils.getRequestQueue().add(new GsonRequest<>(Request.Method.POST, HttpUtils.Urls.CAPTURE_SPOT, params, CaptureSpotBean.class, new Response.Listener<CaptureSpotBean>() {
            @Override
            public void onResponse(CaptureSpotBean response) {
                if (response.getState() == 1) {
                    onSpotForceChangedListener.onSpotForceChanged(OnSpotForceChangedListener.SUCCEEDED, marker);

                    spotBean.setForce(Latitude.getUserInfo().getForce());
                    marker.setIcon(BitmapDescriptorFactory.fromResource(force == Constants.Force.ONE ? R.drawable.marker_force_1 : R.drawable.marker_force_2));

                } else
                    onSpotForceChangedListener.onSpotForceChanged(OnSpotForceChangedListener.FAILED, marker);

                progressDialog.dismiss();


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                onSpotForceChangedListener.onSpotForceChanged(OnSpotForceChangedListener.ERROR_NETWORK, marker);

                progressDialog.dismiss();
            }
        }));
    }

    /**
     * 添加 {@link Marker} 到地图上，将弹出 {@link AddMarketDialog} ，且 {@link AddMarketDialog} 中数据为空
     */
    public void addMarkerToMap() {
        addMarkerToMap(null, null);
    }

    /**
     * 添加 {@link Marker} 到地图上，将弹出 {@link AddMarketDialog} ，且 {@link AddMarketDialog} 中数据为指定数据
     *
     * @param title   显示在 {@link AddMarketDialog} 中的标题
     * @param snippet 显示在 {@link AddMarketDialog} 中的描述
     */
    public void addMarkerToMap(final String title, final String snippet) {
        isAddingMarker = true;
        aMap.animateCamera(CameraUpdateFactory.changeLatLng(lastFix), Constants.ANIM_SLOW_DURATION, new AMap.CancelableCallback() {

            /**
             * 地图动画移动到当前所在点完成
             */
            @Override
            public void onFinish() {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        final AddMarketDialog dialog = AddMarketDialog.newInstance(title, snippet);
                        dialog.setOnAddMarkerDialogListener(new AddMarketDialog.OnAddMarkerDialogListener() {
                            @Override
                            public void onAddMarkerDialogCancelled() {
                                if (onLocationChangedListener != null)
                                    onMarkerAddedListener.onMarkerFailed(title, snippet, OnMarkerAddedListener.CANCELLED);
//                                dialog.dismiss();
                                isAddingMarker = false;
                            }

                            @Override
                            public void onAddMarkerDialogConfirmed(final String title, final String snippet, final Button btn) {

                                HashMap<String, String> params = new HashMap<>();
                                params.put(HttpUtils.Params.USER_ID, String.valueOf(Latitude.getUserInfo().getId()));
                                params.put(HttpUtils.Params.SPOT_TITLE, title);
                                params.put(HttpUtils.Params.SPOT_SNIPPET, snippet);
                                params.put(HttpUtils.Params.LATITUDE, String.valueOf(aMap.getCameraPosition().target.latitude));
                                params.put(HttpUtils.Params.LONGITUDE, String.valueOf(aMap.getCameraPosition().target.longitude));
                                params.put(HttpUtils.Params.FORCE, String.valueOf(Latitude.getUserInfo().getForce()));

                                InterfaceUtils.blinkView(btn, true);

//                                final LatitudeProgressDialog progressDialog = new LatitudeProgressDialog(fragment.getActivity(), fragment.getActivity().getString(R.string.dialog_add_marker_creating));
//                                progressDialog.show();

                                HttpUtils.getRequestQueue().add(new GsonRequest<>(Request.Method.POST, HttpUtils.Urls.SET_SPOT, params, SetSpotBean.class, new Response.Listener<SetSpotBean>() {
                                    @Override
                                    public void onResponse(SetSpotBean response) {

                                        if (response.getState() == 1) { // 成功

//                            aMap.addMarker(getDefaultMarkerOptions(aMap.getCameraPosition().target, Latitude.getUserInfo().getForce()));

                                            if (onMarkerAddedListener != null)
                                                onMarkerAddedListener.onMarkerAdded();

                                        } else {

                                            if (onLocationChangedListener != null)
                                                onMarkerAddedListener.onMarkerFailed(title, snippet, response.getState());

                                        }

//                                        dialog.dismiss();
//                                        progressDialog.dismiss();
                                        InterfaceUtils.blinkView(btn, false);
                                        isAddingMarker = false;
                                    }

                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        if (onLocationChangedListener != null)
                                            onMarkerAddedListener.onMarkerFailed(title, snippet, OnMarkerAddedListener.ERROR_NETWORK);

//                                        dialog.dismiss();
//                                        progressDialog.dismiss();
                                        InterfaceUtils.blinkView(btn, false);
                                        isAddingMarker = false;
                                    }
                                }));

                            }
                        });
                        dialog.show(fragment.getFragmentManager(), "AddMarkerDialog");
                    }
                }, Constants.ANIM_SLOW_DURATION);
            }

            /**
             * 地图动画移动到当前所在点时，用户点按地图，导致移动失败取消
             */
            @Override
            public void onCancel() {
                if (onLocationChangedListener != null)
                    onMarkerAddedListener.onMarkerFailed(title, snippet, OnMarkerAddedListener.CANCELLED);
                isAddingMarker = false;
            }
        });
    }

    /**
     * 加载据点
     */
    public void loadMarkers() {

        int delay = 1000; // 防止太快获取座标出错

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isLoadingMarkers && mapView != null && aMap != null) {
                    isLoadingMarkers = true;

                    if (onMarkerLoadListener != null) {
                        onMarkerLoadListener.onMarkerStartLoading();
                    }

                    HashMap<String, String> params = new HashMap<>();


                    /*
                    *   加载视图据点
                    *
                    * */
//                    VisibleRegion visibleRegion = aMap.getProjection().getVisibleRegion();
//                    LatLng leftTop = visibleRegion.farLeft;
//                    LatLng rightBottom = visibleRegion.nearRight;
//                    Log.e("TAG", String.valueOf(leftTop.latitude) + ", " + String.valueOf(leftTop.longitude));
//                    Log.e("TAG", String.valueOf(rightBottom.latitude) + ", " + String.valueOf(rightBottom.longitude));
//                    params.put(HttpUtils.Params.LEFT_TOP_LATITUDE, String.valueOf(leftTop.latitude));
//                    params.put(HttpUtils.Params.LEFT_TOP_LONGITUDE, String.valueOf(leftTop.longitude));
//                    params.put(HttpUtils.Params.RIGHT_BOTTOM_LATITUDE, String.valueOf(rightBottom.latitude));
//                    params.put(HttpUtils.Params.RIGHT_BOTTOM_LONGITUDE, String.valueOf(rightBottom.longitude));


                    /*
                    *   加载周边据点
                    *
                    * */

                    double lat = latestLatLng[0];
                    double lng = latestLatLng[1];
                    params.put(HttpUtils.Params.LEFT_TOP_LATITUDE, String.valueOf(lat + Constants.GAMING_SCAN_LATITUDE_RADIUS));
                    params.put(HttpUtils.Params.RIGHT_BOTTOM_LATITUDE, String.valueOf(lat - Constants.GAMING_SCAN_LATITUDE_RADIUS));
                    params.put(HttpUtils.Params.LEFT_TOP_LONGITUDE, String.valueOf(lng - Constants.GAMING_SCAN_LONGITUDE_RADIUS));
                    params.put(HttpUtils.Params.RIGHT_BOTTOM_LONGITUDE, String.valueOf(lng + Constants.GAMING_SCAN_LONGITUDE_RADIUS));

                    Log.e("RequestZone#LeftTop", String.valueOf(params.get(HttpUtils.Params.LEFT_TOP_LATITUDE)) + ", " + String.valueOf(params.get(HttpUtils.Params.LEFT_TOP_LONGITUDE)));
                    Log.e("RequestZone#RightBottom", String.valueOf(params.get(HttpUtils.Params.RIGHT_BOTTOM_LATITUDE)) + ", " + String.valueOf(params.get(HttpUtils.Params.RIGHT_BOTTOM_LONGITUDE)));

                    HttpUtils.getRequestQueue().add(new GsonRequest<>(Request.Method.POST, HttpUtils.Urls.GET_SPOTS, params, SpotBean[].class, new Response.Listener<SpotBean[]>() {
                        @Override
                        public void onResponse(SpotBean[] response) {

                            aMap.clear();
                            myLocationCenterCircle = aMap.addCircle(new CircleOptions().center(lastFix).radius(1.5d).fillColor(fragment.getResources().getColor(R.color.green_primary)).zIndex(999).strokeWidth(0f));
                            myLocationRangeCircle = aMap.addCircle(new CircleOptions().center(lastFix).radius(Constants.GAMING_CAPTURE_RANGE).fillColor(fragment.getResources().getColor(R.color.location_green_primary))
                                    .strokeColor(fragment.getResources().getColor(R.color.location_green_primary_light)).strokeWidth(4f));

                            for (int i = 0; i < response.length; i++) {

                                SpotBean spotBean = response[i];

                                MarkerOptions markerOptions = getDefaultMarkerOptions(new LatLng(spotBean.getLatitude(), spotBean.getLongitude()), spotBean.getForce())
                                        .title(spotBean.getTitle())
                                        .snippet(spotBean.getSnippet())
                                        .period(60 * 60 * 1000)
                                        .draggable(false);

                                aMap.addMarker(markerOptions).setObject(spotBean);
                            }

                            isLoadingMarkers = false;

                            if (onMarkerLoadListener != null)
                                onMarkerLoadListener.onMarkerLoaded(true);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            if (onMarkerLoadListener != null) {
                                onMarkerLoadListener.onMarkerLoaded(false);
                            }
                            isLoadingMarkers = false;
                        }
                    }));
                }

            }
        }, delay);
    }

    /**
     * 设备朝向监听
     *
     * @param heading 当前的朝向，12点方向为0，逆时针增加
     */
    @Override
    public void onHeadingChanged(int heading) {

//        headingGap = heading - (int) aMap.getCameraPosition().bearing;
//        this.heading = (headingGap + (int) aMap.getCameraPosition().bearing) % 360;
//        aMap.setMyLocationRotateAngle(this.heading);
    }

    /**
     * {@link InfoWindowAdapter} 中按钮点击事件监听
     *
     * @param marker 发生点击事件的 {@link Marker}
     */
    @Override
    public void onInfoWindowButtonClicked(Marker marker) {
        if (onCaptureButtonClickedListener != null) {
            if (AMapUtils.calculateLineDistance(marker.getPosition(), new LatLng(aMap.getMyLocation().getLatitude(), aMap.getMyLocation().getLongitude())) <= Constants.GAMING_CAPTURE_RANGE) {
                onCaptureButtonClickedListener.onCaptureButtonClicked(true, marker);
            } else {
                onCaptureButtonClickedListener.onCaptureButtonClicked(false, marker);
            }
        }
    }

    /**
     * 激活定位装置
     */
    private void requestLocation() {
        if (locationManagerProxy == null) {
            locationManagerProxy = LocationManagerProxy.getInstance(fragment.getActivity());
            /*
             * ，第一个参数是定位provider，第二个参数时间最短是2000毫秒，第三个参数距离间隔单位是米，第四个参数是定位监听者
			 */
            locationManagerProxy.requestLocationData(
                    LocationProviderProxy.AMapNetwork, Constants.LOCATION_UPDATE_INTERVAL, Constants.LOCATION_UPDATE_ACCURATE, aMapLocationListener);

            if (onLocationFixListener != null)
                onLocationFixListener.startFixLocation();
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        mapView.onCreate(savedInstanceState);
    }

    public void onSaveInstanceState(Bundle outState) {
        mapView.onSaveInstanceState(outState);
    }

    public void onPause() {
        mapView.onPause();
        PreferenceUtils.savePreference(PreferenceUtils.KEY_LATITUDE, (float) latestLatLng[0]);
        PreferenceUtils.savePreference(PreferenceUtils.KEY_LONGITUDE, (float) latestLatLng[1]);
        locationSource.deactivate();
//        sensorUnit.release();
    }

    public void onResume() {
        mapView.onResume();
        requestLocation();
//        sensorUnit.bind();
    }

    public void onDestroy() {
        mapView.onDestroy();
    }

    public void onLowMemory() {
        mapView.onLowMemory();
    }

    /**
     * 查询设备 GPS 是否开启
     *
     * @return {@code true} 为开启， {@code false} 为关闭
     */
    public boolean isGPSEnabled() {
        LocationManager locationManager = (LocationManager) fragment.getActivity().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /**
     * 获取默认样式的 {@link Marker}
     *
     * @param latLng 需要获取 {@link Marker} 的经纬度
     * @param force  需要获取 {@link Marker} 的阵营，从 {@link ng.latitude.support.conf.Constants.Force} 中选择
     * @return 指定经纬度、指定阵营的默认样式 {@link Marker}
     */
    private MarkerOptions getDefaultMarkerOptions(LatLng latLng, int force) {
        return new MarkerOptions()
                .anchor(0.5f, 0.9f)
                .position(latLng)
                .draggable(false)
                .icon(BitmapDescriptorFactory.fromResource(force == Constants.Force.ONE ? R.drawable.marker_force_1 : R.drawable.marker_force_2));
    }

    /**
     * {@link Marker} 添加事件监听器
     */
    public interface OnMarkerAddedListener {
        int SUCCEEDED = 1;
        int ERROR_UNKNOWN = 2;
        int ERROR_MORE_THAN_THREE_SPOTS = 0;
        int ERROR_NETWORK = -1;
        int CANCELLED = 3;

        /**
         * {@link Marker} 被成功添加到地图上
         */
        void onMarkerAdded();

        /**
         * 添加 {@link Marker} 到地图失败
         *
         * @param title   {@link Marker} 的标题
         * @param snippet {@link Marker} 的描述
         * @param state   添加失败识别码，从 {@link ng.latitude.support.map.MapUnit.OnMarkerAddedListener} 中选择
         */
        void onMarkerFailed(String title, String snippet, int state);

    }

    /**
     * 定位状态监听器
     */
    public interface OnLocationFixListener {

        /**
         * 开始定位
         */
        void startFixLocation();

        /**
         * 停止定位
         */
        void stopFixLocation();

        /**
         * WIFI 定位
         */
        void lbsFixedLocation();

        /**
         * GPS 定位
         */
        void gpsFixedLocation();

        /**
         * GPS 设备状态
         *
         * @param status 当前 GPS 设备的状态， {@code true} 为已开启， {@code false} 为关闭
         */
        void gpsStatus(boolean status);
    }

    /**
     * “占领”按钮点击事件监听器
     */
    public interface OnCaptureButtonClickedListener {

        /**
         * “占领”按钮点击事件
         *
         * @param inRange 是否处在占领范围内， {@code true} 为在范围内可占领， {@code false} 为不处在范围内
         * @param marker  发生点击事件的 {@link Marker}
         */
        void onCaptureButtonClicked(boolean inRange, Marker marker);
    }

    /**
     * 据点加载完成事件监听器
     */
    public interface OnMarkerLoadListener {

        /**
         * 据点加载完成事件
         *
         * @param success 据点是否成功完成加载， {@code true} 为成功加载， {@code false} 为加载失败
         */
        void onMarkerLoaded(boolean success);

        /**
         * 据点开始加载事件
         */
        void onMarkerStartLoading();
    }

    /**
     * 据点阵营改变事件监听器
     */
    public interface OnSpotForceChangedListener {

        int ERROR_NETWORK = -1;
        int SUCCEEDED = 1;
        int FAILED = 0;

        /**
         * 据点阵营改变事件
         *
         * @param state  阵营改变状态，从 {@link ng.latitude.support.map.MapUnit.OnSpotForceChangedListener} 中选择
         * @param marker 发生阵营改变事件的 {@link Marker}
         */
        void onSpotForceChanged(int state, Marker marker);
    }

}
