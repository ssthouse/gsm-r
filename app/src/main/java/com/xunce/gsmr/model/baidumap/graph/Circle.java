package com.xunce.gsmr.model.baidumap.graph;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;

/**
 * 地图上的圆
 * Created by ssthouse on 2015/7/30.
 */
public class Circle extends Graph {
    private static final String TAG = "Circle";

    //圆圈的参数
    private static Stroke circleStroke = new Stroke(5, 0xAA000000);

    private LatLng latlngCenter;
    private int radius;

    public Circle(LatLng latlngCenter, int radius) {
        this.latlngCenter = latlngCenter;
        this.radius = radius;
    }

    @Override
    public void draw(BaiduMap baiduMap) {
        OverlayOptions ooCircle = new CircleOptions()
                .fillColor(0x000000FF)
                .center(latlngCenter)
                .stroke(circleStroke)
                .radius(radius);
        baiduMap.addOverlay(ooCircle);
    }


    //getter---and---setter----------------------------------------------
    public LatLng getLatlngCenter() {
        return latlngCenter;
    }

    public void setLatlngCenter(LatLng latlngCenter) {
        this.latlngCenter = latlngCenter;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }


}
