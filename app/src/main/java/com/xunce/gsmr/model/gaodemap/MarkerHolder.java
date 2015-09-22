package com.xunce.gsmr.model.gaodemap;

import android.content.Context;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.xunce.gsmr.R;
import com.xunce.gsmr.model.MarkerItem;
import com.xunce.gsmr.model.PrjItem;
import com.xunce.gsmr.util.LogHelper;
import com.xunce.gsmr.util.gps.DBHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * 持有一个AMap的引用
 * 承载PrjEditActivity中的所有Marker
 * Created by ssthouse on 2015/9/15.
 */
public class MarkerHolder {
    private static final String TAG = "MarkerHolder";

    //标记点相关的
    public static BitmapDescriptor descriptorBlue = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_measure_blue);
    public static BitmapDescriptor descriptorRed = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_measure_red);

    /**
     * 上下文
     */
    private Context context;

    /**
     * 地图--主要数据
     */
    private AMap aMap;
    private PrjItem prjItem;

    /**
     * Marker控制
     */
    private Marker currentMarker;
    private MarkerItem currentMarkerItem;
    private List<Marker> markerList;
    private List<MarkerItem> markerItemList;

    /**
     * 构造方法
     *
     * @param prjItem
     * @param aMap
     */
    public MarkerHolder(Context context, PrjItem prjItem, AMap aMap) {
        this.context = context;
        this.prjItem = prjItem;
        this.aMap = aMap;
        markerList = new ArrayList<>();
        markerItemList = new ArrayList<>();

        //初始化MarkerList数据
        initMarkerList();
    }

    /**
     * 初始化MarkerList数据
     */
    private void initMarkerList() {
        //清除地图图像---清空marker数据
//        aMap.clear();
        for(Marker marker : markerList){
            marker.remove();
        }
        markerList.clear();
        markerItemList.clear();
        currentMarker = null;
        currentMarkerItem = null;
        //填充MarkerList
        markerItemList = DBHelper.getMarkerList(prjItem);
        for (int i = 0; i < markerItemList.size(); i++) {
            com.amap.api.maps.model.LatLng latLng = markerItemList.get(i).getGaodeLatLng();
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.icon(descriptorBlue)
                    .position(latLng)
                    .title("hahaha");
            markerList.add(aMap.addMarker(markerOptions));
            LogHelper.Log(TAG, "我添加了一个点:    " + latLng.latitude + ":" + latLng.longitude);
        }
    }

    /**
     * 取消当前选中的Marker
     */
    public void clearSelection() {
        setAll2Blue();
        //没有当前选中的Marker
        currentMarker = null;
    }

    /**
     * 全部图标变为蓝色
     */
    public void setAll2Blue() {
        for (Marker marker : markerList) {
            if (marker != null) {
                marker.setIcon(descriptorBlue);
            }
        }
    }

    //getter---and--setter--------------------------------------------
    public Marker getCurrentMarker() {
        return currentMarker;
    }

    public MarkerItem getCurrentMarkerItem() {
        return currentMarkerItem;
    }

    public void setCurrentMarker(Marker currentMarker) {
        this.currentMarker = currentMarker;
        this.currentMarkerItem = markerItemList.get(markerList.indexOf(currentMarker));
    }

    public List<Marker> getMarkerList() {
        return markerList;
    }

    public void setMarkerList(List<Marker> markerList) {
        this.markerList = markerList;
    }
}
