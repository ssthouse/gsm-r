package com.xunce.gsmr.model.event;

/**
 * 正在压缩文件时间
 * Created by ssthouse on 2015/12/5.
 */
public class CompressFileEvent {

    /**
     * 状态量
     */
    public enum Event{
        BEGIN, END
    }

    /**
     * 当前event的状态
     */
    private Event state;

    public CompressFileEvent(Event event) {
        this.state = event;
    }

    public Event getState() {
        return state;
    }

    public void setState(Event state) {
        this.state = state;
    }
}
