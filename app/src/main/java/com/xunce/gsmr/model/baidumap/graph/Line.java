package com.xunce.gsmr.model.baidumap.graph;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * 地图上的直线
 * Created by ssthouse on 2015/7/30.
 */
public class Line extends Graph {
    private static final String TAG = "Line";

    //直线的参数
    private static int lineColor =  0xAAFF0000;
    private static int lineWidth = 10;

    private LatLng latLngBegin;

    private LatLng latLngEnd;

    public Line(LatLng latLngBegin, LatLng latLngEnd) {
        this.latLngBegin = latLngBegin;
        this.latLngEnd = latLngEnd;
    }

    @Override
    public void draw(BaiduMap baiduMap) {
        // 添加折线
        List<LatLng> points = new ArrayList<>();
        points.add(latLngBegin);
        points.add(latLngEnd);
        OverlayOptions ooPolyline = new PolylineOptions()
                .width(lineWidth)
                .color(lineColor)
                .points(points);
        baiduMap.addOverlay(ooPolyline);
    }

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
