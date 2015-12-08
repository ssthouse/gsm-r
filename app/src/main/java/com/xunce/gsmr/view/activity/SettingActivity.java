package com.xunce.gsmr.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;

import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.xunce.gsmr.R;
import com.xunce.gsmr.model.event.LocateModeChangeEvent;
import com.xunce.gsmr.util.preference.PreferenceHelper;
import com.xunce.gsmr.util.view.ToastHelper;
import com.xunce.gsmr.util.view.ViewHelper;
import com.xunce.gsmr.view.style.TransparentStyle;

import de.greenrobot.event.EventBus;
import timber.log.Timber;

/**
 * 设置Activity
 * Created by ssthouse on 2015/9/8.
 */
public class SettingActivity extends AppCompatActivity {
    /**
     * 是否使用wifi的switch
     */
    private Switch locateModeSwitch;


    public static void start(Activity activity) {
        activity.startActivity(new Intent(activity, SettingActivity.class));
        activity.overridePendingTransition(R.anim.activity_fade_in, R.anim.activity_fade_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        TransparentStyle.setTransparentStyle(this, R.color.color_primary);

        initView();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        ViewHelper.initActionBar(this, getSupportActionBar(), "设置");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //地图定位模式切换
        locateModeSwitch = (Switch) findViewById(R.id.id_sw_locate_mode);
        //首先设置为preference中的状态
        locateModeSwitch.setChecked(PreferenceHelper.getInstance(SettingActivity.this).getIsWifiLocateMode(this));
        locateModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PreferenceHelper.getInstance(SettingActivity.this).setLocateMode(SettingActivity.this, isChecked);
                //发送定位模式改变event
                EventBus.getDefault().post(new LocateModeChangeEvent());
                if(isChecked){
                    ToastHelper.show(SettingActivity.this, "切换为使用Wifi定位");
                }else{
                    ToastHelper.show(SettingActivity.this, "切换为GPS定位");
                }
            }
        });

        //spinner选择地图类型
        Spinner sp = (Spinner) findViewById(R.id.id_sp_map_type);
        if (PreferenceHelper.getInstance(SettingActivity.this).getMapType() ==
                PreferenceHelper.MapType.BAIDU_MAP) {
            sp.setSelection(0);
        } else {
            sp.setSelection(1);
        }
        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    PreferenceHelper.getInstance(SettingActivity.this)
                            .setMapType(PreferenceHelper.MapType.BAIDU_MAP);
                    Timber.e("我设置了---百度地图");
                } else if (position == 1) {
                    PreferenceHelper.getInstance(SettingActivity.this)
                            .setMapType(PreferenceHelper.MapType.GAODE_MAP);
                    Timber.e("我设置了--高德地图");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //选中点的Icon的设置界面
        findViewById(R.id.id_ll_marker_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MarkerIconSetActivity.start(SettingActivity.this);
            }
        });

        //app版本按钮
        findViewById(R.id.id_tv_app_version).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAppVersionDialog();
            }
        });
    }

    /**
     * 显示app的版本Dialog
     */
    private void showAppVersionDialog() {
        NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(this);
        dialogBuilder.setCustomView(R.layout.dialog_app_version, this)
                .withTitle("版本信息")
                //.withTitleColor(0xffffff)
                .withDialogColor(getResources().getColor(R.color.color_primary_light))
                .isCancelableOnTouchOutside(true)
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
