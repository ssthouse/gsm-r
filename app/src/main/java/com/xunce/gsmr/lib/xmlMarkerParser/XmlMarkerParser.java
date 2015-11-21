package com.xunce.gsmr.lib.xmlMarkerParser;

import android.util.Log;

import com.xunce.gsmr.model.MarkerItem;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import timber.log.Timber;

/**
 * 单例
 * XMl文件中的Marker解析
 * Created by ssthouse on 2015/11/17.
 */
public class XmlMarkerParser extends DefaultHandler {
    /**
     * 单例
     */
    private static XmlMarkerParser xmlMarkerParser;

    /**
     * 解析的Xml文件的路径
     */
    private String filePath;

    /**
     * 初始化___填入文件路径
     */
    public void initXmlMarkerParser(String path) {
        this.filePath = path;
    }

    /**
     * 构造方法
     *
     * @param filePath 文件路径
     */
    private XmlMarkerParser(String filePath) {
        this.filePath = filePath;
    }

    /**
     * 获取唯一的单例
     */
    public static XmlMarkerParser getInstance(String filePath) {
        if (xmlMarkerParser == null) {
            xmlMarkerParser = new XmlMarkerParser(filePath);
        }
        return xmlMarkerParser;
    }

    /**
     * 临时变量___用于保存解析时获取到的数据
     */
    private MarkerItem markerItem;
    /**
     * 保存所有解析出来的数据
     */
    private List<MarkerItem> markerItemList = new ArrayList<>();

    /**
     * 解析文件
     */
    public void parse() {
        try {
            //获取解析器
            SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
            XMLReader xmlReader = saxParser.getXMLReader();
            //设置监听器
            xmlReader.setContentHandler(this);
            xmlReader.setErrorHandler(this);
            //开始解析
            xmlReader.parse(new InputSource(new FileInputStream(filePath)));
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
            Timber.e(Log.getStackTraceString(e));
        }
    }

    @Override
    public void startDocument() throws SAXException {
        Timber.e("开始解析文档");
        super.startDocument();
    }

    @Override
    public void endDocument() throws SAXException {
        Timber.e("结束解析文档");
        super.endDocument();
        //打印获取到的数据
//        for (MarkerItem item : markerItemList) {
//            Timber.e("deviceType:\t" + item.getDeviceType() + "\n"
//                    + "kilomnaterMark:\t" + item.getKilometerMark() + "\n"
//                    + "sideDirection:\t" + item.getSideDirection() + "\n"
//                    + "latitude:\t" + item.getLatitude() + "\n"
//                    + "longitude:\t" + item.getLongitude() + "\n");
//        }
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        //进入一个Marker的时候___更新数据
        if (localName.equals(XmlCons.DAVICE_TYPE)) {
            markerItem = new MarkerItem();
            markerItem.setDeviceType(attributes.getValue(XmlCons.ATTRIBUTE_VALUE));
            return;
        } else if (localName.equals(XmlCons.KILOMETER_MARK)) {
            markerItem.setKilometerMark(attributes.getValue(XmlCons.ATTRIBUTE_VALUE));
        } else if (localName.equals(XmlCons.SIDE_DIRECTION)) {
            markerItem.setSideDirection(attributes.getValue(XmlCons.ATTRIBUTE_VALUE));
        } else if (localName.equals(XmlCons.LATITUDE)) {
            String value = attributes.getValue(XmlCons.ATTRIBUTE_VALUE);
            if(value == null || value.isEmpty()){
                return;
            }
            markerItem.setLatitude(Double.parseDouble(value));
        } else if (localName.equals(XmlCons.LONGITUDE)) {
            String value = attributes.getValue(XmlCons.ATTRIBUTE_VALUE);
            if(value == null || value.isEmpty()){
                return;
            }
            markerItem.setLongitude(Double.parseDouble(value));
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);
        //结束每一个Marker的节点时保存进List
        if (localName.equals(XmlCons.DAVICE_TYPE)) {
            markerItemList.add(markerItem);
        }
    }

    /**
     * XML文件中的标签常量
     */
    class XmlCons {
        //xml文件中的标签
        public static final String DAVICE_TYPE = "devicetype";
        public static final String KILOMETER_MARK = "kmmark";
        public static final String SIDE_DIRECTION = "lateral";
        public static final String LONGITUDE = "longtitude";
        public static final String LATITUDE = "latitude";
        //xml文件中的属性
        public static final String ATTRIBUTE_VALUE = "value";
    }
}
