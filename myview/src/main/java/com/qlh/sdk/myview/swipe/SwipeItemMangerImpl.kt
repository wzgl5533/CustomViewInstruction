package com.qlh.sdk.myview.swipe

import android.view.View

import com.qlh.sdk.myview.swipe.interfaces.SwipeAdapterInterface
import com.qlh.sdk.myview.swipe.interfaces.SwipeItemMangerInterface

import java.util.ArrayList
import java.util.Collections
import java.util.HashSet

/**
 * SwipeItemMangerImpl is a helper class to help all the adapters to maintain open status.
 * 实现通用操作，如果有需求可以在adapter进行个性化设置 可以参考这里
 */
class SwipeItemMangerImpl(private val swipeAdapterInterface: SwipeAdapterInterface) : SwipeItemMangerInterface {

    private var mode: Attributes.Mode = Attributes.Mode.Single
    val INVALID_POSITION = -1

    private var mOpenPosition = INVALID_POSITION
    private var mOpenPositions: MutableSet<Int> = HashSet()
    private var mShownLayouts: MutableSet<SwipeLayout> = HashSet()


    override fun getMode(): Attributes.Mode {
        return mode
    }

    override fun setMode(mode: Attributes.Mode) {
        this.mode = mode
        mOpenPositions.clear()
        mShownLayouts.clear()
        mOpenPosition = INVALID_POSITION
    }

    fun bind(view: View, position: Int) {
        val resId = swipeAdapterInterface.getSwipeLayoutResourceId(position)
        val swipeLayout = view.findViewById<View>(resId) as SwipeLayout

        if (swipeLayout.getTag(resId) == null) {
            val onLayoutListener = OnLayoutListener(position)
            val swipeMemory = SwipeMemory(position)
            swipeLayout.addSwipeListener(swipeMemory)
            swipeLayout.addOnLayoutListener(onLayoutListener)
            swipeLayout.setTag(resId, ValueBox(position, swipeMemory, onLayoutListener))
            mShownLayouts.add(swipeLayout)
        } else {
            val valueBox = swipeLayout.getTag(resId) as ValueBox
            valueBox.swipeMemory.setPosition(position)
            valueBox.onLayoutListener.setPosition(position)
            valueBox.position = position
        }
    }

    override fun openItem(position: Int) {
        if (mode === Attributes.Mode.Multiple) {
            if (!mOpenPositions.contains(position))
                mOpenPositions.add(position)
        } else {
            mOpenPosition = position
        }
        swipeAdapterInterface.notifyDatasetChanged()
    }

    override fun closeItem(position: Int) {
        if (mode === Attributes.Mode.Multiple) {
            mOpenPositions.remove(position)
        } else {
            if (mOpenPosition == position)
                mOpenPosition = INVALID_POSITION
        }
        swipeAdapterInterface.notifyDatasetChanged()
    }

    override fun closeAllExcept(layout: SwipeLayout) {
        for (s in mShownLayouts) {
            if (s !== layout)
                s.close()
        }
    }

    override fun closeAllItems() {
        if (mode === Attributes.Mode.Multiple) {
            mOpenPositions.clear()
        } else {
            mOpenPosition = INVALID_POSITION
        }
        for (s in mShownLayouts) {
            s.close()
        }
    }

    override fun removeShownLayouts(layout: SwipeLayout) {
        mShownLayouts.remove(layout)
    }

    override fun getOpenItems(): List<Int> {
        return if (mode === Attributes.Mode.Multiple) {
            ArrayList(mOpenPositions)
        } else {
            listOf(mOpenPosition)
        }
    }

    override fun getOpenLayouts(): List<SwipeLayout> {
        return ArrayList(mShownLayouts)
    }

    override fun isOpen(position: Int): Boolean {
        return if (mode === Attributes.Mode.Multiple) {
            mOpenPositions.contains(position)
        } else {
            mOpenPosition == position
        }
    }

    internal inner class ValueBox(var position: Int, var swipeMemory: SwipeMemory, var onLayoutListener: OnLayoutListener)

    internal inner class OnLayoutListener(private var position: Int) : SwipeLayout.OnLayout {

        fun setPosition(position: Int) {
            this.position = position
        }

        override fun onLayout(v: SwipeLayout) {
            if (isOpen(position)) {
                v.open(false, false)
            } else {
                v.close(false, false)
            }
        }

    }

    internal inner class SwipeMemory(private var position: Int) : SimpleSwipeListener() {

        override fun onClose(layout: SwipeLayout) {
            if (mode === Attributes.Mode.Multiple) {
                mOpenPositions.remove(position)
            } else {
                mOpenPosition = INVALID_POSITION
            }
        }

        override fun onStartOpen(layout: SwipeLayout) {
            if (mode === Attributes.Mode.Single) {
                closeAllExcept(layout)
            }
        }

        override fun onOpen(layout: SwipeLayout) {
            if (mode === Attributes.Mode.Multiple)
                mOpenPositions.add(position)
            else {
                closeAllExcept(layout)
                mOpenPosition = position
            }
        }

        fun setPosition(position: Int) {
            this.position = position
        }
    }

}
