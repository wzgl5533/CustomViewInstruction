package com.qlh.sdk.myview.button;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.qlh.sdk.myview.R;

/**
 * 作者：QLH on 2018-12-27
 * 描述：带文字的切换按钮
 */
public class TextSwitchButton extends View implements View.OnTouchListener {

    private final int RECT = 0, ROUND = 1;
    private final int animTime = 100;//滑动动画时间
    float leftTextWidth, rightTextWidth; //标记文本的宽度
    //记录文本中心点 cx1:绘制文本1的x坐标  cx2:绘制文本2的x坐标
    //cy记录绘制文本的高度
    float cx1, cy, cx2;
    private String leftText; //左文本
    private String rightText; //右文本
    private int normalColor;//正常文本颜色
    private int selectedColor;//选中文本颜色
    float startX, lastX, disX;  //startX 标记按下的X坐标,  lastX标记移动后的X坐标 ,disX移动的距离
    private int bgColor;
    private int btnColor;
    private int shape = 0;//矩形 圆角（包括圆形）
    //画笔
    private Paint textPaint;
    private Paint bgPaint;
    private Paint btnPaint;
    private int leftDis = 0;//当前距离左边距离
    private int slidingMax;//标记最大滑动距离
    private boolean mRight = false; //标记按钮滑动方向
    private boolean isClickable;//标记是否点击事件
    private boolean isMove; //标记是否移动
    private LeftSelectedListener leftSelectedListener;//左边事件监听器
    private RightSelectedListener rightSelectedListener;//右边事件监听器
    private int textSize = 12; //定义文本大小 dp
    private float mDensity; //密度
    private int btnWidth = 40; //按钮宽度 dp
    private int cornerRadius = 5; //圆角半径
    private RectF bgRectF;//背景区域
    private RectF btnRectF;//按钮区域


    public TextSwitchButton(Context context) {
        this(context, null);
    }

    public TextSwitchButton(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TextSwitchButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mDensity = getResources().getDisplayMetrics().density;
        handleStyleable(context, attrs, defStyleAttr);
        init();
    }

