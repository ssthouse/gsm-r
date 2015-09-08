package com.xunce.gsmr.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.xunce.gsmr.R;
import com.xunce.gsmr.util.PreferenceHelper;
import com.xunce.gsmr.util.ViewHelper;
import com.xunce.gsmr.view.style.TransparentStyle;

/**
 * 设置Activity
 * Created by ssthouse on 2015/9/8.
 */
public class SettingActivity extends AppCompatActivity {

    public static void start(Activity activity){
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
        sw.setChecked(PreferenceHelper.getIsWifiLocateMode(this));

        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    PreferenceHelper.setLocateMode(SettingActivity.this, isChecked);
                }
            }
        });
    }
}
