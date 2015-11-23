package com.xunce.gsmr.model.event;

/**
 * 描述PrjEditActivity的返回状态
 * Created by ssthouse on 2015/11/23.
 */
public class MarkerEditEvent {

    /**
     * 描述返回的状态
     */
    public enum BackState{
        CHANGED, UNCHANGED
    }

    /**
     * 当前event的返回状态
     */
    private BackState backState;

    /**
     * 传入一个状态量的构造方法
     * @param backState
     */
    public MarkerEditEvent(BackState backState) {
        this.backState = backState;
    }

    public BackState getBackState() {
        return backState;
    }

    public void setBackState(BackState backState) {
        this.backState = backState;
    }
}
