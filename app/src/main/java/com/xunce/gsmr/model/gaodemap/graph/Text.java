package com.xunce.gsmr.model.gaodemap.graph;

import android.graphics.Typeface;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.TextOptions;
import com.xunce.gsmr.util.gps.PositionUtil;

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
    private static int textSize = 14;

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
     * 传入经纬度的
     * @param latitude
     * @param longitude
     */
    public Text(double latitude, double longitude, String text){
        this.latLng = PositionUtil.gps84_To_Gcj02(latitude, longitude);
        this.content = text;
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
        }else{
            text.setVisible(true);
        }
    }

    /**
     * 隐藏
     */
    public void hide() {
        if(text != null){
            text.setVisible(false);
        }
    }

    /**
     * 销毁
     */
    public void destory(){
        if(text != null){
            text.remove();
            text.destroy();
        }
    }

    @Override
    public String toString() {
        return "latitude:"+latLng.latitude+"\t"
                +"longitude:"+latLng.longitude+"\t"
                +"content:"+content;
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
