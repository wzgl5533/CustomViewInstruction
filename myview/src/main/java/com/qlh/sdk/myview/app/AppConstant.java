package com.qlh.sdk.myview.app;

import android.os.Environment;

/**
 * 作者：QLH on 2018/10/8 10:18
 * 描述：静态变量
 */
public class AppConstant {

    //-------------图像保存相对路径--------------
    private static final String PIC_PATH = Environment.getExternalStorageDirectory().getPath() + "/QLH/";

    public static final String PIC_DIC = PIC_PATH + "camera/pic";
}
