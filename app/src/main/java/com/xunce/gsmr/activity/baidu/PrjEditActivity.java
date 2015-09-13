package com.xunce.gsmr.activity.baidu;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.xunce.gsmr.R;
import com.xunce.gsmr.activity.PicGridActivity;
import com.xunce.gsmr.activity.PrjSelectActivity;
import com.xunce.gsmr.activity.SettingActivity;
import com.xunce.gsmr.app.Constant;
import com.xunce.gsmr.model.MarkerItem;
import com.xunce.gsmr.model.PrjItem;
import com.xunce.gsmr.model.baidumap.MarkerHolder;
import com.xunce.gsmr.model.baidumap.RailWayHolder;
import com.xunce.gsmr.util.FileHelper;
import com.xunce.gsmr.util.LogHelper;
import com.xunce.gsmr.util.PreferenceHelper;
import com.xunce.gsmr.util.ToastHelper;
import com.xunce.gsmr.util.ViewHelper;
import com.xunce.gsmr.util.gps.DBHelper;
import com.xunce.gsmr.util.gps.LocateHelper;
import com.xunce.gsmr.util.gps.MapHelper;
import com.xunce.gsmr.view.style.TransparentStyle;
import com.xunce.gsmr.view.widget.ZoomControlView;

/**
 * 开启时会接收到一个PrjItem---intent中
 */
public class PrjEditActivity extends AppCompatActivity {
    private static final String TAG = "PrjEditActivity";

    //用于点击两次退出
    private long mExitTime;

    /**
     * 是否首次进入
     */
    private boolean isFistIn = true;

    //Activity请求码
    public static final int REQUEST_CODE_ROUTE_ACTIVITY = 1000;
    public static final int REQUEST_CODE_MARKER_ACTIVITY = 1001;
    public static final int REQUEST_CODE_PICTURE_ACTIVITY = 1002;
    public static final int REQUEST_CODE_MARKER_EDIT_ACTIVITY = 1003;

    //用于控制地图上的Marker
    private MarkerHolder markerHolder;
    //加载的铁路数据
    private RailWayHolder railWayHolder;
    //接收到的数据
    private PrjItem prjItem;

    //地图----定位client
    private BaiduMap mBaiduMap;
    private MapView mMapView;
    private LocationClient mLocationClient;

    //定位按钮
    private ImageButton ibLocate;
    //是否已经定位的标志位
    private boolean isLocated = false;

    //公里标VIew
    private LinearLayout llPosition;
    private EditText etPosition;
    private boolean isPositionShowed = false;

    private InfoWindow mInfoWindow;

    /**
     * 用于更加方便的开启Activity
     * 后面几个参数可以用来传递-----放入intent 的数据
     *
     * @param activity
     */
    public static void start(Activity activity, PrjItem prjItem) {
        Intent intent = new Intent(activity, PrjEditActivity.class);
        intent.putExtra(Constant.EXTRA_KEY_PRJ_ITEM, prjItem);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.activity_fade_in, R.anim.activity_fade_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prj_edit);
        TransparentStyle.setTransparentStyle(this, R.color.color_primary);

        //判断是否有上次编辑的project
        if (PreferenceHelper.hasLastEditPrjItem(this)) {
            prjItem = new PrjItem(PreferenceHelper.getLastEditPrjName(this));
        } else {
            finish();
            startActivity(new Intent(this, PrjSelectActivity.class));
            return;
        }

