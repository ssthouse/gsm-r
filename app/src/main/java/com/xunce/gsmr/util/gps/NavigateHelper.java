package com.xunce.gsmr.util.gps;

import android.content.Context;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.navi.BaiduMapNavigation;
import com.baidu.mapapi.navi.NaviParaOption;

/**
 * 导航工具
 * Created by ssthouse on 2015/7/17.
 */
public class NavigateHelper {
    private static final String TAG = "NavigateHelper";




    public static void startNavigate(Context context, BaiduMap baiduMap){
        BaiduMapNavigation.openBaiduMapNavi(new NaviParaOption(), context);
    }
}
