package com.xunce.gsmr.lib.cadparser;

import android.content.Context;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.LatLng;
import com.xunce.gsmr.model.gaodemap.graph.Line;
import com.xunce.gsmr.model.gaodemap.graph.Point;
import com.xunce.gsmr.model.gaodemap.graph.Text;
import com.xunce.gsmr.util.LogHelper;
import com.xunce.gsmr.util.gps.PositionUtil;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

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
     * 构造方法
     *
     * @param context
     */
    public XmlParser(Context context) {
        this.context = context;

        try {
            //获取解析器
            SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
            XMLReader xmlReader = saxParser.getXMLReader();
            //设置监听器
            xmlReader.setContentHandler(this);
            xmlReader.setErrorHandler(this);
            //开始解析
            xmlReader.parse(new InputSource(context.getAssets().open("123.xml")));
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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
            // text.draw(aMap);
        }
        for (com.xunce.gsmr.model.gaodemap.graph.Vector vector : vectorList) {
            vector.draw(aMap);
            LogHelper.log(TAG, "我又画出了一个vector");
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
            //获取的是高德地图的latlng
            LatLng latLngStart = PositionUtil.gps84_To_Gcj02(Double.parseDouble(attributes.getValue(LineElement.latStart)),
                    Double.parseDouble(attributes.getValue(LineElement.longStart)));
            LatLng latLngEnd = PositionUtil.gps84_To_Gcj02(Double.parseDouble(attributes.getValue(LineElement.latEnd)),
                    Double.parseDouble(attributes.getValue(LineElement.longEnd)));
            line = new Line(latLngStart, latLngEnd);
            lineList.add(line);
        }
//        LogHelper.log(TAG, "uri:    " + uri
//                + "\n" + "locaName:  " + localName
//                + "\n" + "qname: " + qName);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);
        if(Element.DATA.equals(localName)){
            LogHelper.log(TAG, "我添加了最后一个Vector");
            vectorList.add(vector);
        }
    }

    @Override
    public void startDocument() throws SAXException {
        LogHelper.log(TAG, "我开始解析了...");
        super.startDocument();
    }

    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
        LogHelper.log(TAG, "我解析完毕了...");
//        for (Text text : textList) {
//            LogHelper.log(TAG, text.toString());
//        }
//        for (Line line : lineList) {
//            LogHelper.log(TAG, line.toString());
//        }
//        for (com.xunce.gsmr.model.gaodemap.graph.Vector vector : vectorList) {
//            LogHelper.log(TAG, "我的Point的Size是: " + vector.getPointList().size());
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
}
