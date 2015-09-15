package com.xunce.gsmr.model.baidumap.graph;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;

/**
 * 地图上的文字
 * Created by ssthouse on 2015/7/30.
 */
public class Text extends Graph {
    private static final String TAG = "Text";

    //文字的参数
    private static int textColor = 0xFFFF00FF;
    private static int textBgColor = 0x00FF00FF;
    private static int textSize = 24;

    private LatLng latLng;
    private float rotate;
    private String text;

    public Text(LatLng latLng, float rotate, String text) {
        this.latLng = latLng;
        this.rotate = rotate;
        this.text = text;
    }

    public Text(LatLng latLng, String string) {
        this.latLng = latLng;
        this.text = string;
        this.rotate = 0;
    }

    @Override
    public void draw(BaiduMap baiduMap) {
        // 添加文字
        OverlayOptions ooText = new TextOptions()
                .bgColor(textBgColor)
                .fontSize(textSize)
                .fontColor(textColor)
                .text(text)
                .rotate(-rotate)
                .position(latLng);
        baiduMap.addOverlay(ooText);
    }


    //getter----and---setter------------------------------------------------
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

    public float getRotate() {
        return rotate;
    }
    public void setRotate(float rotate) {
        this.rotate = rotate;
    }
}
