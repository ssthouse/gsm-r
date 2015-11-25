package com.xunce.gsmr.lib.kmlParser;

import android.graphics.Color;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.maps.model.TextOptions;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Kml文件解析出的数据
 * Created by ssthouse on 2015/11/25.
 */
class KmlData {
    private String name;
    private String longitude;
    private String latitude;
    private String styleUrl;

    private static final String POLY_STYLE = "#polystyle";
    private static final String TEXT_STYLE = "#msn_shaded_dot";

    /**
     * 蝴蝶形状的所有点
     */
    private List<PolyCoordinates> pointList = new ArrayList<>();

    /**
     * 在地图上画出多边形
     *
     * @param amap
     */
    public void draw(AMap amap) {
        if (styleUrl.equals(POLY_STYLE)) {
            PolylineOptions polylineOptions = new PolylineOptions();
            polylineOptions.width(8)
                    .color(Color.GREEN);
            for (PolyCoordinates polyCoordinates : pointList) {
                polylineOptions.add(polyCoordinates.getLatLng());
            }
            amap.addPolyline(polylineOptions);
        } else if (styleUrl.equals(TEXT_STYLE)) {
            TextOptions textOptions = new TextOptions();
            textOptions.fontSize(12)
                    .position(pointList.get(0).getLatLng())
                    .fontColor(Color.GREEN)
                    .text(name);
            amap.addText(textOptions);
        }
        //TODO---查看所有数据点
        for (PolyCoordinates polyCoordinates : pointList) {
            Timber.e(polyCoordinates + "\n");
        }
    }

    @Override
    public String toString() {
        return name + ":" + longitude + ":" + latitude + "\n" + "我有的点数目为:\t" + pointList.size();
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStyleUrl() {
        return styleUrl;
    }

    public void setStyleUrl(String styleUrl) {
        this.styleUrl = styleUrl;
    }

    public List<PolyCoordinates> getPointList() {
        return pointList;
    }

    public void setPointList(List<PolyCoordinates> pointList) {
        this.pointList = pointList;
    }
}
