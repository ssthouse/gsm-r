package com.xunce.gsmr.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;

import com.xunce.gsmr.R;
import com.xunce.gsmr.util.LogHelper;
import com.xunce.gsmr.util.PreferenceHelper;
import com.xunce.gsmr.util.ViewHelper;
import com.xunce.gsmr.view.style.TransparentStyle;

/**
 * 设置Activity
 * Created by ssthouse on 2015/9/8.
 */
public class SettingActivity extends AppCompatActivity {
    private static final String TAG = "SettingActivity";

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

        //百度地图定位模式切换
        Switch sw = (Switch) findViewById(R.id.id_sw_locate_mode);
        //首先设置为preference中的状态
        sw.setChecked(PreferenceHelper.getInstance(SettingActivity.this).getIsWifiLocateMode(this));
        //LogHelper.Log(TAG, PreferenceHelper.getIsWifiLocateMode(this) + "");
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PreferenceHelper.getInstance(SettingActivity.this).setLocateMode(SettingActivity.this, isChecked);
                LogHelper.Log(TAG, isChecked + "");
            }
        });

        //spinner选择地图类型
        Spinner sp = (Spinner) findViewById(R.id.id_sp_map_type);
        if(PreferenceHelper.getInstance(SettingActivity.this).getMapType() ==
                PreferenceHelper.MapType.BAIDU_MAP) {
            sp.setSelection(0);
        }else{
            sp.setSelection(1);
        }
        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0) {
                    PreferenceHelper.getInstance(SettingActivity.this)
                            .setMapType(PreferenceHelper.MapType.BAIDU_MAP);
                    LogHelper.Log(TAG, "我设置了---百度地图");
                }else if(position ==1){
                    PreferenceHelper.getInstance(SettingActivity.this)
                            .setMapType(PreferenceHelper.MapType.GAODE_MAP);
                    LogHelper.Log(TAG, "我设置了--高德地图");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}
