package com.xunce.gsmr.model.gaodemap.graph;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.PolylineOptions;

/**
 * 高德地图的Line
 * Created by ssthouse on 2015/9/15.
 */
public class Line extends BaseGraph {
    private static final String TAG = "Line";

    //直线的参数
    private static int lineColor = 0xAAFF0000;
    private static int lineWidth = 10;

    /**
     * 坐标点
     */
    private LatLng latLngBegin;
    private LatLng latLngEnd;

    /**
     * 构造方法
     * @param latLngBegin
     * @param latLngEnd
     */
    public Line(LatLng latLngBegin, LatLng latLngEnd) {
        this.latLngBegin = latLngBegin;
        this.latLngEnd = latLngEnd;
    }

    @Override
    public void draw(AMap aMap) {
        PolylineOptions options = new PolylineOptions();
        options.add(latLngBegin)
                .add(latLngEnd)
                .add(latLngEnd)
                .width(lineWidth)
                .color(lineColor);
        aMap.addPolyline(options);
    }

    //getter----and---setter------------------------------------------------
    public LatLng getLatLngBegin() {
        return latLngBegin;
    }

    public void setLatLngBegin(LatLng latLngBegin) {
        this.latLngBegin = latLngBegin;
    }

    public LatLng getLatLngEnd() {
        return latLngEnd;
    }

    public void setLatLngEnd(LatLng latLngEnd) {
        this.latLngEnd = latLngEnd;
    }

}
