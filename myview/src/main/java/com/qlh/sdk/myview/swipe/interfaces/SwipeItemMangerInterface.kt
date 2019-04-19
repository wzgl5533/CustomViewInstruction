package com.qlh.sdk.myview.swipe.interfaces


import com.qlh.sdk.myview.swipe.Attributes
import com.qlh.sdk.myview.swipe.SwipeLayout

interface SwipeItemMangerInterface {

    /**所有打开item的position**/
    fun getOpenItems(): List<Int>

    fun getOpenLayouts(): List<SwipeLayout>

    fun getMode(): Attributes.Mode

    fun setMode(mode:Attributes.Mode)

    fun openItem(position: Int)

    fun closeItem(position: Int)

    fun closeAllExcept(layout: SwipeLayout)

    fun closeAllItems()

    fun removeShownLayouts(layout: SwipeLayout)

    fun isOpen(position: Int): Boolean
}
