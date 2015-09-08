package com.xunce.gsmr.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
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
import com.xunce.gsmr.model.MarkerItem;
import com.xunce.gsmr.util.ToastHelper;
import com.xunce.gsmr.util.ViewHelper;
import com.xunce.gsmr.util.gps.DBHelper;
import com.xunce.gsmr.util.gps.LocateHelper;
import com.xunce.gsmr.util.gps.MapHelper;
import com.xunce.gsmr.util.gps.MarkerHelper;
import com.xunce.gsmr.view.style.TransparentStyle;
import com.xunce.gsmr.view.widget.ZoomControlView;

/**
 * 用于选址的Activity---开启当前的Acitcvity需要传递一个MarkerItem
 * -----如果MarkerItem数据不是0-0就定位到传入的位置--修改选址
 * -----如果MarkerItem数据是0-0,定位到自己当前的位置--新建选址
 * Created by ssthouse on 2015/7/17.
 */
public class MarkerActivity extends AppCompatActivity {
    private static final String TAG = "MarkerActivity";

    //用于判断当前Activity---被开启的状态
    private int requestCode;

    //开启本Activity需要的数据
    private MarkerItem markerItem;

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

    public static void start(Activity activity, MarkerItem markerItem, int requestCode) {
        Intent intent = new Intent(activity, MarkerActivity.class);
        intent.putExtra(Constant.EXTRA_KEY_MARKER_ITEM, markerItem);
        intent.putExtra(Constant.EXTRA_KEY_REQUEST_CODE, requestCode);
        activity.startActivityForResult(intent, requestCode);
        activity.overridePendingTransition(R.anim.activity_fade_in, R.anim.activity_fade_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark);
        TransparentStyle.setTransparentStyle(this,R.color.color_primary);

        //获取数据
        MarkerItem wrongItem = (MarkerItem) getIntent()
                .getSerializableExtra(Constant.EXTRA_KEY_MARKER_ITEM);
        markerItem = DBHelper.getMarkerItemInDB(wrongItem);

        requestCode = getIntent().getIntExtra(Constant.EXTRA_KEY_REQUEST_CODE,
                PrjEditActivity.REQUEST_CODE_MARKER_ACTIVITY);

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

    private void initView() {
        ViewHelper.initActionBar(this, getSupportActionBar(), "选址");

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

        //如果是编辑---定位到编辑的点
        if (markerItem.getLatitude() != 0 && markerItem.getLongitude() != 0) {
            MapHelper.animateToPoint(mBaiduMap,
                    new LatLng(markerItem.getLatitude(), markerItem.getLongitude()));
        }

        //地图的触摸监听事件
        mBaiduMap.setOnMapTouchListener(new BaiduMap.OnMapTouchListener() {
            @Override
            public void onTouch(MotionEvent motionEvent) {
                //一旦摸到---就表示未定位
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    isLocated = false;
                    ibMode.setImageResource(R.drawable.locate1);
                }

//                //去除动画效果
//                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
//                    AnimHelper.rotateBigAnim(MarkerActivity.this, ivMark);
//                }
//                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
//                    AnimHelper.rotateSmallAnim(MarkerActivity.this, ivMark);
//                }
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

        //确认按钮
        Button btnSubmit = (Button) findViewById(R.id.id_btn_submit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MarkerHelper.isDataValid(etLatitude, etLongitude)) {

                    //保存数据---并改变原来的照片的文件夹的名称
                    LatLng latLng = new LatLng(MarkerHelper.getLatitude(etLatitude),
                            MarkerHelper.getLongitude(etLongitude));
                    markerItem.changeName(latLng);
                    //设置返回值
                    setResult(Constant.RESULT_CODE_OK);
                    //退出
                    finish();
                }else{
                    ToastHelper.showSnack(MarkerActivity.this, ivMark, "请选择有效数据");
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
                    locate(mLocClient.getLastKnownLocation());
                   // LogHelper.Log(TAG, "located!!!");
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
        if(location == null){
            return;
        }
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_mark, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.id_action_load_map:
                break;
            case R.id.id_action_load_marker:
                break;
            case android.R.id.home:
                if(requestCode == PrjEditActivity.REQUEST_CODE_MARKER_EDIT_ACTIVITY){
                    finish();
                    return true;
                }
                markerItem.delete();
                setResult(Constant.RESULT_CODE_OK);
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(requestCode == PrjEditActivity.REQUEST_CODE_MARKER_EDIT_ACTIVITY){
            finish();
            return;
        }
        //如果直接想返回---需要删除提前在数据库中保存的数据
        markerItem.delete();
        setResult(Constant.RESULT_CODE_OK);
        super.onBackPressed();
    }

    //生命周期----------------------------------------------
    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
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
