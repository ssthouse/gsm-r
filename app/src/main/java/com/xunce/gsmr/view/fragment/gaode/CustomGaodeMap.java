package com.xunce.gsmr.view.fragment.gaode;

import android.app.Fragment;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.LocationSource;
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
public class CustomGaodeMap extends Fragment implements CustomMap {
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
    private LocationSource.OnLocationChangedListener locationListener;
    private LocationManagerProxy locationManager;
    private AMapLocationListener aMapLocationListener;

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
        //获取控件
        aMap = mapView.getMap();
        uiSettings = aMap.getUiSettings();
        //初始化定位
        initLocate();

        return layout;
    }

    /**
     * 初始化定位
     */
    private void initLocate() {
        //初始化监听器
        aMapLocationListener = new AMapLocationListener() {
            //只有这个有用...................
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                //TODO---接收到位置应该怎么处理??
                LogHelper.Log(TAG, "我接受到了数据");
                if (aMapLocation.getAMapException().getErrorCode() == 0) {
                    // 显示系统小蓝点
                    locationListener.onLocationChanged(aMapLocation);
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
        //开启定位
        aMap.setLocationSource(new LocationSource() {
            @Override
            public void activate(OnLocationChangedListener listener) {
                LogHelper.Log(TAG, "我开启了定位");
                locationListener = listener;
                if (locationManager == null) {
                    locationManager = LocationManagerProxy.getInstance(context);
                    /*
                     * mAMapLocManager.setGpsEnable(false);
                     * 1.0.2版本新增方法，设置true表示混合定位中包含gps定位，false表示纯网络定位，默认是true Location
                     * API定位采用GPS和网络混合定位方式
                     * ，第一个参数是定位provider，第二个参数时间最短是2000毫秒，第三个参数距离间隔单位是米，第四个参数是定位监听者
                     */
                    locationManager.setGpsEnable(true);
                    locationManager.requestLocationData(
                            LocationProviderProxy.AMapNetwork, 1000, 10, aMapLocationListener);
                }
            }

            @Override
            public void deactivate() {
                locationListener = null;
                if (locationManager != null) {
                    locationManager.removeUpdates(aMapLocationListener);
                    locationManager.destroy();
                }
                locationManager = null;
            }
        });
        // 设置定位监听
        uiSettings.setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);
        //设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
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
        if (mapView != null) {
            mapView.onCreate(savedInstanceState);
        }
    }

    @Override
    public void pause() {
        if (mapView != null) {
            mapView.onPause();
        }
    }

    @Override
    public void resume() {
        if (mapView != null) {
            mapView.onResume();
        }
    }

    @Override
    public void destory() {
        if (mapView != null) {
            mapView.onDestroy();
        }
    }

    @Override
    public void saveInstanceState(Bundle state) {
        mapView.onSaveInstanceState(state);
    }
}
