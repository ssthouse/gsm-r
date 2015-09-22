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
import com.xunce.gsmr.model.gaodemap.GaodeRailWayHolder;
import com.xunce.gsmr.model.gaodemap.MarkerHolder;
import com.xunce.gsmr.util.PreferenceHelper;

/**
 * 必须有一个R.id.id_map的高德地图控件
 * 必须被调用init()方法
 * 高德基础地图Activity
 * Created by ssthouse on 2015/9/14.
 */
public class GaodeBaseActivity extends AppCompatActivity implements LocationSource,
        AMapLocationListener {

    /**
     * 地图
     */
    private AMap aMap;
    private MapView mapView;
    private UiSettings mUiSettings;

    /**
     * 定位
     */
    private OnLocationChangedListener mListener;
    private LocationManagerProxy aMapManager;
    private AMapLocation currentAMapLocation;

    /**
     * 地图上的标记点
     */
    private MarkerHolder markerHolder;

    /**
     * 铁路绘图管理器
     */
    private GaodeRailWayHolder railWayHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * 初始化AMap对象
     */
    public void init(Bundle savedInstanceState) {
        /*
         * 设置离线地图存储目录，在下载离线地图或初始化地图设置; 使用过程中可自行设置, 若自行设置了离线地图存储的路径，
		 * 则需要在离线地图下载和使用地图页面都进行路径设置
		 */
        // Demo中为了其他界面可以使用下载的离线地图，使用默认位置存储，屏蔽了自定义设置
        // MapsInitializer.sdcardDir =OffLineMapUtils.getSdCacheDir(this);
        mapView = (MapView) findViewById(R.id.id_map_view);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        if (aMap == null) {
            aMap = mapView.getMap();
            mUiSettings = aMap.getUiSettings();
        }
    }

    /**
     * TODO
     * 加载铁路地图
     */
    public void loadRail(PrjItem prjItem) {
        if (railWayHolder == null) {
            railWayHolder = new GaodeRailWayHolder(this, prjItem);
        }
        railWayHolder.draw(aMap);
    }

    /**
     * TODO
     * 加载marker
     */
    public void loadMarker(PrjItem prjItem) {
        //MarkerHolder模块
        markerHolder = new MarkerHolder(this, prjItem, getaMap());
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
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(false); // 是否显示默认的定位按钮
        aMap.setMyLocationEnabled(false);// 是否可触发定位并显示定位层
    }

    /**
     * 显示定位
     */
    public void showLocate() {
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(false); // 是否显示默认的定位按钮
        aMap.setMyLocationEnabled(true);// 是否可触发定位并显示定位层
    }

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

    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (aMapManager == null) {
            aMapManager = LocationManagerProxy.getInstance(this);
            /*
			 * mAMapLocManager.setGpsEnable(false);//
			 * 1.0.2版本新增方法，设置true表示混合定位中包含gps定位，false表示纯网络定位，默认是true
			 */
            // Location API定位采用GPS和网络混合定位方式，时间最短是2000毫秒
            aMapManager.requestLocationData(
                    LocationProviderProxy.AMapNetwork, 1000, 10, this);
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
            aMapManager.removeUpdates(this);
            aMapManager.destroy();
        }
        aMapManager = null;
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
            deactivate();
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

    public GaodeRailWayHolder getRailWayHolder() {
        return railWayHolder;
    }
}
