package com.xunce.gsmr.util;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import timber.log.Timber;

/**
 * 压缩工具类
 * Created by ssthouse on 2015/11/23.
 */
public class ZipUtil {

    /**
     * 压缩文件夹(将)
     *
     * @param srcFilePath 源文件夹路径
     * @param zipFilePath 目标路径
     * @throws Exception
     */
    public static void zipFolder(String srcFilePath, String zipFilePath) throws Exception {
        // 创建Zip包
        ZipOutputStream outZip = new ZipOutputStream(new FileOutputStream(zipFilePath));
        // 打开要输出的文件
        File srcFile = new File(srcFilePath);
        // 压缩
        zipFiles(srcFile.getParent() + File.separator, srcFile.getName(), outZip);
        // 完成,关闭
        outZip.finish();
        outZip.close();
    }

    /**
     * 将数据库文件放入zip包
     * @param context   上下文
     * @param zipFilePath   压缩包文件路径
     */
    public static void addDataBseToZip(Context context, String zipFilePath){
        ZipEntry zipEntry = new ZipEntry("Location.db");
        FileInputStream inputStream = null;
        try {
            ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(zipFilePath));
            inputStream = new FileInputStream(context.getDatabasePath("Location.db"));
            zipOutputStream.putNextEntry(zipEntry);
            int len;
            byte[] buffer = new byte[100000];
            while ((len = inputStream.read(buffer)) != -1) {
                zipOutputStream.write(buffer, 0, len);
            }
            inputStream.close();
            zipOutputStream.closeEntry();
        } catch (IOException e) {
            e.printStackTrace();
            Timber.e("something is wrong");
        }
    }

    /**
     * 压缩文件
     *
     * @param folderPath    文件路径
     * @param filePath  文件名
     * @param zipOut    输出流
     * @throws Exception
     */
    private static void zipFiles(String folderPath, String filePath, ZipOutputStream zipOut)
            throws Exception {
        if (zipOut == null) {
            return;
        }
        //源文件
        File srcFile = new File(folderPath + filePath);
        // 判断是不是文件
        if (srcFile.isFile()) {
            ZipEntry zipEntry = new ZipEntry(filePath);
            FileInputStream inputStream = new FileInputStream(srcFile);
            zipOut.putNextEntry(zipEntry);
            int len;
            byte[] buffer = new byte[100000];
            while ((len = inputStream.read(buffer)) != -1) {
                zipOut.write(buffer, 0, len);
            }
            inputStream.close();
            zipOut.closeEntry();
        } else {
            // 文件夹的方式,获取文件夹下的子文件
            String fileList[] = srcFile.list();
            // 如果没有子文件, 则添加进去即可
            if (fileList.length <= 0) {
                ZipEntry zipEntry = new ZipEntry(filePath + java.io.File.separator);
                zipOut.putNextEntry(zipEntry);
                zipOut.closeEntry();
            }
            // 如果有子文件, 遍历子文件
            for (String aFileList : fileList) {
                zipFiles(folderPath, filePath + File.separator + aFileList, zipOut);
            }
        }
    }
}