/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.qlh.sdk.myview.camera;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.os.ParcelableCompat;
import android.support.v4.os.ParcelableCompatCreatorCallbacks;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.widget.FrameLayout;


import com.qlh.sdk.myview.R;
import com.qlh.sdk.myview.app.AppConstant;
import com.qlh.sdk.myview.camera.base.AspectRatio;
import com.qlh.sdk.myview.camera.base.Constants;
import com.qlh.sdk.myview.camera.base.DisplayOrientationDetector;
import com.qlh.sdk.myview.camera.base.Size;
import com.qlh.sdk.myview.utils.DateUtil;
import com.qlh.sdk.myview.utils.ImageUtils;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;


public class CameraView extends FrameLayout {

    /**
     * The camera device faces the opposite direction as the device's screen.
     */
    public static final int FACING_BACK = Constants.FACING_BACK;

    /**
     * The camera device faces the same direction as the device's screen.
     */
    public static final int FACING_FRONT = Constants.FACING_FRONT;
    /**
     * Flash will not be fired.
     */
    public static final int FLASH_OFF = Constants.FLASH_OFF;
    /**
     * Flash will always be fired during snapshot.
     */
    public static final int FLASH_ON = Constants.FLASH_ON;
    /**
     * Constant emission of light during preview, auto-focus and snapshot.
     */
    public static final int FLASH_TORCH = Constants.FLASH_TORCH;
    /**
     * Flash will be fired automatically when required.
     */
    public static final int FLASH_AUTO = Constants.FLASH_AUTO;
    /**
     * Flash will be fired in red-eye reduction mode.
     */
    public static final int FLASH_RED_EYE = Constants.FLASH_RED_EYE;
    private final CallbackBridge mCallbacks;
    private final DisplayOrientationDetector mDisplayOrientationDetector;
    protected CameraImpl mImpl;
    private boolean mAdjustViewBounds;

    public CameraView(Context context) {
        this(context, null);
    }

