package com.xunce.gsmr.model.gaodemap;

import android.content.Context;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.xunce.gsmr.R;
import com.xunce.gsmr.model.MarkerIconCons;
import com.xunce.gsmr.model.MarkerItem;
import com.xunce.gsmr.model.PrjItem;
import com.xunce.gsmr.util.DBHelper;
import com.xunce.gsmr.util.preference.PreferenceHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * 持有一个AMap的引用
 * 承载PrjEditActivity中的所有Marker
 * Created by ssthouse on 2015/9/15.
 */
public class MarkerHolder {
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
        for (Marker marker : markerOnMapList) {
            marker.setVisible(false);
            marker.remove();
        }
        markerOnMapList.clear();
        markerOnDbList.clear();
        currentMarker = null;
        currentMarkerItem = null;
        //填充MarkerList
        markerOnDbList = DBHelper.getMarkerList(prjItem);
        for (int i = 0; i < markerOnDbList.size(); i++) {
            //获取latLng
            com.amap.api.maps.model.LatLng latLng = markerOnDbList.get(i).getGaodeLatLng();
            //获取bitmapdescriptor
            BitmapDescriptor bitmapDescriptor;
            String deviceType = markerOnDbList.get(i).getDeviceType();
            String colorStr = PreferenceHelper.getInstance(context).getMarkerColorName(deviceType);
            switch (colorStr){
                case MarkerIconCons.ColorName.BLUE:
                    bitmapDescriptor = BitmapDescriptorFactory
                            .fromResource(R.drawable.icon_marker_blue);
                    break;
                case MarkerIconCons.ColorName.GREEN:
                    bitmapDescriptor = BitmapDescriptorFactory
                            .fromResource(R.drawable.icon_marker_green);
                    break;
                case MarkerIconCons.ColorName.ORANGE:
                    bitmapDescriptor =BitmapDescriptorFactory
                            .fromResource(R.drawable.icon_marker_orange);
                    break;
                case MarkerIconCons.ColorName.PURPLE:
                    bitmapDescriptor = BitmapDescriptorFactory
                            .fromResource(R.drawable.icon_marker_purple);
                    break;
                case MarkerIconCons.ColorName.RED:
                    bitmapDescriptor = BitmapDescriptorFactory
                            .fromResource(R.drawable.icon_marker_red);
                    break;
                default:
                    bitmapDescriptor = BitmapDescriptorFactory
                            .fromResource(R.drawable.icon_marker_blue);
                    break;
            }
            //生成markerOptions
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.icon(bitmapDescriptor)
                    .position(latLng)
                    .title("");
            //正式添加marker
            markerOnMapList.add(aMap.addMarker(markerOptions));
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
                marker.setIcon(MarkerIconCons.descriptorRed);
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
