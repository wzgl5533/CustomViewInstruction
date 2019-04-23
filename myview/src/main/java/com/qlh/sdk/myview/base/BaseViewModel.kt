package com.qlh.sdk.myview.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

/**
 *作者：QLH on 2019-04-22
 *描述：ViewModel基类
 */
open class BaseViewModel constructor(application: Application):AndroidViewModel(application){
    /**加载**/
    val loading = MutableLiveData<String>()
    /**加载完成**/
    val dismissLoading = MutableLiveData<String>()

    val failure = MutableLiveData<String>()
    /**异常**/
    val exception = MutableLiveData<Throwable?>()
    /**提示**/
    val toastMsg = MutableLiveData<String>()

    protected fun showLoading(msg:String =""){
        loading.postValue(msg)
    }

    protected fun dismiss(){
        dismissLoading.postValue("")
    }

    protected fun handleException(e:Throwable){
        exception.postValue(e)
    }

    protected fun toastMsg(msg: String){
        toastMsg.postValue(msg)
    }

}