        //初始化定位Client
        mLocationClient = new LocationClient(this);
        LocateHelper.initLocationClient(this, mLocationClient);
        mLocationClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                if (bdLocation != null && isFistIn) {
                    isFistIn = false;
                    locate(bdLocation);
                }
            }
        });
        mLocationClient.start();

        initView();

        markerHolder = new MarkerHolder(prjItem, mBaiduMap);
        //加载一次地图数据
        loadMapData(mBaiduMap, prjItem);
    }

    private void initView() {
        ViewHelper.initActionBar(this, getSupportActionBar(), prjItem.getPrjName());

        mMapView = (MapView) findViewById(R.id.id_baidu_map);
        mMapView.showZoomControls(false);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMyLocationEnabled(true);
        MapHelper.animateZoom(mBaiduMap, 15);
        mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
                MyLocationConfiguration.LocationMode.NORMAL, true, null));

        //获取缩放控件
        ZoomControlView zoomView = (ZoomControlView) findViewById(R.id.id_zoom_control);
        zoomView.setMapView(mMapView);//设置百度地图控件

        //触摸屏幕则---定位失效
        mBaiduMap.setOnMapTouchListener(new BaiduMap.OnMapTouchListener() {
            @Override
            public void onTouch(MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    ibLocate.setImageResource(R.drawable.locate2);
                    isLocated = false;
                }
                //如果摸屏幕的时候, 公里标是看的见的, 隐藏掉
                if (isPositionShowed) {
                    hideLlPosition();
                }
            }
        });

        //marker的点击事件
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                //如果点的是已经选中了的Marker---变回未选中状态
                if (marker == markerHolder.getCurrentMarker()) {
                    markerHolder.clearSelection();
                    hideInfoWindow();
                } else {
                    markerHolder.setAll2Blue();
                    marker.setIcon(MarkerHolder.descriptorRed);
                    //选中了Marker
                    markerHolder.setCurrentMarker(marker);
                    //弹出InfoWindow
                    LinearLayout ll = (LinearLayout) getLayoutInflater()
                            .inflate(R.layout.view_info_window, null);
                    LatLng latLng = marker.getPosition();
                    mInfoWindow = new InfoWindow(ll, latLng, -47);
                    showInfoWindow();
                }
                return true;
            }
        });

        ibLocate = (ImageButton) findViewById(R.id.id_ib_locate);
        ibLocate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isLocated) {
                    //有可能还没有初始化完成---没有前一个点
                    if (mLocationClient.getLastKnownLocation() == null) {
                        return;
                    }
                    locate(mLocationClient.getLastKnownLocation());
                    isLocated = true;
                    ibLocate.setImageResource(R.drawable.locate1);
                }
            }
        });

