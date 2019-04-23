package com.qlh.sdk.myview.utils

import android.content.Context

/**
 * 作者：qlh on 2018/9/27 19:12
 * 描述：工具类
 */
object QLHUtils {

    fun dp2px(context: Context, dipValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dipValue * scale + 0.5f).toInt()
    }
}
