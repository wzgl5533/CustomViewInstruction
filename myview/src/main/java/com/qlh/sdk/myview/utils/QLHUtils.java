package com.qlh.sdk.myview.utils;

import android.content.Context;

/**
 * 作者：qlh on 2018/9/27 19:12
 * 描述：工具类
 */
public class QLHUtils {

    public static int dp2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}
