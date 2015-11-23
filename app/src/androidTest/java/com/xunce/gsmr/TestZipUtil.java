package com.xunce.gsmr;

import android.test.InstrumentationTestCase;

import com.xunce.gsmr.util.ZipUtil;

/**
 * 测试压缩代码是否成功
 * Created by ssthouse on 2015/11/23.
 */
public class TestZipUtil extends InstrumentationTestCase {


    public void testPathZip(){
        try {
            ZipUtil.zipFolder("F:\\大一文件\\TXT", "F:\\大一文件\\TXT\\test.zip");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
