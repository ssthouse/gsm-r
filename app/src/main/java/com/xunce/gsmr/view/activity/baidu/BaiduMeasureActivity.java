package com.xunce.gsmr.view.activity.baidu;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.xunce.gsmr.R;
import com.xunce.gsmr.app.Constant;
import com.xunce.gsmr.util.L;
import com.xunce.gsmr.util.view.ViewHelper;
import com.xunce.gsmr.util.gps.LocateHelper;
import com.xunce.gsmr.util.gps.MapHelper;
import com.xunce.gsmr.view.style.TransparentStyle;
import com.xunce.gsmr.view.widget.ZoomControlView;

import java.util.ArrayList;
import java.util.List;

/**
 * 测距Activity---开启需要一个Latlng
 * --需要显示出所有的已经标记的点的位置--便于测量距离--还有地图
 * Created by ssthouse on 2015/7/19.
 */
public class BaiduMeasureActivity extends AppCompatActivity {
    private static final String TAG = "BaiduMeasureActivity";

    private LatLng latLng;

    //地图相关
    private BaiduMap mBaiduMap;
    private MapView mMapView;
    private LocationClient mLocationClient;
    private boolean isLocated = false;

    //ui
    private TextView tvLength;
    private ImageButton ibLocate;

    //标记点相关的
    BitmapDescriptor descriptorBlue = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_measure_blue);
    BitmapDescriptor descriptorRed = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_measure_red);
    //手指在地图上点下的标记List---用OverLay来实现
    private List<Marker> markerList = new ArrayList<>();
    private List<LatLng> pointList = new ArrayList<>();
    //构造对象--折线对象
    private OverlayOptions polylineOptions = new PolylineOptions();

    private boolean isFistIn = true;

    public static void start(Activity activity, LatLng latLng) {
        Intent intent = new Intent(activity, BaiduMeasureActivity.class);
        if (latLng != null) {
            intent.putExtra(Constant.EXTRA_KEY_LATITUDE, latLng.latitude);
            intent.putExtra(Constant.EXTRA_KEY_LONGITUDE, latLng.longitude);
        }
        L.log(TAG, latLng.latitude + " : " + latLng.longitude);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.activity_fade_in, R.anim.activity_fade_out);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baidu_measure);
        TransparentStyle.setTransparentStyle(this, R.color.color_primary);

        //接收intent中的数据
        Intent intent = getIntent();
        latLng = new LatLng(intent.getDoubleExtra(Constant.EXTRA_KEY_LATITUDE, 0),
                intent.getDoubleExtra(Constant.EXTRA_KEY_LONGITUDE, 0));

        initView();

        //初始化数据
        mLocationClient = new LocationClient(this);
        LocateHelper.initLocationClient(this, mLocationClient);
        mLocationClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                if (isFistIn) {
                    MapHelper.animateToPoint(mBaiduMap, new LatLng(
                            bdLocation.getLatitude(), bdLocation.getLongitude()));
                    MapHelper.animateZoom(mBaiduMap, 15);
                    isFistIn = false;
                }
            }
        });
        mLocationClient.start();
    }

    private void initView() {
        ViewHelper.initActionBar(this, getSupportActionBar(), "测距");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tvLength = (TextView) findViewById(R.id.id_tv_length);

        mMapView = (MapView) findViewById(R.id.id_map_view);
        mMapView.showZoomControls(false);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMyLocationEnabled(true);
        mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
                MyLocationConfiguration.LocationMode.NORMAL, true, null));
        //定位到中心点
        MapHelper.animateToPoint(mBaiduMap, latLng);

        //获取缩放控件
        ZoomControlView zcvZomm = (ZoomControlView) findViewById(R.id.id_zoom_control);
        zcvZomm.setMapView(mMapView);//设置百度地图控件

        mBaiduMap.setOnMapTouchListener(new BaiduMap.OnMapTouchListener() {
            @Override
            public void onTouch(MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    isLocated = false;
                    ibLocate.setImageResource(R.drawable.locate2);
                }
            }
        });

        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                //将点击位置存入List
                OverlayOptions options = new MarkerOptions().icon(descriptorRed).position(latLng);
                markerList.add((Marker) mBaiduMap.addOverlay(options));
                //添加坐标点
                pointList.add(latLng);
                //重新计算总长
                updateLength();
                //重画
                redraw();
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
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

    /**
     * 重画地图上的点
     */
    private void redraw() {
        mBaiduMap.clear();
        //画出线
        if (pointList.size() > 1) {
            polylineOptions = new PolylineOptions().width(15).color(Color.BLUE).points(pointList);
            mBaiduMap.addOverlay(polylineOptions);
        }
        //画出标记点
        for (int i = 0; i < pointList.size(); i++) {
            if (i == 0 || i == pointList.size() - 1) {
                OverlayOptions redOverlay = new MarkerOptions().position(pointList.get(i)).icon(descriptorRed)
                        .zIndex(9).draggable(true);
                mBaiduMap.addOverlay(redOverlay);
            } else {
                OverlayOptions blueOverlay = new MarkerOptions().position(pointList.get(i)).icon(descriptorBlue)
                        .zIndex(9).draggable(true);
                mBaiduMap.addOverlay(blueOverlay);
            }
        }
    }

    /**
     * 更新总长
     */
    private void updateLength() {
        double length = 0;
        for (int i = 0; i < pointList.size() - 1; i++) {
            double gap = DistanceUtil.getDistance(pointList.get(i), pointList.get(i + 1));
            length += gap;
//            L.log(TAG, gap + "");
        }
        if (length != 0) {
            int result = (int) length;
            tvLength.setText(result + "米");
        } else {
            tvLength.setText(0 + "米");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_measure, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.id_action_back_one_point:
                if (pointList.size() != 0) {
                    pointList.remove(pointList.size() - 1);
                    updateLength();
                    redraw();
                }
                break;
            case R.id.id_action_delete_all:
//                polylineOptions.
                updateLength();
                pointList.clear();
                redraw();
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    //生命周期***********************************************************
    @Override
    protected void onPause() {
        // activity 暂停时同时暂停地图控件
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        // activity 恢复时同时恢复地图控件
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        // 退出时销毁定位
        mLocationClient.stop();
        // activity 销毁时同时销毁地图控件
        mMapView.onDestroy();
        super.onDestroy();
        descriptorBlue.recycle();
        descriptorRed.recycle();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.activity_fade_in, R.anim.activity_fade_out);
    }
}
