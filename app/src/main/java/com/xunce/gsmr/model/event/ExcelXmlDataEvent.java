package com.xunce.gsmr.model.event;

/**
 * 用于描述excel的xml文件中的数据加载情况
 * Created by ssthouse on 2015/11/27.
 */
public class ExcelXmlDataEvent {

    /**
     * 是否解析成功
     */
    private boolean parseSuccess;

    public ExcelXmlDataEvent(boolean parseSuccess) {
        this.parseSuccess = parseSuccess;
    }

    public boolean isParseSuccess() {
        return parseSuccess;
    }

    public void setParseSuccess(boolean parseSuccess) {
        this.parseSuccess = parseSuccess;
    }
}
