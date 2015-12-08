package com.xunce.gsmr.model.event;

import com.xunce.gsmr.lib.cadparser.XmlParser;
import com.xunce.gsmr.lib.digitalmap.DigitalMapHolder;

/**
 * Created by ssthouse on 2015/12/8.
 */
public class DrawMapDataEvent {

    private DigitalMapHolder digitalMapHolder;
    private XmlParser xmlParser;

    public DrawMapDataEvent(DigitalMapHolder digitalMapHolder, XmlParser xmlParser) {
        this.digitalMapHolder = digitalMapHolder;
        this.xmlParser = xmlParser;
    }

    public DigitalMapHolder getDigitalMapHolder() {
        return digitalMapHolder;
    }

    public void setDigitalMapHolder(DigitalMapHolder digitalMapHolder) {
        this.digitalMapHolder = digitalMapHolder;
    }

    public XmlParser getXmlParser() {
        return xmlParser;
    }

    public void setXmlParser(XmlParser xmlParser) {
        this.xmlParser = xmlParser;
    }
}
