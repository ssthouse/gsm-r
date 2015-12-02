package com.xunce.gsmr.model;

import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.xunce.gsmr.R;

/**
 * 将需要多次放在内存中的数据保存一份
 * Created by ssthouse on 2015/11/22.
 */
public class MarkerIconCons {

    //标记点相关的
    public static BitmapDescriptor descriptorBlue = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_marker_blue);
    public static BitmapDescriptor descriptorRed = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_marker_red);
    public static BitmapDescriptor descriptorGreen = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_marker_green);
    public static BitmapDescriptor descriptorPurple = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_marker_purple);
    public static BitmapDescriptor descriptorOrange = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_marker_orange);

    /**
     * 根据给定的名字---获取图标
     * @return
     */
    public static BitmapDescriptor getBitmapDescriptor(String colorName){
        switch (colorName){
            case ColorName.BLUE:
                return descriptorBlue;
            case ColorName.RED:
                return descriptorRed;
            case ColorName.GREEN:
                return descriptorGreen;
            case ColorName.PURPLE:
                return descriptorPurple;
            case ColorName.ORANGE:
                return descriptorOrange;
            default:
                return descriptorBlue;
        }
    }

    public interface ColorName{
        String BLUE = "blue";
        String RED = "red";
        String GREEN = "green";
        String PURPLE = "purple";
        String ORANGE = "orange";
    }
}
