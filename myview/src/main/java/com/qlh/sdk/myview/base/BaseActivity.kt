package com.qlh.sdk.myview.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.qlh.sdk.myview.utils.Gloading

/**
 *作者：QLH on 2019-04-22
 *描述：
 */
open abstract class BaseActivity<VM : BaseViewModel> : AppCompatActivity() {

    var viewModel: VM? = null
    private var holder:Gloading.Holder?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = obtainViewModel()
        holder = Gloading.getDefault().wrap(this)
        subscribe()

    }

    private fun subscribe(){
        viewModel?.let {
            it.loading.observe(this, Observer {
               holder?.showLoading(it)
            })

            it.dismissLoading.observe(this, Observer {
                holder?.showLoadSuccess(it)
            })

            it.failure.observe(this, Observer {
                holder?.showLoadFailed(it)
            })
        }
    }


    /**
     * 子类activity若是viewmodel，可重载该函数
     */
    protected abstract fun obtainViewModel(): VM?
}