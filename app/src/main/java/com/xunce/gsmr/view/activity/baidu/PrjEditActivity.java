package com.xunce.gsmr.view.activity.baidu;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.xunce.gsmr.R;
import com.xunce.gsmr.app.Constant;
import com.xunce.gsmr.model.MarkerItem;
import com.xunce.gsmr.model.PrjItem;
import com.xunce.gsmr.util.FileHelper;
import com.xunce.gsmr.util.LogHelper;
import com.xunce.gsmr.util.PreferenceHelper;
import com.xunce.gsmr.util.ViewHelper;
import com.xunce.gsmr.view.activity.PicGridActivity;
import com.xunce.gsmr.view.activity.PrjSelectActivity;
import com.xunce.gsmr.view.activity.SettingActivity;
import com.xunce.gsmr.view.activity.gaode.GaodePrjEditActivity;
import com.xunce.gsmr.view.fragment.CustomMap;
import com.xunce.gsmr.view.fragment.baidu.CustomBaiduMap;
import com.xunce.gsmr.view.fragment.gaode.CustomGaodeMap;
import com.xunce.gsmr.view.style.TransparentStyle;

/**
 * 开启时会接收到一个PrjItem---intent中
 */
public class PrjEditActivity extends AppCompatActivity {
    private static final String TAG = "PrjEditActivity";

    //Activity请求码
    public static final int REQUEST_CODE_ROUTE_ACTIVITY = 1000;
    public static final int REQUEST_CODE_MARKER_ACTIVITY = 1001;
    public static final int REQUEST_CODE_PICTURE_ACTIVITY = 1002;
    public static final int REQUEST_CODE_MARKER_EDIT_ACTIVITY = 1003;

    /**
     * 地图总控制器
     */
    private CustomMap customMap;

    /**
     * 用于点击两次退出
     */
    private long mExitTime;

    /**
     * 接收到的数据
     */
    private PrjItem prjItem;

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

        prjItem = (PrjItem) getIntent().getSerializableExtra(Constant.EXTRA_KEY_PRJ_ITEM);

        //初始化View
        initView();

        //TODO---
        startActivity(new Intent(this, GaodePrjEditActivity.class));
    }

    /**
     * 初始化View
     */
    private void initView() {
        ViewHelper.initActionBar(this, getSupportActionBar(), prjItem.getPrjName());

        //启动Map的片段
        Bundle bundle = new Bundle();
        bundle.putSerializable("prjItem", prjItem);
        if (PreferenceHelper.getInstance(this).getMapType() == PreferenceHelper.MapType.BAIDU_MAP) {
            customMap = CustomBaiduMap.getInstance(bundle);
        } else {
            customMap = CustomGaodeMap.getInstance(bundle);
        }
        getFragmentManager().beginTransaction().replace(R.id.id_fragment_container,
                (Fragment) customMap).commit();

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
    }

    /**
     * 和InfoWindow绑定的点击事件
     *
     * @param v
     */
    public void clickEdit(View v) {
        customMap.hideInfoWindow();
        //生成MarkerItem--跳转到MarkerEditActivity
        LatLng latLng = customMap.getCurrentMarkerLatLng();
        MarkerActivity.start(this, new MarkerItem(prjItem.getPrjName(), latLng),
                PrjEditActivity.REQUEST_CODE_MARKER_EDIT_ACTIVITY);
    }

    /**
     * 和InfoWindow绑定的点击事件
     *
     * @param v
     */
    public void clickPhoto(View v) {
        customMap.hideInfoWindow();
        LatLng latLng = customMap.getCurrentMarkerLatLng();
        PicGridActivity.start(this, new MarkerItem(prjItem.getPrjName(), latLng),
                PrjEditActivity.REQUEST_CODE_PICTURE_ACTIVITY);
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
                customMap.loadRail();
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
                    customMap.loadMarker();
                }
                break;
            case REQUEST_CODE_MARKER_EDIT_ACTIVITY:
                if (resultCode == Constant.RESULT_CODE_OK) {
                    customMap.loadMarker();
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

    //生命周期***********************************************************
    @Override
    protected void onPause() {
        super.onPause();
        customMap.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        customMap.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        customMap.destory();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        customMap.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }
}
