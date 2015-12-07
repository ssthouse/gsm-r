package com.xunce.gsmr.util.gps;

import android.content.Context;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.xunce.gsmr.util.preference.PreferenceHelper;

/**
 * 用于定位
 * Created by ssthouse on 2015/7/17.
 */
public class LocateHelper {

    private LocateHelper() {
    }

    /**
     * 初始化LocationClient的数据
     *
     * @param locationClient
     */
    public static void initLocationClient(Context context, LocationClient locationClient) {
        if (locationClient == null) {
            return;
        }
        LocationClientOption locateOptions = new LocationClientOption();
        //设置定位模式----用的那个设备,应该是用gps定位
        if(PreferenceHelper.getInstance(context).getIsWifiLocateMode(context)) {
            locateOptions.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        }else{
            locateOptions.setLocationMode(LocationClientOption.LocationMode.Device_Sensors);
        }
        locateOptions.setCoorType("bd09ll");    //返回的定位结果是百度经纬度,默认值gcj02
        locateOptions.setScanSpan(1000);        //设置发起定位请求的间隔时间为5000ms
        locateOptions.setIsNeedAddress(true);   //返回的定位结果包含地址信息
        locateOptions.setNeedDeviceDirect(true);//返回的定位结果包含手机机头的方向
        locationClient.setLocOption(locateOptions);
    }
}
