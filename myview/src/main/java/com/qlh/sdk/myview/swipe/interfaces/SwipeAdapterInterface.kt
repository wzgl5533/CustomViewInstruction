package com.qlh.sdk.myview.swipe.interfaces

interface SwipeAdapterInterface {

    fun getSwipeLayoutResourceId(position: Int): Int

    fun notifyDatasetChanged()

}
