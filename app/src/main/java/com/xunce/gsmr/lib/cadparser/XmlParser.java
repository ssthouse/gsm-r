package com.xunce.gsmr.lib.cadparser;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.LatLng;
import com.xunce.gsmr.model.event.ProgressbarEvent;
import com.xunce.gsmr.model.gaodemap.graph.Line;
import com.xunce.gsmr.model.gaodemap.graph.Point;
import com.xunce.gsmr.model.gaodemap.graph.Text;
import com.xunce.gsmr.util.gps.PositionUtil;
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

import de.greenrobot.event.EventBus;
import timber.log.Timber;

/**
 * CAD导出的xml文件的解析
 * 解析出来的数据为:
 * Created by ssthouse on 2015/10/30.
 */
public class XmlParser extends DefaultHandler {
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
     * 当前解析的xml文件的路径
     */
    private String xmlFilePath;

    /**
     * 构造方法
     *
     * @param context
     */
    public XmlParser(final Context context, final String xmlFilePath) {
        this.context = context;
        this.xmlFilePath = xmlFilePath;
        //开启线程执行前显示进度条
        EventBus.getDefault().post(new ProgressbarEvent(true));

        //开一个新线程完成解析任务
        new AsyncTask<Void, Void, Void>(){

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    //获取解析器
                    SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
                    XMLReader xmlReader = saxParser.getXMLReader();
                    //设置监听器
                    xmlReader.setContentHandler(XmlParser.this);
                    xmlReader.setErrorHandler(XmlParser.this);
                    //开始解析
                    xmlReader.parse(new InputSource(new FileInputStream(xmlFilePath)));
                } catch (ParserConfigurationException | SAXException | IOException e) {
                    e.printStackTrace();
                    Timber.e(Log.getStackTraceString(e));
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                EventBus.getDefault().post(new ProgressbarEvent(false));
                ToastHelper.show(context, "Xml文件加载完成");
            }
        }.execute();
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
        }
    }

    /**
     * 仅仅画出文字
     * @param aMap
     */
    public void drawText(AMap aMap){
        for (Text text : textList) {
            text.draw(aMap);
        }
    }

    /**
     * 仅仅画出线段
     * @param aMap
     */
    public void drawLine(AMap aMap){
        for (Line line : lineList) {
            line.draw(aMap);
        }
        for (com.xunce.gsmr.model.gaodemap.graph.Vector vector : vectorList) {
            vector.draw(aMap);
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
     * 隐藏文字
     */
    public void hideText(){
        for (Text text : textList) {
            text.hide();
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
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);
        if (Element.DATA.equals(localName)) {
            Timber.e("我添加了最后一个Vector");
            vectorList.add(vector);
        }
    }

    @Override
    public void startDocument() throws SAXException {
        Timber.e("我开始解析了...");
        super.startDocument();
    }

    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
        Timber.e("我解析完毕了...");
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
