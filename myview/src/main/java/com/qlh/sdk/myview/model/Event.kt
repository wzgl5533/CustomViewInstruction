package com.qlh.sdk.myview.model

/**
 *作者：QLH on 2019-04-22
 *描述：
 */
/**加载事件**/
data class LoadingEvent(val msg: String = "")

/**加载完成事件**/
data class LoadingFinishEvent(val msg: String = "")