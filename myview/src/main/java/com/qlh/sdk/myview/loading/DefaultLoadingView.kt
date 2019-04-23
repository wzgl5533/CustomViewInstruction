package com.qlh.sdk.myview.loading

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.qlh.sdk.myview.R
import kotlinx.android.synthetic.main.default_loading_view.view.*

/**
 *作者：QLH on 2019-04-22
 *描述：默认loading的view,只有加载显示功能
 */
class DefaultLoadingView constructor(context: Context) : LinearLayout(context) {
    private var root: View

    init {
        orientation = LinearLayout.VERTICAL
        gravity = Gravity.CENTER
        root = LayoutInflater.from(context).inflate(R.layout.default_loading_view, this, true)
    }

    fun show(msg:String?=null){

        msg?.let {
            root.msg_tv.text = msg
        }
        visibility = View.VISIBLE
    }

    fun dismiss(){
        visibility = View.GONE
    }
}