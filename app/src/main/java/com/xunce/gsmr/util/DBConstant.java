package com.xunce.gsmr.util;

import com.xunce.gsmr.app.Constant;

/**
 * Created by Xingw on 2016/1/14.
 */
public class DBConstant {
    public static final String photo_column_prjname = "prjName";       //工程名
    public static final String photo_column_maker_id = "markerId";      //照片对应的基站ID
    public static final String photo_column_picName = "photoName";      //照片的名称
    public static final String photo_column_blob = "photoData";          //照片数据
    public static final String basestation_column_MarkerId = "markerId";
    public static final String basestation_column_distance_to_rail = "distance_to_rail";
    public static final String basestation_column_antenna_direction_1 = "antenna_direction_1";
    public static final String basestation_column_antenna_direction_2 = "antenna_direction_2";
    public static final String basestation_column_antenna_direction_3 = "antenna_direction_3";
    public static final String basestation_column_antenna_direction_4 = "antenna_direction_4";
    public static final String basestation_column_tower_height = "tower_height";
    public static final String basestation_column_side_direction = "side_direction";
    public static final String basestation_column_latitude = "latitude";
    public static final String basestation_column_longitude = "longitude";
    public static final String basestation_column_device_type = "device_type";
    public static final String basestation_column_tower_type = "tower_type";
    public static final String basestation_column_kilometer_mark = "kilometer_mark";
    public static final String basestation_column_Id = "id";
    public static final String basestation_column_comment = "comment";
    public static final String prjInfo_coloum_prjName = "prjName";
    public static final String id = "id";
    public static final String layer = "layer";
    public static final String orderId = "orderId";
    public static final String longitude = "longitude";
    public static final String longitude_start = "longitude_start";
    public static final String longitude_end = "longitude_end";
    public static final String latitude = "latitude";
    public static final String latitude_start = "latitude_start";
    public static final String latitude_end = "latitude_end";
    public static final String name = "name";
    public static final String content = "content";


    public static String Photo_sql_insert = "insert into " + Constant.TABLE_PICTURE_ITEM +
            " (" + photo_column_maker_id +
            ", " + photo_column_picName +
            ", " + photo_column_blob +
            ") values (?,?,?);";

    public static String Basestation_xml_sql_insert = "insert into " + Constant.TABLE_MARKER_ITEM +
            " (" + basestation_column_MarkerId +
            ", " + basestation_column_Id +
            ", " + basestation_column_device_type +
            ", " + basestation_column_kilometer_mark +
            ", " + basestation_column_side_direction +
            ", " + basestation_column_distance_to_rail +
            ", " + basestation_column_longitude +
            ", " + basestation_column_latitude +
            ", " + basestation_column_comment +
            ") values (?,?,?,?,?,?,?,?,?);";

    public static String Line_sql_insert ="insert into " + Constant.TABLE_LINE +
            " (" + longitude_start +
            ", " + latitude_start +
            ", " + longitude_end +
            ", " + latitude_end +
            ") values (?,?,?,?);";

    public static String Poly_sql_insert ="insert into " + Constant.TABLE_POLY +
            " (" + id +
            ", " + orderId +
            ", " + longitude +
            ", " + latitude +
            ") values (?,?,?,?);";

    public static String Text_sql_insert ="insert into " + Constant.TABLE_TEXT +
            " (" + longitude +
            ", " + latitude +
            ", " + content +
            ") values (?,?,?);";

    public static String P2DPoly_sql_insert ="insert into " + Constant.TABLE_P2DPOLY +
            " (" + id +
            ", " + orderId +
            ", " + longitude +
            ", " + latitude +
            ") values (?,?,?,?);";
}
