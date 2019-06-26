package com.qlh.sdk.myview.utils

import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * RecyclerView无限循环滚动的完美实现方案
 *
 * 水平方向
 **/
class LooperHorizontalLayoutManager : RecyclerView.LayoutManager() {

    private var looperEnable = true
    private val TAG = "LooperHorizontal"

    fun setLooperEnable(looperEnable: Boolean) {
        this.looperEnable = looperEnable
    }

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun canScrollHorizontally(): Boolean {
        return true
    }

    override fun canScrollVertically(): Boolean {
        return false
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        if (itemCount <= 0) {
            return
        }
        //preLayout主要支持动画，直接跳过
        if (state.isPreLayout) {
            return
        }
        //将视图分离放入scrap缓存中，以准备重新对view进行排版
        detachAndScrapAttachedViews(recycler)

        var actualWidth = 0
        for (i in 0 until itemCount) {
            //初始化，将在屏幕内的view填充
            val itemView = recycler.getViewForPosition(i)
            addView(itemView)
            //测量itemView的宽高
            measureChildWithMargins(itemView, 0, 0)
            val width = getDecoratedMeasuredWidth(itemView)
            val height = getDecoratedMeasuredHeight(itemView)
            //根据itemView的宽高进行布局
            layoutDecorated(itemView, actualWidth, 0, actualWidth + width, height)

            actualWidth += width
            //如果当前布局过的itemView的宽度总和大于RecyclerView的宽，则不再进行布局
            if (actualWidth > getWidth()) {
                break
            }
        }
    }

    override fun scrollHorizontallyBy(dx: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State): Int {
        //1.左右滑动的时候，填充子view
        val travel = fill(dx, recycler, state)
        if (travel == 0) {
            return 0
        }

        //2.滚动
        offsetChildrenHorizontal(travel * -1)

        //3.回收已经离开界面的
        recyclerHideView(dx, recycler, state)
        return travel
    }

    /**
     * 左右滑动的时候，填充
     */
    private fun fill(dx: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State): Int {
        var dx = dx
        if (dx > 0) {
            //标注1.向左滚动
            val lastView = getChildAt(childCount - 1) ?: return 0
            val lastPos = getPosition(lastView)
            //标注2.可见的最后一个itemView完全滑进来了，需要补充新的
            if (lastView!!.right < width) {
                var scrap: View? = null
                //标注3.判断可见的最后一个itemView的索引，
                // 如果是最后一个，则将下一个itemView设置为第一个，否则设置为当前索引的下一个
                if (lastPos == itemCount - 1) {
                    if (looperEnable) {
                        scrap = recycler.getViewForPosition(0)
                    } else {
                        dx = 0
                    }
                } else {
                    scrap = recycler.getViewForPosition(lastPos + 1)
                }
                if (scrap == null) {
                    return dx
                }
                //标注4.将新的itemView add进来并对其测量和布局
                addView(scrap)
                measureChildWithMargins(scrap, 0, 0)
                val width = getDecoratedMeasuredWidth(scrap)
                val height = getDecoratedMeasuredHeight(scrap)
                layoutDecorated(scrap, lastView!!.right, 0,
                        lastView!!.right + width, height)
                return dx
            }
        } else {
            //向右滚动
            val firstView = getChildAt(0) ?: return 0
            val firstPos = getPosition(firstView)

            if (firstView!!.left >= 0) {
                var scrap: View? = null
                if (firstPos == 0) {
                    if (looperEnable) {
                        scrap = recycler.getViewForPosition(itemCount - 1)
                    } else {
                        dx = 0
                    }
                } else {
                    scrap = recycler.getViewForPosition(firstPos - 1)
                }
                if (scrap == null) {
                    return 0
                }
                addView(scrap, 0)
                measureChildWithMargins(scrap, 0, 0)
                val width = getDecoratedMeasuredWidth(scrap)
                val height = getDecoratedMeasuredHeight(scrap)
                layoutDecorated(scrap, firstView!!.left - width, 0,
                        firstView!!.left, height)
            }
        }
        return dx
    }

    /**
     * 回收界面不可见的view
     */
    private fun recyclerHideView(dx: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        for (i in 0 until childCount) {
            val view = getChildAt(i) ?: continue
            if (dx > 0) {
                //向左滚动，移除一个左边不在内容里的view
                if (view!!.right < 0) {
                    removeAndRecycleView(view, recycler)
                    Log.d(TAG, "循环: 移除 一个view  childCount= $childCount")
                }
            } else {
                //向右滚动，移除一个右边不在内容里的view
                if (view!!.left > width) {
                    removeAndRecycleView(view, recycler)
                    Log.d(TAG, "循环: 移除 一个view  childCount= $childCount")
                }
            }
        }

    }
}
