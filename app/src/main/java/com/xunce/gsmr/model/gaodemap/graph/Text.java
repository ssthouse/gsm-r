package com.xunce.gsmr.model.gaodemap.graph;

import android.graphics.Typeface;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.TextOptions;

/**
 * 高德地图的Text
 * Created by ssthouse on 2015/9/15.
 */
public class Text extends Graph {
    private static final String TAG = "Text";

    /**
     * 画笔参数
     */
    private static int textColor = 0xFFFF00FF;
    private static int textBgColor = 0x00FF00FF;
    private static int textSize = 24;

    /**
     * 文字参数
     */
    private LatLng latLng;
    private float rotate;
    private String text;

    /**
     * 构造方法
     *
     * @param latLng
     * @param text
     */
    public Text(LatLng latLng, float rotate, String text) {
        this.latLng = latLng;
        this.rotate = rotate;
        this.text = text;
    }

    /**
     * 构造方法
     *
     * @param latLng
     * @param text
     */
    public Text(LatLng latLng, String text) {
        this.latLng = latLng;
        this.text = text;
    }

    @Override
    public void draw(AMap aMap) {
        TextOptions textOptions = new TextOptions()
                .position(latLng)
                .text(text)
                .fontColor(textColor)
                .fontSize(textSize)
                .rotate(rotate)
                .align(com.amap.api.maps.model.Text.ALIGN_CENTER_HORIZONTAL,
                        com.amap.api.maps.model.Text.ALIGN_CENTER_VERTICAL)
                .zIndex(1.f)
                .typeface(Typeface.DEFAULT_BOLD);
        aMap.addText(textOptions);
    }

    //getter----and---setter------------------------------------------------
    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public float getRotate() {
        return rotate;
    }

    public void setRotate(float rotate) {
        this.rotate = rotate;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
