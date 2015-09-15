package com.xunce.gsmr.view.activity.gaode;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.xunce.gsmr.R;
import com.xunce.gsmr.app.Constant;
import com.xunce.gsmr.model.MarkerItem;
import com.xunce.gsmr.model.PrjItem;
import com.xunce.gsmr.util.FileHelper;
import com.xunce.gsmr.util.LogHelper;
import com.xunce.gsmr.util.ViewHelper;
import com.xunce.gsmr.view.activity.PicGridActivity;
import com.xunce.gsmr.view.activity.PrjSelectActivity;
import com.xunce.gsmr.view.activity.SettingActivity;
import com.xunce.gsmr.view.activity.baidu.MarkerActivity;
import com.xunce.gsmr.view.activity.baidu.OfflineActivity;

/**
 * 高德地图编辑Activity
 * Created by ssthouse on 2015/9/14.
 */
public class GaodePrjEditActivity extends GaodeBaseActivity {
    private static String TAG = "GaodePrjEditActivity";

    //Activity请求码
    public static final int REQUEST_CODE_ROUTE_ACTIVITY = 1000;
    public static final int REQUEST_CODE_MARKER_ACTIVITY = 1001;
    public static final int REQUEST_CODE_PICTURE_ACTIVITY = 1002;
    public static final int REQUEST_CODE_MARKER_EDIT_ACTIVITY = 1003;

    /**
     * 用于点击两次退出
     */
    private long mExitTime;

    /**
     * 编辑的PrjItem
     */
    private PrjItem prjItem;

    /**
     * 公里标显示标志位
     */
    private View llPosition;
    private boolean isLlPositionShowed;

    /**
     * 启动Activity
     *
     * @param activity
     * @param prjItem
     */
    public static void start(Activity activity, PrjItem prjItem) {
        Intent intent = new Intent(activity, GaodePrjEditActivity.class);
        intent.putExtra(Constant.EXTRA_KEY_PRJ_ITEM, prjItem);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.activity_fade_in, R.anim.activity_fade_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gaode_prj_edit);
        super.init(savedInstanceState);

        //接收数据
        prjItem = (PrjItem) getIntent().getSerializableExtra(Constant.EXTRA_KEY_PRJ_ITEM);

        //加载Marker
        loadMarker(prjItem);

        //启动定位
        super.showLocate();

        //初始化View
        initView();
    }

    /**
     * 初始化View
     */
    private void initView() {
        ViewHelper.initActionBar(this, getSupportActionBar(), prjItem.getPrjName());

        //填充InfoWindow
        getaMap().setInfoWindowAdapter(new AMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return View.inflate(GaodePrjEditActivity.this, R.layout.view_info_window, null);
            }

            @Override
            public View getInfoContents(Marker marker) {
                return View.inflate(GaodePrjEditActivity.this, R.layout.view_info_window, null);
            }
        });

        //设置Marker点击事件
        getaMap().setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.showInfoWindow();
                getMarkerHolder().setCurrentMarker(marker);
                LogHelper.Log(TAG, "这个点的经纬度是:   " + marker.getPosition().latitude + ":"
                        + marker.getPosition().longitude);
                return true;
            }
        });

        //选址
        findViewById(R.id.id_btn_mark).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
            }
        });

        //定位
        findViewById(R.id.id_ib_locate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GaodePrjEditActivity.super.animateToMyLocation();
            }
        });

        //测量
        findViewById(R.id.id_ib_measure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
            }
        });

        //公里标
        llPosition = findViewById(R.id.id_ll_position);
        findViewById(R.id.id_ib_position).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleLlPosition();
            }
        });
    }

    /**
     * 和InfoWindow绑定的点击事件
     *
     * @param v
     */
    public void clickEdit(View v) {
        //生成MarkerItem--跳转到MarkerEditActivity
        LatLng latLng = getMarkerHolder().getCurrentMarker().getPosition();
        MarkerActivity.start(this, new MarkerItem(prjItem.getPrjName(), latLng),
                REQUEST_CODE_MARKER_EDIT_ACTIVITY);
    }

    /**
     * 和InfoWindow绑定的点击事件
     *
     * @param v
     */
    public void clickPhoto(View v) {
        LatLng latLng = getMarkerHolder().getCurrentMarker().getPosition();
        PicGridActivity.start(this, new MarkerItem(prjItem.getPrjName(), latLng),
                REQUEST_CODE_PICTURE_ACTIVITY);
    }


    /**
     * 切换公里标显示状态
     */
    private void toggleLlPosition() {
        if (isLlPositionShowed) {
            isLlPositionShowed = false;
            llPosition.startAnimation(AnimationUtils.loadAnimation(this, R.anim.drop_down));
            llPosition.setVisibility(View.GONE);
        } else {
            isLlPositionShowed = true;
            llPosition.setVisibility(View.VISIBLE);
            llPosition.startAnimation(AnimationUtils.loadAnimation(this, R.anim.pop_up));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_prj_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.id_action_change_project:
                finish();
                PrjSelectActivity.start(this, true);
                break;
            // TODO---加载铁路地图
            case R.id.id_action_load_map:
                //首先判断数据库是否绑定
                loadRail(prjItem);
                break;
            //数据导出
            case R.id.id_action_export_data:
                FileHelper.sendDbFile(this);
                break;
            //开启离线地图
            case R.id.id_action_offline_map:
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
                    loadMarker(prjItem);
                }
                break;
            case REQUEST_CODE_MARKER_EDIT_ACTIVITY:
                if (resultCode == Constant.RESULT_CODE_OK) {
                    loadMarker(prjItem);
                }
                break;
            case Constant.REQUEST_CODE_DB_FILE:
                //如果是加载.db文件
                Uri uri = data.getData();
                LogHelper.Log(TAG, uri.getEncodedPath());
                break;
            case REQUEST_CODE_ROUTE_ACTIVITY:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 实现两次返回退出程序
     */
    @Override
    public void onBackPressed() {
        if ((System.currentTimeMillis() - mExitTime) > 2000) {
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            mExitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }
}