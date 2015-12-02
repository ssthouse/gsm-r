package com.xunce.gsmr.util.preference;

import android.content.Context;
import android.content.SharedPreferences;

import com.xunce.gsmr.model.MarkerIconCons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理preferencer的工具类
 * Created by ssthouse on 2015/7/28.
 */
public class PreferenceHelper {
    private static PreferenceHelper preferenceHelper;

    private SharedPreferences sharedPreferences;

    private final String PREFERENCE = "preference";

    //用于保存上一次编辑的prjItem的key
    private static final String KEY_PRJNAME = "prjName";

    //是否是第一次进入的KEY
    private static final String KEY_IS_FIST_IN = "is_fist_in";

    /**
     * 获取唯一的单例
     *
     * @param context
     * @return
     */
    public static PreferenceHelper getInstance(Context context) {
        if (preferenceHelper == null) {
            preferenceHelper = new PreferenceHelper(context);
        }
        return preferenceHelper;
    }

    /**
     * 构造方法
     *
     * @param context
     */
    private PreferenceHelper(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
    }

    /**
     * 是否有上一次编辑的工程
     *
     * @param context
     * @return
     */
    public boolean hasLastEditPrjItem(Context context) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
        }
        String prjName = sharedPreferences.getString(KEY_PRJNAME, null);
        if (prjName != null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取上一次编辑的工程的名称
     *
     * @param context
     * @return
     */
    public String getLastEditPrjName(Context context) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
        }
        return sharedPreferences.getString(KEY_PRJNAME, "");
    }

    /**
     * 设置上一次编辑的工程名称
     *
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
        editor.putString(KEY_PRJNAME, prjName);
        editor.commit();
    }

    /**
     * 删除上一次编辑的Project的记录
     *
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
        editor.remove(KEY_PRJNAME);
        editor.commit();
    }

    //设置百度地图的定位方式
    private static final String PREFERENCE_LOCATE_MODE_USE_WIFI = "locate_mode_use_wifi";

    /**
     * 改变百度地图的定位方式
     *
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
     *
     * @param context
     * @return
     */
    public boolean getIsWifiLocateMode(Context context) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE);
        }
        return sharedPreferences.getBoolean(PREFERENCE_LOCATE_MODE_USE_WIFI, false);
    }

    /**
     * 获取地图类型
     *
     * @return
     */
    public int getMapType() {
        int mapType = sharedPreferences.getInt(MapType.KEY, MapType.GAODE_MAP);
        return mapType;
    }

    /**
     * 设置map类型
     *
     * @param mapType
     */
    public void setMapType(int mapType) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
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

    /**
     * 为每一个文字设置不同的图标
     * 默认的是用蓝色的图标
     */

    interface MarkerTypeNameCons {
        String names[] = {"车站基站", "区间基站", "直放站", "既有基站", "既有直放站"};
    }

    private static final String KEY_MARKER_ICON_SIZE = "marker_icon_size";

    private static final String KEY_MARKER_ICON_NAME_PREFIX = "marker_icon_name_";
    private static final String KEY_MARKER_ICON_VALUE_PREFIX = "marker_icon_value_";

    /**
     * 第一次进入时---进行初始化
     */
    public void initMarkerIconPreference() {
        boolean isFistIn = sharedPreferences.getBoolean(KEY_IS_FIST_IN, true);
        //只有第一次进入需要初始化
        if (isFistIn) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            for (int i = 0; i < MarkerTypeNameCons.names.length; i++) {
                //将size写入
                editor.putInt(KEY_MARKER_ICON_SIZE, MarkerTypeNameCons.names.length);
                //将name写入
                editor.putString(KEY_MARKER_ICON_NAME_PREFIX + i, MarkerTypeNameCons.names[i]);
                //将对应颜色名字的数据写入
                editor.putString(MarkerTypeNameCons.names[i], "blue");
            }
            //不再是第一次进入
            editor.putBoolean(KEY_IS_FIST_IN, false);
            //提交
            editor.commit();
        }
    }

    /**
     * 获取所有设置了的设备类型的key-value
     *
     * @return
     */
    public Map<String, String> getMarkerIconMap() {
        Map<String, String> map = new HashMap<>();
        int size = sharedPreferences.getInt(KEY_MARKER_ICON_SIZE, 0);
        for (int i = 0; i < size; i++) {
            //将键值对写入map
            String name = sharedPreferences.getString(KEY_MARKER_ICON_NAME_PREFIX + i, "");
            //获取name对应的颜色str(默认是blue)
            String colorName = sharedPreferences.getString(name, MarkerIconCons.ColorName.BLUE);
            //添加进map
            map.put(name, colorName);
        }
        return map;
    }

    /**
     * 将新的name-color name键值对写入sharedpreference
     */
    public void setMarkerIconMap(Map<String, String> map) {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        //删除之前的数据
        int size = sharedPreferences.getInt(KEY_MARKER_ICON_SIZE, 0);
        List<String> deviceTypeNames = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            String name = sharedPreferences.getString(KEY_MARKER_ICON_NAME_PREFIX + i, "");
            deviceTypeNames.add(name);
        }
        //将name对应的colorStr删除
        for (String name : deviceTypeNames) {
            editor.remove(name);
        }
        //将deviceName也去掉
        for (int i = 0; i < size; i++) {
            editor.remove(KEY_MARKER_ICON_NAME_PREFIX + i);
        }

        //写入新的数据
        //写入size
        editor.putInt(KEY_MARKER_ICON_SIZE, map.size());
        //循环写入map数据
        int i = 0;
        for (Map.Entry<String, String> entity : map.entrySet()) {
            //写入name
            editor.putString(KEY_MARKER_ICON_NAME_PREFIX + i, entity.getKey());
            //写入color name
            editor.putString(entity.getKey(), entity.getValue());
            //下一个name
            i++;
        }
        editor.commit();
    }

    /**
     * 获取指定设别类型的Marker的颜色str
     *
     * @return
     */
    public String getMarkerColorName(String deviceType) {
        if (deviceType == null) {
            return "";
        } else {
            return sharedPreferences.getString(deviceType, "");
        }
    }
}
