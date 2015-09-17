package com.xunce.gsmr.view.activity.baidu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.xunce.gsmr.R;
import com.xunce.gsmr.app.Constant;
import com.xunce.gsmr.util.AnimHelper;
import com.xunce.gsmr.util.ToastHelper;
import com.xunce.gsmr.util.gps.LocateHelper;
import com.xunce.gsmr.util.gps.MapHelper;
import com.xunce.gsmr.util.gps.MarkerHelper;
import com.xunce.gsmr.view.style.TransparentStyle;
import com.xunce.gsmr.view.widget.ZoomControlView;

/**
 * 取点Activity
 * Created by ssthouse on 2015/7/24.
 */
public class BaiduGetLatLngActivity extends AppCompatActivity {
    private static final String TAG = "BaiduGetLatLngActivity";

    //地图View
    private MapView mMapView;
    //地图控制器
    private BaiduMap mBaiduMap;
    // 定位相关
    private LocationClient mLocClient;
    //跟随----普通---罗盘---三种定位方式
    private MyLocationConfiguration.LocationMode mCurrentMode =
            MyLocationConfiguration.LocationMode.NORMAL;

    //经纬度的输入框
    private EditText etLatitude, etLongitude;
    //视图中间的marker
    private ImageView ivMark;
    //控制状态的button
    private ImageButton ibMode;

    //用于Activity开启时的第一次定位
    private boolean isFistIn = true;
    //用于判断是否已经定位
    private boolean isLocated = true;

