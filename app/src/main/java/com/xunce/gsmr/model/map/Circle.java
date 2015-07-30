package com.xunce.gsmr.model.map;

import com.baidu.mapapi.model.LatLng;

/**
 * Created by ssthouse on 2015/7/30.
 */
public class Circle {

    private LatLng latLng;

    private float radius;

    public Circle(LatLng latLng, float radius) {
        this.latLng = latLng;
        this.radius = radius;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }
}
