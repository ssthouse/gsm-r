package com.xunce.gsmr.lib.digitalmap;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Environment;

import com.amap.api.maps.AMap;
import com.xunce.gsmr.model.event.ProgressbarEvent;
import com.xunce.gsmr.model.gaodemap.graph.Point;
import com.xunce.gsmr.model.gaodemap.graph.Text;
import com.xunce.gsmr.model.gaodemap.graph.Vector;
import com.xunce.gsmr.util.gps.PositionUtil;
import com.xunce.gsmr.util.view.ToastHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import timber.log.Timber;

/**
 * 将所有的数据库数据读出来-----然后画在地图上
 * Created by ssthouse on 2015/10/14.
 */
public class DigitalMapHolder {
    //数据库地址
    private String dbPath;
    //context
    private Context context;

    //高德地图的文字显示数据
    private List<Text> textList = new ArrayList<>();
    private List<Vector> vectorList = new ArrayList<>();

    public DigitalMapHolder(){}

    /**
     * 传入数据库地址的构造方法
     *
     * @param dbPath 数据库文件路径
     */
    public DigitalMapHolder(final Context context, final String dbPath) {
        this.dbPath = dbPath;
        this.context = context;

        //启动线程前___显示progressbar
        EventBus.getDefault().post(new ProgressbarEvent(true));
        //启动异步线程解析数据
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                //打开数据库
                SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(new File(dbPath), null);
                //读取数据库里面所有的-----Text
                Cursor cursor = database.rawQuery("SELECT * FROM TextPoint", null);
                while (cursor.moveToNext()) {
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
                        vector = new Vector(cursorVector.getString(0));
                        //L.log(TAG, "我新建了个Vector"+vector.getName());
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
                for (Vector vector : vectorList) {
                    vectorPointSum += vector.getPointList().size();
                }
                Timber.e( "我一共解释出来了这么多个VectorPoint的数据: " + vectorPointSum);
                Timber.e("我一共解释出来了这么多个Text的数据: " + textList.size());
                //运行完将progressbar隐藏
                EventBus.getDefault().post(new ProgressbarEvent(false));
                ToastHelper.show(context, "数字地图加载成功!");
            }
        }.execute();
    }

    /**
     * 画出所有--数字地图数据
     */
    public void draw(AMap aMap) {
        //画出文字信息
        for (Text text : textList) {
            text.draw(aMap);
        }
        //画出所有的Vector
        for (int i = 0; i < vectorList.size(); i++) {
            vectorList.get(i).draw(aMap);
            //L.log(TAG, "我画出了一个Vector:   " + i);
        }
    }

    /**
     * 画出数字地图---线
     */
    public void drawLine(AMap aMap) {
        //画出所有的Vector
        for (int i = 0; i < vectorList.size(); i++) {
            vectorList.get(i).draw(aMap);
            //L.log(TAG, "我画出了一个Vector:   " + i);
        }
    }

    /**
     * 画出数字地图---文字
     */
    public void drawText(AMap aMap) {
        //画出文字信息
        for (Text text : textList) {
            text.draw(aMap);
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
     * 仅隐藏文字
     */
    public void hideText(){
        //隐藏文字信息
        for (Text text : textList) {
            text.hide();
        }
    }

    /**
     * 清除数据
     */
    public void clearData(){
        for(Text text : textList){
            text.setText(null);
        }
        for(Vector vector : vectorList){
            vector.setPolyline(null);
        }
    }

    /**
     * 将之前地图画的数据destory
     */
    public void destory() {
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
           Timber.e("路径是:    " + file.getAbsolutePath());
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

    //getter---------------------setter---------------------------------------

    public List<Text> getTextList() {
        return textList;
    }

    public void setTextList(List<Text> textList) {
        this.textList = textList;
    }

    public List<Vector> getVectorList() {
        return vectorList;
    }

    public void setVectorList(List<Vector> vectorList) {
        this.vectorList = vectorList;
    }
}
