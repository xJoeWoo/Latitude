package ng.latitude.support.map;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
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
import ng.latitude.support.bean.SetSpotBean;
import ng.latitude.support.conf.Constants;
import ng.latitude.support.conf.Latitude;
import ng.latitude.support.conf.PreferenceUtils;
import ng.latitude.support.network.GsonRequest;
import ng.latitude.support.network.HttpUtils;
import ng.latitude.support.ui.AddMarketDialog;

/**
 * Created by Ng on 15/6/13.
 */
public class MapUnit implements AMap.OnMarkerClickListener,
        AMap.OnInfoWindowClickListener, AMap.OnMarkerDragListener, AMap.OnMapLoadedListener, AMap.OnCameraChangeListener,
        LocationSource,
        AMapLocationListener, AMap.OnMapClickListener, SensorUnit.OnHeadingChangedListener {

    private MapView mapView;
    private AMap aMap;
    private double[] position = new double[2];
    private LatLngBounds initBounds;
    private Context context;
    private LocationSource.OnLocationChangedListener onLocationChangedListener;
    private LocationManagerProxy locationManagerProxy;
    private OnLocationFixListener onLocationFixListener;
    private OnMarkerAddedListener onMarkerAddedListener;
    private SensorUnit sensorUnit;
    private int heading;
    private int headingGap;
    private Marker currentMarker;

    public MapUnit(Context context, View view) {

        this.mapView = (MapView) view;
        this.context = context;

        sensorUnit = new SensorUnit(context);
//        sensorUnit.setOnHeadingChangedListener(this);
//        sensorUnit.bind();

        if (PreferenceUtils.getPreference(PreferenceUtils.KEY_LATITUDE) != PreferenceUtils.VALUE_NOT_EXIST) {
            position[0] = PreferenceUtils.getPreference(PreferenceUtils.KEY_LATITUDE);
            position[1] = PreferenceUtils.getPreference(PreferenceUtils.KEY_LONGITUDE);
            initBounds = new LatLngBounds.Builder().include(new LatLng(position[0], position[1])).build();
        }

        if (aMap == null) {
            aMap = mapView.getMap();

            aMap.setOnMarkerDragListener(this);// 设置marker可拖拽事件监听器
            aMap.setOnMapLoadedListener(this);// 设置amap加载成功事件监听器
            aMap.setOnMarkerClickListener(this);// 设置点击marker事件监听器
            aMap.setOnInfoWindowClickListener(this);// 设置点击infoWindow事件监听器
            aMap.setOnMapClickListener(this);
            aMap.setOnCameraChangeListener(this);
            aMap.setInfoWindowAdapter(new InfoWindowAdapter());// 设置自定义InfoWindow样式


            MyLocationStyle myLocationStyle = new MyLocationStyle();
            myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.arrow));// 设置小蓝点的图标
            // myLocationStyle.anchor(int,int)//设置小蓝点的锚点

            myLocationStyle.radiusFillColor(context.getResources().getColor(R.color.location_green_primary));// 设置圆形的填充颜色
            myLocationStyle.strokeColor(context.getResources().getColor(R.color.location_green_primary_light));// 设置圆形的边框颜色
            myLocationStyle.strokeWidth(4.0f);// 设置圆形的边框粗细

            aMap.setMyLocationStyle(myLocationStyle);
            aMap.setLocationSource(this);// 设置定位监听
            aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
            aMap.getUiSettings().setCompassEnabled(true);// 指南针
            aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
            aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);//设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
            aMap.getUiSettings().setZoomPosition(AMapOptions.ZOOM_POSITION_RIGHT_CENTER);// 缩放按钮位置
