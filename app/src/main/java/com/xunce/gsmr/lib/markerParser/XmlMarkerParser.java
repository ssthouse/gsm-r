package com.xunce.gsmr.lib.markerParser;

import android.content.Context;
import android.util.Log;

import com.xunce.gsmr.kilometerMark.KilometerMarkHolder;
import com.xunce.gsmr.model.MarkerItem;
import com.xunce.gsmr.util.view.ToastHelper;

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
 * XMl文件中的Marker解析
 * Created by ssthouse on 2015/11/17.
 */
public class XmlMarkerParser extends DefaultHandler {
    /**
     * 解析的Xml文件的路径
     */
    private String filePath;
    /**
     * 上下文
     */
    private Context context;

    /**
     * 构造方法
     *
     * @param filePath 文件路径
     */
    public XmlMarkerParser(Context context, String filePath) {
        this.context = context;
        this.filePath = filePath;
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

    /**
     * 设置解析出来的MarkerItem的prjName
     */
    public void saveMarkerItem(String prjName, KilometerMarkHolder kilometerMarkHolder) {
        //必须先加载好了Xml文件
        if (kilometerMarkHolder == null) {
            ToastHelper.show(context, "请先加载cad文件（.xml）");
            return;
        }
        //首先要看数据能不能计算出经纬度--不能的就不添加将marker
        int addCount = 0;
        int failCount = 0;
        for (MarkerItem markerItem : markerItemList) {
            //判断数据是否可用
            if (kilometerMarkHolder.isDataValid(markerItem.getKilometerMark(),
                    markerItem.getSideDirection(), markerItem.getDistanceToRail())) {
                //计算经纬度___第一个是纬度___第二个是经度
                double position[] = kilometerMarkHolder.getPosition(markerItem.getKilometerMark(),
                        markerItem.getSideDirection(), markerItem.getDistanceToRail());
                markerItem.setLongitude(position[1]);
                markerItem.setLatitude(position[0]);
                markerItem.setPrjName(prjName);
                //正式将数据写入数据库
                markerItem.save();
                //计数加一
                addCount++;
            } else {
                failCount++;
            }
        }
        ToastHelper.show(context, addCount + "个标记点添加到当前工程中");
        ToastHelper.show(context, failCount + "个标记点因格式不符无法添加");
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
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        //进入一个Marker的时候___更新数据
        switch (localName) {
            case XmlCons.DEVICE_TYPE:
                markerItem = new MarkerItem();
                markerItem.setDeviceType(attributes.getValue(XmlCons.ATTRIBUTE_VALUE));
                break;
            case XmlCons.KILOMETER_MARK:
                markerItem.setKilometerMark(attributes.getValue(XmlCons.ATTRIBUTE_VALUE));
                break;
            case XmlCons.SIDE_DIRECTION:
                markerItem.setSideDirection(attributes.getValue(XmlCons.ATTRIBUTE_VALUE));
                break;
            case XmlCons.DISTANCE_TO_RAIL:
                markerItem.setDistanceToRail(
                        Double.parseDouble(attributes.getValue(XmlCons.ATTRIBUTE_VALUE)));
                break;
            case XmlCons.COMMENT:
                markerItem.setComment(attributes.getValue(XmlCons.ATTRIBUTE_VALUE));
                break;
            case XmlCons.LATITUDE: {
                String value = attributes.getValue(XmlCons.ATTRIBUTE_VALUE);
                if (value == null || value.isEmpty()) {
                    return;
                }
                markerItem.setLatitude(Double.parseDouble(value));
                break;
            }
            case XmlCons.LONGITUDE: {
                String value = attributes.getValue(XmlCons.ATTRIBUTE_VALUE);
                if (value == null || value.isEmpty()) {
                    return;
                }
                markerItem.setLongitude(Double.parseDouble(value));
                break;
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);
        //结束每一个Marker的节点时保存进List
        if (localName.equals(XmlCons.DEVICE_TYPE)) {
            markerItemList.add(markerItem);
        }
    }

    /**
     * XML文件中的标签常量
     */
    class XmlCons {
        //xml文件中的标签
        public static final String DEVICE_TYPE = "devicetype";
        public static final String KILOMETER_MARK = "kmmark";
        public static final String SIDE_DIRECTION = "lateral";
        public static final String DISTANCE_TO_RAIL = "distanceToRail";
        public static final String LONGITUDE = "longitude";
        public static final String LATITUDE = "latitude";
        public static final String COMMENT = "comment";
        //xml文件中的属性
        public static final String ATTRIBUTE_VALUE = "value";
    }
}
