package com.xunce.gsmr.model;

import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.xunce.gsmr.R;

/**
 * 将需要多次放在内存中的数据保存一份
 * Created by ssthouse on 2015/11/22.
 */
public class MarkerCons {

    //标记点相关的
    public static BitmapDescriptor descriptorBlue = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_measure_blue);
    public static BitmapDescriptor descriptorRed = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_measure_red);
}
