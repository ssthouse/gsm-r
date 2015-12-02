package com.xunce.gsmr.model.event;

/**
 * 用于表示MarekrIcon是否发生变化的Event
 * Created by ssthouse on 2015/12/2.
 */
public class MarkerIconChangeEvent {
    private boolean isChanged;

    public MarkerIconChangeEvent(boolean isChanged) {
        this.isChanged = isChanged;
    }

    public boolean isChanged() {
        return isChanged;
    }

    public void setChanged(boolean changed) {
        isChanged = changed;
    }
}
