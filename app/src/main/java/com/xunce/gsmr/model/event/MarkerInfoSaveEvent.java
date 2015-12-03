package com.xunce.gsmr.model.event;

import com.xunce.gsmr.model.MarkerItem;

/**
 * MarkerInfo编辑保存的Event
 * Created by ssthouse on 2015/12/3.
 */
public class MarkerInfoSaveEvent {

    private MarkerItem markerItem;

    public MarkerInfoSaveEvent(MarkerItem markerItem) {
        this.markerItem = markerItem;
    }

    public MarkerItem getMarkerItem() {
        return markerItem;
    }

    public void setMarkerItem(MarkerItem markerItem) {
        this.markerItem = markerItem;
    }
}
