package com.xunce.gsmr.model.gaodemap;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.LatLng;
import com.xunce.gsmr.app.Constant;
import com.xunce.gsmr.kilometerMark.KilometerMark;
import com.xunce.gsmr.kilometerMark.KilometerMarkHolder;
import com.xunce.gsmr.model.PrjItem;
import com.xunce.gsmr.model.event.ProgressbarEvent;
import com.xunce.gsmr.model.gaodemap.graph.Circle;
import com.xunce.gsmr.model.gaodemap.graph.Line;
import com.xunce.gsmr.model.gaodemap.graph.Point;
import com.xunce.gsmr.model.gaodemap.graph.Text;
import com.xunce.gsmr.model.gaodemap.graph.Vector;
import com.xunce.gsmr.util.DBConstant;
import com.xunce.gsmr.util.DBHelper;
import com.xunce.gsmr.util.L;
import com.xunce.gsmr.util.gps.PositionUtil;
import com.xunce.gsmr.util.view.ToastHelper;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import timber.log.Timber;

/**
 * 高德地图地图铁路绘图管理器 Created by ssthouse on 2015/9/15.
 */
public class GaodeRailWayHolder {
    private static final String TAG = "GaodeRailWayHolder";

    /**
     * 用于接收数据的临时变量
     */
    private Line line;
    private Text text;
    private Vector vector;
    /**
     * 所有绘图数据
     */
    private List<Circle> circles;
    private List<Line> lineList = new ArrayList<>();
    private List<Text> textList = new ArrayList<>();
    private List<Vector> vectorList = new ArrayList<>();

    /**
     * 公里标管理器
     */
    private KilometerMarkHolder kilometerMarkHolder = new KilometerMarkHolder();

