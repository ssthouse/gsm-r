package com.xunce.gsmr.lib;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.LatLng;
import com.xunce.gsmr.model.gaodemap.graph.Point;
import com.xunce.gsmr.model.gaodemap.graph.Text;
import com.xunce.gsmr.model.gaodemap.graph.Vector;
import com.xunce.gsmr.util.LogHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 将所有的数据库数据读出来-----然后画在地图上
 * Created by ssthouse on 2015/10/14.
 */
public class DigitalMapHolder {
    private static final String TAG = "DigitalMapHelper";


    //这些是---操作从外部接收的数据库文件的方法
    public static final String EXTERNAL_DB_PATH = "/storage/sdcard0/GSM/DataBase/";
    public static final String TEMP_DB_PATH = "storage/sdcard0/ssthouse/test.db";

    //数据库地址
    private String dbPath;

    private Context context;

    //TODO---测试高德地图的文字显示
    private List<Text> gaodeTextList = new ArrayList<>();
    private List<Vector> vectorList = new ArrayList<>();

    /**
     * 传入数据库地址的构造方法
     *
     * @param dbPath
     */
    public DigitalMapHolder(Context context, final String dbPath) {
        this.dbPath = dbPath;
        this.context = context;

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                //首先打开数据库
                SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(new File(dbPath), null);

                //读取数据库里面所有的-----Text
                Cursor cursor = database.rawQuery("SELECT * FROM TextPoint", null);
                while (cursor.moveToNext()) {
                    LogHelper.Log(TAG, cursor.getString(2) + " : " + cursor.getDouble(0) + " : " + cursor.getDouble(1));
                    gaodeTextList.add(new Text(new LatLng(cursor.getLong(1), cursor.getDouble(0)),
                            cursor.getString(2)));
                }

                //读取所有的Vector的数据
                Cursor cursorVector = database.rawQuery("SELECT * FROM Vector", null);
                while (cursorVector.moveToNext()) {
                    //创建一个新的Vector
                    int vectorId = cursorVector.getInt(1);
                    String vectorName = cursorVector.getString(0);
                    Vector vector = new Vector(vectorName);

                    //读取数据库中对应的Point数据
                    Cursor textCursor = database.rawQuery("SELECT * FROM Point WHERE vactorID=" + "'" + vectorId + "'", null);
                    while (textCursor.moveToNext()) {
                        vector.getPointList().add(new Point(textCursor.getDouble(1), textCursor.getDouble(0)));
                    }
                    LogHelper.Log(TAG, "我还在运行");
                    vectorList.add(vector);
                    textCursor.close();
                }

                cursorVector.close();
                return null;
            }
        }.execute();
    }

    /**
     * 在高德地图上画出当前的数据地图数据
     *
     * @param aMap
     */
    public void draw(AMap aMap) {
        //画出文字信息
        for (Text text : gaodeTextList) {
            text.draw(aMap);
        }

        //画出所有的Vector
        for (int i = 0; i < vectorList.size(); i++) {
            vectorList.get(i).draw(aMap);
            LogHelper.Log(TAG, "我画出了一个Vector:   " + i);
        }
    }
}
