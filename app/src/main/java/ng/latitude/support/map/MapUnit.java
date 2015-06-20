package ng.latitude.support.map;

import android.app.Fragment;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

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
import com.amap.api.maps.model.VisibleRegion;
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
import ng.latitude.support.ui.LatitudeProgressDialog;

/**
 * Created by Ng on 15/6/13.
 */
public class MapUnit implements SensorUnit.OnHeadingChangedListener, InfoWindowAdapter.OnInfoWindowButtonClickedListener {

    private MapView mapView;
    private AMap aMap;
    private double[] position = new double[2];
    private LatLngBounds initBounds;
    private Fragment fragment;
    private LocationSource.OnLocationChangedListener onLocationChangedListener;
    private LocationManagerProxy locationManagerProxy;
    private OnLocationFixListener onLocationFixListener;
    private OnMarkerAddedListener onMarkerAddedListener;
    private OnCaptureButtonClickedListener onCaptureButtonClickedListener;
    private OnMarkerLoadListener onMarkerLoadListener;
    private OnSpotForceChangedListener onSpotForceChangedListener;
    private SensorUnit sensorUnit;
    private LatLng lastFix = new LatLng(0d, 0d);
    //    private SpotBean[] spotBeans;
    //    private HashMap<String, Integer> markerIdToPositionOfSpotBeans = new HashMap<>();
    private float lastZoom = 99;
    private int heading;
    private int headingGap;
    private Marker currentMarker;
    private Circle myLocationCircle;
    private boolean isLoadingMarkers = false;
    private boolean isRanBefore = false;

    private AMapLocationListener aMapLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aLocation) {
            if (onLocationFixListener != null)
                onLocationFixListener.gpsStatus(isGPSEnabled());

            LatLng currentFix = new LatLng(aLocation.getLatitude(), aLocation.getLongitude());

            if (onLocationChangedListener != null && aLocation.hasAccuracy()
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
                aLocation.setAccuracy(Constants.GAMING_CAPTURE_RANGE); // 范围圈

                Log.e("onLocationChanged", String.format("%s: %f %f\tbearing: %f", aLocation.getProvider(), aLocation.getLatitude(), aLocation.getLongitude(), aLocation.getBearing()));

                if (myLocationCircle != null) {
                    myLocationCircle.setCenter(currentFix);
                } else {
                    myLocationCircle = aMap.addCircle(new CircleOptions().center(currentFix).radius(1.5d).fillColor(fragment.getResources().getColor(R.color.green_primary)).zIndex(999).strokeWidth(0f));
                }

                onLocationChangedListener.onLocationChanged(aLocation);// 显示系统小蓝点
//            aMap.setMyLocationRotateAngle(heading);// 设置小蓝点旋转角度

                if (!isRanBefore) {
                    isRanBefore = true;
                    PreferenceUtils.savePreference(PreferenceUtils.KEY_RAN_BEFORE, true);
                    aMap.moveCamera(CameraUpdateFactory.zoomTo(Constants.MAP_INIT_ZOOM_LEVEL));
                }

                aMap.moveCamera(CameraUpdateFactory.changeTilt(Constants.MAP_INIT_TILT));
                position[0] = aLocation.getLatitude();
                position[1] = aLocation.getLongitude();
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

    public MapUnit(Fragment fragment, View view) {

        this.mapView = (MapView) view;
        this.fragment = fragment;

        sensorUnit = new SensorUnit(this.fragment.getActivity());
//        sensorUnit.setOnHeadingChangedListener(this);
//        sensorUnit.bind();

        position[0] = PreferenceUtils.getFloat(PreferenceUtils.KEY_LATITUDE);
        if (position[0] != PreferenceUtils.FLOAT_NOT_EXIST) {
            position[1] = PreferenceUtils.getFloat(PreferenceUtils.KEY_LONGITUDE);
            initBounds = new LatLngBounds.Builder().include(new LatLng(position[0], position[1])).build();
        }

        if (aMap == null) {
            aMap = mapView.getMap();

            /*
            *   地图加载成功监听
            *
            * */
            aMap.setOnMapLoadedListener(new OnMapLoadedListener() { // 设置amap加载成功事件监听器
                @Override
                public void onMapLoaded() {
                    if (initBounds != null) {
                        aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(initBounds, 0));
                        aMap.moveCamera(CameraUpdateFactory.zoomTo(Constants.MAP_INIT_ZOOM_LEVEL));
                    }
//                    aMap.moveCamera(CameraUpdateFactory.changeTilt(Constants.MAP_INIT_TILT));
                }
            });

            /*
            *   Marker点击监听
            *
            * */
            aMap.setOnMarkerClickListener(new OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) { // 设置点击marker事件监听器
                    currentMarker = marker;
                    marker.showInfoWindow();
                    return true;
                }
            });


            /*
            *   地图点击监听
            *
            * */
            aMap.setOnMapClickListener(new OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    currentMarker.hideInfoWindow();
                }
            });

            /*
            *   视图改变监听
            *
            * */
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

                    aMap.moveCamera(CameraUpdateFactory.changeTilt(Constants.MAP_INIT_TILT));
                }
            });

            /*
            *   定位监听
            *
            * */
            aMap.setLocationSource(locationSource);

            aMap.setInfoWindowAdapter(new InfoWindowAdapter().setOnInfoWindowButtonClickedListener(this));// 设置自定义InfoWindow样式


            MyLocationStyle myLocationStyle = new MyLocationStyle();
            myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.transperant));// 设置小蓝点的图标
            // myLocationStyle.anchor(int,int)//设置小蓝点的锚点
            myLocationStyle.radiusFillColor(this.fragment.getResources().getColor(R.color.location_green_primary));// 设置圆形的填充颜色
            myLocationStyle.strokeColor(this.fragment.getResources().getColor(R.color.location_green_primary_light));// 设置圆形的边框颜色
            myLocationStyle.strokeWidth(4.0f);// 设置圆形的边框粗细

            aMap.setMyLocationStyle(myLocationStyle);
            aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
            aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);//设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
            aMap.getUiSettings().setZoomPosition(AMapOptions.ZOOM_POSITION_RIGHT_CENTER);// 缩放按钮位置
            aMap.getUiSettings().setTiltGesturesEnabled(false);
            aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
            aMap.getUiSettings().setCompassEnabled(true);// 指南针
