package com.xunce.gsmr.lib;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Environment;

import com.amap.api.maps.AMap;
import com.xunce.gsmr.model.gaodemap.graph.Point;
import com.xunce.gsmr.model.gaodemap.graph.Text;
import com.xunce.gsmr.model.gaodemap.graph.Vector;
import com.xunce.gsmr.util.LogHelper;
import com.xunce.gsmr.util.gps.PositionUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 将所有的数据库数据读出来-----然后画在地图上
 * Created by ssthouse on 2015/10/14.
 */
public class DigitalMapHolder {
    private static final String TAG = "DigitalMapHelper";

    //数据库地址
    private String dbPath;

    private Context context;

    private AMap aMap;

    //TODO---测试高德地图的文字显示
    private List<Text> gaodeTextList = new ArrayList<>();
    private List<Vector> vectorList = new ArrayList<>();

    /**
     * 传入数据库地址的构造方法
     *
     * @param dbPath
     */
    public DigitalMapHolder(final Context context, final String dbPath, final AMap aMap) {
        this.dbPath = dbPath;
        this.context = context;
        this.aMap = aMap;

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                //打开数据库
                SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(new File(dbPath), null);

                //读取数据库里面所有的-----Text
                Cursor cursor = database.rawQuery("SELECT * FROM TextPoint", null);
                while (cursor.moveToNext()) {
//                    LogHelper.log(TAG, cursor.getString(2) + " : " + cursor.getDouble(0) + " : " + cursor.getDouble(1));
                    Text text = new Text(PositionUtil.gps84_To_Gcj02(cursor.getDouble(1), cursor.getDouble(0)),
                            cursor.getString(2));
                    //提前初始化好数据
                    gaodeTextList.add(text);
                }
                cursor.close();

                //读取所有的Vector的数据(数据库的表结构发生了变化---需要重新写)
//                {
//                    Cursor cursorVector = database.rawQuery("SELECT * FROM Vector", null);
//                    while (cursorVector.moveToNext()) {
//                        //创建一个新的Vector
//                        int vectorId = cursorVector.getInt(1);
//                        String vectorName = cursorVector.getString(0);
//                        Vector vector = new Vector(vectorName);
//                        //读取数据库中对应的Point数据
//                        Cursor textCursor = database.rawQuery("SELECT * FROM Point WHERE vactorID=" + "'" + vectorId + "'", null);
//                        while (textCursor.moveToNext()) {
//                            vector.getPointList().add(new Point(textCursor.getDouble(0), textCursor.getDouble(1)));
//                        }
//                        LogHelper.log(TAG, "我还在运行");
//                        vectorList.add(vector);
//                        textCursor.close();
//                    }
//                }

                //现在读取数据库中的数据快多了
                Cursor cursorVector = database.rawQuery("SELECT * FROM Vector", null);
                Vector vector = null;
                while (cursorVector.moveToNext()) {
                    //获取改点在Vector中的位置
                    int orderInVector = cursorVector.getInt(3);
                    //如果是0----先把已经保存下来的数据放进去---然后创建一个新的Vector
                    if (orderInVector == 0) {
                        //保存已有数据
                        if (vector != null && vector.getPointList().size() != 0) {
                            vectorList.add(vector);
                            //初始化Vector中的画图数据
                            vector.initPolylineOptions();
                        }
                        //更新vector
                        vector = new Vector(cursorVector.getString(0));
                        //LogHelper.log(TAG, "我新建了个Vector"+vector.getName());
                    }
                    vector.getPointList().add(new Point(cursorVector.getDouble(1), cursorVector.getDouble(2)));
                }
                cursorVector.close();
                return null;
            }
        }.execute();
    }

    /**
     * 在高德地图上画出当前的数据地图数据
     * 因为画的比较忙----性能问题--在开始时显示一个Dialog
     * 结束时---取消Dialog
     */
    public void draw() {
        //画出文字信息
        for (Text text : gaodeTextList) {
            text.draw(aMap);
        }
        //画出所有的Vector
        for (int i = 0; i < vectorList.size(); i++) {
            vectorList.get(i).draw(aMap);
            LogHelper.log(TAG, "我画出了一个Vector:   " + i);
        }
    }


    /**
     * 将数据库文件从asset中复制到sd卡中
     * 然后把复制好的文件返回
     */
    private File readAssetFileToSdcard(Context context) {
        try {
            //首先获取数据库文件输入流
            InputStream is = context.getAssets().open("test.db");

            //在sd卡中先生成好要存放的文件
            File file = new File(Environment.getExternalStorageDirectory() + "/GSM/tempDatabase/test.db");
            LogHelper.log(TAG, "路径是:    " + file.getAbsolutePath());
            if (!file.exists()) {
                file.getParentFile().mkdirs();
            }

            //将文件输入流写到出处文件中去
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            byte[] buffer = new byte[512];
            int count = 0;
            while ((count = is.read(buffer)) > 0) {
                fileOutputStream.write(buffer, 0, count);
            }
            fileOutputStream.flush();
            fileOutputStream.close();
            is.close();
            //最后将保存好的文件返回
            return file;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
