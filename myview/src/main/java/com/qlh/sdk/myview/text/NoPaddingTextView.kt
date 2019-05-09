package com.qlh.sdk.myview.text

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView


/**
 *作者：QLH on 2019-05-09
 *描述：无内边距TextView
 */
class NoPaddingTextView:AppCompatTextView{

    private val mPaint = paint
    private val mBounds = Rect()
    private var mRemoveFontPadding = false//是否去除字体内边距，true：去除 false：不去除

    constructor(context: Context?) : this(context,null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs,0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr){
        initAttributes(context!!, attrs)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (mRemoveFontPadding) {
            calculateTextParams()
            setMeasuredDimension(mBounds.right - mBounds.left, -mBounds.top + mBounds.bottom)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
    }

    override fun onDraw(canvas: Canvas) {
        drawText(canvas)
    }


    /**
     * 初始化属性
     */
    private fun initAttributes(context: Context, attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, com.qlh.sdk.myview.R.styleable.NoPaddingTextView)
        mRemoveFontPadding = typedArray.getBoolean(com.qlh.sdk.myview.R.styleable.NoPaddingTextView_removeDefaultPadding, false)
        typedArray.recycle()
    }

    /**
     * 计算文本参数
     */
    private fun calculateTextParams(): String {
        val text = text.toString()
        val textLength = text.length
        mPaint.getTextBounds(text, 0, textLength, mBounds)
        if (textLength == 0) {
            mBounds.right = mBounds.left
        }
        return text
    }


    /**
     * 绘制文本
     */
    private fun drawText(canvas: Canvas) {
        val text = calculateTextParams()
        val left = mBounds.left.toFloat()
        val bottom = mBounds.bottom.toFloat()
        mBounds.offset(-mBounds.left, -mBounds.top)
        mPaint.isAntiAlias = true
        mPaint.color = currentTextColor
        canvas.drawText(text, (-left), (mBounds.bottom.toFloat() - bottom), mPaint)
    }

}