    /**
     * 构造方法 直接读取数据库取出数据
     *
     * @param context
     * @param dbPath
     */
    public GaodeRailWayHolder(final Context context, final String dbPath) {
        kilometerMarkHolder = new KilometerMarkHolder();
        //启动线程前___显示progressbar
        EventBus.getDefault().post(new ProgressbarEvent(true));
        //启动异步线程解析数据
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                SQLiteDatabase db = DBHelper.openDatabase(dbPath);
                db.beginTransaction();
                //开启线程执行前显示进度条
                getLineList(db);
                getTextList(db);
                getPolyList(db);
                getP2DPolyList(db);
                db.setTransactionSuccessful();
                db.endTransaction();
                db.close();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                //将公里标进行排序
                kilometerMarkHolder.sort();
                //运行完将progressbar隐藏
                EventBus.getDefault().post(new ProgressbarEvent(false));
                ToastHelper.show(context, "地图数据加载成功!");
            }
        }.execute();
    }

    /**
     * 构造方法 传入数据
     * @param lineList
     * @param textList
     * @param vectorList
     */
    public GaodeRailWayHolder(List<Line> lineList, List<Text> textList, List<Vector> vectorList) {
        this.lineList = lineList;
        this.textList = textList;
        this.vectorList = vectorList;

        kilometerMarkHolder = new KilometerMarkHolder();
        for (Text text1 : textList) {
            //文字需要判断是不是公里标(是的话需要加入KilometerMarkHolder中)
            KilometerMark kilometerMark = KilometerMark.getKilometerMark(text1.getLatLng().longitude,
                    text1.getLatLng().latitude, text1.getContent());
            kilometerMarkHolder.addKilometerMark(kilometerMark);
        }

    }
    /**
     * 画出自己
     */
    public void draw(AMap aMap) {
        for (Line line : lineList) {
            line.draw(aMap);
        }
        for (Text text : textList) {
            text.draw(aMap);
        }
        for (Vector vector : vectorList) {
            vector.draw(aMap);
        }
    }

    /**
     * 获取LineList
     *
     * @param database
     * @return
     */
    private void getLineList(SQLiteDatabase database) {
        Cursor cursor = database.query(Constant.TABLE_LINE, null, null, null, null, null, null);
        if (cursor == null) {
            return;
        }
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            LatLng latLngStart = PositionUtil.gps84_To_Gcj02(
                    cursor.getDouble(cursor.getColumnIndex(DBConstant.latitude_start)),
                    cursor.getDouble(cursor.getColumnIndex(DBConstant.longitude_start)));
            LatLng latLngEnd = PositionUtil.gps84_To_Gcj02(
                    cursor.getDouble(cursor.getColumnIndex(DBConstant.latitude_end)),
                    cursor.getDouble(cursor.getColumnIndex(DBConstant.longitude_end)));
            line = new Line(latLngStart, latLngEnd);
            lineList.add(line);
            cursor.moveToNext();
        }
        return;
    }



    /**
     * 获取Circle列表
     *
     * @param database
     * @return
     */
    private void getCircleList(SQLiteDatabase database) {
        List<Circle> circleList = new ArrayList<>();
        Cursor cursor = database.query("Circle", null, null, null, null, null, null);
        if (cursor == null) {
            return;
        }
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            LatLng latLng = new LatLng(cursor.getFloat(1), cursor.getFloat(2));
            circleList.add(new Circle(latLng, (int) cursor.getFloat(3)));
            L.log(TAG, cursor.getFloat(1) + ":" + cursor.getFloat(2) + ":" +
                    cursor.getFloat(3));
            cursor.moveToNext();
        }
        return;
    }

    /**
     * 获取文字List
     *
     * @param database
     * @return
     */
    private List<Text> getTextList(SQLiteDatabase database) {
        List<Text> circleList = new ArrayList<>();
        Cursor cursor = database.query(Constant.TABLE_TEXT, null, null, null, null, null, null);
        if (cursor == null) {
            return null;
        }
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            double latitude = cursor.getDouble(cursor.getColumnIndex(DBConstant.latitude));
            double longitude = cursor.getDouble(cursor.getColumnIndex(DBConstant.longitude));
            LatLng latLng = PositionUtil.gps84_To_Gcj02(latitude, longitude);
            String content = cursor.getString(cursor.getColumnIndex(DBConstant.content));
            text = new Text(latLng, content);
            textList.add(text);
            //文字需要判断是不是公里标(是的话需要加入KilometerMarkHolder中)
            KilometerMark kilometerMark = KilometerMark.getKilometerMark(longitude, latitude, content);
            kilometerMarkHolder.addKilometerMark(kilometerMark);
            cursor.moveToNext();
        }
        return circleList;
    }

    /**
     * 获取Poly矢量List
     *
     * @param database
     * @return
     */
    private void getPolyList(SQLiteDatabase database) {
        Cursor cursor = database.rawQuery("SELECT * FROM " + Constant.TABLE_POLY + " ORDER BY " +
                DBConstant.id + " , " + DBConstant.orderId, null);
        if (cursor == null || !cursor.moveToFirst()) {
            return;
        }
        do {
            int order = Integer.parseInt(cursor.getString(cursor.getColumnIndex(DBConstant
                    .orderId)));
            if (order == 0) {
                if (vector != null && vector.getPointList().size() != 0) {
                    vectorList.add(vector);
                }
                //存入Vector的图层名
                vector = new Vector(cursor.getString(cursor.getColumnIndex(DBConstant.layer)));
            } else {
                double longitude = cursor.getDouble(cursor.getColumnIndex(DBConstant.longitude));
                double latitude = cursor.getDouble(cursor.getColumnIndex(DBConstant.latitude));
                vector.getPointList().add(new Point(longitude, latitude));
            }
        } while (cursor.moveToNext());
        vectorList.add(vector);
        return;
    }

    /**
     * 获取二维poly矢量组
     *
     * @param database
     * @return
     */
    private void getP2DPolyList(SQLiteDatabase database) {
        Cursor cursor = database.rawQuery("SELECT * FROM " + Constant.TABLE_P2DPOLY + " ORDER BY " +
                DBConstant.id + " , " + DBConstant.orderId, null);
        if (cursor == null || !cursor.moveToFirst()) {
            return;
        }
        do {
            int order = Integer.parseInt(cursor.getString(cursor.getColumnIndex(DBConstant
                    .orderId)));
            if (order == 0) {
                if (vector != null && vector.getPointList().size() != 0) {
                    vectorList.add(vector);
                }
                vector = new Vector("");
            } else {
                double longitude = cursor.getDouble(cursor.getColumnIndex(DBConstant.longitude));
                double latitude = cursor.getDouble(cursor.getColumnIndex(DBConstant.latitude));
                vector.getPointList().add(new Point(longitude, latitude));
            }
        } while (cursor.moveToNext());
        vectorList.add(vector);
        return;
    }

    /**
     * 仅仅画出文字
     *
     * @param aMap
     */
    public void drawText(AMap aMap) {
        for (Text text : textList) {
            text.draw(aMap);
        }
    }

    /**
     * 仅仅画出线段
     *
     * @param aMap
     */
    public void drawLine(AMap aMap) {
        for (Line line : lineList) {
            line.draw(aMap);
        }
        for (com.xunce.gsmr.model.gaodemap.graph.Vector vector : vectorList) {
            vector.draw(aMap);
            //Timber.e("我画了一条vector");
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
        for (Vector vector : vectorList) {
            vector.hide();
        }
    }

    /**
     * 隐藏文字
     */
    public void hideText() {
        for (Text text : textList) {
            text.hide();
        }
    }

    /**
     * 清除数据
     */
    public void clearData(){
        for (Line line : lineList) {
            line.setPolyline(null);
        }
        for (Text text : textList) {
            text.setText(null);
        }
        for (Vector vector : vectorList) {
            vector.setPolyline(null);
        }
    }

    public KilometerMarkHolder getKilometerMarkHolder() {
        return kilometerMarkHolder;
    }

    public void setKilometerMarkHolder(KilometerMarkHolder kilometerMarkHolder) {
        this.kilometerMarkHolder = kilometerMarkHolder;
    }

    public List<Circle> getCircles() {
        return circles;
    }

    public void setCircles(List<Circle> circles) {
        this.circles = circles;
    }

    public List<Line> getLineList() {
        return lineList;
    }

    public void setLineList(List<Line> lineList) {
        this.lineList = lineList;
    }

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

