package com.xunce.gsmr.lib.kmlParser;

import com.amap.api.maps.model.LatLng;
import com.xunce.gsmr.util.gps.PositionUtil;

/**
 * 表示一个点的坐标
 * Created by ssthouse on 2015/11/25.
 */
public class GpsPoint {

    private double longitude;

    private double latitude;

    public GpsPoint(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    @Override
    public String toString() {
        return longitude+"\t"+latitude;
    }

    public LatLng getLatLng(){
        return PositionUtil.gps84_To_Gcj02(latitude, longitude);
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}
