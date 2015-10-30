package com.xunce.gsmr.app;

import android.app.Application;

import com.activeandroid.ActiveAndroid;
import com.baidu.mapapi.SDKInitializer;

import im.fir.sdk.FIR;

/**
 * 程序入口
 * Created by ssthouse on 2015/7/16.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        //fir统计
        FIR.init(this);
        super.onCreate();
        //百度地图初始化
        SDKInitializer.initialize(getApplicationContext());
        //初始化数据库
        ActiveAndroid.initialize(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        ActiveAndroid.dispose();
    }
}
