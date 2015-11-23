package com.xunce.gsmr.model.gaodemap.graph;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;

/**
 * 高德地图的Cirrcle
 * Created by ssthouse on 2015/9/15.
 */
public class Circle extends BaseGraph {
    /**
     * 画笔参数
     */
    private float strokeWith = 5;
    private int strokeColor = 0xAA000000;

    /**
     * 圆的参数
     */
    private LatLng latlngCenter;
    private float radius;

    /**
     * 构造方法
     *
     * @param latlngCenter
     * @param radius
     */
    public Circle(LatLng latlngCenter, int radius) {
        this.latlngCenter = latlngCenter;
        this.radius = radius;
    }


    @Override
    public void draw(AMap aMap) {
        // 绘制一个椭圆
        CircleOptions options = new CircleOptions();
        options.center(latlngCenter)
                .radius(radius)
                .strokeWidth(strokeWith)
                .strokeColor(strokeColor)
                .strokeWidth(4f);
        aMap.addCircle(options);
    }

    //getter---and---setter----------------------------------------------
    public LatLng getLatlngCenter() {
        return latlngCenter;
    }

    public void setLatlngCenter(LatLng latlngCenter) {
        this.latlngCenter = latlngCenter;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }
}