    //定位监听器---每秒触发
    public BDLocationListener myListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            //locate(location);
            if (isFistIn) {
                locate(location);
                isFistIn = false;
            }
        }
    };


    public static void start(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, BaiduGetLatLngActivity.class);
        intent.putExtra(Constant.EXTRA_KEY_REQUEST_CODE, requestCode);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_latlng);
        TransparentStyle.setTransparentStyle(this, R.color.color_primary);

        // 定位初始化
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);
        //初始化定位---设置
        LocateHelper.initLocationClient(this, mLocClient);
        //开启定位
        mLocClient.start();

        //初始化视图
        initView();
    }

    /**
     * 初始化视图
     */
    private void initView() {
        mMapView = (MapView) findViewById(R.id.id_map_view);
        mMapView.showZoomControls(false);
        mBaiduMap = mMapView.getMap();
        UiSettings uiSettings = mBaiduMap.getUiSettings();
        //开启指南针
        uiSettings.setCompassEnabled(true);
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
                MyLocationConfiguration.LocationMode.NORMAL, true, null));

        //获取缩放控件
        ZoomControlView zcvZomm = (ZoomControlView) findViewById(R.id.id_zoom_control);
        zcvZomm.setMapView(mMapView);//设置百度地图控件

        //地图的触摸监听事件
        mBaiduMap.setOnMapTouchListener(new BaiduMap.OnMapTouchListener() {
            @Override
            public void onTouch(MotionEvent motionEvent) {
                //一旦摸到---就表示未定位
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    isLocated = false;
                    ibMode.setImageResource(R.drawable.locate1);
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    AnimHelper.rotateBigAnim(BaiduGetLatLngActivity.this, ivMark);
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    AnimHelper.rotateSmallAnim(BaiduGetLatLngActivity.this, ivMark);
                }
            }
        });

        //地图状态变化监听---用于监听选取的Marker位置
        mBaiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus) {
            }

            @Override
            public void onMapStatusChange(MapStatus mapStatus) {
            }

            @Override
            public void onMapStatusChangeFinish(MapStatus mapStatus) {
                //更新输入框经纬度数据
                LatLng latlng = mBaiduMap.getMapStatus().target;
                etLatitude.setText(latlng.latitude + "");
                etLongitude.setText(latlng.longitude + "");
            }
        });

        ivMark = (ImageView) findViewById(R.id.id_iv_mark_icon);

        etLatitude = (EditText) findViewById(R.id.id_et_latitude);
        etLongitude = (EditText) findViewById(R.id.id_et_longitude);

        Button btnSubmit = (Button) findViewById(R.id.id_btn_submit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MarkerHelper.isDataValid(etLatitude, etLongitude)) {
                    Intent intent = getIntent();
                    intent.putExtra(Constant.EXTRA_KEY_LATITUDE, MarkerHelper.getLatitude(etLatitude));
                    intent.putExtra(Constant.EXTRA_KEY_LONGITUDE, MarkerHelper.getLongitude(etLongitude));
                    //设置返回值
                    setResult(RESULT_OK, intent);
                    //退出
                    finish();
                } else {
                    ToastHelper.showSnack(BaiduGetLatLngActivity.this, ivMark, "请选择有效数据");
                }
            }
        });

        //模式切换按钮---兼定位按钮
        ibMode = (ImageButton) findViewById(R.id.id_ib_locate);
        ibMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLocated) {
                    //如果已经定位了---切换视图
                    if (mCurrentMode == MyLocationConfiguration.LocationMode.NORMAL) {
                        // LogHelper.Log(TAG, "change to compass mode");
                        mCurrentMode = MyLocationConfiguration.LocationMode.COMPASS;
                        ibMode.setImageResource(R.drawable.location_mode_2);
                        enableEagle();
                    } else if (mCurrentMode == MyLocationConfiguration.LocationMode.COMPASS) {
                        // LogHelper.Log(TAG, "change to normal mode");
                        mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
                        ibMode.setImageResource(R.drawable.location_mode_1);
                        disableEagle();
                    }
                } else {
                    //手动定位
                    if (mLocClient.getLastKnownLocation() != null) {
                        locate(mLocClient.getLastKnownLocation());
                    }
                    isLocated = true;
                    //判断当前状态---切换图标
                    if (mCurrentMode == MyLocationConfiguration.LocationMode.NORMAL) {
                        ibMode.setImageResource(R.drawable.location_mode_1);
                    } else if (mCurrentMode == MyLocationConfiguration.LocationMode.COMPASS) {
                        ibMode.setImageResource(R.drawable.location_mode_2);
                    }
                }
            }
        });
    }

    /**
     * 根据BDLocation定位
     *
     * @param location
     */
    private void locate(BDLocation location) {
        //更新我的位置
        MyLocationData locData = new MyLocationData.Builder()
                .accuracy(location.getRadius())
                        // 此处设置开发者获取到的方向信息，顺时针0-360
                .direction(location.getDirection())
                .latitude(location.getLatitude())
                .longitude(location.getLongitude()).build();
        mBaiduMap.setMyLocationData(locData);
        //更新地图中心点
        LatLng ll = new LatLng(location.getLatitude(),
                location.getLongitude());
        MapHelper.animateToPoint(mBaiduMap, ll);
    }

    private void enableEagle() {
        //改变可视角度
        MapStatus ms = new MapStatus.Builder(mBaiduMap.getMapStatus()).overlook(-100).build();
        MapStatusUpdate u = MapStatusUpdateFactory.newMapStatus(ms);
        mBaiduMap.animateMapStatus(u);
    }

    private void disableEagle() {
        //改变可视角度
        MapStatus ms = new MapStatus.Builder(mBaiduMap.getMapStatus()).overlook(0).build();
        MapStatusUpdate u = MapStatusUpdateFactory.newMapStatus(ms);
        mBaiduMap.animateMapStatus(u);
    }

    @Override
    public void onBackPressed() {
        setResult(Constant.RESULT_CODE_NOT_OK);
        super.onBackPressed();
    }


    //生命周期----------------------------------------------
    @Override
    protected void onPause() {
        if (mMapView != null) {
            mMapView.onPause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (mMapView != null) {
            mMapView.onResume();
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        // 退出时销毁定位
        mLocClient.stop();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
    }
}
