package com.qlh.sdk.myview.base

import android.Manifest
import android.content.Context
import android.graphics.*
import android.hardware.Camera
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.ImageUtils
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.ToastUtils
import com.qlh.sdk.myview.R
import com.qlh.sdk.myview.callback.TakePictureSuccess
import com.qlh.sdk.myview.utils.BitmapUtils
import com.tbruyelle.rxpermissions2.RxPermissions
import java.util.*
import kotlin.Comparator
import kotlin.math.abs

/**
 *作者：QLH on 2020-06-06
 *描述：
 */
abstract class CameraBaseActivity : AppCompatActivity(), SurfaceHolder.Callback {

    private val TAG by lazy { javaClass.simpleName }
    private var mCamera: Camera? = null
    private var cameraPosition =
            Camera.CameraInfo.CAMERA_FACING_BACK //1:采集指纹的前置摄像头. 0:拍照的后置摄像头.
    private var isGranted = false
    private var cropBitmap: Bitmap? = null//最终拍照的裁剪图片
    private var mHolder: SurfaceHolder? = null
    private var maxPreSize: Camera.Size? = null
    private var maxPicSize: Camera.Size? = null
    private val sizeComparator = CameraSizeComparator()
    private var mSensorControler: SensorControler? = null
    private var root :View? = null

    abstract fun getLayoutId():View

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {}

    override fun surfaceDestroyed(holder: SurfaceHolder?) { releaseCamera() }

    override fun surfaceCreated(holder: SurfaceHolder?) { initCamera() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        root = getLayoutId()
        setContentView(root)
        initView()
    }

    override fun onStart() {
        super.onStart()
        mSensorControler?.let {
            it.onStart()
            it.setCameraFocusListener {
                focus()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        mSensorControler?.onStop()
    }

    private fun initView (){
        val detectScreenOrientation = DetectScreenOrientation(this)
        detectScreenOrientation.enable()

        mHolder = root?.findViewById<SurfaceView>(R.id.surface_view)?.holder
        mHolder?.apply {
            addCallback(this@CameraBaseActivity)
            setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
        }
    }

    //------------------------------------------------
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        val decorView = window.decorView
        decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }

    //切换摄像头(备用)
    fun changeCameraOrientation() {
        val cameraInfo = Camera.CameraInfo()
        Camera.getCameraInfo(cameraPosition, cameraInfo)
        mCamera?.stopPreview() //停掉原来摄像头的预览
        mCamera?.release() //释放资源
        mCamera = null //取消原来摄像头
        cameraPosition = if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) { //此时后置
            Log.e(TAG, cameraPosition.toString() + "BACK")
            Camera.CameraInfo.CAMERA_FACING_FRONT
        } else { //此时前置
            Log.e(TAG, cameraPosition.toString() + "For")
            Camera.CameraInfo.CAMERA_FACING_BACK
        }
        initCamera()
        setCameraParams()
    }

    private fun initCamera() {

        RxPermissions(this).request(Manifest.permission.CAMERA)
                .subscribe {
                    if (it) {
                        try {
                            mCamera = Camera.open(cameraPosition) //1:采集指纹的前置摄像头. 0:拍照的后置摄像头.
                            mCamera?.setPreviewDisplay(mHolder)
                            setCameraParams()
                            isGranted = true
                        } catch (e: Exception) {
                            //5.0权限拒绝或者不选，都走异常
                            ToastUtils.showShort("摄像头或存储权限被拒绝,请到权限管理中设置")
                            finish()
                            e.printStackTrace()
                        }
                    } else {
                        ToastUtils.showShort("摄像头或存储权限被拒绝,请到权限管理中设置")
                        finish()
                    }
                }
    }

