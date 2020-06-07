package com.qlh.sdk.myview.callback

import android.graphics.Bitmap

/**
 *作者：QLH on 2020-06-06
 *描述：
 */
interface TakePictureSuccess {
    fun success(cropBitmap: Bitmap?)
}