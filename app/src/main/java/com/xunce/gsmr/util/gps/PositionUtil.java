package com.xunce.gsmr.util.gps;

import com.amap.api.maps.model.LatLng;

/**
 * 各地图API坐标系统比较与转换;
 * WGS84坐标系：即地球坐标系，国际上通用的坐标系。设备一般包含GPS芯片或者北斗芯片获取的经纬度为WGS84地理坐标系,
 * 谷歌地图采用的是WGS84地理坐标系（中国范围除外）;
 * GCJ02坐标系：即火星坐标系，是由中国国家测绘局制订的地理信息系统的坐标系统。由WGS84坐标系经加密后的坐标系。
 * 谷歌中国地图和搜搜中国地图采用的是GCJ02地理坐标系; BD09坐标系：即百度坐标系，GCJ02坐标系经加密后的坐标系;
 * 搜狗坐标系、图吧坐标系等，估计也是在GCJ02基础上加密而成的。 chenhua
 */
public class PositionUtil {

    public static final String BAIDU_LBS_TYPE = "bd09ll";

    public static double pi = 3.1415926535897932384626;
    public static double a = 6378245.0;
    public static double ee = 0.00669342162296594323;

    /**
     * 获取高德地图的坐标
     * 84 to 火星坐标系 (GCJ-02) World Geodetic System ==> Mars Geodetic System
     *
     * @param lat
     * @param lon
     * @return
     */
    public static LatLng gps84_To_Gcj02(double lat, double lon) {
          double dLat = transformLat(lon - 105.0, lat - 35.0);
          double dLon = transformLon(lon - 105.0, lat - 35.0);
          double radLat = lat / 180.0 * pi;
          double magic = Math.sin(radLat);
          magic = 1 - ee * magic * magic;
          double sqrtMagic = Math.sqrt(magic);
          dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * pi);
          dLon = (dLon * 180.0) / (a / sqrtMagic * Math.cos(radLat) * pi);
          double mgLat = lat + dLat;
          double mgLon = lon + dLon;
          return new LatLng(mgLat,mgLon);
    }


    /**
     * 将百度LatLng转换为高德LatLng
     *
     * @param latLng
     * @return
     */
    public static LatLng bd_2_gaode_latlng(com.baidu.mapapi.model.LatLng latLng) {
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

    /**
     * 火星坐标系 (GCJ-02) to 84
     *
     * @param lon
     * @param lat
     * @return 返回的数据---纬度---经度
     * */
    public static double[] gcj_To_Gps84(double lat, double lon) {
        double tempLatlng [] = transform(lat, lon);
        double longitude = lon * 2 - tempLatlng[1];
        double latitude = lat * 2 - tempLatlng[0];
        //输出一个数组
        double[] result = new double[2];
        result[0] = latitude;
        result[1] = longitude;
        return result;
    }

    /**
     * 火星坐标系 (GCJ-02) 与百度坐标系 (BD-09) 的转换算法 将 GCJ-02 坐标转换成 BD-09 坐标
     *
     * @param gg_lat
     * @param gg_lon
     */
    public static double[] gcj02_To_Bd09(double gg_lat, double gg_lon) {
        double x = gg_lon, y = gg_lat;
        double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * pi);
        double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * pi);
        double bd_lon = z * Math.cos(theta) + 0.0065;
        double bd_lat = z * Math.sin(theta) + 0.006;
        //输出数据
        double result[] = new double[2];
        result[0] = bd_lat;
        result[1] = bd_lon;
        return result;
    }

    /**
     * * 火星坐标系 (GCJ-02) 与百度坐标系 (BD-09) 的转换算法 * * 将 BD-09 坐标转换成GCJ-02 坐标 * * @param
     * bd_lat * @param bd_lon * @return
     */
    public static double[] bd09_To_Gcj02(double bd_lat, double bd_lon) {
        double x = bd_lon - 0.0065, y = bd_lat - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * pi);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * pi);
        double gg_lon = z * Math.cos(theta);
        double gg_lat = z * Math.sin(theta);
        //输出数据
        double result[] = new double[2];
        result[0] = bd_lat;
        result[1] = bd_lon;
        return result;
    }

    /**
     * (BD-09)-->84
     * @param bd_lat
     * @param bd_lon
     * @return
     */
    public static double[] bd09_To_Gps84(double bd_lat, double bd_lon) {
        double gcjLatlng[] = PositionUtil.bd09_To_Gcj02(bd_lat, bd_lon);
        double wgsLatlng[] = PositionUtil.gcj_To_Gps84(gcjLatlng[0], gcjLatlng[1]);
        return wgsLatlng;
    }

    public static boolean outOfChina(double lat, double lon) {
        if (lon < 72.004 || lon > 137.8347)
            return true;
        if (lat < 0.8293 || lat > 55.8271)
            return true;
        return false;
    }

    public static double[] transform(double lat, double lon) {
        if (outOfChina(lat, lon)) {
            return null;
        }
        double dLat = transformLat(lon - 105.0, lat - 35.0);
        double dLon = transformLon(lon - 105.0, lat - 35.0);
        double radLat = lat / 180.0 * pi;
        double magic = Math.sin(radLat);
        magic = 1 - ee * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * pi);
        dLon = (dLon * 180.0) / (a / sqrtMagic * Math.cos(radLat) * pi);
        double mgLat = lat + dLat;
        double mgLon = lon + dLon;
        //配置输出
        double result[] = new double[2];
        result[0] = mgLat;
        result[1] = mgLon;
        return result;
    }

    public static double transformLat(double x, double y) {
        double ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y
                + 0.2 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(y * pi) + 40.0 * Math.sin(y / 3.0 * pi)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(y / 12.0 * pi) + 320 * Math.sin(y * pi / 30.0)) * 2.0 / 3.0;
        return ret;
    }

    public static double transformLon(double x, double y) {
        double ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1
                * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(x * pi) + 40.0 * Math.sin(x / 3.0 * pi)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(x / 12.0 * pi) + 300.0 * Math.sin(x / 30.0
                * pi)) * 2.0 / 3.0;
        return ret;
    }


}