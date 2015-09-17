package com.xunce.gsmr.view.activity.gaode;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.LatLng;
import com.xunce.gsmr.R;
import com.xunce.gsmr.app.Constant;
import com.xunce.gsmr.model.MarkerItem;
import com.xunce.gsmr.util.ToastHelper;
import com.xunce.gsmr.util.ViewHelper;
import com.xunce.gsmr.util.gps.DBHelper;
import com.xunce.gsmr.util.gps.MarkerHelper;

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

    /**
     * View控件
     */
    //经纬度输入框
    private EditText etLatitude, etLongitude;

    public static void start(Activity activity, MarkerItem markerItem, int requestCode) {
        //填充intent进去markerItem和requestCode
        Intent intent = new Intent(activity, GaodeMarkerActivity.class);
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
                GaodePrjEditActivity.REQUEST_CODE_MARKER_ACTIVITY);

        //如果是编辑---定位到编辑的点
        if (markerItem != null && markerItem.getLatitude() != 0 && markerItem.getLongitude() != 0) {
            super.animateToPoint(markerItem.getGaodeLatLng());
        }else{
            showLocate();
            animateToMyLocation();
        }

        //初始化View
        initView();
    }

    /**
     * 初始化View
     */
    private void initView(){
        ViewHelper.initActionBar(this, getSupportActionBar(), "选址");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //找到UI控件
        etLatitude = (EditText) findViewById(R.id.id_et_latitude);
        etLongitude = (EditText) findViewById(R.id.id_et_longitude);

        //确认按钮
        findViewById(R.id.id_btn_submit).setOnClickListener(new View.OnClickListener() {
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
                } else {
                    ToastHelper.showSnack(GaodeMarkerActivity.this, etLatitude, "请选择有效数据");
                }
            }
        });

        //定位按钮
        findViewById(R.id.id_ib_locate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GaodeMarkerActivity.super.animateToMyLocation();
            }
        });

        //地图状态变化监听---用于监听选取的Marker位置
        getaMap().setOnMapTouchListener(new AMap.OnMapTouchListener() {
            @Override
            public void onTouch(MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    //TODO---抬手时更新EditText数据
                    //更新输入框经纬度数据
                    LatLng latlng = getaMap().getCameraPosition().target;
                    etLatitude.setText(latlng.latitude + "");
                    etLongitude.setText(latlng.longitude + "");
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_mark, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //TODO
            case R.id.id_action_load_map:
                break;
            //TODO
            case R.id.id_action_load_marker:
                break;
            case android.R.id.home:
                if (requestCode == GaodePrjEditActivity.REQUEST_CODE_MARKER_EDIT_ACTIVITY) {
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
        if (requestCode == GaodePrjEditActivity.REQUEST_CODE_MARKER_EDIT_ACTIVITY) {
            finish();
            return;
        }
        //如果直接想返回---需要删除提前在数据库中保存的数据
        markerItem.delete();
        setResult(Constant.RESULT_CODE_OK);
        super.onBackPressed();
    }
}
