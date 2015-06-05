package ng.latitude;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

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
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;

import ng.latitude.support.map.InfoWindowAdapter;
import ng.latitude.support.ui.BottomButtons;
import ng.latitude.support.conf.Constants;
import ng.latitude.support.ui.BottomInfo;
import ng.latitude.support.ui.GravityInterpolator;
import ng.latitude.support.conf.PreferenceUtils;


public class MainActivity extends AppCompatActivity implements AMap.OnMarkerClickListener,
        AMap.OnInfoWindowClickListener, AMap.OnMarkerDragListener, AMap.OnMapLoadedListener, LocationSource,
        AMapLocationListener, AMap.OnMapClickListener {

    private MapView mapView;
    private AMap aMap;
    private Toolbar toolbar;
    private OnLocationChangedListener onLocationChangedListener;
    private LocationManagerProxy locationManagerProxy;
    private double[] position = new double[2];
    private LatLngBounds initBounds;
    private boolean initLocation = false;

    private ImageView ivSetPosition;
    private BottomButtons bBtn;
    private BottomInfo bInf;

    private static final float INIT_ZOOM_LEVEL = 17f;

    private enum BackStatus {Normal, SettingPosition}

    private BackStatus currentBackStatus = BackStatus.Normal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);

        findViews();
        setListeners();

        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        setSupportActionBar(toolbar);

        mapView.onCreate(savedInstanceState);

        initMap();
    }

    private void findViews() {
        ivSetPosition = (ImageView) findViewById(R.id.iv_main_set_position);
        mapView = (MapView) findViewById(R.id.map_main);
        toolbar = (Toolbar) findViewById(R.id.tb_main);
        bBtn = (BottomButtons) findViewById(R.id.bbtn_main);
        bInf=(BottomInfo)findViewById(R.id.binf_main);
    }

    private void setListeners() {
        bBtn.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMarkersToMap(aMap.getCameraPosition().target);
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPosition(false);
            }
        });
    }

    private void initMap() {

        if (PreferenceUtils.getPreference(PreferenceUtils.KEY_LATITUDE) != PreferenceUtils.VALUE_NOT_EXIST) {
            position[0] = PreferenceUtils.getPreference(PreferenceUtils.KEY_LATITUDE);
            position[1] = PreferenceUtils.getPreference(PreferenceUtils.KEY_LONGTITUDE);
            initBounds = new LatLngBounds.Builder().include(new LatLng(position[0], position[1])).build();
        }

        if (aMap == null) {
            aMap = mapView.getMap();

            aMap.setOnMarkerDragListener(this);// 设置marker可拖拽事件监听器
            aMap.setOnMapLoadedListener(this);// 设置amap加载成功事件监听器
            aMap.setOnMarkerClickListener(this);// 设置点击marker事件监听器
            aMap.setOnInfoWindowClickListener(this);// 设置点击infoWindow事件监听器
            aMap.setOnMapClickListener(this);
            aMap.setInfoWindowAdapter(new InfoWindowAdapter());// 设置自定义InfoWindow样式

            MyLocationStyle myLocationStyle = new MyLocationStyle();
            myLocationStyle.myLocationIcon(BitmapDescriptorFactory
                    .fromResource(android.R.drawable.ic_menu_add));// 设置小蓝点的图标
            // myLocationStyle.anchor(int,int)//设置小蓝点的锚点

            myLocationStyle.radiusFillColor(getResources().getColor(R.color.location_green_primary));// 设置圆形的填充颜色
            myLocationStyle.strokeColor(getResources().getColor(R.color.location_green_primary_light));// 设置圆形的边框颜色
            myLocationStyle.strokeWidth(4.0f);// 设置圆形的边框粗细

            aMap.setMyLocationStyle(myLocationStyle);
            aMap.setLocationSource(this);// 设置定位监听
            aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
            aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
            aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);//设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
            aMap.setMapType(AMap.MAP_TYPE_SATELLITE);// 设置地图底图

            bInf.show();
        }
    }


    /**
     * 地图载入完成
     */
    @Override
    public void onMapLoaded() {
        setBounds();
    }

    private void setBounds() {
        if (initBounds != null) {
            aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(initBounds, 0));
            aMap.moveCamera(CameraUpdateFactory.zoomTo(INIT_ZOOM_LEVEL));
            initLocation=true;
        }

    }


    /**
     * Marker点击监听
     */
    @Override
    public boolean onMarkerClick(final Marker marker) {

//        Toast.makeText(this, "clicked" + marker.getId() + "\n" + marker.getTitle(), Toast.LENGTH_SHORT).show();

        return false;
    }


    /**
     * 位置改变
     */
    @Override
    public void onLocationChanged(AMapLocation aLocation) {
        if (onLocationChangedListener != null && aLocation != null && aLocation.getLatitude()!=0d && aLocation.getLongitude()!=0d) {

            String provider = aLocation.getProvider();

            if(provider.equals(Constants.LOCATION_PROVIDER_GPS)){// GPS定位
                if(!initLocation){
                    initLocation=true;
                    bInf.hide();
                }
            }else if(provider.equals(Constants.LOCATION_PROVIDER_LBS)){// 网络定位

            }

            Log.e("onLocationChanged", String.format("%s: %f %f", aLocation.getProvider(), aLocation.getLatitude(),aLocation.getLongitude()));

            onLocationChangedListener.onLocationChanged(aLocation);// 显示系统小蓝点
            aMap.setMyLocationRotateAngle(aMap.getCameraPosition().bearing);// 设置小蓝点旋转角度


        }
    }

    /**
     * 地图点击监听
     */
    @Override
    public void onMapClick(final LatLng latLng) {
//        Log.e("adsfasdf", String.format("Map Clicked: %.3f %.3f", latLng.latitude, latLng.longitude));
    }

    private void addMarkersToMap(final LatLng latLng) {

        View v = getLayoutInflater().inflate(R.layout.dialog_add_marker, (RelativeLayout) findViewById(R.id.dialog_add_marker_rv));
        final EditText etName = (EditText) v.findViewById(R.id.et_dialog_name);
        final EditText etSnippet = (EditText) v.findViewById(R.id.et_dialog_snippet);

        new AlertDialog.Builder(this).setTitle(R.string.dialog_add_marker_title).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) { //确认
                setPosition(false);
                aMap.addMarker(new MarkerOptions().anchor(0.5f,0.9f).title(etName.getText().toString()).snippet(etSnippet.getText().toString()).position(latLng).draggable(false).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
            }
        }).setNegativeButton(android.R.string.cancel, null).setView(v).show();

    }


    /**
     * 窗口监听
     */
    @Override
    public void onInfoWindowClick(final Marker marker) {
        new AlertDialog.Builder(this).setMessage(R.string.dialog_remove_marker_message).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //marker.remove();
                marker.destroy();
            }
        }).setNegativeButton(android.R.string.cancel, null).show();
    }

    /**
     * Market拖动
     */
    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

    }

    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener listener) {
        onLocationChangedListener = listener;
        if (locationManagerProxy == null) {
            locationManagerProxy = LocationManagerProxy.getInstance(this);
            /*
             * mAMapLocManager.setGpsEnable(false);
			 * 1.0.2版本新增方法，设置true表示混合定位中包含gps定位，false表示纯网络定位，默认是true Location
			 * API定位采用GPS和网络混合定位方式
			 * ，第一个参数是定位provider，第二个参数时间最短是2000毫秒，第三个参数距离间隔单位是米，第四个参数是定位监听者
			 */
            locationManagerProxy.requestLocationData(
                    LocationProviderProxy.AMapNetwork, 5000, 10, this);
        }
    }

    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        onLocationChangedListener = null;
        if (locationManagerProxy != null) {
            locationManagerProxy.removeUpdates(this);
            locationManagerProxy.destory();
        }
        locationManagerProxy = null;
    }

    @Override
    public void onBackPressed() {

        switch (currentBackStatus) {
            case SettingPosition:
                setPosition(false);
                break;
            case Normal:
                super.onBackPressed();
                break;
            default:
                super.onBackPressed();
                break;

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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

    private void setPosition(boolean isSetting) {
        if (isSetting) {
            currentBackStatus = BackStatus.SettingPosition;
            startSetPositionAnim(true);
            bBtn.show();
            aMap.getUiSettings().setZoomPosition(AMapOptions.ZOOM_POSITION_RIGHT_CENTER);
            findViewById(R.id.action_add).setVisibility(View.GONE);
        } else {
            currentBackStatus = BackStatus.Normal;
            startSetPositionAnim(false);
            bBtn.hide();
            aMap.getUiSettings().setZoomPosition(AMapOptions.ZOOM_POSITION_RIGHT_BUTTOM);
            findViewById(R.id.action_add).setVisibility(View.VISIBLE);
        }
    }

    private void startSetPositionAnim(final boolean visible) {

        float transY = findViewById(R.id.rv_main).getHeight() - ivSetPosition.getY();
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


//        ObjectAnimator menuOA = ObjectAnimator.ofFloat(menuAdd, Constants.OBJECT_ANIM_ALPHA, visible ? 1f : 0f, visible ? 0f : 1f).setDuration(Constants.ANIM_COMMON_DURATION);
//        menuOA.setInterpolator(new LinearInterpolator());
//        menuOA.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationStart(Animator animation) {
//                if (!visible)
//                    menuAdd.setVisibility(View.VISIBLE);
//            }
//
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                if (visible)
//                    menuAdd.setVisibility(View.GONE);
//            }
//        });
//        menuOA.start();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        deactivate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        PreferenceUtils.savePreference(PreferenceUtils.KEY_LATITUDE, (float) position[0]);
        PreferenceUtils.savePreference(PreferenceUtils.KEY_LONGTITUDE, (float) position[1]);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    /**
     * 废弃方法
     */
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


}
