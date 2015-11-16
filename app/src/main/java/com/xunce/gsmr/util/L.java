package com.xunce.gsmr.util;

import android.util.Log;

import com.baidu.location.BDLocation;
import com.xunce.gsmr.model.MarkerItem;

/**
 * 打印调试日志
 * Created by ssthouse on 2015/7/17.
 */
public class L {

    public static void log(String tag, String msg) {
        Log.e(tag, msg);
    }

    public static void log(String tag, BDLocation bdLocation) {
        Log.e(tag, bdLocation.getLongitude() + " : " + bdLocation.getLatitude());
    }

    public static void log(String tag, MarkerItem markerItem) {
        Log.e(tag, markerItem.getLongitude() + " : " + markerItem.getLatitude());
    }
}


