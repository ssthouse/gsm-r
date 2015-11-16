package com.xunce.gsmr.lib.cadparser;

import android.content.Context;
import android.util.Log;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.LatLng;
import com.xunce.gsmr.model.gaodemap.graph.Line;
import com.xunce.gsmr.model.gaodemap.graph.Point;
import com.xunce.gsmr.model.gaodemap.graph.Text;
import com.xunce.gsmr.util.L;
import com.xunce.gsmr.util.gps.PositionUtil;

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

/**
 * CAD导出的xml文件的解析
 * 解析出来的数据为:
 * <p/>
 * Created by ssthouse on 2015/10/30.
 */
public class XmlParser extends DefaultHandler {
    private static final String TAG = "XmlParser";

    private Context context;

    /**
     * 用于接收数据的临时变量
     */
    private Line line;
    private Text text;
    private com.xunce.gsmr.model.gaodemap.graph.Vector vector;

    /**
     * 从xml中解析出来的数据
     */
    private List<Line> lineList = new ArrayList<>();
    private List<Text> textList = new ArrayList<>();
    private List<com.xunce.gsmr.model.gaodemap.graph.Vector> vectorList = new ArrayList<>();

    /**
     * 唯一的XmlParser单例
     */
    private static XmlParser xmlParser;
    /**
     * 当前解析的xml文件的路径
     */
    private String xmlFilePath;

    /**
     * @param context
     * @return
     */
    public static XmlParser loadXmlParser(Context context, String xmlFilePath) {
        //如果之前有加载文件---去除内存
        if(xmlParser != null){
            xmlParser.destory();
        }
        //如果xmlParser是空的---或者路径改了---就创建新的xmlParser
        xmlParser = new XmlParser(context, xmlFilePath);
        return xmlParser;
    }

    /**
     * 获取XmlParser
     *
     * @return
     */
    public static XmlParser getXmlParser() {
        return xmlParser;
    }

    /**
     * 构造方法
     *
     * @param context
     */
    private XmlParser(Context context, String xmlFilePath) {
        this.context = context;
        this.xmlFilePath = xmlFilePath;

        try {
            //获取解析器
            SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
            XMLReader xmlReader = saxParser.getXMLReader();
            //设置监听器
            xmlReader.setContentHandler(this);
            xmlReader.setErrorHandler(this);
            //开始解析
            xmlReader.parse(new InputSource(new FileInputStream(xmlFilePath)));
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
            L.log(TAG, Log.getStackTraceString(e));
        }
    }