    public CameraView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CameraView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (isInEditMode()) {
            mCallbacks = null;
            mDisplayOrientationDetector = null;
            return;
        }
        mCallbacks = new CallbackBridge();
        mImpl = new CameraImpl(mCallbacks, new TextureViewPreview(context, this));
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CameraView, defStyleAttr,
                R.style.Widget_CameraView);
        mAdjustViewBounds = a.getBoolean(R.styleable.CameraView_android_adjustViewBounds, false);
        setFacing(a.getInt(R.styleable.CameraView_cv_facing, FACING_BACK));
        String aspectRatio = a.getString(R.styleable.CameraView_cv_aspectRatio);
        if (aspectRatio != null) {
            setAspectRatio(AspectRatio.parse(aspectRatio));
        } else {
            setAspectRatio(Constants.DEFAULT_ASPECT_RATIO);
        }
        setAutoFocus(a.getBoolean(R.styleable.CameraView_cv_autoFocus, true));
        setFlash(a.getInt(R.styleable.CameraView_cv_flash, Constants.FLASH_AUTO));
        a.recycle();
        // Display orientation detector
        mDisplayOrientationDetector = new DisplayOrientationDetector(context) {
            @Override
            public void onDisplayOrientationChanged(int displayOrientation) {
                mImpl.setDisplayOrientation(displayOrientation);
            }
        };
    }

    public int getPreviewRotation() {
        return mImpl.getPreviewRotation();
    }

    public CameraImpl getCamera() {
        return mImpl;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!isInEditMode()) {
            mDisplayOrientationDetector.enable(ViewCompat.getDisplay(this));
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        if (!isInEditMode()) {
            mDisplayOrientationDetector.disable();
        }
        super.onDetachedFromWindow();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (isInEditMode()) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        // Handle android:adjustViewBounds
        if (mAdjustViewBounds) {
            if (!isCameraOpened()) {
                mCallbacks.reserveRequestLayoutOnOpen();
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                return;
            }
            final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
            final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
            if (widthMode == MeasureSpec.EXACTLY && heightMode != MeasureSpec.EXACTLY) {
                final AspectRatio ratio = getAspectRatio();
                assert ratio != null;
                int height = (int) (MeasureSpec.getSize(widthMeasureSpec) * ratio.toFloat());
                if (heightMode == MeasureSpec.AT_MOST) {
                    height = Math.min(height, MeasureSpec.getSize(heightMeasureSpec));
                }
                super.onMeasure(widthMeasureSpec,
                        MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
            } else if (widthMode != MeasureSpec.EXACTLY && heightMode == MeasureSpec.EXACTLY) {
                final AspectRatio ratio = getAspectRatio();
                assert ratio != null;
                int width = (int) (MeasureSpec.getSize(heightMeasureSpec) * ratio.toFloat());
                if (widthMode == MeasureSpec.AT_MOST) {
                    width = Math.min(width, MeasureSpec.getSize(widthMeasureSpec));
                }
                super.onMeasure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                        heightMeasureSpec);
            } else {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            }
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
        // Measure the TextureView
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        AspectRatio ratio = getAspectRatio();
        if (mDisplayOrientationDetector.getLastKnownDisplayOrientation() % 180 == 0) {
            ratio = ratio.inverse();
        }
        assert ratio != null;
        if (height < width * ratio.getY() / ratio.getX()) {
            mImpl.getView().measure(
                    MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(width * ratio.getY() / ratio.getX(),
                            MeasureSpec.EXACTLY));
        } else {
            mImpl.getView().measure(
                    MeasureSpec.makeMeasureSpec(height * ratio.getX() / ratio.getY(),
                            MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
        }
    }

    /**
     * @return {@code true} if the camera is opened.
     */
    public boolean isCameraOpened() {
        return mImpl.isCameraOpened();
    }

    /**
     * Gets the current aspect ratio of camera.
     *
     * @return The current {@link AspectRatio}. Can be {@code null} if no camera is opened yet.
     */
    @Nullable
    public AspectRatio getAspectRatio() {
        return mImpl.getAspectRatio();
    }

    /**
     * Sets the aspect ratio of camera.
     *
     * @param ratio The {@link AspectRatio} to be set.
     */
    public void setAspectRatio(@NonNull AspectRatio ratio) {
        if (mImpl.setAspectRatio(ratio)) {
            requestLayout();
        }
    }

    /**
     * Open a camera device and start showing camera preview. This is typically called from
     * {@link Activity#onResume()}.
     */
    public void start() {
        if (!mImpl.start()) {
            //store the state ,and restore this state after fall back o CameraImpl
            Parcelable state = onSaveInstanceState();
            // Camera2 uses legacy hardware layer; fall back to CameraImpl
            mImpl = new CameraImpl(mCallbacks, new TextureViewPreview(getContext(), this));
            onRestoreInstanceState(state);
            mImpl.start();
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        SavedState state = new SavedState(super.onSaveInstanceState());
        state.facing = getFacing();
        state.ratio = getAspectRatio();
        state.autoFocus = getAutoFocus();
        state.flash = getFlash();
        return state;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        setFacing(ss.facing);
        setAspectRatio(ss.ratio);
        setAutoFocus(ss.autoFocus);
        setFlash(ss.flash);
    }

    /**
     * Gets the direction that the current camera faces.
     *
     * @return The camera facing.
     */
    @Facing
    public int getFacing() {
        //noinspection WrongConstant
        return mImpl.getFacing();
    }

    /**
     * Chooses camera by the direction it faces.
     *
     * @param facing The camera facing. Must be either {@link #FACING_BACK} or
     *               {@link #FACING_FRONT}.
     */
    public void setFacing(@Facing int facing) {
        mImpl.setFacing(facing);
    }

    /**
     * Returns whether the continuous auto-focus mode is enabled.
     *
     * @return {@code true} if the continuous auto-focus mode is enabled. {@code false} if it is
     * disabled, or if it is not supported by the current camera.
     */
    public boolean getAutoFocus() {
        return mImpl.getAutoFocus();
    }

    /**
     * Enables or disables the continuous auto-focus mode. When the current camera doesn't support
     * auto-focus, calling this method will be ignored.
     *
     * @param autoFocus {@code true} to enable continuous auto-focus mode. {@code false} to
     *                  disable it.
     */
    public void setAutoFocus(boolean autoFocus) {
        mImpl.setAutoFocus(autoFocus);
    }

    /**
     * Gets the current flash mode.
     *
     * @return The current flash mode.
     */
    @Flash
    public int getFlash() {
        //noinspection WrongConstant
        return mImpl.getFlash();
    }

    /**
     * Sets the flash mode.
     *
     * @param flash The desired flash mode.
     */
    public void setFlash(@Flash int flash) {
        mImpl.setFlash(flash);
    }

    /**
     * 保存图片
     **/
    public boolean saveBitmap(Bitmap bitmap) {
        String name = DateUtil.formatDateToSecond(new Date());
        File file = new File(AppConstant.PIC_DIC, name + ".jpg");
        return ImageUtils.save(bitmap, file, Bitmap.CompressFormat.JPEG);
    }

    /**
     * 图片镜像翻转
     *
     * @param bitmap 图片源
     **/
    public Bitmap mirrorRotate(Bitmap bitmap) {

        return ImageUtils.mirrorRotate(bitmap);
    }

    /**
     * 主要是矫正图片（如某些三星等手机会有图片旋转），前置摄像头镜像矫正（看需求）
     */
    public Bitmap adjustBitmap(byte[] data, boolean isMirrorRotate) {
        return ImageUtils.adjustBitmap(data, isMirrorRotate, getFacing());
    }

    public Size getPreviewSize() {
        return mImpl.getPreviewSize();
    }

    public Size getPictureSize() {
        return mImpl.getPictureSize();
    }

    /**
     * Stop camera preview and close the device. This is typically called from
     * {@link Activity#onPause()}.
     */
    public void stop() {
        mImpl.stop();
    }

    /**
     * Add a new callback.
     */
    public void addCallback(@NonNull CameraImpl.Callback callback) {
        mCallbacks.add(callback);
    }

    /**
     * Remove a callback.
     */
    public void removeCallback(@NonNull CameraImpl.Callback callback) {
        mCallbacks.remove(callback);
    }

    /**
     * @return True when this CameraView is adjusting its bounds to preserve the aspect ratio of
     * camera.
     * @see #setAdjustViewBounds(boolean)
     */
    public boolean getAdjustViewBounds() {
        return mAdjustViewBounds;
    }

    /**
     * @param adjustViewBounds {@code true} if you want the CameraView to adjust its bounds to
     *                         preserve the aspect ratio of camera.
     * @see #getAdjustViewBounds()
     */
    public void setAdjustViewBounds(boolean adjustViewBounds) {
        if (mAdjustViewBounds != adjustViewBounds) {
            mAdjustViewBounds = adjustViewBounds;
            requestLayout();
        }
    }

    /**
     * Gets all the aspect ratios supported by the current camera.
     */
    public Set<AspectRatio> getSupportedAspectRatios() {
        return mImpl.getSupportedAspectRatios();
    }

    /**
     * Take a picture. The result will be returned to
     */
    public void takePicture() {
        mImpl.takePicture();
    }

    /**
     * Direction the camera faces relative to device screen.
     */
    @IntDef({FACING_BACK, FACING_FRONT})
    @Retention(RetentionPolicy.SOURCE)
    @interface Facing {
    }

    /**
     * The mode for for the camera device's flash control
     */
    @IntDef({FLASH_OFF, FLASH_ON, FLASH_TORCH, FLASH_AUTO, FLASH_RED_EYE})
    @interface Flash {
    }

    protected static class SavedState extends BaseSavedState {

        public static final Creator<SavedState> CREATOR
                = ParcelableCompat.newCreator(new ParcelableCompatCreatorCallbacks<SavedState>() {

            @Override
            public SavedState createFromParcel(Parcel in, ClassLoader loader) {
                return new SavedState(in, loader);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }

        });
        @Facing
        int facing;
        AspectRatio ratio;
        boolean autoFocus;
        @Flash
        int flash;

        @SuppressWarnings("WrongConstant")
        public SavedState(Parcel source, ClassLoader loader) {
            super(source);
            facing = source.readInt();
            ratio = source.readParcelable(loader);
            autoFocus = source.readByte() != 0;
            flash = source.readInt();
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(facing);
            out.writeParcelable(ratio, 0);
            out.writeByte((byte) (autoFocus ? 1 : 0));
            out.writeInt(flash);
        }

    }

    private class CallbackBridge extends CameraImpl.Callback {

        private final ArrayList<CameraImpl.Callback> mCallbacks = new ArrayList<>();

        private boolean mRequestLayoutOnOpen;

        CallbackBridge() {
        }

        public void add(CameraImpl.Callback callback) {
            mCallbacks.add(callback);
        }

        public void remove(CameraImpl.Callback callback) {
            mCallbacks.remove(callback);
        }

        @Override
        public void onCameraOpened(CameraImpl camera) {
            if (mRequestLayoutOnOpen) {
                mRequestLayoutOnOpen = false;
                requestLayout();
            }
            for (CameraImpl.Callback callback : mCallbacks) {
                callback.onCameraOpened(camera);
            }
        }

        @Override
        public void onCameraClosed(CameraImpl camera) {
            for (CameraImpl.Callback callback : mCallbacks) {
                callback.onCameraClosed(camera);
            }
        }

        @Override
        public void onPicturePreview(CameraImpl camera, byte[] data) {
            for (CameraImpl.Callback callback : mCallbacks) {
                callback.onPicturePreview(camera, data);
            }
        }

        @Override
        public void onPictureTaken(CameraImpl camera, byte[] data,int rotation) {
            for (CameraImpl.Callback callback : mCallbacks) {
                callback.onPictureTaken(camera, data,rotation);
            }
        }

        public void reserveRequestLayoutOnOpen() {
            mRequestLayoutOnOpen = true;
        }
    }

}
