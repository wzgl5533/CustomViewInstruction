package com.qlh.sdk.myview.utils

import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * RecyclerView无限循环滚动的完美实现方案
 *
 * 垂直方向
 **/
class LooperVerticalLayoutManager : RecyclerView.LayoutManager() {

    private var looperEnable = true
    private val TAG = "LooperVertical"

    fun setLooperEnable(looperEnable: Boolean) {
        this.looperEnable = looperEnable
    }

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun canScrollHorizontally(): Boolean {
        return false
    }

    override fun canScrollVertically(): Boolean {
        return true
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

        var actualHeight = 0
        for (i in 0 until itemCount) {
            //初始化，将在屏幕内的view填充
            val itemView = recycler.getViewForPosition(i)
            addView(itemView)
            //测量itemView的宽高
            measureChildWithMargins(itemView, 0, 0)
            val width = getDecoratedMeasuredWidth(itemView)
            val height = getDecoratedMeasuredHeight(itemView)
            //根据itemView的宽高进行布局
            layoutDecorated(itemView, 0, actualHeight, width, actualHeight + height)

            actualHeight += height
            //如果当前布局过的itemView的高度总和大于RecyclerView的高，则不再进行布局
            if (actualHeight > getHeight()) {
                break
            }
        }
    }

    override fun scrollVerticallyBy(dy: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State): Int {
        //1.上下滑动的时候，填充子view
        val travel = fill(dy,recycler,state)
        if (travel == 0){
            return 0
        }
        //2.滚动
        offsetChildrenVertical(travel*-1)
        //3.回收已经离开界面的
        recyclerHideView(dy,recycler,state)
        return travel
    }

    /**
     * 左右滑动的时候，填充
     */
    private fun fill(dy: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State): Int {
        var dy = dy
        if (dy > 0) {
            //标注1.向上滚动
            val lastView = getChildAt(childCount - 1) ?: return 0
            val lastPos = getPosition(lastView)
            //标注2.可见的最后一个itemView完全滑进来了，需要补充新的
            if (lastView!!.bottom < height) {
                var scrap: View? = null
                //标注3.判断可见的最后一个itemView的索引，
                // 如果是最后一个，则将下一个itemView设置为第一个，否则设置为当前索引的下一个
                if (lastPos == itemCount - 1) {
                    if (looperEnable) {
                        scrap = recycler.getViewForPosition(0)
                    } else {
                        dy = 0
                    }
                } else {
                    scrap = recycler.getViewForPosition(lastPos + 1)
                }
                if (scrap == null) {
                    return dy
                }
                //标注4.将新的itemView add进来并对其测量和布局
                addView(scrap)
                measureChildWithMargins(scrap, 0, 0)
                val width = getDecoratedMeasuredWidth(scrap)
                val height = getDecoratedMeasuredHeight(scrap)
                layoutDecorated(scrap, 0, lastView.bottom, width, lastView.bottom + height)
                return dy
            }
        } else {
            //向下滚动
            val firstView = getChildAt(0) ?: return 0
            val firstPos = getPosition(firstView)

            if (firstView!!.top >= 0) {
                var scrap: View? = null
                if (firstPos == 0) {
                    if (looperEnable) {
                        scrap = recycler.getViewForPosition(itemCount - 1)
                    } else {
                        dy = 0
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
                layoutDecorated(scrap,0,firstView.top - height,width,firstView.top)
            }
        }
        return dy
    }

    /**
     * 回收界面不可见的view
     */
    private fun recyclerHideView(dy: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        for (i in 0 until childCount) {
            val view = getChildAt(i) ?: continue
            if (dy > 0) {
                //向上滚动，移除一个左边不在内容里的view
                if (view!!.bottom < 0) {
                    removeAndRecycleView(view, recycler)
                    Log.d(TAG, "循环: 移除 一个view  childCount= $childCount")
                }
            } else {
                //向下滚动，移除一个右边不在内容里的view
                if (view!!.top > height) {
                    removeAndRecycleView(view, recycler)
                    Log.d(TAG, "循环: 移除 一个view  childCount= $childCount")
                }
            }
        }

    }
}
