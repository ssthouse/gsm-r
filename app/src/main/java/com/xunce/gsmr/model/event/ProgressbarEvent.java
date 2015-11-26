package com.xunce.gsmr.model.event;

/**
 * 控制prjEditActivity主界面的progressbar是否显示的event
 * Created by ssthouse on 2015/11/26.
 */
public class ProgressbarEvent {

    /**
     * 是否显示progressbar
     */
    private boolean show;

    public ProgressbarEvent(boolean show) {
        this.show = show;
    }

    public boolean isShow() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }
}
