package com.xunce.gsmr.util;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.orhanobut.logger.Logger;
import com.xunce.gsmr.app.Constant;

/**
 * Created by Xingw on 2016/1/3.
 */
public class DBSqliteHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "Location.db";
    public static final String column_prjname = "prjName";       //工程名
    public static final String column_maker_id = "MakerId";      //照片路径的文件名
    public static final String column_picName = "picName";      //照片路径的文件名
    public static final String column_blob = "Picture";          //照片存储名

    public static String sql_create_table = "CREATE TABLE IF NOT EXISTS " + Constant.TABLE_PICTURE_ITEM
            + " (ID INTEGER PRIMARY KEY, " +
            column_prjname + " TEXT," +
            column_maker_id + " TEXT," +
            column_picName + " TEXT," +
            column_blob + " BLOB);";

    public static String sql_insert = "insert into " + Constant.TABLE_PICTURE_ITEM +
            " (" + column_prjname +
            ", " + column_maker_id +
            ", " + column_picName +
            ", " + column_blob +
            ") values (?,?,?,?);";

    public DBSqliteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DBSqliteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(sql_create_table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