    /**
     * XmlParser是不是空的
     *
     * @return
     */
    public static boolean isXmlParserEmpty() {
        if (xmlParser == null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 将解析出来的数据画出来
     */
    public void draw(AMap aMap) {
        for (Line line : lineList) {
            line.draw(aMap);
        }
        for (Text text : textList) {
            text.draw(aMap);
        }
        for (com.xunce.gsmr.model.gaodemap.graph.Vector vector : vectorList) {
            vector.draw(aMap);
            //L.log(TAG, "我又画出了一个vector");
        }
    }

    /**
     * 将画好的图像隐藏
     */
    public void hide() {
        for (Line line : lineList) {
            line.hide();
        }
        for (Text text : textList) {
            text.hide();
        }
        for (com.xunce.gsmr.model.gaodemap.graph.Vector vector : vectorList) {
            vector.hide();
        }
    }

    /**
     * 销毁
     */
    public void destory(){
        for (Line line : lineList) {
            line.destory();
        }
        for (Text text : textList) {
            text.hide();
        }
        for (com.xunce.gsmr.model.gaodemap.graph.Vector vector : vectorList) {
            vector.hide();
        }
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        if (Element.LINE.equals(localName)) {
            //获取的是高德地图的latlng
            LatLng latLngStart = PositionUtil.gps84_To_Gcj02(Double.parseDouble(attributes.getValue(LineElement.latStart)),
                    Double.parseDouble(attributes.getValue(LineElement.longStart)));
            LatLng latLngEnd = PositionUtil.gps84_To_Gcj02(Double.parseDouble(attributes.getValue(LineElement.latEnd)),
                    Double.parseDouble(attributes.getValue(LineElement.longEnd)));
            line = new Line(latLngStart, latLngEnd);
            lineList.add(line);
        } else if (Element.TEXT.equals(localName)) {
            LatLng latLng = PositionUtil.gps84_To_Gcj02(Double.parseDouble(attributes.getValue(TextElement.latitude)),
                    Double.parseDouble(attributes.getValue(TextElement.longitude)));
            text = new Text(latLng, attributes.getValue(TextElement.value));
            textList.add(text);
        } else if (Element.P2DPOLY.equals(localName)) {
            //判断order是0的话---要把前面的数据放进去
            int order = Integer.parseInt(attributes.getValue(Vector.order));
            if (order == 0) {
                if (vector != null && vector.getPointList().size() != 0) {
                    vectorList.add(vector);
                }
                vector = new com.xunce.gsmr.model.gaodemap.graph.Vector("");
            } else {
                vector.getPointList().add(new Point(Double.parseDouble(attributes.getValue(Vector.longitude)),
                        Double.parseDouble(attributes.getValue(Vector.latitude))));
            }
        } else if (Element.POLY.equals(localName)) {
//            //获取的是高德地图的latlng
//            LatLng latLngStart = PositionUtil.gps84_To_Gcj02(Double.parseDouble(attributes.getValue(LineElement.latStart)),
//                    Double.parseDouble(attributes.getValue(LineElement.longStart)));
//            LatLng latLngEnd = PositionUtil.gps84_To_Gcj02(Double.parseDouble(attributes.getValue(LineElement.latEnd)),
//                    Double.parseDouble(attributes.getValue(LineElement.longEnd)));
//            line = new Line(latLngStart, latLngEnd);
//            lineList.add(line);
            //判断order是0的话---要把前面的数据放进去
            int order = Integer.parseInt(attributes.getValue(Vector.order));
            if (order == 0) {
                if (vector != null && vector.getPointList().size() != 0) {
                    vectorList.add(vector);
                }
                vector = new com.xunce.gsmr.model.gaodemap.graph.Vector("");
            } else {
                vector.getPointList().add(new Point(Double.parseDouble(attributes.getValue(Vector.longitude)),
                        Double.parseDouble(attributes.getValue(Vector.latitude))));
            }
        }
//        L.log(TAG, "uri:    " + uri
//                + "\n" + "locaName:  " + localName
//                + "\n" + "qname: " + qName);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);
        if (Element.DATA.equals(localName)) {
            L.log(TAG, "我添加了最后一个Vector");
            vectorList.add(vector);
        }
    }

    @Override
    public void startDocument() throws SAXException {
        L.log(TAG, "我开始解析了...");
        super.startDocument();
    }

    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
        L.log(TAG, "我解析完毕了...");
//        for (Text text : textList) {
//            L.log(TAG, text.toString());
//        }
//        for (Line line : lineList) {
//            L.log(TAG, line.toString());
//        }
//        for (com.xunce.gsmr.model.gaodemap.graph.Vector vector : vectorList) {
//            L.log(TAG, "我的Point的Size是: " + vector.getPointList().size());
//        }
    }

    class Element {
        static final String DATA = "Data";
        static final String LINE = "LINE";
        static final String POLY = "POLY";
        static final String TEXT = "TEXT";
        static final String P2DPOLY = "P2DPOLY";
    }

    class LineElement {
        static final String longStart = "longitude_start";
        static final String latStart = "latitude_start";
        static final String longEnd = "longitude_end";
        static final String latEnd = "latitude_end";
    }

    class PolyElement {
        static final String longStart = "longitude_start";
        static final String latStart = "latitude_start";
        static final String longEnd = "longitude_end";
        static final String latEnd = "latitude_end";
    }

    class TextElement {
        static final String longitude = "longitude";
        static final String latitude = "latitude";
        static final String value = "value";
    }

    class Vector {
        static final String longitude = "longitude";
        static final String latitude = "latitude";
        static final String order = "order";
    }

    //getter----and-----setter----------------------------------

    public String getXmlFilePath() {
        return xmlFilePath;
    }

    public void setXmlFilePath(String xmlFilePath) {
        this.xmlFilePath = xmlFilePath;
    }
}
