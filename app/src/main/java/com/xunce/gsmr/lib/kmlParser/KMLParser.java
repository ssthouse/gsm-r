package com.xunce.gsmr.lib.kmlParser;

import android.util.Log;

import com.amap.api.maps.AMap;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import timber.log.Timber;

/**
 * KML文件加载器
 * Created by ssthouse on 2015/11/3.
 */
public class KMLParser extends DefaultHandler {
    private KmlData mydata = new KmlData();
    private List<KmlData> polyList = new ArrayList<>();
    private List<KmlData> textList = new ArrayList<>();
    private String qname = null;

    /**
     * 构造方法
     *
     * @param path
     */
    public KMLParser(String path) {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(true);//设定该解析器工厂支持名称空间
            SAXParser saxParser = factory.newSAXParser();
            saxParser.parse(new File(path), this);
        } catch (Exception e) {
            Timber.e(Log.getStackTraceString(e));
        }
    }

    public void draw(AMap amap){
        for(KmlData data : textList){
            data.draw(amap);
        }
        for(KmlData data : polyList){
            data.draw(amap);
        }
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) {
        qname = qName;
        if (qName.equals("Placemark")) {
            mydata = new KmlData();
        }
    }

    public void characters(char[] ch, int start, int length) {
        String text = new String(ch, start, length);
        if (qname.equals("name"))//如果标记间的数据为文本数据
        {
            String str = text.trim();
            if (str.length() > 0) {
                mydata.setName(str);
            }
        } else if (qname.equals("longitude")) {
            String str = text.trim();
            if (str.length() > 0) {
                mydata.setLongitude(str);
            }
        } else if (qname.equals("latitude")) {
            String str = text.trim();
            if (str.length() > 0) {
                mydata.setLatitude(str);
            }
        } else if (qname.equals("coordinates")) {
            String str = text.trim();
            if (str.length() > 0) {
                String strs[] = str.split(",");
                double longitude = Double.parseDouble(strs[0]);
                double latitude = Double.parseDouble(strs[1]);
                if(longitude>0.0 && latitude>0) {
                    mydata.getPointList().add(new PolyCoordinates(longitude, latitude));
                }
            }
        } else if (qname.equals("styleUrl")) {
            String str = text.trim();
            if (str.length() > 0) {
                mydata.setStyleUrl(str);
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        if (qName.equals("Placemark")) {
            if (mydata.getName() != null && mydata.getLatitude() != null && mydata.getLongitude() != null) {
                if (mydata.getStyleUrl().equals("#polystyle")) {
                    polyList.add(mydata);
                } else {
                    textList.add(mydata);
                }
            }
        }
    }

    @Override
    public void endDocument() throws SAXException {
//        for (KmlData data : polyList) {
//            Timber.e("poly:\t" + data.toString());
//        }
//        for (KmlData data : textList) {
//            Timber.e("text:\t" + data.toString());
//        }
        super.endDocument();
    }
}