//        //路线---暂时隐藏
//        findViewById(R.id.id_btn_route).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                RoutePlanActivity.start(PrjEditActivity.this);
//            }
//        });

        //选址
        findViewById(R.id.id_btn_mark).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //先保存进数据库---然后传递
                MarkerItem markerItem = new MarkerItem(prjItem);
                markerItem.save();
                MarkerActivity.start(PrjEditActivity.this, markerItem, REQUEST_CODE_MARKER_ACTIVITY);
            }
        });

        //测量按钮
        findViewById(R.id.id_ib_measure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //将当前地图的中心点传给MeasureActivity
                LatLng latLng = mBaiduMap.getMapStatus().target;
                //将中心点传递过去
                MeasureActivity.start(PrjEditActivity.this, latLng);
            }
        });

        //公里标开关按钮
        findViewById(R.id.id_ib_position).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPositionShowed) {
                    hideLlPosition();
                } else {
                    showLlPosition();
                }
            }
        });
        llPosition = (LinearLayout) findViewById(R.id.id_ll_position);
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
     * 和InfoWindow绑定的点击事件
     *
     * @param v
     */
    public void clickEdit(View v) {
        if (markerHolder.getCurrentMarker() == null) {
            ToastHelper.showSnack(PrjEditActivity.this, ibLocate, "请先选择一个基址点");
            return;
        }
        mBaiduMap.hideInfoWindow();
        //生成MarkerItem--跳转到MarkerEditActivity
        LatLng latLng = markerHolder.getCurrentMarker().getPosition();
        MarkerActivity.start(PrjEditActivity.this,
                new MarkerItem(prjItem.getPrjName(), latLng),
                REQUEST_CODE_MARKER_EDIT_ACTIVITY);
    }

    /**
     * 和InfoWindow绑定的点击事件
     *
     * @param v
     */
    public void clickPhoto(View v) {
        mBaiduMap.hideInfoWindow();
        if (markerHolder.getCurrentMarker() == null) {
            ToastHelper.showSnack(PrjEditActivity.this, ibLocate, "请先选择一个基址点");
        } else {
            LatLng latLng = markerHolder.getCurrentMarker().getPosition();
            PicGridActivity.start(PrjEditActivity.this,
                    new MarkerItem(prjItem.getPrjName(), latLng),
                    REQUEST_CODE_PICTURE_ACTIVITY);
        }
    }

    /**
     * 手动定位
     * @param location
     */
    private void locate(BDLocation location) {
        if (location == null) {
            return;
        }
        //更新我的位置
        MyLocationData locData = new MyLocationData.Builder()
                .accuracy(location.getRadius())
                .latitude(location.getLatitude())
                .longitude(location.getLongitude()).build();
        mBaiduMap.setMyLocationData(locData);
        //更新地图中心点
        LatLng ll = new LatLng(location.getLatitude(),
                location.getLongitude());
        MapHelper.animateToPoint(mBaiduMap, ll);
    }

    /**
     * 显示公里标输入框
     */
    private void showLlPosition() {
        isPositionShowed = true;
        llPosition.setVisibility(View.VISIBLE);
        llPosition.startAnimation(AnimationUtils.loadAnimation(this, R.anim.pop_up));
    }

    /**
     * 隐藏公里标输入框
     */
    private void hideLlPosition() {
        isPositionShowed = false;
        llPosition.startAnimation(AnimationUtils.loadAnimation(this, R.anim.drop_down));
        llPosition.setVisibility(View.GONE);
    }

    private void showInfoWindow(){
        mBaiduMap.showInfoWindow(mInfoWindow);
    }

    private void hideInfoWindow(){
        mBaiduMap.hideInfoWindow();
    }

    /**
     * 加载Marker
     *
     * @param baiduMap
     * @param prjItem
     * @return
     */
    public void loadMapData(BaiduMap baiduMap, PrjItem prjItem) {
        if (DBHelper.isPrjEmpty(prjItem)) {
            return;
        }
        //更新Marker数据
        markerHolder.initMarkerList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_prj_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //切换工程
            case R.id.id_action_change_project:
                finish();
                PrjSelectActivity.start(this);
                //加载铁路地图
            case R.id.id_action_load_map:
                //TODO---加载铁路地图
                //首先判断数据库是否绑定

                if (railWayHolder == null) {
                    railWayHolder = new RailWayHolder(this, prjItem);
                    railWayHolder.draw(mBaiduMap);
                } else {
                    ToastHelper.showToast(this, "铁路已加载");
                }
                break;
            //数据导出
            case R.id.id_action_export_data:
                FileHelper.sendDbFile(this);
                break;
            case R.id.id_action_offline_map:
                //开启离线地图管理Activity
                OfflineActivity.start(this);
                break;
            //设置
            case R.id.id_action_setting:
                SettingActivity.start(this);
                break;
            //返回
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_MARKER_ACTIVITY:
                if (resultCode == Constant.RESULT_CODE_OK) {
                    MapHelper.loadMarker(mBaiduMap, prjItem);
                    //更新当前Activity中的数据
                    loadMapData(mBaiduMap, prjItem);
                }
                break;
            case REQUEST_CODE_MARKER_EDIT_ACTIVITY:
                if (resultCode == Constant.RESULT_CODE_OK) {
                    MapHelper.loadMarker(mBaiduMap, prjItem);
                    //更新当前Activity中的数据
                    loadMapData(mBaiduMap, prjItem);
                }
                break;
            case Constant.REQUEST_CODE_DB_FILE:
                //如果是加载.db文件
                Uri uri = data.getData();
                LogHelper.Log(TAG, uri.getEncodedPath());
                break;
            case REQUEST_CODE_ROUTE_ACTIVITY:
                break;
            case REQUEST_CODE_PICTURE_ACTIVITY:
                markerHolder.clearSelection();
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if (isPositionShowed) {
            hideLlPosition();
        } else {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
            } else {
                finish();
            }
        }
    }

    //生命周期***********************************************************
    @Override
    protected void onPause() {
        if (mMapView != null) {
            // activity 暂停时同时暂停地图控件
            mMapView.onPause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (mMapView != null) {
            // activity 恢复时同时恢复地图控件
            mMapView.onResume();
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if (mMapView != null && mLocationClient != null) {
            // 退出时销毁定位
            mLocationClient.stop();
            // activity 销毁时同时销毁地图控件
            mMapView.onDestroy();
        }
        super.onDestroy();
    }
}
