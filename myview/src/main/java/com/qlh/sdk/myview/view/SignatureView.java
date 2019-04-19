package com.qlh.sdk.myview.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.qlh.sdk.myview.constant.Constant;
import com.qlh.sdk.myview.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 作者：QLH on 2018-12-26
 * 描述：手写签名
 */
public class SignatureView extends View {

    /**
     * 笔画X坐标起点
     */
    private float mX;
    /**
     * 笔画Y坐标起点
     */
    private float mY;
    /**
     * 手写画笔
     */
    private final Paint mGesturePaint = new Paint();
    /**
     * 路径
     */
    private final Path mPath = new Path();
    /**
     * 签名画布
     */
    private Canvas cacheCanvas;
    /**
     * 保存签名的bitmap
     */
    private Bitmap cacheBitmap;
    /**
     * 是否已经签名
     */
    private boolean isSignature = false;
    /**
     * 画笔宽度 dp
     */
    private int mPaintWidth = 3;
    /**
     * 画笔颜色
     */
    private int mPaintColor;
    /**
     * 画布颜色（指最终签名结果文件的背景颜色，默认为透明色）
     */
    private int mBackColor;

    /**签名开始与结束**/
    private Touch touch;

    //密度
    private float mDensity;

    public SignatureView(Context context) {
        this(context,null);
    }

    public SignatureView(Context context, @Nullable AttributeSet attrs) {
        this(context,attrs,0);
    }

