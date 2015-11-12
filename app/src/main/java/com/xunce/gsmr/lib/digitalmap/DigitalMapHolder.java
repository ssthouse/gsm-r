package com.xunce.gsmr.lib.digitalmap;

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
    //context
    private Context context;
    //高德地图
    private AMap aMap;

    //高德地图的文字显示数据
    private List<Text> textList = new ArrayList<>();
    private List<Vector> vectorList = new ArrayList<>();

    /**
     * 唯一的单例
     */
    private static DigitalMapHolder digitalMapHolder;

    /**
     * 加载数字文件
     * @param context
     * @param dbPath
     * @param aMap
     */
    public static void loadDigitalMapHolder(Context context, String dbPath, AMap aMap) {
        //如果之前有加载过文件----清除内存
        if(digitalMapHolder != null){
            digitalMapHolder.destory();
        }
        digitalMapHolder = new DigitalMapHolder(context, dbPath, aMap);
    }

    /**
     * 获取DigitalHolder
     * @return
     */
    public static DigitalMapHolder getDigitalMapHolder() {
        if (digitalMapHolder != null) {
            return digitalMapHolder;
        }
        return null;
    }

    /**
     * 是不是空的
     * @return
     */
    public static boolean isEmpty() {
        return digitalMapHolder == null;
    }

    /**
     * 传入数据库地址的构造方法
     *
     * @param dbPath
     */
    private DigitalMapHolder(final Context context, final String dbPath, final AMap aMap) {
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
                    textList.add(text);
                }
                cursor.close();

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

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                int vectorPointSum = 0;
                for(Vector vector : vectorList){
                    vectorPointSum += vector.getPointList().size();
                }
                LogHelper.log(TAG, "我一共解释出来了这么多个VectorPoint的数据: "+vectorPointSum);
                LogHelper.log(TAG, "我一共解释出来了这么多个Text的数据: " + textList.size());
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
        for (Text text : textList) {
            text.draw(aMap);
        }
        //画出所有的Vector
        for (int i = 0; i < vectorList.size(); i++) {
            vectorList.get(i).draw(aMap);
            //LogHelper.log(TAG, "我画出了一个Vector:   " + i);
        }
    }

    /**
     * 隐藏数字地图数据
     */
    public void hide() {
        //隐藏文字信息
        for (Text text : textList) {
            text.hide();
        }
        //隐藏所有的Vector
        for (int i = 0; i < vectorList.size(); i++) {
            vectorList.get(i).hide();
        }
    }

    /**
     * 将之前地图画的数据destory
     */
    public void destory(){
        //destory文字信息
        for (Text text : textList) {
            text.hide();
        }
        //destory所有的Vector
        for (int i = 0; i < vectorList.size(); i++) {
            vectorList.get(i).hide();
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
