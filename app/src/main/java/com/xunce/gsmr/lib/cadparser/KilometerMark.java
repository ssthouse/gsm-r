package com.xunce.gsmr.lib.cadparser;

/**
 * 保存一个公里标
 * 只需要一个 经纬度 即可
 * note:
 * 其实可以直接用graph中的text, 但是会有一些多余的属性
 * Created by ssthouse on 2015/11/27.
 */
public class KilometerMark {
    /**
     * 经度
     */
    private double longitude;
    /**
     * 纬度
     */
    private double latitude;
    /**
     * 公里标文字
     */
    private String text;

    private KilometerMark(double longitude, double latitude, String text) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.text = text;
    }

    /**
     * 传入经纬度的构造方法
     *
     * @param longitude
     * @param latitude
     */
    public static KilometerMark getKilometerMark(double longitude, double latitude, String text) {
        //需要对Text进行筛选
        //清除左右空格
        text = text.trim();
        if (!text.contains("DK")) {
            return null;
        } else {
            if (text.contains("+")) {
                return null;
            } else {
                return new KilometerMark(longitude, latitude, text);
            }
        }
    }

    @Override
    public String toString() {
        return "公里标: " + text + "\t经度: " + longitude + "\t纬度: " + latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
