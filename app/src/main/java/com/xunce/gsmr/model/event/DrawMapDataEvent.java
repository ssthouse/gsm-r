package com.xunce.gsmr.model.event;

import com.xunce.gsmr.lib.xmlparser.XmlParser;
import com.xunce.gsmr.lib.digitalmap.DigitalMapHolder;
import com.xunce.gsmr.model.gaodemap.GaodeRailWayHolder;

/**
 * Created by ssthouse on 2015/12/8.
 */
public class DrawMapDataEvent {

    private GaodeRailWayHolder railWayHolder;

    public DrawMapDataEvent(GaodeRailWayHolder railWayHolder) {
        this.railWayHolder = railWayHolder;
    }

    public GaodeRailWayHolder getRailWayHolder() {
        return railWayHolder;
    }

    public void setRailWayHolder(GaodeRailWayHolder railWayHolder) {
        this.railWayHolder = railWayHolder;
    }

    //    private DigitalMapHolder digitalMapHolder;
//    private XmlParser xmlParser;
//
//    public DrawMapDataEvent(DigitalMapHolder digitalMapHolder, XmlParser xmlParser) {
//        this.digitalMapHolder = digitalMapHolder;
//        this.xmlParser = xmlParser;
//    }
//
//    public DigitalMapHolder getDigitalMapHolder() {
//        return digitalMapHolder;
//    }
//
//    public void setDigitalMapHolder(DigitalMapHolder digitalMapHolder) {
//        this.digitalMapHolder = digitalMapHolder;
//    }
//
//    public XmlParser getXmlParser() {
//        return xmlParser;
//    }
//
//    public void setXmlParser(XmlParser xmlParser) {
//        this.xmlParser = xmlParser;
//    }


}
