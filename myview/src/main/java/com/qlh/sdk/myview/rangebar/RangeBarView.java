package com.qlh.sdk.myview.rangebar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.qlh.sdk.myview.R;

public class RangeBarView extends View {

    private int rectLineHeight;//圆角矩形线的高度，若设置高度大于圆的半径(或者未设置)就会默认给圆的1/4作为高度
    private int rectLineCornerRadius;//圆角矩形线的圆角半径
    private int rectLineDefaultColor;//默认颜色
    private int rectLineCheckedColor;//选中颜色
    private int circleRadius;//圆半径
    private int circleStrokeWidth;//圆边框的大小
    private int leftCircleSolidColor;//左边实心圆颜色
    private int leftCircleStrokeColor;//左边圆边框的颜色
    private int rightCircleSolidColor;//右边实心圆颜色
    private int rightCircleStrokeColor;//右边圆边框的颜色
    private int rangeTextSize;//刻度文本大小
    private int rangeTextColor;//刻度文本颜色
    private int rangeTextToProgressSpace;//刻度到轨道垂直距离
    private int rectDialogWidth;//价格显示对话框宽度
    private int rectDialogColor;//价格显示对话框颜色,包括底部三角形颜色
    private int rectDialogCornerRadius;//价格显示对话框圆角半径
    private int rectDialogTextSize;//价格对话框文字大小
    private int rectDialogTextColor;//价格对话框文字颜色
    private int rectDialogSpaceToProgress;//价格对话框离轨道垂直距离

    //画笔
    private Paint leftCirclePaint;
    private Paint leftCircleStrokePaint;
    private Paint rightCirclePaint;
    private Paint rightCircleStrokePaint;
    private Paint defaultLinePaint;
    private Paint selectedLinePaint;
    private Paint textPaint;
    private Paint dialogBgPaint;
    private Paint dialogTextPaint;
    //左右两个圆对象
    private CirclePoint leftCircleObj;
    private CirclePoint rightCircleObj;
    //默认颜色的圆角矩形
    private RectF defaultCornerLineRect;
    //中间选中颜色的圆角矩形
    private RectF selectedCornerLineRect;
    //表示价格数据的矩形
    private RectF numberDescRect;
    //画小三角形
    private Path trianglePath;
    //等边三角形边长
    private int triangleLength = 15;
    //等边三角形的高
    private int triangleHeight;
    private float downX;
    private boolean touchLeftCircle;

    //控件的实际宽(去除内边距之后的)
    private int realWidth;
    //半径+边框的总值
    private int strokeRadius;
    //描述信息弹窗高度+与进度条之间的间隙距离
    private int rectDialogHeightAndSpace;

    //默认分成10份
    private int slice = 10;
    //表示每一份所占的距离长度
    private int perSlice;
    //最大值，默认为100
    private int maxValue = 10;
    //最小值，默认为0
    private int minValue = 0;
    //每一份对应的数值，数值默认值为10
    private int sliceValue = 1;
    //左边数值，右边数值
    private int leftValue, rightValue;
    //描述信息弹窗中的文字
    private String textDesc = "0";
    //是否显示描述信息弹窗
    private boolean isShowRectDialog;
    private OnMoveValueListener listener;

    private float mDensity;

    public RangeBarView(Context context) {
        this(context, null);
    }

