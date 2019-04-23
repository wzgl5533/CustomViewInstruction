package com.qlh.sdk.myview.loading

import android.view.View
import com.qlh.sdk.myview.utils.Gloading
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

/**
 *作者：QLH on 2019-04-22
 *描述：默认的loading适配器
 */
class DefaultLoadingAdapter : Gloading.Adapter {
    override fun getView(holder: Gloading.Holder, convertView: View?, status: Int, msg: String): View {
        val view = if (convertView == null) DefaultLoadingView(holder.context) else convertView as DefaultLoadingView
        when (status) {
            Gloading.STATUS_LOADING -> {//加载
               view.apply {
                   show(msg)
                   Flowable.timer(500,TimeUnit.MILLISECONDS,AndroidSchedulers.mainThread())
                           .subscribe { dismiss() }
               }
            }

            Gloading.STATUS_LOAD_SUCCESS,Gloading.STATUS_LOAD_FAILED->{//成功,失败
               view.apply {
                   show(msg)
                   Flowable.timer(500,TimeUnit.MILLISECONDS,AndroidSchedulers.mainThread())
                           .subscribe { dismiss() }
               }
            }

            else-> view.dismiss()

        }

        return view
    }

}