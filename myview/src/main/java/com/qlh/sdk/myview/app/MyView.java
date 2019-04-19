package com.qlh.sdk.myview.app;

import java.io.File;
import java.util.Map;

/**
 * 作者：QLH on 2019-01-11
 * 描述：初始化必须的
 */
final public class MyView {
    /**保存未完成的倒计时**/
    public static Map<String, Long> map;

    public static void init(){
        //配置图片目录
        initPicDocument();
    }


    private static void initPicDocument() {
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
