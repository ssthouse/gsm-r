package com.xunce.gsmr;

import android.app.Application;

import com.activeandroid.ActiveAndroid;
import com.baidu.mapapi.SDKInitializer;

/**
 * Created by ssthouse on 2015/7/16.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
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
