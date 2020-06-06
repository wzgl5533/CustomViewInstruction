package com.qlh.sdk.myview.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import android.graphics.RectF
import com.qlh.sdk.myview.utils.DateUtil
import com.qlh.sdk.myview.utils.ImageUtils
import java.io.File
import java.util.*


/**
 *作者：QLH on 2020-05-31
 *描述：处理图片工具类
 */
object BitmapUtils {

    /**
     * 利用正确视角（和预览视角相同）的裁剪图片
     *
     * @param bitmap    原始图片
     * @param preWidth  预览视图宽
     * @param preHeight 预览视图高
     * @param frameRect 裁剪框
     **/
    @JvmStatic
    fun getCropPicture(bitmap: Bitmap, preWidth: Int, preHeight: Int, frameRect: Rect)
            : Bitmap {

        //原始照片的宽高
        val picWidth = bitmap.width
        val picHeight = bitmap.height

        //预览界面和照片的比例
        val preRW = picWidth * 1.0f / preWidth
        val preRH = picHeight * 1.0f / preHeight

        //裁剪框的位置和宽高
        val frameLeft = frameRect.left
        val frameTop = frameRect.top
        val frameWidth = frameRect.width()
        val frameHeight = frameRect.height()

        val cropLeft = (frameLeft * preRW).toInt()
        val cropTop = (frameTop * preRH).toInt()
        val cropWidth = (frameWidth * preRW).toInt()
        val cropHeight = (frameHeight * preRH).toInt()

        val cropBitmap = Bitmap.createBitmap(bitmap, cropLeft, cropTop, cropWidth, cropHeight)
        return cropBitmap
    }

    /**保存图片
     *@return
     ***/
    @JvmStatic
    fun saveBitmap(context: Context, bitmap: Bitmap): String {
        val path = context.getExternalFilesDir("")?.absolutePath+File.separator+"crop"
        val file = File(path, "${DateUtil.formatDateToMillSecond(Date(System.currentTimeMillis()))}.jpg")
        ImageUtils.save(bitmap, file, Bitmap.CompressFormat.JPEG, true)
        return file.absolutePath
    }
}