//            aMap.setMapType(AMap.MAP_TYPE_SATELLITE);// 设置地图底图

        }
    }

    public void setOnLocationFixListener(OnLocationFixListener onLocationFixListener) {
        this.onLocationFixListener = onLocationFixListener;
    }

    public void setOnMarkerAddedListener(OnMarkerAddedListener onMarkerAddedListener) {
        this.onMarkerAddedListener = onMarkerAddedListener;
    }

    public boolean addMarkersToMap(String title, String snippet) {

        new AddMarketDialog(context, new AddMarketDialog.OnMarkerConfirmedListener() {
            @Override
            public void onMarkerConfirmed(final String title, final String snippet) {

                HashMap<String, String> params = new HashMap<>();
                params.put(Constants.PARAM_USER_ID, String.valueOf(Latitude.getUserInfo().getId()));
                params.put(Constants.PARAM_SPOT_TITLE, title);
                params.put(Constants.PARAM_SPOT_SNIPPET, snippet);
                params.put(Constants.PARAM_LATITUDE, String.valueOf(aMap.getCameraPosition().target.latitude));
                params.put(Constants.PARAM_LONGITUDE, String.valueOf(aMap.getCameraPosition().target.longitude));
                params.put(Constants.PARAM_FORCE, String.valueOf(Latitude.getUserInfo().getForce()));

                final ProgressDialog dialog = new ProgressDialog(context);
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setIndeterminate(true);
                dialog.setCanceledOnTouchOutside(false);
                dialog.setMessage(context.getString(R.string.dialog_add_marker_creating));
                dialog.show();

                HttpUtils.getRequestQueue().add(new GsonRequest<>(Request.Method.POST, Constants.URL_SET_SPOT, params, SetSpotBean.class, new Response.Listener<SetSpotBean>() {
                    @Override
                    public void onResponse(SetSpotBean response) {
                        dialog.dismiss();

                        if (response.getState() == 1) { // 成功

                            aMap.addMarker(new MarkerOptions()
                                    .anchor(0.5f, 0.9f)
                                    .title(title)
                                    .snippet(snippet)
                                    .position(aMap.getCameraPosition().target)
                                    .draggable(false)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));

                            if (onMarkerAddedListener != null)
                                onMarkerAddedListener.onMarkerAdded();
                        } else {

                            if (onLocationChangedListener != null)
                                onMarkerAddedListener.onMarkerFailed(title, snippet);

                        }
                    }

                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dialog.dismiss();

                        if (onLocationChangedListener != null)
                            onMarkerAddedListener.onMarkerFailed(title, snippet);

                    }
                }));


            }
        }, title, snippet)
                .show();

        return false;
    }

    @Override
    public void onLocationChanged(AMapLocation aLocation) {

        if (onLocationChangedListener != null && aLocation != null && aLocation.hasAccuracy()) {

            String provider = aLocation.getProvider();

            if (provider.equals(Constants.LOCATION_PROVIDER_GPS)) {// GPS定位
                if (onLocationFixListener != null)
                    onLocationFixListener.gpsFixedLocation();

            } else if (provider.equals(Constants.LOCATION_PROVIDER_LBS)) {// 网络定位

                if (onLocationFixListener != null)
                    onLocationFixListener.lbsFixedLocation();

            }

//            aLocation.setBearing(Math.abs(heading - 90) % 360); // 总之要减90


            aLocation.setBearing(360 - heading); // ALocation为顺时针计数

            Log.e("onLocationChanged", String.format("%s: %f %f\tbearing: %f", aLocation.getProvider(), aLocation.getLatitude(), aLocation.getLongitude(), aLocation.getBearing()));

            onLocationChangedListener.onLocationChanged(aLocation);// 显示系统小蓝点
//            aMap.setMyLocationRotateAngle(heading);// 设置小蓝点旋转角度


            position[0] = aLocation.getLatitude();
            position[1] = aLocation.getLongitude();
        }

    }

    @Override
    public void onHeadingChanged(int heading) {

        headingGap = heading - (int) aMap.getCameraPosition().bearing;

        this.heading = (headingGap + (int) aMap.getCameraPosition().bearing) % 360;

        aMap.setMyLocationRotateAngle(this.heading);
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        heading = (headingGap + (int) cameraPosition.bearing) % 360;
        aMap.setMyLocationRotateAngle(heading);
    }

    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {

    }

    @Override
    public void activate(OnLocationChangedListener listener) {

        if (onLocationChangedListener == null)
            onLocationChangedListener = listener;
    }

    @Override
    public void deactivate() {
        if (locationManagerProxy != null) {
            locationManagerProxy.removeUpdates(this);
            locationManagerProxy.destory();
        }
        locationManagerProxy = null;

        if (onLocationFixListener != null)
            onLocationFixListener.stopFixLocation();
    }

    private void requestLocation() {
        if (locationManagerProxy == null) {
            locationManagerProxy = LocationManagerProxy.getInstance(context);
            /*
             * mAMapLocManager.setGpsEnable(false);
			 * 1.0.2版本新增方法，设置true表示混合定位中包含gps定位，false表示纯网络定位，默认是true Location
			 * API定位采用GPS和网络混合定位方式
			 * ，第一个参数是定位provider，第二个参数时间最短是2000毫秒，第三个参数距离间隔单位是米，第四个参数是定位监听者
			 */
            locationManagerProxy.requestLocationData(
                    LocationProviderProxy.AMapNetwork, 5000, 10, this);

            Log.e(Thread.currentThread().getStackTrace()[1].getClassName() + "#" + Thread.currentThread().getStackTrace()[1].getMethodName()
                    , "");

            if (onLocationFixListener != null)
                onLocationFixListener.gpsStatus(isGPSEnabled());

            if (onLocationFixListener != null)
                onLocationFixListener.startFixLocation();
        }
    }

    @Override
    public void onInfoWindowClick(final Marker marker) {

        new AlertDialog.Builder(context).setMessage(R.string.dialog_remove_marker_message).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //marker.remove();
                marker.destroy();
            }
        }).setNegativeButton(android.R.string.cancel, null).show();

    }

    @Override
    public void onMapLoaded() {

        if (initBounds != null) {
            aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(initBounds, 0));
            aMap.moveCamera(CameraUpdateFactory.zoomTo(Constants.INIT_ZOOM_LEVEL));
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
        deactivate();
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
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        currentMarker.hideInfoWindow();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        currentMarker = marker;
        marker.showInfoWindow();
        return true;
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

    }

    @Override
    public void onLocationChanged(Location aLocation) {

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

    public interface OnMarkerAddedListener {
        void onMarkerAdded();

        void onMarkerFailed(String title, String snippet);
    }

    public interface OnLocationFixListener {
        void startFixLocation();

        void stopFixLocation();

        void lbsFixedLocation();

        void gpsFixedLocation();

        void gpsStatus(boolean status);
    }

}
