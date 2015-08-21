package com.xunce.gsmr.model.map;

import com.baidu.mapapi.map.BaiduMap;

import java.util.List;

/**
 * 铁路的管理类
 * 1.一条铁路应该是对应的一个数据库中的数据
 * Created by ssthouse on 2015/8/21.
 */
public class RailWay {

    private List<Circle> circles;

    private List<Line> lines;

    private List<Text> texts;

    public RailWay(){
        //读取数据库中的数据
        //TODO---先手动添加一些数据
    }

    /**
     * 画出自己
     */
    public void draw(BaiduMap baiduMap){
        for(Circle circle : circles){
            circle.draw(baiduMap);
        }
        for(Line line :lines){
            line.draw(baiduMap);
        }
        for(Text text : texts){
            text.draw(baiduMap);
        }
    }
}
