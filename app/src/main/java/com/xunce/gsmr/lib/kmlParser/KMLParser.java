package com.xunce.gsmr.lib.kmlParser;

import android.util.Log;

import com.xunce.gsmr.util.L;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * KML文件加载器
 * Created by ssthouse on 2015/11/3.
 */
public class KMLParser extends DefaultHandler
{
    private static final String TAG = "KMLLParser";
    data mydata = new data();
    ArrayList<data> datalist = new ArrayList<>();
    String qname = null;

    /**
     * 构造方法
     * @param path
     */
    public KMLParser(String path){
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(true);//设定该解析器工厂支持名称空间
            SAXParser saxParser = factory.newSAXParser();
            saxParser.parse(new File(path), this);
        }catch (Exception e){
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) {
        qname = qName;

        if (qName.equals("Placemark")) {
            mydata = new data();
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
                mydata.setLongtitude(str);
            }
        } else if (qname.equals("latitude")) {
            String str = text.trim();
            if (str.length() > 0) {
                mydata.setLatitude(str);
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        if (qName.equals("Placemark")) {
            if (mydata.getName() != null && mydata.getLatitude() != null && mydata.getLongtitude() != null) {
                datalist.add(mydata);
            }
        }
        L.log(TAG, "我解析出来了: " + datalist.size() + " 条数据");
    }

}

class data {
    private String name;
    private String longtitude;
    private String latitude;

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(String longtitude) {
        this.longtitude = longtitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}