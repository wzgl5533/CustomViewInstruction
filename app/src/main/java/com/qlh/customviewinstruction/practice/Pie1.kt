package com.qlh.customviewinstruction.practice

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.qlh.customviewinstruction.R
import io.reactivex.Flowable
import java.util.concurrent.TimeUnit

/**
 *作者：QLH on 2019-12-18
 *描述：
 */
class Pie1 : View {

    var rectF = RectF(20f, 20f, 480f, 480f)
    val paint = Paint()
    var col = PorterDuff.Mode.DST_OUT

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)
    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : super(context, attributeSet, defStyleAttr){
        //draw1()
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawColor(Color.parseColor("#CCCCCC"))
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.timg)

        val matrix = Matrix()
        matrix.postScale(width*1f/bitmap.width,height*1f/bitmap.height)

        val bitmap1 = Bitmap.createBitmap(bitmap,0,0,bitmap.width,bitmap.height,matrix,true)

        val shader = BitmapShader(bitmap1,Shader.TileMode.CLAMP,Shader.TileMode.CLAMP)
        paint.shader = shader
        //paint.colorFilter = LightingColorFilter(0x000000,0x00f000)
        paint.colorFilter = PorterDuffColorFilter(Color.parseColor("#80000000"), PorterDuff.Mode.DST_ATOP)

        canvas?.drawCircle(300f,300f,300f,paint)

        //canvas?.drawBitmap(bitmap,matrix,Paint())
    }


    private fun draw1(){
        val type = arrayOf(PorterDuff.Mode.SRC_ATOP,PorterDuff.Mode.SRC,
                PorterDuff.Mode.SRC_IN,PorterDuff.Mode.SRC_OUT, PorterDuff.Mode.SRC_OVER,
                PorterDuff.Mode.DST_ATOP,PorterDuff.Mode.DST,
                PorterDuff.Mode.DST_OVER,PorterDuff.Mode.DST_IN,PorterDuff.Mode.DST_OUT
        )
        Flowable.interval(1,2,TimeUnit.SECONDS)
                .subscribe {
                   col = type[it.toInt()%type.size]
                    Log.e("11111","$it")
                    invalidate()
                }

    }

}