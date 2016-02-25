package com.xunce.gsmr.view.activity.baidu;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.xunce.gsmr.R;
import com.xunce.gsmr.app.Constant;
import com.xunce.gsmr.model.MarkerItem;
import com.xunce.gsmr.model.PrjItem;
import com.xunce.gsmr.util.FileHelper;
import com.xunce.gsmr.util.L;
import com.xunce.gsmr.util.view.ViewHelper;
import com.xunce.gsmr.view.activity.PicGridActivity;
import com.xunce.gsmr.view.activity.PrjSelectActivity;
import com.xunce.gsmr.view.activity.SettingActivity;
import com.xunce.gsmr.view.style.TransparentStyle;

/**
 * 开启时会接收到一个PrjItem---intent中
 */
public class BaiduPrjEditActivity extends AppCompatActivity {
    private static final String TAG = "BaiduPrjEditActivity";

    //Activity请求码
    public static final int REQUEST_CODE_ROUTE_ACTIVITY = 1000;
    public static final int REQUEST_CODE_MARKER_ACTIVITY = 1001;
    public static final int REQUEST_CODE_PICTURE_ACTIVITY = 1002;
    public static final int REQUEST_CODE_MARKER_EDIT_ACTIVITY = 1003;

    /**
     * 地图总控制器
     */
    private BaiduMapFragment baiduMapFragment;

    /**
     * 用于点击两次退出
     */
    private long mExitTime;

    /**
     * 接收到的数据
     */
    private PrjItem prjItem;

    /**
     * map_mode选择控件
     */
    private RadioGroup rg;

    /**
     * 用于更加方便的开启Activity
     * 后面几个参数可以用来传递-----放入intent 的数据
     *
     * @param activity
     */
    public static void start(Activity activity, PrjItem prjItem) {
        Intent intent = new Intent(activity, BaiduPrjEditActivity.class);
        intent.putExtra(Constant.EXTRA_KEY_PRJ_ITEM, prjItem);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.activity_fade_in, R.anim.activity_fade_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baidu_prj_edit);
        TransparentStyle.setTransparentStyle(this, R.color.color_primary);

        prjItem = (PrjItem) getIntent().getSerializableExtra(Constant.EXTRA_KEY_PRJ_ITEM);

        //初始化View
        initView();
    }

    /**
     * 初始化View
     */
    private void initView() {
        ViewHelper.initActionBar(this, getSupportActionBar(), prjItem.getPrjName());

        //初始化map_mode控件
        initMapMode();

        //启动Map的片段
        Bundle bundle = new Bundle();
        bundle.putSerializable("prjItem", prjItem);
        baiduMapFragment = BaiduMapFragment.getInstance(bundle);
        getFragmentManager().beginTransaction().replace(R.id.id_fragment_container,
                baiduMapFragment).commit();

        //选址
        findViewById(R.id.id_btn_mark).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //先保存进数据库---然后传递
                MarkerItem markerItem = new MarkerItem(prjItem);
//                markerItem.save();
                BaiduMarkerActivity.start(BaiduPrjEditActivity.this, markerItem,prjItem.getDbLocation(),
                        REQUEST_CODE_MARKER_ACTIVITY);
            }
        });
    }

    /**
     * 初始化地图Mode控件
     */
    private void initMapMode() {
        //map_mode可见性的切换
        findViewById(R.id.id_ib_open_map_mode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rg.getVisibility() == View.VISIBLE) {
                    rg.startAnimation(AnimationUtils.loadAnimation(BaiduPrjEditActivity.this,
                            R.anim.slide_right));
                    rg.setVisibility(View.INVISIBLE);
                } else {
                    rg.startAnimation(AnimationUtils.loadAnimation(BaiduPrjEditActivity.this,
                            R.anim.slide_left));
                    rg.setVisibility(View.VISIBLE);
                }
            }
        });
        //切换map_mode 的选项
        rg = (RadioGroup) findViewById(R.id.id_rg_map_mode);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.id_rb_mode_normal: {
                        baiduMapFragment.getBaiduMap().setMapType(BaiduMap.MAP_TYPE_NORMAL);
                        MapStatus ms = new MapStatus.Builder(baiduMapFragment.getBaiduMap()
                                .getMapStatus()).overlook(0).build();
                        MapStatusUpdate u = MapStatusUpdateFactory.newMapStatus(ms);
                        baiduMapFragment.getBaiduMap().animateMapStatus(u);
                        break;
                    }
                    case R.id.id_rb_mode_satellite: {
                        baiduMapFragment.getBaiduMap().setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                        MapStatus ms = new MapStatus.Builder(baiduMapFragment.getBaiduMap()
                                .getMapStatus()).overlook(0).build();
                        MapStatusUpdate u = MapStatusUpdateFactory.newMapStatus(ms);
                        baiduMapFragment.getBaiduMap().animateMapStatus(u);
                        break;
                    }
                    case R.id.id_rb_mode_3d: {
                        int overlookAngle = -45;
                        MapStatus ms = new MapStatus.Builder(baiduMapFragment.getBaiduMap()
                                .getMapStatus()).overlook(overlookAngle).build();
                        MapStatusUpdate u = MapStatusUpdateFactory.newMapStatus(ms);
                        baiduMapFragment.getBaiduMap().animateMapStatus(u);
                        baiduMapFragment.getBaiduMap().setMapType(BaiduMap.MAP_TYPE_NORMAL);
                        break;
                    }
                }
            }
        });
    }

    /**
     * 和InfoWindow绑定的点击事件
     *
     * @param v
     */
    public void clickEdit(View v) {
        baiduMapFragment.hideInfoWindow();
        //生成MarkerItem--跳转到MarkerEditActivity
        BaiduMarkerActivity.start(this, baiduMapFragment.getMarkerHolder().getCurrentMarkerItem()
                ,prjItem.getDbLocation(), BaiduPrjEditActivity.REQUEST_CODE_MARKER_EDIT_ACTIVITY);
    }

    /**
     * 和InfoWindow绑定的点击事件
     *
     * @param v
     */
    public void clickPhoto(View v) {
        baiduMapFragment.hideInfoWindow();
        PicGridActivity.start(this, baiduMapFragment.getMarkerHolder().getCurrentMarkerItem(),
                prjItem.getDbLocation(),
                BaiduPrjEditActivity.REQUEST_CODE_PICTURE_ACTIVITY);
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
                PrjSelectActivity.start(this, true);
                //加载铁路地图
            case R.id.id_action_load_digital_file:
                //TODO---加载铁路地图
                //首先判断数据库是否绑定
                baiduMapFragment.loadRail();
                break;
            //数据导出
            case R.id.id_action_export_data:
                FileHelper.sendDbFile(this,prjItem.getDbLocation());
                break;
            case R.id.id_action_offline_map:
                //开启离线地图管理Activity
                BaiduOfflineActivity.start(this);
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
                    baiduMapFragment.loadMarker();
                }
                break;
            case REQUEST_CODE_MARKER_EDIT_ACTIVITY:
                if (resultCode == Constant.RESULT_CODE_OK) {
                    baiduMapFragment.loadMarker();
                }
                break;
            case Constant.REQUEST_CODE_DB_FILE:
                //如果是加载.db文件
                Uri uri = data.getData();
                L.log(TAG, uri.getEncodedPath());
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
        baiduMapFragment.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        baiduMapFragment.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        baiduMapFragment.destory();
    }
}