    private void handleStyleable(Context context, AttributeSet attrs, int defStyle) {

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TextSwitchButton);
        bgColor = ta.getColor(R.styleable.TextSwitchButton_tsb_bg_color, getResources().getColor(R.color.white));
        btnColor = ta.getColor(R.styleable.TextSwitchButton_tsb_btn_color, getResources().getColor(R.color.black));
        normalColor = ta.getColor(R.styleable.TextSwitchButton_tsb_text_normal_color,getResources().getColor(R.color.black));
        selectedColor = ta.getColor(R.styleable.TextSwitchButton_tsb_text_selected_color,getResources().getColor(R.color.white));
        btnWidth = ta.getDimensionPixelSize(R.styleable.TextSwitchButton_tsb_btn_width, (int) (btnWidth * mDensity));
        cornerRadius = ta.getDimensionPixelSize(R.styleable.TextSwitchButton_tsb_corner_radius, (int) (cornerRadius * mDensity));
        shape = ta.getInt(R.styleable.TextSwitchButton_tsb_shape, RECT);
        leftText = ta.getString(R.styleable.TextSwitchButton_tsb_left_text);
        rightText = ta.getString(R.styleable.TextSwitchButton_tsb_right_text);
        textSize = ta.getDimensionPixelSize(R.styleable.TextSwitchButton_tsb_text_size, (int) (textSize * mDensity));
        //注意此操作
        ta.recycle();
    }

    private void init() {

        textPaint = new Paint();
        bgPaint = new Paint();
        btnPaint = new Paint();

        textPaint.setAntiAlias(true);
        textPaint.setTextSize(textSize);

        setOnTouchListener(this);
        bgPaint.setColor(bgColor);
        bgPaint.setStyle(Paint.Style.FILL);//全部填充
        bgPaint.setAntiAlias(true);

        btnPaint.setColor(btnColor);
        btnPaint.setStyle(Paint.Style.FILL);
        btnPaint.setStrokeWidth(10);
        btnPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (shape == RECT) {//矩形
            canvas.drawRect(bgRectF, bgPaint);  //绘制背景
            canvas.drawRect(btnRectF, btnPaint); //绘制按钮
        } else if (shape == ROUND) {//圆角
            canvas.drawRoundRect(bgRectF, cornerRadius, cornerRadius, bgPaint);
            canvas.drawRoundRect(btnRectF, cornerRadius, cornerRadius, btnPaint);
        }
        if (!mRight) {
            textPaint.setColor(selectedColor);
            canvas.drawText(leftText, cx1, cy, textPaint);
            textPaint.setColor(normalColor);
            canvas.drawText(rightText, cx2, cy, textPaint);
        } else {
            textPaint.setColor(normalColor);
            canvas.drawText(leftText, cx1, cy, textPaint);
            textPaint.setColor(selectedColor);
            canvas.drawText(rightText, cx2, cy, textPaint);

        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        //设置背景和按钮绘制参数
        bgRectF = new RectF(0, 0, getWidth(), getHeight());
        btnRectF = new RectF(leftDis, 0,
                btnWidth + leftDis, getHeight());

        slidingMax = getWidth() - btnWidth;
        leftTextWidth = textPaint.measureText(leftText);
        rightTextWidth = textPaint.measureText(rightText);
        //计算左边或右边文本绘制的x坐标
        cx1 = btnWidth / 2 - leftTextWidth / 2;
        cx2 = getWidth() - btnWidth / 2 - rightTextWidth / 2;
        //测量绘制文本高度
        //Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        //float fontHeight = fontMetrics.bottom - fontMetrics.top;
        Paint.FontMetricsInt fontMetricsInt = textPaint.getFontMetricsInt();
        cy = getHeight() / 2 - fontMetricsInt.descent + (fontMetricsInt.descent - fontMetricsInt.ascent) / 2;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                isClickable = true;
                startX = event.getX();
                isMove = false;
                break;
            case MotionEvent.ACTION_MOVE:
                lastX = event.getX();
                disX = lastX - startX;
                if (Math.abs(disX) < 5) break;
                isMove = true;
                isClickable = false;
                moveBtn();
                startX = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                if (isClickable) {
                    clickSmoothSlide();
                }
                if (isMove) {
                    if (leftDis > slidingMax / 2) {
                        mRight = false;
                    } else {
                        mRight = true;
                    }
                    flushView();
                }
                break;
        }
        return true;
    }

    //----------------------------------------private-----------------------------------------------
    //移动后判断位置
    private void moveBtn() {
        leftDis += disX;
        if (leftDis > slidingMax) {
            leftDis = slidingMax;
        } else if (leftDis < 0) {
            leftDis = 0;
        }
        btnRectF.left = leftDis;
        btnRectF.right = btnWidth + btnRectF.left;
        invalidate();
    }

    /**
     * 点击平滑移动
     **/
    private void clickSmoothSlide() {

        mRight = !mRight;
        ValueAnimator valueAnimator = ValueAnimator.ofInt(0, slidingMax);
        valueAnimator.setDuration(animTime);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                if (mRight) {//向右
                    btnRectF.left = value;
                    btnRectF.right = btnWidth + value;
                } else {//向左
                    btnRectF.left = slidingMax - value ;
                    btnRectF.right = btnRectF.left + btnWidth;
                }
                invalidate();
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (mRight) {
                    if (rightSelectedListener != null) {
                        rightSelectedListener.rightSelect();
                    }
                } else {
                    if (leftSelectedListener != null) {
                        leftSelectedListener.leftSelect();
                    }
                }
            }
        });
        valueAnimator.start();
    }

    //刷新视图
    private void flushView() {
        mRight = !mRight;
        if (mRight) {
            leftDis = slidingMax;
            if (rightSelectedListener != null) {
                rightSelectedListener.rightSelect();
            }
        } else {
            leftDis = 0;
            if (leftSelectedListener != null) {
                leftSelectedListener.leftSelect();
            }
        }
        btnRectF.left = leftDis;
        btnRectF.right = btnWidth + btnRectF.left;
        invalidate();
    }

    //----------------------------------------public------------------------------------------------
    //设置左边按钮点击事件监听器
    public void setLeftSelectedListener(LeftSelectedListener leftSelectedListener) {
        this.leftSelectedListener = leftSelectedListener;
    }

    //设置右边按钮点击事件监听器
    public void setRightSelectedListener(RightSelectedListener rightSelectedListener) {
        this.rightSelectedListener = rightSelectedListener;
    }

    //选中左边事件
    public interface LeftSelectedListener {
        void leftSelect();
    }

    //选中右边事件
    public interface RightSelectedListener {
        void rightSelect();
    }

}
