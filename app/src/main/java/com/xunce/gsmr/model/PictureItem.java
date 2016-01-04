package com.xunce.gsmr.model;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import java.io.ByteArrayOutputStream;
/**
 * Created by Xingw on 2015/12/27.
 */
//@Table(name = Constant.TABLE_PICTURE_ITEM)
public class PictureItem{
    /**
     * 保存数据常量
     */
    public interface PictureItemCons{
        String column_prjname = "prjName";                      //工程名
        String column_maker_id = "MakerId";      //照片路径的文件名
        String column_blob = "Picture";
    }

    /**
     * 工程名
     */
    //@Column(name = PictureItemCons.column_prjname)
    private String prjName;
    /**
     * 照片路径的文件名
     */
    //@Column(name = PictureItemCons.column_maker_id)
    private String makerId;
    /**
     * 照片数据
     */
    //@Column(name = PictureItemCons.column_blob)
    private Bitmap photoData;

    /**
     * 构造方法
     * @param prjName 工程名
     * @param photoPathName 存储路径
     * @param photoData 存储的数据 byte形式
     */
    public PictureItem(String prjName, String photoPathName, Bitmap photoData) {
        super();
        this.prjName = prjName;
        this.makerId = photoPathName;
        this.photoData = photoData;
    }

    /**
     * 空构造方法
     */
    public PictureItem() {
        super();
    }

//    /**
//     * 构造方法
//     * @param prjName 工程名
//     * @param photoPathName 存储路径
//     * @param bitmap   存储的数据 Bitmap形式
//     */
//    public PictureItem(String prjName, String photoPathName, Bitmap bitmap) {
//        super();
//        this.prjName = prjName;
//        this.makerId = photoPathName;
//        ByteArrayOutputStream os = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
//        this.photoData = os.toByteArray();
//    }

    /**
     * 根据MarkerItem生成的构造方法
     * @param markerItem  Maker实例
     * @param bitmap  存储的数据 Bitmap
     */
    public PictureItem(MarkerItem markerItem, Bitmap bitmap) {
        super();
        this.prjName = markerItem.getPrjName();
        this.makerId = markerItem.getPhotoPathName();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
//        this.photoData = os.toByteArray();
        this.photoData=bitmap;
    }

    public void insert(SQLiteDatabase db,Bitmap bitmap){

    }

    public String getPrjName() {
        return prjName;
    }

    public void setPrjName(String prjName) {
        this.prjName = prjName;
    }

    public String getMakerId() {
        return makerId;
    }

    public void setMakerId(String makerId) {
        this.makerId = makerId;
    }

    public Bitmap getPhotoData() {
        return photoData;
    }

    public void setPhotoData(Bitmap photoData) {
        this.photoData = photoData;
    }

//    public byte[] getPhotoData() {
//        return photoData;
//    }
//
//    public void setPhotoData(byte[] photoData) {
//        this.photoData = photoData;
//    }
//
//    public Bitmap getBitmap(){
//        return BitmapFactory.decodeByteArray(photoData, 0,photoData.length);
//    }
}
