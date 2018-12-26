package com.qlh.customviewinstruction

import android.Manifest
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.qlh.sdk.myview.camera.CameraImpl
import com.qlh.sdk.myview.camera.CameraView
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
            window.statusBarColor = Color.TRANSPARENT
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        view_range_bar.setRectLineHeight(10)
        view_range_bar.setDialogToProgressSpace(10)
        view_range_bar.setRangeTextToProgressSpace(10)
        view_range_bar.setDialogColor(resources.getColor(R.color.blue_275D9D))
        view_range_bar.setDialogWidth(100)
        view_range_bar.setDialogCornerRadius(20)
        RxPermissions(this).request(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(object : Observer<Boolean> {
                    override fun onNext(t: Boolean) {
                        if (t) {
                            cam.start()
                        } else {
                            Toast.makeText(this@MainActivity, "请开启相机权限！", Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onError(e: Throwable) {

                    }

                    override fun onComplete() {

                    }
                })
        btn.setOnClickListener {
            cam.takePicture()
        }
        switchca.setOnClickListener {
            if (cam.facing == CameraView.FACING_BACK){
                cam.facing = CameraView.FACING_FRONT
                cam.flash = CameraView.FLASH_ON
            }else if (cam.facing == CameraView.FACING_FRONT){
                cam.facing = CameraView.FACING_BACK
                cam.flash = CameraView.FLASH_OFF
            }
        }
        cam.addCallback(object : CameraImpl.Callback() {
            override fun onCameraOpened(camera: CameraImpl?) {
                super.onCameraOpened(camera)
                Log.e("11111", "open")
            }

            override fun onCameraClosed(camera: CameraImpl?) {
                super.onCameraClosed(camera)
                Log.e("11111", "onCameraClosed")
            }

            override fun onPictureTaken(camera: CameraImpl?, data: ByteArray?,rotation:Int) {
                super.onPictureTaken(camera, data,rotation)
                Log.e("11111", "onPictureTaken")
                if (data != null) {
                    val bitmap = cam.adjustBitmap(data,true)
                    img.setImageBitmap(bitmap)
                    cam.saveBitmap(bitmap)

                }
            }

            override fun onPicturePreview(camera: CameraImpl?, data: ByteArray?) {
                super.onPicturePreview(camera, data)
                //Log.e("11111","onPicturePreview")
            }
        })

        //签名
        sign1()
    }

   private fun sign1(){

    sign_view.setTouch {
        Log.d("11111",it.toString())
    }

       clear.setOnClickListener {
           sign_view.clear()
       }
       save.setOnClickListener {
           if (sign_view.signature){
               try {
                   sign_view.save("/sdcard/qm.png", true, 10,Bitmap.CompressFormat.PNG)
               }catch (e:IOException){
                   e.printStackTrace()
               }
           }
       }

   }
}
