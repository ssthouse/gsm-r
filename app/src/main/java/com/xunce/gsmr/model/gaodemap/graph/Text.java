package com.xunce.gsmr.model.gaodemap.graph;

import android.graphics.Typeface;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.TextOptions;

/**
 * 高德地图的Text
 * Created by ssthouse on 2015/9/15.
 */
public class Text extends BaseGraph {
    private static final String TAG = "Text";

    /**
     * 画笔参数
     */
    private static int textColor = 0xFFFF00FF;
    private static int textBgColor = 0x00FFFFFF;
    private static int textSize = 18;

    /**
     * 文字参数
     */
    private LatLng latLng;
    private float rotate;
    private String content;

    //TODO---试试能不能提高速度
    private TextOptions textOptions;
    //用于保存画在地图上的文字
    private com.amap.api.maps.model.Text text;

    private void initTextOptions() {
        textOptions = new TextOptions()
                .position(latLng)
                .text(content)
                .fontColor(textColor)
                .backgroundColor(textBgColor)
                .fontSize(textSize)
                .rotate(rotate)
                .align(com.amap.api.maps.model.Text.ALIGN_CENTER_HORIZONTAL,
                        com.amap.api.maps.model.Text.ALIGN_CENTER_VERTICAL)
                .zIndex(1.f)
                .typeface(Typeface.DEFAULT_BOLD);
    }

    /**
     * 构造方法
     *
     * @param latLng
     * @param text
     */
    public Text(LatLng latLng, float rotate, String text) {
        this.latLng = latLng;
        this.rotate = rotate;
        this.content = text;

        //初始化
        initTextOptions();
    }

    /**
     * 构造方法
     *
     * @param latLng
     * @param text
     */
    public Text(LatLng latLng, String text) {
        this.latLng = latLng;
        this.content = text;

        //初始化
        initTextOptions();
    }

    @Override
    public void draw(AMap aMap) {
        if (text == null) {
            text = aMap.addText(textOptions);
//            text.setVisible(false);
        }else{
            text.setVisible(true);
        }
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