    public RangeBarView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RangeBarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mDensity = getResources().getDisplayMetrics().density;
        handleStyleable(context, attrs, defStyleAttr);
        //初始化画笔
        initPaints();
    }

    private void handleStyleable(Context context, AttributeSet attrs, int defStyle) {
        //初始化属性值，同时将每一个属性的默认值设置在styles.xml文件中
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RangeBarView, defStyle, R.style.default_range_bar_value);

        rectLineDefaultColor = typedArray.getColor(R.styleable.RangeBarView_rect_line_default_color, ContextCompat.getColor(context, R.color.grey_CD));
        rectLineCheckedColor = typedArray.getColor(R.styleable.RangeBarView_rect_line_checked_color, ContextCompat.getColor(context, R.color.blue_275D9D));
        rectLineHeight = typedArray.getDimensionPixelSize(R.styleable.RangeBarView_rect_line_height, getResources().getDimensionPixelSize(R.dimen.rect_line_height));
        circleRadius = typedArray.getDimensionPixelSize(R.styleable.RangeBarView_circle_radius, getResources().getDimensionPixelSize(R.dimen.circle_radius));
        circleStrokeWidth = typedArray.getDimensionPixelSize(R.styleable.RangeBarView_circle_stroke_width, getResources().getDimensionPixelSize(R.dimen.circle_stroke_width));
        leftCircleSolidColor = typedArray.getColor(R.styleable.RangeBarView_left_circle_solid_color, ContextCompat.getColor(context, R.color.white));
        leftCircleStrokeColor = typedArray.getColor(R.styleable.RangeBarView_left_circle_stroke_color, rectLineDefaultColor);
        rightCircleSolidColor = typedArray.getColor(R.styleable.RangeBarView_right_circle_solid_color, ContextCompat.getColor(context, R.color.white));
        rightCircleStrokeColor = typedArray.getColor(R.styleable.RangeBarView_right_circle_stroke_color, rectLineDefaultColor);
        rangeTextSize = typedArray.getDimensionPixelSize(R.styleable.RangeBarView_range_text_size, getResources().getDimensionPixelSize(R.dimen.item_text_size));
        rangeTextColor = typedArray.getColor(R.styleable.RangeBarView_range_text_color, ContextCompat.getColor(context, R.color.grey_33));
        rangeTextToProgressSpace = typedArray.getDimensionPixelSize(R.styleable.RangeBarView_view_text_space, getResources().getDimensionPixelSize(R.dimen.view_and_text_space));
        rectDialogWidth = typedArray.getDimensionPixelSize(R.styleable.RangeBarView_rect_price_desc_dialog_width, getResources().getDimensionPixelSize(R.dimen.rect_dialog_width));
        rectDialogColor = typedArray.getColor(R.styleable.RangeBarView_rect_price_desc_dialog_color, rectLineCheckedColor);
        rectDialogCornerRadius = typedArray.getDimensionPixelSize(R.styleable.RangeBarView_rect_price_desc_dialog_corner_radius, getResources().getDimensionPixelSize(R.dimen.rect_dialog_corner_radius));
        rectDialogTextSize = typedArray.getDimensionPixelSize(R.styleable.RangeBarView_rect_price_desc_text_size, getResources().getDimensionPixelSize(R.dimen.rect_dialog_text_size));
        rectDialogTextColor = typedArray.getColor(R.styleable.RangeBarView_rect_price_desc_text_color, ContextCompat.getColor(context, R.color.white));
        rectDialogSpaceToProgress = typedArray.getDimensionPixelSize(R.styleable.RangeBarView_rect_price_desc_space_to_progress, getResources().getDimensionPixelSize(R.dimen.rect_dialog_space_to_progress));

        typedArray.recycle();
    }

    private void initPaints() {
        defaultLinePaint = new Paint();
        defaultLinePaint.setAntiAlias(true);
        defaultLinePaint.setDither(true);
        defaultLinePaint.setColor(rectLineDefaultColor);

        selectedLinePaint = new Paint();
        selectedLinePaint.setAntiAlias(true);
        selectedLinePaint.setDither(true);
        selectedLinePaint.setColor(rectLineCheckedColor);

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setDither(true);
        textPaint.setTextSize(rangeTextSize);
        textPaint.setColor(rangeTextColor);


        //初始化左边实心圆
        leftCirclePaint = setPaint(leftCircleSolidColor, 0, 0f, true);
        //初始化左边圆的边框
        leftCircleStrokePaint = setPaint(0, leftCircleStrokeColor, circleStrokeWidth, false);
        //初始化右边实心圆
        rightCirclePaint = setPaint(rightCircleSolidColor, 0, 0f, true);
        //初始化左边圆的边框
        rightCircleStrokePaint = setPaint(0, rightCircleStrokeColor, circleStrokeWidth, false);
        //价格对话框画笔
        dialogBgPaint = setPaint(rectDialogColor, 0, 0, true);
        //价格对话框文字画笔
        dialogTextPaint = new Paint();
        dialogTextPaint.setAntiAlias(true);
        dialogTextPaint.setDither(true);
        dialogTextPaint.setTextSize(rectDialogTextSize);
        dialogTextPaint.setColor(rectDialogTextColor);

        //默认颜色的圆角矩形线
        defaultCornerLineRect = new RectF();
        //中间选中颜色的圆角矩形
        selectedCornerLineRect = new RectF();
        //数值描述圆角矩形
        numberDescRect = new RectF();
        //画小三角形
        trianglePath = new Path();
        //小三角形的高
        triangleHeight = (int) Math.sqrt(triangleLength * triangleLength - triangleLength / 2 * (triangleLength / 2));
    }

    private Paint setPaint(int bgColor, int strokeColor, float strokeWidth, boolean hasFillStyle) {
        Paint mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(hasFillStyle ? Paint.Style.FILL : Paint.Style.STROKE);
        mPaint.setColor(hasFillStyle ? bgColor : strokeColor);
        mPaint.setStrokeWidth(strokeWidth);
        return mPaint;
    }

    public void setDatas(int minValue, int maxValue, int sliceValue, OnMoveValueListener listener) {
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.sliceValue = sliceValue;
        this.listener = listener;
        int num = (maxValue - minValue) / sliceValue;
        slice = (maxValue - minValue) % sliceValue == 0 ? num : num + 1;
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                //判断手指按下的点是在左边圆圈的位置范围还是在右边圆圈的位置范围
                touchLeftCircle = checkIsLeftOrRight(downX);
                if (touchLeftCircle) {//表示按下的点位于左边圆圈活动范围内
                    leftCircleObj.cx = (int) downX;
                } else {
                    rightCircleObj.cx = (int) downX;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float moveX = event.getX();
                isShowRectDialog = true;
                if (leftCircleObj.cx == rightCircleObj.cx) {//两圆圈重合的情况
                    if (touchLeftCircle) {
                        //极端情况的优化处理，滑动左边圆到达最右边时，再次滑动时设置为左滑，即：继续让左边圆向左滑动
                        if (leftCircleObj.cx == getWidth() - getPaddingRight() - strokeRadius) {
                            touchLeftCircle = true;
                            leftCircleObj.cx = (int) moveX;
                        } else {
                            //当滑动左边圆在中间某处与右边圆重合时，此时再次继续滑动则左边圆处于右边圆位置处不动，右边圆改为向右滑动
                            touchLeftCircle = false;
                            rightCircleObj.cx = (int) moveX;
                        }
                    } else {
                        if (rightCircleObj.cx == getPaddingLeft() + strokeRadius) {
                            touchLeftCircle = false;
                            rightCircleObj.cx = (int) moveX;
                        } else {
                            touchLeftCircle = true;
                            leftCircleObj.cx = (int) moveX;
                        }
                    }
                } else {
                    if (touchLeftCircle) {
                        //滑动左边圆圈时，如果位置等于或者超过右边圆的位置时，设置右边圆圈的坐标给左边圆圈，就相当于左边圆圈停留在右边圆圈之前的位置上，然后移动右边圆圈
                        leftCircleObj.cx = leftCircleObj.cx - rightCircleObj.cx >= 0 ? rightCircleObj.cx : (int) moveX;
                    } else {
                        //同理
                        rightCircleObj.cx = rightCircleObj.cx - leftCircleObj.cx <= 0 ? leftCircleObj.cx : (int) moveX;
                    }
                }
                int leftData1 = getSliceByCoordinate(leftCircleObj.cx) * sliceValue + minValue;
                int rightData1 = getSliceByCoordinate(rightCircleObj.cx) * sliceValue + minValue;
                leftValue = leftData1 > maxValue ? maxValue : leftData1;
                rightValue = rightData1 > maxValue ? maxValue : rightData1;
                break;
            case MotionEvent.ACTION_UP:
                if (touchLeftCircle) {
                    int partsOfLeft = getSliceByCoordinate((int) event.getX());
                    leftCircleObj.cx = leftCircleObj.cx - rightCircleObj.cx >= 0 ? rightCircleObj.cx : partsOfLeft * perSlice + strokeRadius;
                } else {
                    int partsOfRight = getSliceByCoordinate((int) event.getX());
                    rightCircleObj.cx = rightCircleObj.cx - leftCircleObj.cx <= 0 ? leftCircleObj.cx : partsOfRight * perSlice + strokeRadius;
                }

                int leftData = getSliceByCoordinate(leftCircleObj.cx) * sliceValue + minValue;
                int rightData = getSliceByCoordinate(rightCircleObj.cx) * sliceValue + minValue;
                leftValue = leftData > maxValue ? maxValue : leftData;
                rightValue = rightData > maxValue ? maxValue : rightData;
                //回调
                if (listener != null) {
                    listener.onMoveValue(leftValue, rightValue);
                }
                break;
        }

        //防止越界处理
        if (touchLeftCircle) {
            if (leftCircleObj.cx > rightCircleObj.cx) {
                leftCircleObj.cx = rightCircleObj.cx;
            } else {
                if (leftCircleObj.cx < getPaddingLeft() + strokeRadius) {
                    leftCircleObj.cx = getPaddingLeft() + strokeRadius;
                }
                if (leftCircleObj.cx > getWidth() - getPaddingRight() - strokeRadius) {
                    leftCircleObj.cx = getWidth() - getPaddingRight() - strokeRadius;
                }
            }
        } else {
            if (leftCircleObj.cx > rightCircleObj.cx) {
                rightCircleObj.cx = leftCircleObj.cx;
            } else {
                if (rightCircleObj.cx > getWidth() - getPaddingRight() - strokeRadius) {
                    rightCircleObj.cx = getWidth() - getPaddingRight() - strokeRadius;
                }

                if (rightCircleObj.cx < getPaddingLeft() + strokeRadius) {
                    rightCircleObj.cx = getPaddingLeft() + strokeRadius;
                }


            }
        }

        //由于按下的点的坐标在改变，所以中间的圆角矩形选中的范围坐标也要跟着改变
        selectedCornerLineRect.left = leftCircleObj.cx;
        selectedCornerLineRect.right = rightCircleObj.cx;
        //不管是滑动左边圆还是右边圆，都要计算两圆间的中心距离作为展示价格进度框的中心点
        numberDescRect.left = (rightCircleObj.cx + leftCircleObj.cx) / 2 - rectDialogWidth / 2;
        numberDescRect.right = (rightCircleObj.cx + leftCircleObj.cx) / 2 + rectDialogWidth / 2;

        invalidate();
        return true;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //控件实际宽度 = 宽度w(包含内边距的) - paddingLeft - paddingRight;
        Log.e("TAG", "宽----> " + w);

        realWidth = w - getPaddingLeft() - getPaddingRight();
        strokeRadius = circleRadius + circleStrokeWidth;
        rectDialogHeightAndSpace = rectDialogCornerRadius * 2 + rectDialogSpaceToProgress;
        //左边圆的圆心坐标
        leftCircleObj = new CirclePoint();
        leftCircleObj.cx = getPaddingLeft() + strokeRadius;
        leftCircleObj.cy = getPaddingTop() + rectDialogHeightAndSpace + strokeRadius;
        //右边圆的圆心坐标
        rightCircleObj = new CirclePoint();
        rightCircleObj.cx = w - getPaddingRight() - strokeRadius;
        rightCircleObj.cy = getPaddingTop() + rectDialogHeightAndSpace + strokeRadius;
        //默认圆角矩形进度条
        setDefaultLine(w);
        //选中状态圆角矩形进度条
        selectedCornerLineRect.left = leftCircleObj.cx;
        selectedCornerLineRect.top = getPaddingTop() + rectDialogHeightAndSpace + strokeRadius - rectLineCornerRadius;
        selectedCornerLineRect.right = rightCircleObj.cx;
        selectedCornerLineRect.bottom = getPaddingTop() + rectDialogHeightAndSpace + strokeRadius + rectLineCornerRadius;
        //数值描述圆角矩形
        numberDescRect.left = w / 2 - rectDialogWidth / 2;
        numberDescRect.top = getPaddingTop();
        numberDescRect.right = w / 2 + rectDialogWidth / 2;
        numberDescRect.bottom = getPaddingTop() + rectDialogCornerRadius * 2;
        //每一份对应的距离
        perSlice = (realWidth - strokeRadius * 2) / slice;
    }

    private void setDefaultLine(int w) {
        //默认圆角矩形进度条
        rectLineCornerRadius = rectLineHeight / 2;//圆角半径
        defaultCornerLineRect.left = getPaddingLeft() + strokeRadius;
        defaultCornerLineRect.top = getPaddingTop() + rectDialogHeightAndSpace + strokeRadius - rectLineCornerRadius;
        defaultCornerLineRect.right = w - getPaddingRight() - strokeRadius;
        defaultCornerLineRect.bottom = getPaddingTop() + rectDialogHeightAndSpace + strokeRadius + rectLineCornerRadius;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //绘制中间圆角矩形线
        drawDefaultCornerRectLine(canvas);
        //绘制两圆之间的圆角矩形
        drawSelectedRectLine(canvas);
        //画左边圆以及圆的边框
        drawLeftCircle(canvas);
        //画右边圆以及圆的边框
        drawRightCircle(canvas);
        //绘制文字
        drawBottomText(canvas);
        //绘制描述信息圆角矩形弹窗
        drawRectDialog(canvas);
        //绘制描述信息弹窗中的文字
        drawTextOfRectDialog(canvas);
        //绘制小三角形
        drawSmallTriangle(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width, height;
        int wSize = getPaddingLeft() + circleRadius * 2 + getPaddingRight() + circleStrokeWidth * 2;
        int hSize = getPaddingTop() + rectDialogCornerRadius * 2 + triangleHeight + rectDialogSpaceToProgress + circleRadius * 2 + circleStrokeWidth * 2 + rangeTextToProgressSpace + rangeTextSize + getPaddingBottom();

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = Math.min(widthSize, wSize);
        } else {
            width = wSize;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(heightSize, hSize);
        } else {
            height = hSize;
        }
        Log.e("TAG", "宽onMeasure----> " + width);
        setMeasuredDimension(width, height);
    }
