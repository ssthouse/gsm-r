package com.xunce.gsmr.model.event;

/**
 * PrjEditActivity接收到的消息
 * Created by ssthouse on 2015/9/13.
 */
public class PrjEditActivityEvent {

    public EventType eventType;

    public PrjEditActivityEvent(EventType eventType){
        this.eventType = eventType;
    }

    public enum EventType{
        MEASURE,
    }
}
