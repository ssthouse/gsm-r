package com.xunce.gsmr.view.fragment.baidu;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.xunce.gsmr.R;
import com.xunce.gsmr.model.PrjItem;
import com.xunce.gsmr.model.baidumap.MarkerHolder;
import com.xunce.gsmr.model.baidumap.RailWayHolder;
import com.xunce.gsmr.util.PreferenceHelper;
import com.xunce.gsmr.util.gps.MapHelper;
import com.xunce.gsmr.view.activity.baidu.MeasureActivity;
import com.xunce.gsmr.view.fragment.CustomMap;

/**
 * 包含百度地图的一些组件
 * 简化Activity代码
 * Created by ssthouse on 2015/9/13.
 */
public class CustomBaiduMap extends Fragment implements CustomMap {
    private static final String TAG = "CustomBaiduMap";

    /**
     * 上下文
     */
    private Context context;

    /**
     * Fragment的layout
     */
    private View layout;

    /**
     * 控制的百度地图
     */
    private MapView mapView;
    private BaiduMap baiduMap;
    //地图显示的InfoWindow
    private InfoWindow infoWindow;
    private LinearLayout llInfoWindow;

    /**
     * 控制的Project数据
     */
    private PrjItem prjItem;

    /**
     * 控制地图上的Marker
     */
    private MarkerHolder markerHolder;

    /**
     * 地图Graph的控制器
     */
    private RailWayHolder railWayHolder;

    /**
     * 控制定位
     */
    private LocationClient locationClient;
    //当前获取的定位位置
    private BDLocation currentBDLocation;
    //是否已经定位
    private boolean isLocated;
    //定位按钮
    private ImageButton btnLocate;

    /**
     * 是否首次进入
     */
    private boolean isFistIn = true;

    /**
     * 公里标VIew
     */
    private LinearLayout llPosition;
    private EditText etPosition;
    private boolean isPositionShowed = false;

    /**
     * 获取Instance
     *
     * @param bundle
     * @return
     */
    public static CustomBaiduMap getInstance(Bundle bundle) {
        CustomBaiduMap customBaiduMap = new CustomBaiduMap();
        customBaiduMap.setArguments(bundle);
        return customBaiduMap;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.fragment_prj_edit_baidu, null);
        //初始化数据
        mapView = (MapView) layout.findViewById(R.id.id_map_view);
        context = getActivity();
        btnLocate = (ImageButton) layout.findViewById(R.id.id_ib_locate);
        prjItem = (PrjItem) getArguments().getSerializable("prjItem");