//------------------------绘制函数-----------------------------------

    /**
     * 绘制中间圆角矩形线
     **/
    private void drawDefaultCornerRectLine(Canvas canvas) {
        canvas.drawRoundRect(defaultCornerLineRect, rectLineCornerRadius, rectLineCornerRadius, defaultLinePaint);
    }

    /**
     * 绘制两圆之间的圆角矩形
     **/
    private void drawSelectedRectLine(Canvas canvas) {
        canvas.drawRoundRect(selectedCornerLineRect, rectLineCornerRadius, rectLineCornerRadius, selectedLinePaint);
    }

    /**
     * 画左边圆以及圆的边框
     **/
    private void drawLeftCircle(Canvas canvas) {
        canvas.drawCircle(leftCircleObj.cx, leftCircleObj.cy, circleRadius, leftCirclePaint);
        canvas.drawCircle(leftCircleObj.cx, leftCircleObj.cy, circleRadius, leftCircleStrokePaint);
    }

    /**
     * 画右边圆以及圆的边框
     **/
    private void drawRightCircle(Canvas canvas) {
        canvas.drawCircle(rightCircleObj.cx, rightCircleObj.cy, circleRadius, rightCirclePaint);
        canvas.drawCircle(rightCircleObj.cx, rightCircleObj.cy, circleRadius, rightCircleStrokePaint);
    }

    /**
     * 绘制底部刻度文字
     **/
    private void drawBottomText(Canvas canvas) {
        textPaint.setColor(rangeTextColor);
        textPaint.setTextSize(rangeTextSize);
        for (int i = 0; i <= slice; i++) {
            int value = i * sliceValue > maxValue ? maxValue : i * sliceValue + minValue;
            String text = String.valueOf(value);
            float textWidth = textPaint.measureText(text);
            canvas.drawText(text, i * perSlice - textWidth / 2 + (getPaddingLeft() + strokeRadius), getPaddingTop() + rectDialogHeightAndSpace + strokeRadius * 2 + rangeTextToProgressSpace + rangeTextSize / 2, textPaint);
        }
    }

    /**
     * 绘制描述信息圆角矩形弹窗
     **/
    private void drawRectDialog(Canvas canvas) {
        if (isShowRectDialog) {
            dialogBgPaint.setColor(rectDialogColor);
            canvas.drawRoundRect(numberDescRect, rectDialogCornerRadius, rectDialogCornerRadius, dialogBgPaint);
        }
    }

    /**
     * 绘制描述信息弹窗中的文字
     **/
    private void drawTextOfRectDialog(Canvas canvas) {
        if (leftValue == minValue && (rightValue == maxValue || rightValue < maxValue)) {
            textDesc = rightValue + "万以下";
        } else if (leftValue > minValue && rightValue == maxValue) {
            textDesc = leftValue + "万以上";
        } else if (leftValue > minValue && rightValue < maxValue) {
            if (leftValue == rightValue) {
                textDesc = rightValue + "万以下";
            } else
                textDesc = leftValue + "-" + rightValue + "万";
        }

        if (isShowRectDialog) {
            textPaint.setColor(rectDialogTextColor);
            textPaint.setTextSize(rectDialogTextSize);
            float textWidth = textPaint.measureText(textDesc);
            float textLeft = numberDescRect.left + rectDialogWidth / 2 - textWidth / 2;
            canvas.drawText(textDesc, textLeft, getPaddingTop() + rectDialogCornerRadius + rectDialogTextSize / 4, dialogTextPaint);
        }
    }

    /**
     * 绘制小三角形
     **/
    private void drawSmallTriangle(Canvas canvas) {
        if (isShowRectDialog) {
            trianglePath.reset();
            trianglePath.moveTo(numberDescRect.left + rectDialogWidth / 2 - triangleLength / 2, getPaddingTop() + rectDialogCornerRadius * 2);
            trianglePath.lineTo(numberDescRect.left + rectDialogWidth / 2 + triangleLength / 2, getPaddingTop() + rectDialogCornerRadius * 2);
            trianglePath.lineTo(numberDescRect.left + rectDialogWidth / 2, getPaddingTop() + rectDialogCornerRadius * 2 + triangleHeight);
            trianglePath.close();
            canvas.drawPath(trianglePath, dialogBgPaint);
        }
    }
