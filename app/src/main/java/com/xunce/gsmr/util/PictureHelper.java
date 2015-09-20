package com.xunce.gsmr.util;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import com.xunce.gsmr.R;
import com.xunce.gsmr.app.Constant;
import com.xunce.gsmr.model.BitmapItem;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 处理图片的工具类
 * Created by ssthouse on 2015/7/18.
 */
public class PictureHelper {
    private static String TAG = "PictureHelper";

    /**
     * 删除文件
     *
     * @param path
     */
    public static void deletePicture(String path) {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * 将图片保存到指定的目录
     *
     * @param photo
     * @param savePath
     * @return
     */
    public static boolean saveImage(Bitmap photo, String savePath) {
        try {
            //如果文件不存在则新建路径
            File file = new File(savePath);
            file.getParentFile().mkdirs();
            if (!file.exists()) {
                file.createNewFile();
            }
            BufferedOutputStream bos = new BufferedOutputStream(
                    new FileOutputStream(savePath, false));
            photo.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
            //LogHelper.Log(TAG, "我在保存临时的照片");
        } catch (Exception e) {
            e.printStackTrace();
            LogHelper.Log(TAG, "image save is wrong");
            return false;
        }
        return true;
    }

    /**
     * 将图片从指定文件夹---复制到目标文件夹
     *
     * @param srcPath
     * @param targetPath
     * @return
     */
    public static boolean saveImage(String srcPath, String targetPath) {
//        Log.e(TAG, "我是源文件" + srcPath);
//        Log.e(TAG, "我是目标文件" + targetPath);
        //判断路径是否为空
        if (srcPath == null || targetPath == null) {
            return false;
        }
        try {
            //判断源文件是否存在
            File srcFile = new File(srcPath);
            if (srcFile.exists() == false) {
                return false;
            }
            //如果目标文件不存在---创建
            File targetFile = new File(targetPath);
            targetFile.getParentFile().mkdirs();
            if (!targetFile.exists()) {
                targetFile.createNewFile();
//                LogHelper.Log(TAG, targetPath);
            }
            //设置目标文件权限
            targetFile.setReadable(true);
            targetFile.setWritable(true);
            targetFile.setExecutable(true);
            //复制文件
            FileInputStream fosFrom = new FileInputStream(srcFile);
            FileOutputStream fosTo = new FileOutputStream(targetFile);
            byte bt[] = new byte[1024];
            int c;
            while ((c = fosFrom.read(bt)) > 0) {
                fosTo.write(bt, 0, c); //将内容写到新文件当中
            }
            fosFrom.close();
            fosTo.close();
        } catch (IOException e) {
            e.printStackTrace();
            LogHelper.Log(TAG, "something is wrong" + e.toString());
        }
        return true;
    }

    /**
     * 按从小到大的顺序排列File数组
     *
     * @param files
     */
    public static void sortFileArray(File[] files) {
        for (int j = 0; j < files.length - 1; j++) {
            for (int i = j + 1; i < files.length; i++) {
                if (getFileNameInFloat(files[j]) > getFileNameInFloat(files[i])) {
                    File tempFile = files[i];
                    files[i] = files[j];
                    files[j] = tempFile;
                }
            }
        }
    }

    /**
     * 获取图片文件的float文件名----用于比较创建时间
     *
     * @param file
     */
    public static double getFileNameInFloat(File file) {
        try {
            String fileName = file.getName();
            String floatString = fileName.replace(".jpg", "");
            double time = Double.parseDouble(floatString);
            return time;
        } catch (Exception e) {
            e.printStackTrace();
            LogHelper.Log(TAG, "something is wrong");
        }
        return 0;
    }

    private static boolean hasTempPicture(File file) {
        File tempFile = new File(Constant.TEMP_FILE_PATH + file.getName());
        if (tempFile.exists()) {
            return true;
        } else {
            return false;
        }
    }

    private static File getTempPicture(File file) {
        return new File(Constant.TEMP_FILE_PATH + file.getName());
    }

    public static List<BitmapItem> getBitmapItemList(String path) {
        //要返回的数据
        List<BitmapItem> bitmapList = new ArrayList<>();
        //列出picture文件
        File[] files;
        File dir = new File(path);
        if (dir.exists()) {
            files = dir.listFiles();
        } else {
            dir.mkdirs();
            files = dir.listFiles();
        }
        //整理顺序
        sortFileArray(files);
        //将每个文件转化为bitmap
        for (File file : files) {
            //对于每个文件---不一定都要重新decode--可能在缓存文件夹里面就哟
            if (hasTempPicture(file)) {
                Bitmap bitmap = BitmapFactory.decodeFile(Constant.TEMP_FILE_PATH + file.getName());
                bitmapList.add(new BitmapItem(bitmap, file.getAbsolutePath()));
            } else {
                //获取缩略图
                Bitmap bitmap = getSmallBitmap(file.getAbsolutePath(), 120, 120);
                bitmapList.add(new BitmapItem(bitmap, file.getAbsolutePath()));
                //将其缓存
                saveImage(bitmap, Constant.TEMP_FILE_PATH + file.getName());
            }
        }
        return bitmapList;
    }

    /**
     * 获取压缩后的bitmap
     *
     * @param imagePath
     * @param width
     * @param height
     * @return
     */
    public static Bitmap getSmallBitmap(String imagePath, int width, int height) {
        Bitmap bitmap;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // 获取这个图片的宽和高，注意此处的bitmap为null
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        options.inJustDecodeBounds = false; // 设为 false
        // 计算缩放比
        int h = options.outHeight;
        int w = options.outWidth;
        int beWidth = w / width;
        int beHeight = h / height;
        int be = 1;
        if (beWidth < beHeight) {
            be = beWidth;
        } else {
            be = beHeight;
        }
        if (be <= 0) {
            be = 1;
        }
        options.inSampleSize = be;
        // 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        // 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }

    /**
     * 开启图库获取照片
     *
     * @param activity
     */
    public static void getPictureFromAlbum(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");//相片类型
        activity.startActivityForResult(intent, Constant.REQUEST_CODE_ALBUM);
    }

    /**
     * 开启照相机获取照片
     *
     * @param activity
     */
    public static void getPictureFromCamera(Activity activity, String path) {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            //localTempImgDir和localTempImageFileName是自己定义的名字
            File file = new File(path + System.currentTimeMillis() + ".jpg");
            //如果该路径前面的parent路径不存在就创建
            file.getParentFile().mkdirs();
            Uri uri = Uri.fromFile(file);
            Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            activity.startActivityForResult(intent, Constant.REQUEST_CODE_CAMERA);
            activity.overridePendingTransition(R.anim.activity_fade_in, R.anim.activity_fade_out);
        } else {
            Toast.makeText(activity, "请确认已经插入SD卡", Toast.LENGTH_SHORT).show();
        }
    }
}
