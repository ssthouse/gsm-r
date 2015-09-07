package com.xunce.gsmr.util.gps;

import android.content.Context;

/**
 * 自己调用设备的GPS获取定位信息
 * Created by ssthouse on 2015/9/5.
 */
public class CustomLocate {
    private static final String TAG = "CustomLocate";

    private static CustomLocate customLocate;

    private Context context;

    private CustomLocate(Context context){
        this.context = context;
    }

    public static CustomLocate getInstance(Context context){
        if(customLocate == null){
            customLocate = new CustomLocate(context);
        }
        return customLocate;
    }



}
