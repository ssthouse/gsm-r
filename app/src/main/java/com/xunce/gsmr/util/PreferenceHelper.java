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
        String prjName = sharedPreferences.getString(PREFERENCE_PRJNAME, "");
        if (prjName.length() > 0) {
            LogHelper.Log(TAG, "wofanhui de true");
            return true;
        } else {
            LogHelper.Log(TAG, "wofanhui de false");
            return false;
        }
    }

    public static String getLastEditPrjName(Context context){
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
        }
        return sharedPreferences.getString(PREFERENCE_PRJNAME, "");
    }

    public static void saveLstEditPrjName(Context context, String prjName){
        if(context == null || prjName == null){
            return;
        }
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREFERENCE_PRJNAME, prjName);
        editor.commit();
    }
}
