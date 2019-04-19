package com.qlh.sdk.myview.swipe

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.qlh.sdk.myview.swipe.interfaces.SwipeAdapterInterface
import com.qlh.sdk.myview.swipe.interfaces.SwipeItemMangerInterface

/**
 *作者：QLH on 2019-04-17
 *描述：
 */
abstract class RecyclerCustomSwipeAdapter<T, K : BaseViewHolder> constructor(layoutResId: Int, d: List<T>) : BaseQuickAdapter<T, K>(layoutResId, d),
        SwipeItemMangerInterface, SwipeAdapterInterface {

    val mItemManger = SwipeItemMangerImpl(this)

    abstract override fun convert(helper: K, item: T)

    override fun notifyDatasetChanged() {
        super.notifyDataSetChanged()
    }

    override fun openItem(position: Int) {
        mItemManger.openItem(position)
    }

    override fun closeItem(position: Int) {
        mItemManger.closeItem(position)
    }

    override fun closeAllExcept(layout: SwipeLayout) {
        mItemManger.closeAllExcept(layout)
    }

    override fun closeAllItems() {
        mItemManger.closeAllItems()
    }

    override fun getOpenItems(): List<Int> = mItemManger.getOpenItems()

    override fun getOpenLayouts(): List<SwipeLayout> = mItemManger.getOpenLayouts()

    override fun removeShownLayouts(layout: SwipeLayout) {
        mItemManger.removeShownLayouts(layout)
    }

    override fun isOpen(position: Int): Boolean = mItemManger.isOpen(position)

    override fun getMode(): Attributes.Mode = mItemManger.getMode()

    override fun setMode(mode: Attributes.Mode) {
        mItemManger.setMode(mode)
    }
}