//            aMap.setMapType(AMap.MAP_TYPE_SATELLITE);// 设置地图底图

        }
    }

    public void setOnLocationFixListener(OnLocationFixListener onLocationFixListener) {
        this.onLocationFixListener = onLocationFixListener;
    }

    public void setOnMarkerAddedListener(OnMarkerAddedListener onMarkerAddedListener) {
        this.onMarkerAddedListener = onMarkerAddedListener;
    }

    public void setOnCaptureButtonClickedListener(OnCaptureButtonClickedListener onCaptureButtonClickedListener) {
        this.onCaptureButtonClickedListener = onCaptureButtonClickedListener;
    }

    public void setOnMarkerLoadListener(OnMarkerLoadListener onMarkerLoadListener) {
        this.onMarkerLoadListener = onMarkerLoadListener;
    }

    public void setOnSpotForceChangedListener(OnSpotForceChangedListener onSpotForceChangedListener) {
        this.onSpotForceChangedListener = onSpotForceChangedListener;
    }

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
                    onSpotForceChangedListener.onSpotForceChanged(1, marker);

                    spotBean.setForce(Latitude.getUserInfo().getForce());
                    marker.setIcon(BitmapDescriptorFactory.fromResource(force == Constants.Force.ONE ? R.drawable.marker_force_1 : R.drawable.marker_force_2));

                } else
                    onSpotForceChangedListener.onSpotForceChanged(0, marker);

                progressDialog.dismiss();


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                onSpotForceChangedListener.onSpotForceChanged(-1, marker);

                progressDialog.dismiss();
            }
        }));
    }

    public boolean addMarkerToMap() {
        return addMarkerToMap(null, null);
    }

    public boolean addMarkerToMap(String title, String snippet) {

        final AddMarketDialog dialog = AddMarketDialog.newInstance(title, snippet);

        dialog.setOnMarkerConfirmedListener(new AddMarketDialog.OnMarkerConfirmedListener() {
            @Override
            public void onMarkerConfirmed(final String title, final String snippet) {
                HashMap<String, String> params = new HashMap<>();
                params.put(HttpUtils.Params.USER_ID, String.valueOf(Latitude.getUserInfo().getId()));
                params.put(HttpUtils.Params.SPOT_TITLE, title);
                params.put(HttpUtils.Params.SPOT_SNIPPET, snippet);
                params.put(HttpUtils.Params.LATITUDE, String.valueOf(aMap.getCameraPosition().target.latitude));
                params.put(HttpUtils.Params.LONGITUDE, String.valueOf(aMap.getCameraPosition().target.longitude));
                params.put(HttpUtils.Params.FORCE, String.valueOf(Latitude.getUserInfo().getForce()));

                final LatitudeProgressDialog progressDialog = new LatitudeProgressDialog(fragment.getActivity(), fragment.getActivity().getString(R.string.dialog_add_marker_creating));
                progressDialog.show();

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

                        dialog.dismiss();
                        progressDialog.dismiss();
                    }

                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (onLocationChangedListener != null)
                            onMarkerAddedListener.onMarkerFailed(title, snippet, -1);

                        dialog.dismiss();
                        progressDialog.dismiss();

                    }
                }));

            }
        });
        dialog.show(fragment.getFragmentManager(), "AddMarkerDialog");

        return false;
    }

    @Override
    public void onHeadingChanged(int heading) {

//        headingGap = heading - (int) aMap.getCameraPosition().bearing;
//        this.heading = (headingGap + (int) aMap.getCameraPosition().bearing) % 360;
//        aMap.setMyLocationRotateAngle(this.heading);
    }


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

                    VisibleRegion visibleRegion = aMap.getProjection().getVisibleRegion();
                    LatLng leftTop = visibleRegion.farLeft;
                    LatLng rightBottom = visibleRegion.nearRight;

                    Log.e("TAG", String.valueOf(leftTop.latitude) + ", " + String.valueOf(leftTop.longitude));
                    Log.e("TAG", String.valueOf(rightBottom.latitude) + ", " + String.valueOf(rightBottom.longitude));
                    HashMap<String, String> params = new HashMap<>();
                    params.put(HttpUtils.Params.LEFT_TOP_LATITUDE, String.valueOf(leftTop.latitude));
                    params.put(HttpUtils.Params.LEFT_TOP_LONGITUDE, String.valueOf(leftTop.longitude));
                    params.put(HttpUtils.Params.RIGHT_BOTTOM_LATITUDE, String.valueOf(rightBottom.latitude));
                    params.put(HttpUtils.Params.RIGHT_BOTTOM_LONGITUDE, String.valueOf(rightBottom.longitude));

                    HttpUtils.getRequestQueue().add(new GsonRequest<>(Request.Method.POST, HttpUtils.Urls.GET_SPOTS, params, SpotBean[].class, new Response.Listener<SpotBean[]>() {
                        @Override
                        public void onResponse(SpotBean[] response) {

                            for (Marker marker : aMap.getMapScreenMarkers()) {
                                marker.remove();
                            }

                            for (int i = 0; i < response.length; i++) {

                                SpotBean spotBean = response[i];

                                MarkerOptions markerOptions = getDefaultMarkerOptions(new LatLng(spotBean.getLatitude(), spotBean.getLongitude()), spotBean.getForce())
                                        .title(spotBean.getTitle())
                                        .snippet(spotBean.getSnippet());

                                aMap.addMarker(markerOptions).setObject(spotBean);
//                                markerIdToPositionOfSpotBeans.put(aMap.addMarker(markerOptions).getId(), i);
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
        PreferenceUtils.savePreference(PreferenceUtils.KEY_LATITUDE, (float) position[0]);
        PreferenceUtils.savePreference(PreferenceUtils.KEY_LONGITUDE, (float) position[1]);
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

    public boolean isGPSEnabled() {
        LocationManager locationManager = (LocationManager) fragment.getActivity().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private MarkerOptions getDefaultMarkerOptions(LatLng latLng, int force) {
        return new MarkerOptions()
                .anchor(0.5f, 0.9f)
                .position(latLng)
                .draggable(false)
                .icon(BitmapDescriptorFactory.fromResource(force == Constants.Force.ONE ? R.drawable.marker_force_1 : R.drawable.marker_force_2));
    }

    @Override
    public void onInfoWindowButtonClicked(Marker marker) {
        if (onCaptureButtonClickedListener != null) {
//            if (AMapUtils.calculateLineDistance(marker.getPosition(), new LatLng(aMap.getMyLocation().getLatitude(), aMap.getMyLocation().getLongitude())) <= Constants.GAMING_CAPTURE_RANGE) {
            onCaptureButtonClickedListener.onCaptureButtonClicked(true, marker);
//            } else {
//                onCaptureButtonClickedListener.onCaptureButtonClicked(false, marker);
//            }
        }
    }


    public interface OnMarkerAddedListener {
        void onMarkerAdded();

        void onMarkerFailed(String title, String snippet, int state);
    }

    public interface OnLocationFixListener {
        void startFixLocation();

        void stopFixLocation();

        void lbsFixedLocation();

        void gpsFixedLocation();

        void gpsStatus(boolean status);
    }

    public interface OnCaptureButtonClickedListener {
        void onCaptureButtonClicked(boolean inRange, Marker marker);
    }

    public interface OnMarkerLoadListener {
        void onMarkerLoaded(boolean success);

        void onMarkerStartLoading();
    }

    public interface OnSpotForceChangedListener {
        void onSpotForceChanged(int state, Marker marker);
    }

}
