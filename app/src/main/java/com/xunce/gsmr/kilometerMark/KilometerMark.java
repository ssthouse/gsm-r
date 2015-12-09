package com.xunce.gsmr.kilometerMark;

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

    /**
     * 构造方法
     *
     * @param longitude
     * @param latitude
     * @param text
     */
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
        if (text.contains("+")) {
            return null;
        } else if (text.contains("AK") || text.contains("CK") || text.contains("DK")) {
            return new KilometerMark(longitude, latitude, text);
        }
        return null;
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
