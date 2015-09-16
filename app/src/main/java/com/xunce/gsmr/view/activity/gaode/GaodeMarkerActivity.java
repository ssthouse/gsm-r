package com.xunce.gsmr.view.activity.gaode;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;

import com.amap.api.maps.AMap;
import com.xunce.gsmr.R;
import com.xunce.gsmr.app.Constant;
import com.xunce.gsmr.model.MarkerItem;
import com.xunce.gsmr.util.ViewHelper;
import com.xunce.gsmr.util.gps.DBHelper;
import com.xunce.gsmr.view.activity.baidu.BaiduMarkerActivity;
import com.xunce.gsmr.view.activity.baidu.BaiduPrjEditActivity;

/**
 * 高德选取Marker的Activity
 * Created by ssthouse on 2015/9/15.
 */
public class GaodeMarkerActivity extends GaodeBaseActivity{
    private static final String TAG = "GaodeMarkerActivity";

    /**
     * 开启本Activity需要的数据
     */
    private MarkerItem markerItem;
    private int requestCode;

    public static void start(Activity activity, MarkerItem markerItem, int requestCode) {
        //填充intent进去markerItem和requestCode
        Intent intent = new Intent(activity, BaiduMarkerActivity.class);
        intent.putExtra(Constant.EXTRA_KEY_MARKER_ITEM, markerItem);
        intent.putExtra(Constant.EXTRA_KEY_REQUEST_CODE, requestCode);
        //启动Activity
        activity.startActivityForResult(intent, requestCode);
        activity.overridePendingTransition(R.anim.activity_fade_in, R.anim.activity_fade_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gaode_mark);
        super.init(savedInstanceState);

        //获取数据
        MarkerItem wrongItem = (MarkerItem) getIntent()
                .getSerializableExtra(Constant.EXTRA_KEY_MARKER_ITEM);
        markerItem = DBHelper.getMarkerItemInDB(wrongItem);
        requestCode = getIntent().getIntExtra(Constant.EXTRA_KEY_REQUEST_CODE,
                BaiduPrjEditActivity.REQUEST_CODE_MARKER_ACTIVITY);

        //初始化View
        initView();
    }

    /**
     * 初始化View
     */
    private void initView(){
        ViewHelper.initActionBar(this, getSupportActionBar(), "选址");

        //如果是编辑---定位到编辑的点
        if (markerItem.getLatitude() != 0 && markerItem.getLongitude() != 0) {
            super.animateToPoint(markerItem.getGaodeLatLng());
        }

        //地图状态变化监听---用于监听选取的Marker位置
        getaMap().setOnMapTouchListener(new AMap.OnMapTouchListener() {
            @Override
            public void onTouch(MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    //TODO---抬手时更新EditText数据
//                    getaMap().get
                }
            }
        });
    }
}