//------------------------绘制函数结束-----------------------------------

    /**
     * 检测在左边还是右边
     *
     * @return true:左边 false：右边
     **/
    private boolean checkIsLeftOrRight(float downX) {
        //如果按下的区域位于左边区域，则按下坐标downX的值就会比较小(即按下坐标点在左边)，那么leftCircleObj.cx - downX的绝对值也会比较小
        //rightCircleObj.cx - downX绝对值肯定是大于leftCircleObj.cx - downX绝对值的，两者相减肯定是小于0的
        if (Math.abs(leftCircleObj.cx - downX) - Math.abs(rightCircleObj.cx - downX) > 0) {//表示按下的区域位于右边
            return false;
        }
        return true;
    }

    /**
     * 根据滑动距离获取所占份数
     **/
    private int getSliceByCoordinate(int moveDistance) {
        //此位置坐标对应的距离能分多少份
        int parts = moveDistance / perSlice;//总距离 / 每一份的距离
        parts = moveDistance % perSlice >= perSlice / 2 ? parts + 1 : parts;
        Log.e("TAG", "左边-----> moveDistance：" + moveDistance);
        Log.e("TAG", "左边-----> perSlice：" + perSlice);
        Log.e("TAG", "左边-----> parts：" + parts);
        return parts > slice ? slice : parts;
    }

    public void setRectLineHeight(int lineHeightDp) {
        rectLineHeight = (int) (lineHeightDp * mDensity);
        invalidate();
    }

    public void setRectLineDefaultColor(int color) {
        rectLineDefaultColor = color;
        invalidate();
    }

    public void setRectLineCheckedColor(int color) {
        rectLineCheckedColor = color;
        invalidate();
    }

    public void setCircleRadius(int circleRadiusDp) {
        circleRadius = (int) (circleRadiusDp * mDensity);
        invalidate();
    }

    public void setCircleStrokeWidth(int circleStrokeWidthDp) {
        circleStrokeWidth = (int) (circleStrokeWidthDp * mDensity);
        invalidate();
    }

    public void setLeftCircleSolidColor(int color) {
        leftCircleSolidColor = color;
        invalidate();
    }

    public void setLeftCircleStrokeColor(int color) {
        leftCircleStrokeColor = color;
        invalidate();
    }

    public void setRightCircleSolidColor(int color) {
        rightCircleSolidColor = color;
        invalidate();
    }

    public void setRightCircleStrokeColor(int color) {
        rightCircleStrokeColor = color;
        invalidate();
    }

    public void setRangeTextSize(int rangeTextSizeDp) {
        rangeTextSize = (int) (rangeTextSizeDp * mDensity);
        invalidate();
    }

    public void setRangeTextColor(int color) {
        rangeTextColor = color;
        invalidate();
    }

    public void setRangeTextToProgressSpace(int viewTextSpaceDp) {
        rangeTextToProgressSpace = (int) (viewTextSpaceDp * mDensity);
        invalidate();
    }

    public void setDialogWidth(int dialogWidthDp) {
        rectDialogWidth = (int) (dialogWidthDp * mDensity);
        invalidate();
    }

    public void setDialogColor(int color) {
        rectDialogColor = color;
        invalidate();
    }

    public void setDialogCornerRadius(int dialogCornerRadiusDp) {
        rectDialogCornerRadius = (int) (dialogCornerRadiusDp * mDensity);
        invalidate();
    }

    public void setDialogTextSize(int dialogTextSizeDp) {
        rectDialogTextSize = (int) (dialogTextSizeDp * mDensity);
        invalidate();
    }

    public void setDialogTextColor(int color) {
        rectDialogTextColor = color;
        invalidate();
    }

    public void setDialogToProgressSpace(int dialogToProgressSpaceDp) {
        rectDialogSpaceToProgress = (int) (dialogToProgressSpaceDp * mDensity);
        invalidate();
    }

    public interface OnMoveValueListener {
        void onMoveValue(int leftValue, int rightValue);
    }

    private class CirclePoint {
        //圆的圆心坐标
        public int cx;
        public int cy;
    }
}
