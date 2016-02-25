package com.xunce.gsmr.app;

/**
 * 常量
 * Created by ssthouse on 2015/7/17.
 */
public class Constant {

    //app的文件夹名
    public static  final String APP_FOLDER_NAME = "GSM";

    //数据库文件的路径
    public static final String DbPath = "/data/data/com.xunce.gsmr/databases/Location.db";

    //外部SD卡数据存储路径
    public static final String PICTURE_PATH = "/storage/sdcard0/GSM/Picture/";
    public static final String TEMP_FILE_PATH = "/storage/sdcard0/GSM/Temp/";
    public static final String TEMP_SHARE_PATH = "/storage/sdcard0/GSM/Share/";
    public static final String DATA_BASE_FILE_PATH = "/storage/sdcard0/GSM/DataBase/";

    //table名
    public static final String TABLE_PRJ_ITEM = "Projects";
    public static final String TABLE_MARKER_ITEM = "BaseStation";
    public static final String TABLE_PICTURE_ITEM = "Photo";
    public static final String TABLE_PROJECT_INFO = "ProjectInfo";
    public static final String TABLE_POLY = "Poly";
    public static final String TABLE_P2DPOLY = "P2DPoly";
    public static final String TABLE_TEXT = "Text";
    public static final String TABLE_LINE = "Line";
    public static final String TABLE_KML_POLY = "KMLPoly";
    public static final String TABLE_KML_TEXT = "KMLText";

    //Extra的key
    public static final String EXTRA_KEY_PRJ_ITEM = "prjItem";
    public static final String EXTRA_KEY_MARKER_ITEM = "markerItem";
    public static final String EXTRA_KEY_REQUEST_CODE = "requestCode";
    public static final String EXTRA_KEY_LATITUDE = "latitude";
    public static final String EXTRA_KEY_LONGITUDE = "longitude";
    public static final String EXTRA_KEY_DBPATH = "dbPath";

    //result_code
    public static final int RESULT_CODE_OK = 2000;
    public static final int RESULT_CODE_NOT_OK = 2001;
    public static final int REQUEST_CODE_ALBUM = 2002;
    public static final int REQUEST_CODE_CAMERA = 2003;

    //.db文件的requestCode
    public static final int REQUEST_CODE_DB_FILE = 2004;



}
