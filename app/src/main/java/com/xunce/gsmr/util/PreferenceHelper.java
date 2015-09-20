package com.xunce.gsmr.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 管理preferencer的工具类
 * Created by ssthouse on 2015/7/28.
 */
public class PreferenceHelper {
    private static final String TAG = "PreferenceHelper";

    private static PreferenceHelper preferenceHelper;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private final String PREFERENCE = "preference";

    //用于保存上一次编辑的prjItem的key
    private static final String PREFERENCE_PRJNAME = "prjName";

    /**
     * 获取唯一的单例
     * @param context
     * @return
     */
    public static PreferenceHelper getInstance(Context context){
        if(preferenceHelper == null){
            preferenceHelper = new PreferenceHelper(context);
        }
        return preferenceHelper;
    }

    /**
     * 构造方法
     * @param context
     */
    private PreferenceHelper(Context context){
        sharedPreferences = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    /**
     * 是否有上一次编辑的工程
     * @param context
     * @return
     */
    public boolean hasLastEditPrjItem(Context context) {
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

    /**
     * 获取上一次编辑的工程的名称
     * @param context
     * @return
     */
    public String getLastEditPrjName(Context context) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
        }
        return sharedPreferences.getString(PREFERENCE_PRJNAME, "");
    }

    /**
     * 设置上一次编辑的工程名称
     * @param context
     * @param prjName
     */
    public void setLastEditPrjName(Context context, String prjName) {
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

    /**
     * 删除上一次编辑的Project的记录
     * @param context
     */
    public void deleteLastEditPrjName(Context context) {
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
    public void setLocateMode(Context context, boolean isWifiMode) {
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
    public boolean getIsWifiLocateMode(Context context){
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
        }
        return sharedPreferences.getBoolean(PREFERENCE_LOCATE_MODE_USE_WIFI, false);
    }

    /**
     * 获取地图类型
     * @return
     */
    public int getMapType(){
        int mapType = sharedPreferences.getInt(MapType.KEY, MapType.GAODE_MAP);
        return mapType;
    }

    /**
     * 设置map类型
     * @param mapType
     */
    public void setMapType(int mapType){
        editor.putInt(MapType.KEY, mapType);
        editor.commit();
    }

    /**
     * 地图类型
     */
    public interface MapType {
        String KEY = "map_type";
        int BAIDU_MAP = 1001;
        int GAODE_MAP = 1002;
    }
}
