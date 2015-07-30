package com.xunce.gsmr.model.map;

import com.baidu.mapapi.model.LatLng;

/**
 * Created by ssthouse on 2015/7/30.
 */
public class Line {

    private LatLng latLngBegin;

    private LatLng latLngEnd;

    public Line(LatLng latLngBegin, LatLng latLngEnd) {
        this.latLngBegin = latLngBegin;
        this.latLngEnd = latLngEnd;
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
