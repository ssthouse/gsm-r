package com.xunce.gsmr.model.map;

import com.baidu.mapapi.model.LatLng;

/**
 * Created by ssthouse on 2015/7/30.
 */
public class Text {

    private LatLng latLng;

    private String text;

    public Text(LatLng latLng, String text) {
        this.latLng = latLng;
        this.text = text;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
