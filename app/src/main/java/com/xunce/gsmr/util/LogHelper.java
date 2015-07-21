package com.xunce.gsmr.util;

import android.util.Log;

import com.baidu.location.BDLocation;
import com.xunce.gsmr.model.MarkerItem;
import com.xunce.gsmr.model.PhotoItem;

/**
 * Created by ssthouse on 2015/7/17.
 */
public class LogHelper {

    public static void Log(String tag, String msg) {
        Log.e(tag, msg);
    }

    public static void Log(String tag, BDLocation bdLocation) {
        Log.e(tag, bdLocation.getLongitude() + " : " + bdLocation.getLatitude());
    }


    public static void Log(String tag, MarkerItem markerItem) {
        Log.e(tag, markerItem.getLongitude() + " : " + markerItem.getLatitude());
    }

    public static void Log(String tag, PhotoItem photoItem) {
        Log.e(tag, photoItem.getPrjName() + ":" + photoItem.getLongitude() +
                " : " + photoItem.getLatitude());
    }
}


