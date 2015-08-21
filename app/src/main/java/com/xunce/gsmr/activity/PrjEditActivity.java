package com.xunce.gsmr.activity;

import android.content.Context;
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

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.xunce.gsmr.R;
import com.xunce.gsmr.app.Constant;
import com.xunce.gsmr.model.MarkerItem;
import com.xunce.gsmr.model.PrjItem;
import com.xunce.gsmr.model.map.RailWay;
import com.xunce.gsmr.test.OfflineActivity;
import com.xunce.gsmr.util.LogHelper;
import com.xunce.gsmr.util.PreferenceHelper;
import com.xunce.gsmr.util.ToastHelper;
import com.xunce.gsmr.util.ViewHelper;
import com.xunce.gsmr.util.gps.DBHelper;
import com.xunce.gsmr.util.gps.LocateHelper;
import com.xunce.gsmr.util.gps.MapHelper;
import com.xunce.gsmr.view.style.TransparentStyle;
import com.xunce.gsmr.view.widget.ZoomControlView;

import java.util.ArrayList;
import java.util.List;

/**
 * 开启时会接收到一个PrjItem---intent中
 */
public class PrjEditActivity extends AppCompatActivity {
    private static final String TAG = "PrjEditActivity";

    public static final int REQUEST_CODE_ROUTE_ACTIVITY = 1000;
    public static final int REQUEST_CODE_MARKER_ACTIVITY = 1001;
    public static final int REQUEST_CODE_PICTURE_ACTIVITY = 1002;
    public static final int REQUEST_CODE_MARKER_EDIT_ACTIVITY = 1003;

