package com.xunce.gsmr.view.fragment.gaode;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapFragment;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.Marker;
import com.baidu.mapapi.model.LatLng;
import com.xunce.gsmr.R;
import com.xunce.gsmr.util.LogHelper;
import com.xunce.gsmr.view.fragment.CustomMap;

/**
 * 高德地图的PrjEditFragment
 * Created by ssthouse on 2015/9/14.
 */
public class CustomGaodeMap extends MapFragment implements CustomMap , LocationSource,
        AMapLocationListener{
    private static final String TAG = "CustomGaodeMap";

    /**
     * 主视图
     */
    private View layout;

    /**
     * 上下文
     */
    private Context context;

    /**
     * 控制的地图
     */
    private MapView mapView;
    private AMap aMap;
    private UiSettings uiSettings;
    //显示的InfoWindow
    private Marker infoWindow;

    /**
     * 定位相关
     */
    private OnLocationChangedListener mListener;
    private LocationManagerProxy aMapManager;

    /**
     * 获取Instance
     *
     * @param bundle
     * @return
     */
    public static CustomGaodeMap getInstance(Bundle bundle) {
        CustomGaodeMap customGaodeMap = new CustomGaodeMap();
        customGaodeMap.setArguments(bundle);
        return customGaodeMap;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.fragment_prj_edit_gaode, null);
        mapView = (MapView) layout.findViewById(R.id.id_map_view);
        mapView.onCreate(savedInstanceState);
        aMap = mapView.getMap();
//        uiSettings = aMap.getUiSettings();
        //初始化定位
//        initLocate();

        //初始化View
        initView();

        return layout;
    }

    /**
     * 初始化View
     */
    private void initView(){
        Switch sw = (Switch) layout.findViewById(R.id.id_sw_locate);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                aMap.setLocationSource(CustomGaodeMap.this);// 设置定位监听
                aMap.getUiSettings().setMyLocationButtonEnabled(isChecked); // 是否显示默认的定位按钮
                aMap.setMyLocationEnabled(isChecked);// 是否可触发定位并显示定位层
            }
        });
    }


    @Override
    public LatLng getCurrentMarkerLatLng() {
        return null;
    }

    @Override
    public LatLng getTarget() {
        return null;
    }

    @Override
    public void loadRail() {

    }

    @Override
    public void loadMarker() {

    }

    @Override
    public void locate() {

    }

    @Override
    public void hideInfoWindow() {

    }

    //----生命周期----------------------------------------------
    @Override
    public void create(Bundle savedInstanceState) {
//        if (mapView != null) {
//            mapView.onCreate(savedInstanceState);
//        }
    }

    @Override
    public void pause() {
//        if (mapView != null) {
//            mapView.onPause();
//            deactivate();
//        }
    }

    @Override
    public void resume() {
//        if (mapView != null) {
//            mapView.onResume();
//        }
    }

    @Override
    public void destory() {
//        if (mapView != null) {
//            mapView.onDestroy();
//        }
    }

    @Override
    public void saveInstanceState(Bundle state) {
        mapView.onSaveInstanceState(state);
    }

    /**
     * 定位成功后回调函数
     */
    @Override
    public void onLocationChanged(AMapLocation aLocation) {
        LogHelper.Log(TAG, "收到数据");
        if (mListener != null) {
            mListener.onLocationChanged(aLocation);// 显示系统小蓝点
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

    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (aMapManager == null) {
            aMapManager = LocationManagerProxy.getInstance(context);
			/*
			 * mAMapLocManager.setGpsEnable(false);//
			 * 1.0.2版本新增方法，设置true表示混合定位中包含gps定位，false表示纯网络定位，默认是true
			 */
            // Location API定位采用GPS和网络混合定位方式，时间最短是2000毫秒
            aMapManager.requestLocationData(
                    LocationProviderProxy.AMapNetwork, 2000, 10, this);
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

    //------------------------------------------------------------------
//    /**
//     * 方法必须重写
//     */
//    @Override
//    public void onResume() {
//        super.onResume();
//        mapView.onResume();
//    }
//
//    /**
//     * 方法必须重写
//     */
//    @Override
//    public void onPause() {
//        super.onPause();
//        mapView.onPause();
//        deactivate();
//    }
//
//    /**
//     * 方法必须重写
//     */
//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        mapView.onSaveInstanceState(outState);
//    }
//
//    /**
//     * 方法必须重写
//     */
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        mapView.onDestroy();
//    }

}
