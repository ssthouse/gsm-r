package com.xunce.gsmr.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 管理preferencer的工具类
 * Created by ssthouse on 2015/7/28.
 */
public class PreferenceHelper {
    private static final String TAG = "PreferenceHelper";

    private static SharedPreferences sharedPreferences;
    private static final String PREFERENCE = "preference";

    //用于保存上一次编辑的prjItem的key
    private static final String PREFERENCE_PRJNAME = "prjName";

    public static boolean hasLastEditPrjItem(Context context) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
        }
        String prjName = sharedPreferences.getString(PREFERENCE_PRJNAME, null);
        if (prjName != null) {
            return true;
        } else {
            return false;
        }
    }

    public static String getLastEditPrjName(Context context) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
        }
        return sharedPreferences.getString(PREFERENCE_PRJNAME, "");
    }

    public static void setLastEditPrjName(Context context, String prjName) {
        if (context == null || prjName == null) {
            return;
        }
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREFERENCE_PRJNAME, prjName);
        editor.commit();
    }

    public static void deleteLastEditPrjName(Context context) {
        if (context == null) {
            return;
        }
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(PREFERENCE_PRJNAME);
        editor.commit();
    }

    //设置百度地图的定位方式
    private static final String PREFERENCE_LOCATE_MODE_USE_WIFI = "locate_mode_use_wifi";

    /**
     * 改变百度地图的定位方式
     * @param context
     * @param isWifiMode
     */
    public static void setLocateMode(Context context, boolean isWifiMode) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(PREFERENCE_LOCATE_MODE_USE_WIFI, isWifiMode);
        editor.commit();
    }

    /**
     * 获取定位方式
     * @param context
     * @return
     */
    public static boolean getIsWifiLocateMode(Context context){
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
        }
        return sharedPreferences.getBoolean(PREFERENCE_LOCATE_MODE_USE_WIFI, false);
    }
}