    private fun setCameraParams() {
        mCamera ?: return
        try {
            val parameters = mCamera?.parameters
            val info = Camera.CameraInfo()
            Camera.getCameraInfo(cameraPosition, info)
            var degrees = when (judgeScreenOrientation()) {
                Surface.ROTATION_0 -> 0
                Surface.ROTATION_90 -> 90
                Surface.ROTATION_180 -> 180
                Surface.ROTATION_270 -> 270
                else -> 0
            }
            val result = if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                val temp = (info.orientation + degrees) % 360
                (360 - temp) % 360
            } else {
                (info.orientation - degrees + 360) % 360
            }
            mCamera?.setDisplayOrientation(result)

            //parameters.setRotation(result);

            parameters?.apply {
                getPreSize(this)
                pictureFormat = PixelFormat.JPEG
                focusMode = Camera.Parameters.FOCUS_MODE_AUTO
                Log.e(TAG,"${ScreenUtils.getScreenHeight()}---${ScreenUtils.getScreenWidth()}")
                if (isSupportScreenPicture(getPicSize(this))
                        && isSupportScreenPreView(getPreSize(this))
                ) {
                    setPictureSize(ScreenUtils.getScreenHeight(), ScreenUtils.getScreenWidth())
                    setPreviewSize(ScreenUtils.getScreenHeight(), ScreenUtils.getScreenWidth())
                } else {//如果没有和屏幕一致的，找到预览和图片与屏幕比例一致的
                    maxPicSize?.let { setPictureSize(it.width, it.height) }
                    maxPreSize?.let { setPreviewSize(it.width, it.height) }
                }
                mCamera?.parameters = this
                mCamera?.startPreview()
                focus()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 判断屏幕方向
     *
     * @return 0：竖屏 1：左横屏 2：反向竖屏 3：右横屏
     */
    private fun judgeScreenOrientation(): Int {
        return windowManager.defaultDisplay.rotation
    }

    private fun releaseCamera() {
        mCamera?.stopPreview()
        mCamera?.release()
        mCamera = null
    }

    //判断是否授权
     fun isGranted(): Boolean {
        return isGranted
    }

    /**
     * 对焦，在CameraActivity中触摸对焦或者自动对焦
     */
    fun focus() {
        mCamera?.let {
            try {
                it.autoFocus(null)
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }

    /**
     * 获取相机能支持的预览分辨率
     */
    private fun getPreSize(parameters: Camera.Parameters): List<Camera.Size> {
        val preSize =
                parameters.supportedPreviewSizes
        Collections.sort(preSize, sizeComparator)
        val screenScale = ScreenUtils.getScreenHeight() * 1.0 / ScreenUtils.getScreenWidth()
        maxPreSize = preSize[0]
        for (size in preSize) {
            Log.e(TAG, size.width.toString() + "--pre--" + size.height)
        }
        preSize.forEach {
            //需找预览比例最接近屏幕比例的
            if (abs(it.width * 1.0 / it.height - screenScale) <
                    abs(maxPreSize!!.width * 1.0 / maxPreSize!!.height - screenScale)) {
                maxPreSize = it
                Log.e(TAG,maxPreSize?.toString())
                return@forEach
            }
        }
        return preSize
    }

    //拍照是否支持屏幕分辨率
    private fun isSupportScreenPicture(picSize: List<Camera.Size>): Boolean {
        var isSupport = false
        val height = ScreenUtils.getScreenHeight()
        val width = ScreenUtils.getScreenWidth()
        for (size in picSize) {
            if (size.width == height && size.height == width) {
                isSupport = true
                break
            }
        }
        return isSupport
    }

    /**
     * 获取相机能支持的拍照分辨率
     */
    private fun getPicSize(parameters: Camera.Parameters): List<Camera.Size> {
        val picSize =
                parameters.supportedPictureSizes
        Collections.sort(picSize, sizeComparator)
        val screenScale = ScreenUtils.getScreenHeight() * 1.0 / ScreenUtils.getScreenWidth()
        maxPicSize = picSize[0]
        for (size in picSize) {
            Log.e(TAG, size.width.toString() + "--pic--" + size.height)
        }
        picSize.forEach {
            //需找预览比例最接近屏幕比例的
            if (abs(it.width * 1.0 / it.height - screenScale) <
                    abs(maxPicSize!!.width * 1.0 / maxPicSize!!.height - screenScale)) {
                maxPicSize = it
                Log.e(TAG,maxPicSize?.toString())
                return@forEach
            }
        }

        return picSize
    }

    //预览图是否支持屏幕分辨率
    private fun isSupportScreenPreView(preSize: List<Camera.Size>): Boolean {
        var isSupport = false
        val height = ScreenUtils.getScreenHeight()
        val width = ScreenUtils.getScreenWidth()
        for (size in preSize) {
            if (size.width == height && size.height == width) {
                isSupport = true
                break
            }
        }
        return isSupport
    }

    /**
     * 拍照,（前置摄像头目前在三星某些手机上并不能自动获取焦点，拍照失效），可以先把mCamera.autoFocus去掉，直接拍照，本demo使用后置的演示
     */
    fun takePicture(callBack:TakePictureSuccess? = null) {
        mCamera?.autoFocus { success, camera ->
            if (success) {
                mCamera?.cancelAutoFocus()
                mCamera?.takePicture(Camera.ShutterCallback { }, null, null, Camera.PictureCallback { data, camera ->
                    var originalBitmap =
                            BitmapFactory.decodeByteArray(data, 0, data.size)
                    Log.e(TAG,
                            originalBitmap.width.toString() + "--original--" + originalBitmap.height)

                    //前置图片顺时针旋转180度加镜像翻转
                    if (cameraPosition == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                        originalBitmap = mirrorRotate(rotate(originalBitmap!!, 180))
                    }

                    //目前本人测试的几款手机都会有90度的旋转，所以需要复原图片，如果有不同之处，请在github留言
                    originalBitmap =
                            if (cameraPosition == Camera.CameraInfo.CAMERA_FACING_BACK) {
                                rotate(originalBitmap, 90)
                            } else {
                                rotate(originalBitmap, -90)
                            }
                    //到此图片和预览视角相同
                    Log.e(TAG,
                            originalBitmap.width
                                    .toString() + "----" + originalBitmap.height)
                    val frameRect = Rect()
                    val fixCutView = root?.findViewById<View>(R.id.fix_cut_view)
                    fixCutView?.getGlobalVisibleRect(frameRect)
                    cropBitmap = BitmapUtils.getCropPicture(originalBitmap,
                            ScreenUtils.getScreenWidth(),
                            ScreenUtils.getScreenHeight(), frameRect)
                    //BitmapUtils.saveBitmap(this, originalBitmap)
                    callBack?.success(cropBitmap)
                    originalBitmap.recycle()

                    Log.e(TAG,
                            cropBitmap!!.width.toString() + "--crop--" + cropBitmap!!.height
                    )
                    mCamera?.startPreview()
                })
            }
        }
    }

    /**
     * 用来监测左横屏和右横屏切换时旋转摄像头的角度
     */
    private inner class DetectScreenOrientation : OrientationEventListener {

        constructor(context: Context):super(context)
        override fun onOrientationChanged(orientation: Int) {
            if (orientation in 261..289) {
                setCameraParams()
            } else if (orientation in 81..99) {
                setCameraParams()
            }
        }
    }

    private inner class CameraSizeComparator : Comparator<Camera.Size> {
        override fun compare(o1: Camera.Size, o2: Camera.Size): Int {
            return when {
                o1.width == o2.width -> {
                    0
                }
                o1.width < o2.width -> {
                    1
                }
                else -> {
                    -1
                }
            }
        }
    }

    /**前置图片顺时针旋转180度 */
    private fun rotate(bitmap: Bitmap, degree: Int): Bitmap {
        return ImageUtils.rotate(
                bitmap, degree,
                bitmap.width / 2.toFloat(), bitmap.height / 2.toFloat(), true
        )
    }

    /**图片镜像翻转 */
    private fun mirrorRotate(bitmap: Bitmap): Bitmap {
        val matrix = Matrix()
        matrix.postScale(-1f, 1f) // 镜像水平翻转
        return Bitmap.createBitmap(
                bitmap, 0, 0,
                bitmap.width, bitmap.height, matrix, true
        )
    }


}