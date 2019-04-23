package com.qlh.customviewinstruction

import android.app.Application
import com.qlh.sdk.myview.base.BaseViewModel
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

/**
 *作者：QLH on 2019-04-22
 *描述：
 */
class MainViewModel(application: Application) : BaseViewModel(application) {

    init {
        loading()
    }

    private fun loading() {
//        Gloading.initDefault(DefaultLoadingAdapter())
//
//        val holder = Gloading.getDefault().wrap(this)

        Flowable.interval(3,2, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {

                    when (it.toInt() % 3) {
                        0 -> loading?.value = "加载开始..."
                        1 -> dismissLoading?.value = "加载成功..."
                        2 -> failure?.value = "加载失败..."
                    }
                }
    }
}