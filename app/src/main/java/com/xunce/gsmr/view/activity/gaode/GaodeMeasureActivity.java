package com.xunce.gsmr.view.activity.gaode;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.xunce.gsmr.R;
import com.xunce.gsmr.app.Constant;
import com.xunce.gsmr.util.view.ViewHelper;
import com.xunce.gsmr.view.style.TransparentStyle;

import java.util.ArrayList;
import java.util.List;

/**
 * 高德地图测量Activity
 * Created by ssthouse on 2015/9/16.
 */
public class GaodeMeasureActivity extends GaodeBaseActivity {
    private static final String TAG = "GaodeMeasureActivity";

    //标记点相关的
    BitmapDescriptor descriptorBlue = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_measure_blue);
    BitmapDescriptor descriptorRed = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_measure_red);

    /**
     * 接收的数据
     */
    private static final String EXTRA_LATLNG = "extra_latlng";
    private LatLng latLng;

    /**
     * 标记点数据
     */
    private List<Marker> markerList = new ArrayList<>();
    private List<LatLng> pointList = new ArrayList<>();
    //折线对象
    private PolylineOptions polylineOptions = new PolylineOptions();

    /**
     * View
     */
    private TextView tvLength;

    /**
     * 启动当前Activity
     *
     * @param activity
     * @param latLng
     */
    public static void start(Activity activity, LatLng latLng) {
        Intent intent = new Intent(activity, GaodeMeasureActivity.class);
        if (latLng == null) {
            return;
        }
        intent.putExtra(Constant.EXTRA_KEY_LATITUDE, latLng.latitude);
        intent.putExtra(Constant.EXTRA_KEY_LONGITUDE, latLng.longitude);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.activity_fade_in, R.anim.activity_fade_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gaode_measure);
        TransparentStyle.setTransparentStyle(this, R.color.color_primary);
        super.init(savedInstanceState);

        //接收数据
        Intent intent = getIntent();
        latLng = new LatLng(intent.getDoubleExtra(Constant.EXTRA_KEY_LATITUDE, 0),
                intent.getDoubleExtra(Constant.EXTRA_KEY_LONGITUDE, 0));

        //初始化View
        initView();
    }

    /**
     * 初始化View
     */
    private void initView() {
        ViewHelper.initActionBar(this, getSupportActionBar(), "测距");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //将地图移动到目标点
        animateToPoint(latLng);
        //开启定位
        super.showLocate();

        //view---和点击事件
        tvLength = (TextView) findViewById(R.id.id_tv_length);
        findViewById(R.id.id_ib_locate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GaodeMeasureActivity.super.animateToMyLocation();
            }
        });

        //地图触控事件
        getaMap().setOnMapClickListener(new AMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                //将点击位置存入List
                MarkerOptions options = new MarkerOptions().icon(descriptorRed).position(latLng);
                markerList.add((getaMap().addMarker(options)));
                //添加坐标点
                pointList.add(latLng);
                //重新计算总长
                updateLength();
                //重画
                redraw();
            }
        });
    }

    /**
     * 重画地图上的点
     */
    private void redraw() {
        //清除marker--显示
        getaMap().clear();
//        showLocate();
        //画出线
        if (pointList.size() > 1) {
            polylineOptions = new PolylineOptions()
                    .width(15)
                    .color(Color.BLUE)
                    .addAll(pointList);
            getaMap().addPolyline(polylineOptions);
        }
        //画出标记点
        for (int i = 0; i < pointList.size(); i++) {
            if (i == 0 || i == pointList.size() - 1) {
                MarkerOptions markerOptions = new MarkerOptions().position(pointList.get(i)).icon(descriptorRed)
                        .zIndex(9).draggable(true);
                getaMap().addMarker(markerOptions);
            } else {
                MarkerOptions markerOptions = new MarkerOptions().position(pointList.get(i)).icon(descriptorBlue)
                        .zIndex(9).draggable(true);
                getaMap().addMarker(markerOptions);
            }
        }
    }

    /**
     * 更新总长
     */
    private void updateLength() {
        double length = 0;
        for (int i = 0; i < pointList.size() - 1; i++) {
            double gap = AMapUtils.calculateLineDistance(pointList.get(i), pointList.get(i + 1));
            length += gap;
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
}