    //标记点相关的
    BitmapDescriptor descriptorBlue = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_measure_blue);
    BitmapDescriptor descriptorRed = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_measure_red);

    //接收到的数据
    private PrjItem prjItem;

    //加载的铁路数据
    private RailWay railWay;

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

    private List<LatLng> pointList = new ArrayList<>();
    private List<Marker> markerList = new ArrayList<>();
    private List<MarkerItem> markerItemList = new ArrayList<>();
    private Marker currentMarker;
    private InfoWindow mInfoWindow;

    /**
     * 用于更加方便的开启Activity
     * 后面几个参数可以用来传递-----放入intent 的数据
     *
     * @param context
     */
    public static void start(Context context, PrjItem prjItem) {
        Intent intent = new Intent(context, PrjEditActivity.class);
        intent.putExtra(Constant.EXTRA_KEY_PRJ_ITEM, prjItem);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prj_edit);
        TransparentStyle.setTransparentStyle(this,R.color.color_primary);

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
        LocateHelper.initLocationClient(mLocationClient);
        mLocationClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
            }
        });
        mLocationClient.start();

        initView();

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
        ZoomControlView zcvZomm = (ZoomControlView) findViewById(R.id.id_zoom_control);
        zcvZomm.setMapView(mMapView);//设置百度地图控件

        //触摸屏幕则---定位失效
        mBaiduMap.setOnMapTouchListener(new BaiduMap.OnMapTouchListener() {
            @Override
            public void onTouch(MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    ibLocate.setImageResource(R.drawable.locate2);
                    isLocated = false;
                }
            }
        });

        //marker的点击事件
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                //如果点的是已经选中了的Marker---变回未选中状态
                if (marker == currentMarker) {
                    marker.setIcon(descriptorBlue);
                    currentMarker = null;
                    mBaiduMap.hideInfoWindow();
                    return true;
                }
                for (Marker item : markerList) {
                    item.setIcon(descriptorBlue);
                }
                //选中了MArker---进行操作准备
                marker.setIcon(descriptorRed);
                //填充一个Button到marker上方的对话框中---WindowInfo
                LinearLayout ll = (LinearLayout) getLayoutInflater().inflate(R.layout.info_window, null);
                LatLng latLng = marker.getPosition();
                mInfoWindow = new InfoWindow(ll, latLng, -47);
                mBaiduMap.showInfoWindow(mInfoWindow);
                currentMarker = marker;
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
                MeasureActivity.start(PrjEditActivity.this,latLng);
            }
        });

        //公里标开关按钮
        findViewById(R.id.id_ib_position).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPositionShowed){
                    hideLlPosition();
                }else{
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

        //加载一次地图数据
        loadMapData(mBaiduMap, prjItem);
    }

    public void clickEdit(View v){
        LogHelper.Log(TAG, "edit");
        if (currentMarker == null || markerList.indexOf(currentMarker) == -1) {
            ToastHelper.showSnack(PrjEditActivity.this, ibLocate, "请先选择一个基址点");
            return;
        }
        mBaiduMap.hideInfoWindow();
        MarkerActivity.start(PrjEditActivity.this,
                DBHelper.getMarkerList(prjItem).get(markerList.indexOf(currentMarker)),
                REQUEST_CODE_MARKER_EDIT_ACTIVITY);
    }


    public void clickPhoto(View v){
        LogHelper.Log(TAG, "photo");
        mBaiduMap.hideInfoWindow();
        if (currentMarker == null || markerList.indexOf(currentMarker) == -1) {
            ToastHelper.showSnack(PrjEditActivity.this, ibLocate, "请先选择一个基址点");
            return;
        } else {
            PicGridActivity.start(PrjEditActivity.this,
                    markerItemList.get(markerList.indexOf(currentMarker)));
        }
    }

    private void locate(BDLocation location) {
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

   private void showLlPosition(){
       isPositionShowed = true;
       llPosition.setVisibility(View.VISIBLE);
       llPosition.startAnimation(AnimationUtils.loadAnimation(this, R.anim.pop_up));
   }

    private void hideLlPosition(){
        isPositionShowed = false;
        llPosition.startAnimation(AnimationUtils.loadAnimation(this, R.anim.drop_down));
        llPosition.setVisibility(View.GONE);
    }

    /**
     * 加载Marker
     *
     * @param baiduMap
     * @param prjItem
     * @return
     */
    public boolean loadMapData(BaiduMap baiduMap, PrjItem prjItem) {
        if (prjItem == null || baiduMap == null) {
            return false;
        }
        if (DBHelper.isPrjEmpty(prjItem)) {
            return false;
        }
        markerItemList.clear();
        markerList.clear();
        pointList.clear();
        markerItemList = DBHelper.getMarkerList(prjItem);
        //加载marker
        for (int i = 0; i < markerItemList.size(); i++) {
            LatLng latLng = new LatLng(markerItemList.get(i).getLatitude(),
                    markerItemList.get(i).getLongitude());
            OverlayOptions redOverlay = new MarkerOptions()
                    .position(latLng)
                    .icon(descriptorBlue)
                    .zIndex(9)
                    .draggable(false);
            markerList.add((Marker) baiduMap.addOverlay(redOverlay));
            pointList.add(latLng);
        }
        MapHelper.animateToPoint(baiduMap,
                new LatLng(markerItemList.get(0).getLatitude(),
                        markerItemList.get(0).getLongitude()));
        MapHelper.animateZoom(baiduMap, 15);
        return true;
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
                startActivity(new Intent(this, PrjSelectActivity.class));
                //加载铁路地图
            case R.id.id_action_load_map:
                //TODO---加载铁路地图
                if(railWay == null){
                    railWay = new RailWay(this, prjItem);
                    railWay.draw(mBaiduMap);
                }else{
                    ToastHelper.showToast(this, "铁路已加载");
                }
                break;
            case R.id.id_action_offline_map:
                //TODO
                //开启离线地图管理Acitvity
                OfflineActivity.start(this);
                break;
            //设置
            case R.id.id_action_setting:

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
            //如果是加载.db文件
            case Constant.REQUEST_CODE_DB_FILE:
                Uri uri = data.getData();
                LogHelper.Log(TAG, uri.getEncodedPath());

                break;
            case REQUEST_CODE_ROUTE_ACTIVITY:
                break;
            case REQUEST_CODE_PICTURE_ACTIVITY:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if(isPositionShowed){
            hideLlPosition();
        }else{
            super.onBackPressed();
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
            descriptorBlue.recycle();
            descriptorRed.recycle();
        }
        super.onDestroy();
    }
}
