package com.xunce.gsmr;

/**
 * 常量
 * Created by ssthouse on 2015/7/17.
 */
public class Constant {

    //app的文件夹名
    public static  final String APP_FOLDER_NAME = "GSM";

    //数据库文件的路径
    public static final String DbPath = "data/data/com.xunce.gsmr/Location.db";

    public static final String PICTURE_PATH = "/storage/sdcard0/GSM/Picture/";
    public static final String TEMP_FILE_PATH = "/storage/sdcard0/GSM/Temp/";

    //table名
    public static final String TABLE_PRJ_ITEM = "Projects";
    public static final String TABLE_MARKER_ITEM = "Markers";
    public static final String TABLE_PHOTO_ITEM = "Photos";

    //Extra的key
    public static final String EXTRA_KEY_PRJ_ITEM = "prjItem";
    public static final String EXTRA_KEY_MARKER_ITEM = "markerItem";
    public static final String EXTRA_KEY_REQUEST_CODE = "requestCode";
    public static final String EXTRA_KEY_LATITUDE = "latitude";
    public static final String EXTRA_KEY_LONGITUDE = "longitude";


    //result_code
    public static final int RESULT_CODE_OK = 2000;
    public static final int RESULT_CODE_NOT_OK = 2001;
    public static final int REQUEST_CODE_ALBUM = 2002;
    public static final int REQUEST_CODE_CAMERA = 2003;
}
