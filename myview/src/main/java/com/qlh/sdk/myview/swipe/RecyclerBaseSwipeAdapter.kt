package com.qlh.sdk.myview.swipe

import android.view.ViewGroup


import com.qlh.sdk.myview.swipe.interfaces.SwipeAdapterInterface
import com.qlh.sdk.myview.swipe.interfaces.SwipeItemMangerInterface

import androidx.recyclerview.widget.RecyclerView

abstract class RecyclerBaseSwipeAdapter<VH : RecyclerView.ViewHolder> : RecyclerView.Adapter<VH>(), SwipeItemMangerInterface, SwipeAdapterInterface {

    var mItemManger = SwipeItemMangerImpl(this)

    abstract override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH

    abstract override fun onBindViewHolder(viewHolder: VH, position: Int)

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

    override fun getOpenItems(): List<Int> {
        return mItemManger.getOpenItems()
    }

    override fun getOpenLayouts(): List<SwipeLayout> {
        return mItemManger.getOpenLayouts()
    }

    override fun removeShownLayouts(layout: SwipeLayout) {
        mItemManger.removeShownLayouts(layout)
    }

    override fun isOpen(position: Int): Boolean {
        return mItemManger.isOpen(position)
    }

    override fun getMode(): Attributes.Mode {
        return mItemManger.getMode()
    }

    override fun setMode(mode: Attributes.Mode) {
        mItemManger.setMode(mode)
    }
}