        //正式初始化
        init();
        return layout;
    }

    /**
     * 正式初始化
     */
    private void init() {
        //初始化数据
        this.baiduMap = mapView.getMap();
        //初始化BaiduMap
        initBaiduMap();
        //初始化marker控制器
        markerHolder = new MarkerHolder(prjItem, baiduMap);
        //初始化Graph控制器
        railWayHolder = new RailWayHolder(context, prjItem);
        //初始化定位控制器
        initLocationClient();
    }

    /**
     * 初始化BaiduMap
     */
    private void initBaiduMap() {
        baiduMap.setMyLocationEnabled(true);
        MapHelper.animateZoom(baiduMap, 15);
        baiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
                MyLocationConfiguration.LocationMode.NORMAL, true, null));

        //触摸屏幕则---定位失效
        baiduMap.setOnMapTouchListener(new BaiduMap.OnMapTouchListener() {
            @Override
            public void onTouch(MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    btnLocate.setImageResource(R.drawable.locate2);
                    isLocated = false;
                }
            }
        });

        //初始化InfoWindow内容
        llInfoWindow = (LinearLayout) LayoutInflater.from(context)
                .inflate(R.layout.view_info_window, null);

        //marker的点击事件
        baiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                //如果点的是已经选中了的Marker---变回未选中状态
                if (marker == markerHolder.getCurrentMarker()) {
                    markerHolder.clearSelection();
                    baiduMap.hideInfoWindow();
                } else {
                    markerHolder.setAll2Blue();
                    marker.setIcon(MarkerHolder.descriptorRed);
                    //选中了Marker
                    markerHolder.setCurrentMarker(marker);
                    //弹出InfoWindow
                    showInfoWindow(marker.getPosition());
                }
                return true;
            }
        });

        //定位按钮点击事件
        layout.findViewById(R.id.id_ib_locate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locate();
            }
        });

        //启动测量的按钮
        layout.findViewById(R.id.id_ib_measure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //开启测量Activity
                MeasureActivity.start((Activity) context, baiduMap.getMapStatus().target);
            }
        });

        //公里标开关按钮
        layout.findViewById(R.id.id_ib_position).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPositionShowed) {
                    hideLlPosition();
                } else {
                    showLlPosition();
                }
            }
        });
        llPosition = (LinearLayout) layout.findViewById(R.id.id_ll_position);
        etPosition = (EditText) llPosition.findViewById(R.id.id_et);
        llPosition.findViewById(R.id.id_btn_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO---地图定位到指定的DK文字位置去...
                hideLlPosition();
            }
        });
    }

    /**
     * 显示公里标输入框
     */
    private void showLlPosition() {
        isPositionShowed = true;
        llPosition.setVisibility(View.VISIBLE);
        llPosition.startAnimation(AnimationUtils.loadAnimation(context, R.anim.pop_up));
    }

    /**
     * 隐藏公里标输入框
     */
    private void hideLlPosition() {
        isPositionShowed = false;
        llPosition.startAnimation(AnimationUtils.loadAnimation(context, R.anim.drop_down));
        llPosition.setVisibility(View.GONE);
    }

    /**
     * 显示InfoWindow
     *
     * @param latLng
     */
    public void showInfoWindow(LatLng latLng) {
        infoWindow = new InfoWindow(llInfoWindow, latLng, -47);
        baiduMap.showInfoWindow(infoWindow);
    }

    public void hideInfoWindow() {
        baiduMap.hideInfoWindow();
    }



    @Override
    public LatLng getCurrentMarkerLatLng() {
        return markerHolder.getCurrentMarker().getPosition();
    }

    /**
     * 初始化LocationClient
     */
    private void initLocationClient() {
        //创建client
        locationClient = new LocationClient(context);
        final LocationClientOption locateOptions = new LocationClientOption();
        //设置Options
        if (PreferenceHelper.getInstance(context).getIsWifiLocateMode(context)) {
            locateOptions.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        } else {
            locateOptions.setLocationMode(LocationClientOption.LocationMode.Device_Sensors);
        }
        locateOptions.setCoorType("bd09ll");    //返回的定位结果是百度经纬度,默认值gcj02
        locateOptions.setScanSpan(1000);        //设置发起定位请求的间隔时间为5000ms
        locateOptions.setIsNeedAddress(true);   //返回的定位结果包含地址信息
        locateOptions.setNeedDeviceDirect(true);//返回的定位结果包含手机机头的方向
        locationClient.setLocOption(locateOptions);
        //注册监听事件
        locationClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                if (bdLocation != null) {
                    //如果是第一次获取到数据----将地图定位到改点
                    currentBDLocation = bdLocation;
                    if (isFistIn) {
                        locate();
                        isFistIn = false;
                    }
                }
            }
        });
        locationClient.start();
    }

    /**
     * 定位到当前接收到的定位点
     */
    @Override
    public void locate() {
        if (currentBDLocation != null) {
            //更新我的位置
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(currentBDLocation.getRadius())
                    .latitude(currentBDLocation.getLatitude())
                    .longitude(currentBDLocation.getLongitude()).build();
            baiduMap.setMyLocationData(locData);
            //更新地图中心点
            LatLng ll = new LatLng(currentBDLocation.getLatitude(),
                    currentBDLocation.getLongitude());
            MapHelper.animateToPoint(baiduMap, ll);
        }
    }


    /**
     * 动画聚焦到一个点
     *
     * @param latLng
     */
    public void animateToPoint(LatLng latLng) {
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(latLng);
        baiduMap.animateMapStatus(u);
    }

    /**
     * 动画放大
     *
     * @param zoomLevel
     */
    public void animateZoom(int zoomLevel) {
        MapStatusUpdate u = MapStatusUpdateFactory.zoomTo(zoomLevel);
        baiduMap.animateMapStatus(u);
    }

    /**
     * 加载Marker图标
     */
    @Override
    public void loadMarker() {
        markerHolder.initMarkerList();
    }

    @Override
    public LatLng getTarget() {
        return baiduMap.getMapStatus().target;
    }

    /**
     * 加载Rail的图形数据
     */
    @Override
    public void loadRail() {
        railWayHolder.draw(baiduMap);
    }

    //--------------生命周期--------------------------------------------
    @Override
    public void create(Bundle savedInstanceState) {

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
        if (locationClient != null) {
            locationClient.stop();
        }
    }
    @Override
    public void saveInstanceState(Bundle state) {
    }

}