    public SignatureView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mDensity = getResources().getDisplayMetrics().density;
        handleStyleable(context,attrs,defStyleAttr);
        init();
    }

    private void handleStyleable(Context context, AttributeSet attrs, int defStyle) {

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SignatureView,defStyle,0);
        mPaintWidth = ta.getDimensionPixelSize(R.styleable.SignatureView_sv_paint_width, (int) (mPaintWidth*mDensity));
        mPaintColor = ta.getColor(R.styleable.SignatureView_sv_paint_color,Color.BLACK);
        mBackColor = ta.getColor(R.styleable.SignatureView_sv_canvas_color,Color.TRANSPARENT);
        ta.recycle();
    }

    private void init(){
        //设置抗锯齿
        mGesturePaint.setAntiAlias(true);
        //设置签名笔画样式
        mGesturePaint.setStyle(Paint.Style.STROKE);
        //设置笔画宽度
        mGesturePaint.setStrokeWidth(mPaintWidth);
        //设置签名颜色
        mGesturePaint.setColor(mPaintColor);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //创建跟view一样大的bitmap，用来保存签名
        cacheBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        cacheCanvas = new Canvas(cacheBitmap);
        cacheCanvas.drawColor(mBackColor);
        isSignature = false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchDown(event);
                if (touch != null) touch.OnTouch(Constant.ACTION_DOWN);
                break;
            case MotionEvent.ACTION_MOVE:
                isSignature = true;
                touchMove(event);
                if (touch != null) touch.OnTouch(Constant.ACTION_MOVE);
                break;
            case MotionEvent.ACTION_UP:
                //将路径画到bitmap中，即一次笔画完成才去更新bitmap，而手势轨迹是实时显示在画板上的。
                cacheCanvas.drawPath(mPath, mGesturePaint);
                mPath.reset();
                if (touch != null) touch.OnTouch(Constant.ACTION_UP);
                break;
        }
        // 更新绘制
        invalidate();
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //画此次笔画之前的签名
        canvas.drawBitmap(cacheBitmap, 0, 0, mGesturePaint);
        // 通过画布绘制多点形成的图形
        canvas.drawPath(mPath, mGesturePaint);
    }

    //---------------------------------------private-----------------------------------------

    // 手指点下屏幕时调用
    private void touchDown(MotionEvent event) {
        // 重置绘制路线
        mPath.reset();
        float x = event.getX();
        float y = event.getY();
        mX = x;
        mY = y;
        // mPath绘制的绘制起点
        mPath.moveTo(x, y);
    }

    // 手指在屏幕上滑动时调用
    private void touchMove(MotionEvent event) {
        final float x = event.getX();
        final float y = event.getY();
        final float previousX = mX;
        final float previousY = mY;
        final float dx = Math.abs(x - previousX);
        final float dy = Math.abs(y - previousY);
        // 两点之间的距离大于等于3时，生成贝塞尔绘制曲线
        if (dx >= 3 || dy >= 3) {
            // 设置贝塞尔曲线的操作点为起点和终点的一半
            float cX = (x + previousX) / 2;
            float cY = (y + previousY) / 2;
            // 二次贝塞尔，实现平滑曲线；previousX, previousY为操作点，cX, cY为终点
            mPath.quadTo(previousX, previousY, cX, cY);
            // 第二次执行时，第一次结束调用的坐标值将作为第二次调用的初始坐标值
            mX = x;
            mY = y;
        }
    }

    /**
     * 逐行扫描 清楚边界空白。
     *
     * @param bp
     * @param blank 边距留多少个像素
     * @return
     */
    private Bitmap clearBlank(Bitmap bp, int blank) {
        int HEIGHT = bp.getHeight();
        int WIDTH = bp.getWidth();
        int top = 0, left = 0, right = 0, bottom = 0;
        int[] pixs = new int[WIDTH];
        boolean isStop;
        //扫描上边距不等于背景颜色的第一个点
        for (int y = 0; y < HEIGHT; y++) {
            bp.getPixels(pixs, 0, WIDTH, 0, y, WIDTH, 1);
            isStop = false;
            for (int pix : pixs) {
                if (pix != mBackColor) {
                    top = y;
                    isStop = true;
                    break;
                }
            }
            if (isStop) {
                break;
            }
        }
        //扫描下边距不等于背景颜色的第一个点
        for (int y = HEIGHT - 1; y >= 0; y--) {
            bp.getPixels(pixs, 0, WIDTH, 0, y, WIDTH, 1);
            isStop = false;
            for (int pix : pixs) {
                if (pix != mBackColor) {
                    bottom = y;
                    isStop = true;
                    break;
                }
            }
            if (isStop) {
                break;
            }
        }
        pixs = new int[HEIGHT];
        //扫描左边距不等于背景颜色的第一个点
        for (int x = 0; x < WIDTH; x++) {
            bp.getPixels(pixs, 0, 1, x, 0, 1, HEIGHT);
            isStop = false;
            for (int pix : pixs) {
                if (pix != mBackColor) {
                    left = x;
                    isStop = true;
                    break;
                }
            }
            if (isStop) {
                break;
            }
        }
        //扫描右边距不等于背景颜色的第一个点
        for (int x = WIDTH - 1; x > 0; x--) {
            bp.getPixels(pixs, 0, 1, x, 0, 1, HEIGHT);
            isStop = false;
            for (int pix : pixs) {
                if (pix != mBackColor) {
                    right = x;
                    isStop = true;
                    break;
                }
            }
            if (isStop) {
                break;
            }
        }
        if (blank < 0) {
            blank = 0;
        }
        //计算加上保留空白距离之后的图像大小
        left = left - blank > 0 ? left - blank : 0;
        top = top - blank > 0 ? top - blank : 0;
        right = right + blank > WIDTH - 1 ? WIDTH - 1 : right + blank;
        bottom = bottom + blank > HEIGHT - 1 ? HEIGHT - 1 : bottom + blank;
        return Bitmap.createBitmap(bp, left, top, right - left, bottom - top);
    }

    //------------------------------------public--------------------------------------------
    /**
     * 清除画板
     */
    public void clear() {
        if (cacheCanvas != null) {
            isSignature = false;
            //更新画板信息
            mGesturePaint.setColor(mPaintColor);
            cacheCanvas.drawColor(mBackColor, PorterDuff.Mode.CLEAR);
            mGesturePaint.setColor(mPaintColor);
            invalidate();
        }
    }

    public Touch getTouch() {
        return touch;
    }

    public void setTouch(Touch touch) {
        this.touch = touch;
    }

    public interface Touch {

        void OnTouch(int action);
    }

    /**
     * 保存画板，默认格式png
     *
     * @param path 保存到路径
     */
    public void save(String path) throws IOException {
        save(path, false, 0,Bitmap.CompressFormat.PNG);
    }

    /**
     * 保存画板
     *
     * @param path       保存到路径
     * @param clearBlank 是否清除边缘空白区域
     * @param blank      要保留的边缘空白距离
     */
    public void save(String path, boolean clearBlank, int blank, Bitmap.CompressFormat format) throws IOException {

        Bitmap bitmap = cacheBitmap;
        //BitmapUtil.createScaledBitmapByHeight(srcBitmap, 300);//  压缩图片
        if (clearBlank) {
            bitmap = clearBlank(bitmap, blank);
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(format, 100, bos);
        byte[] buffer = bos.toByteArray();
        if (buffer != null) {
            File file = new File(path);
            if (file.exists()) {
                file.delete();
            }
            OutputStream outputStream = new FileOutputStream(file);
            outputStream.write(buffer);
            outputStream.close();
        }
    }

    /**
     * 设置画笔宽度 默认宽度为3dp
     *
     * @param mPaintWidth
     */
    public void setPaintWidth(int mPaintWidth) {
        mPaintWidth = mPaintWidth > 0 ? mPaintWidth : 10;
        this.mPaintWidth = mPaintWidth;
        mGesturePaint.setStrokeWidth(mPaintWidth);

    }


    /**
     * 设置画布背景色，默认透明
     *
     * @param backColor
     */
    public void setBackColor(@ColorInt int backColor) {
        mBackColor = backColor;
    }


    /**
     * 设置画笔颜色，默认黑色
     *
     * @param mPaintColor
     */
    public void setPaintColor(int mPaintColor) {
        this.mPaintColor = mPaintColor;
        mGesturePaint.setColor(mPaintColor);
    }

    /**
     * 是否有签名
     *
     * @return
     */
    public boolean getSignature() {
        return isSignature;
    }
}
