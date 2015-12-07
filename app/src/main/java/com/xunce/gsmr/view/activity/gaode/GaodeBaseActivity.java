package com.xunce.gsmr.view.activity.gaode;

import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.LatLng;
import com.xunce.gsmr.R;
import com.xunce.gsmr.model.PrjItem;
import com.xunce.gsmr.model.gaodemap.MarkerHolder;
import com.xunce.gsmr.util.preference.PreferenceHelper;

/**
 * 必须有一个R.id.id_map的高德地图控件
 * 必须被调用init()方法
 * 高德基础地图Activity
 * Created by ssthouse on 2015/9/14.
 */
public class GaodeBaseActivity extends AppCompatActivity {

    /**
     * 地图
     */
    private AMap aMap;
    private MapView mapView;
    private UiSettings mUiSettings;


    /**
     * 定位回调
     */
    private LocationSource.OnLocationChangedListener mListener;
    /**
     * 定位管理器
     */
    private LocationManagerProxy aMapManager;
    /**
     * 保存当前位置
     */
    private AMapLocation currentAMapLocation;

    /**
     * 地图上的标记点管理器
     */
    private MarkerHolder markerHolder;

    /**
     * 定位回调
     */
    private LocationSource locationSource = new LocationSource() {
        /**
         * 激活定位
         */
        @Override
        public void activate(OnLocationChangedListener listener) {
            mListener = listener;
            if (aMapManager == null) {
                aMapManager = LocationManagerProxy.getInstance(GaodeBaseActivity.this);
            /*
			 * mAMapLocManager.setGpsEnable(false);//
			 * 1.0.2版本新增方法，设置true表示混合定位中包含gps定位，false表示纯网络定位，默认是true
			 */
                // Location API定位采用GPS和网络混合定位方式，时间最短是2000毫秒
                aMapManager.requestLocationData(
                        LocationProviderProxy.AMapNetwork, 1000, 10, locationListener);
                //判断是否开启GPS定位
                if (PreferenceHelper.getInstance(GaodeBaseActivity.this)
                        .getIsWifiLocateMode(GaodeBaseActivity.this)) {
                    aMapManager.setGpsEnable(false);
                }
            }
        }

        /**
         * 停止定位
         */
        @Override
        public void deactivate() {
            mListener = null;
            if (aMapManager != null) {
                aMapManager.removeUpdates(locationListener);
                aMapManager.destroy();
            }
            aMapManager = null;
        }
    };

    /**
     * 获取定位的回调
     */
    private AMapLocationListener locationListener = new AMapLocationListener() {
        /**
         * 定位回调方法
         */
        @Override
        public void onLocationChanged(AMapLocation aLocation) {
            if (mListener != null && aLocation != null) {
                currentAMapLocation = aLocation;
                mListener.onLocationChanged(aLocation);// 显示系统小蓝点
            }
        }
        //废弃方法---------------------------------------------
        @Override
        public void onLocationChanged(Location location) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
    };

    /**
     * 初始化AMap对象
     */
    public void init(Bundle savedInstanceState) {
        mapView = (MapView) findViewById(R.id.id_map_view);
        // 此方法必须重写
        mapView.onCreate(savedInstanceState);
        if (aMap == null) {
            aMap = mapView.getMap();
            mUiSettings = aMap.getUiSettings();
        }
    }

    /**
     * 加载marker
     */
    public void loadMarker(PrjItem prjItem) {
        //MarkerHolder模块
        if(markerHolder == null){
            markerHolder = new MarkerHolder(this, prjItem, getaMap());
        }else{
            markerHolder.initMarker();
        }
    }

    /**
     * 地图中心移动到一个点
     *
     * @param latLng
     */
    public void animateToPoint(LatLng latLng) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.changeLatLng(latLng);
        aMap.animateCamera(cameraUpdate);
    }

    /**
     * 定位到我的位置
     */
    public void animateToMyLocation() {
        if (currentAMapLocation == null) {
            return;
        }
        LatLng latLng = new LatLng(currentAMapLocation.getLatitude(),
                currentAMapLocation.getLongitude());
        animateToPoint(latLng);
    }

    //定位相关-------------------------------------------------------------

    /**
     * 隐藏定位
     */
    public void hideLocate() {
        aMap.setLocationSource(locationSource);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(false); // 是否显示默认的定位按钮
        aMap.setMyLocationEnabled(false);// 是否可触发定位并显示定位层
    }

    /**
     * 显示定位
     */
    public void showLocate() {
        aMap.setLocationSource(locationSource);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(false); // 是否显示默认的定位按钮
        aMap.setMyLocationEnabled(true);// 是否可触发定位并显示定位层
    }

    //生命周期---------------------------------------------------------
    @Override
    protected void onResume() {
        super.onResume();
        if (mapView != null) {
            mapView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mapView != null) {
            mapView.onPause();
            locationSource.deactivate();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mapView != null) {
            mapView.onSaveInstanceState(outState);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mapView != null) {
            mapView.onDestroy();
        }
    }

    //getter----and----setter--------------------------------------
    public AMap getaMap() {
        return aMap;
    }

    public void setaMap(AMap aMap) {
        this.aMap = aMap;
    }

    public MapView getMapView() {
        return mapView;
    }

    public void setMapView(MapView mapView) {
        this.mapView = mapView;
    }

    public MarkerHolder getMarkerHolder() {
        return markerHolder;
    }
}
