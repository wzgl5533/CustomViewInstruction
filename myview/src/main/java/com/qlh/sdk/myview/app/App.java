package com.qlh.sdk.myview.app;

import android.app.Application;

import java.io.File;
import java.util.Map;

/**
 * 作者：dell on 2018/9/29 11:05
 * 描述：
 */
public class App extends Application{
    /**保存未完成的倒计时**/
    public static Map<String, Long> map;

    @Override
    public void onCreate() {
        super.onCreate();
        //配置图片目录
        initPicDocument();
    }

    private void initPicDocument() {
        File file;
        //--------------------------图片目录-----------
        file = new File(AppConstant.PIC_DIC);
        // 若在SD卡的文件夹不存在
        if (!file.exists()) {
            // 创建该文件夹
            file.mkdirs();
        }
    }
}
