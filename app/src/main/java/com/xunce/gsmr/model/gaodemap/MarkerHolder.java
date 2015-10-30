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
    private List<Marker> markerOnMapList;
    private List<MarkerItem> markerOnDbList;

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
        markerOnMapList = new ArrayList<>();
        markerOnDbList = new ArrayList<>();

        //初始化Marker
        initMarker();
    }

    /**
     * 初始化Marker
     */
    public void initMarker() {
        //清除地图图像---清空marker数据
        for(Marker marker : markerOnMapList){
            marker.setVisible(false);
            marker.remove();
//            LogHelper.log(TAG, "我去除了一个Marker");
        }
        markerOnMapList.clear();
        markerOnDbList.clear();
        currentMarker = null;
        currentMarkerItem = null;
        //填充MarkerList
        markerOnDbList = DBHelper.getMarkerList(prjItem);
        for (int i = 0; i < markerOnDbList.size(); i++) {
            com.amap.api.maps.model.LatLng latLng = markerOnDbList.get(i).getGaodeLatLng();
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.icon(descriptorBlue).position(latLng);
            markerOnMapList.add(aMap.addMarker(markerOptions));
//            LogHelper.log(TAG, "我添加了一个点:    " + latLng.latitude + ":" + latLng.longitude);
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
        for (Marker marker : markerOnMapList) {
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
        this.currentMarkerItem = markerOnDbList.get(markerOnMapList.indexOf(currentMarker));
    }

    public List<Marker> getMarkerOnMapList() {
        return markerOnMapList;
    }

    public void setMarkerOnMapList(List<Marker> markerOnMapList) {
        this.markerOnMapList = markerOnMapList;
    }
}
