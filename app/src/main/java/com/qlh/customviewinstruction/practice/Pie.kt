package com.qlh.customviewinstruction.practice

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

/**
 *作者：QLH on 2019-12-18
 *描述：
 */
class Pie : View {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)
    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : super(context, attributeSet, defStyleAttr)


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.drawColor(Color.YELLOW)

        val paint = Paint()
        //实心圆 红色
        paint.style = Paint.Style.FILL
        paint.isAntiAlias = true
        paint.color = Color.RED
        canvas?.drawCircle(50f,50f,50f,paint)
        //空心圆 红色
        paint.style = Paint.Style.STROKE
        canvas?.drawCircle(150f,50f,50f,paint)

        //蓝色实心圆
        paint.style = Paint.Style.FILL
        paint.color = Color.BLUE
        canvas?.drawCircle(250f,50f,50f,paint)

        //线宽为20的空心圆
        paint.style = Paint.Style.STROKE
        paint.color = Color.RED
        paint.strokeWidth = 20f
        canvas?.drawCircle(360f,60f,50f,paint)

        //矩形
        paint.style = Paint.Style.FILL
        canvas?.drawRect(0f,150f,100f,200f,paint)
        canvas?.drawRoundRect(150f,150f,250f,200f,10f,10f,paint)

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 10f
        canvas?.drawRoundRect(300f,150f,400f,200f,10f,10f,paint)
        canvas?.drawRect(450f,150f,550f,200f,paint)
    }
}