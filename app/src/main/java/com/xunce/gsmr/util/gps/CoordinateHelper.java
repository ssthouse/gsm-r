package com.xunce.gsmr.util.gps;

import com.amap.api.maps.model.LatLng;

/**
 * 坐标转换工具类
 * Created by ssthouse on 2015/9/23.
 */
public class CoordinateHelper {


    /**
     * 将百度LatLng转换为高德LatLng
     *
     * @param latLng
     * @return
     */
    public static LatLng getGaodeLatLng(com.baidu.mapapi.model.LatLng latLng) {
        double pi = 3.14159265358979324;
        double a = 6378245.0;
        double ee = 0.00669342162296594323;
        double x_pi = 3.14159265358979324 * 3000.0 / 180.0;

        double bdLatitude = latLng.latitude;
        double bdLongitude = latLng.longitude;
        double x = bdLongitude - 0.0065, y = bdLatitude - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * x_pi);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * x_pi);
        double gdLongitude = z * Math.cos(theta);
        double gdLatitude = z * Math.sin(theta);
        return new LatLng(gdLatitude, gdLongitude);
    }


}
