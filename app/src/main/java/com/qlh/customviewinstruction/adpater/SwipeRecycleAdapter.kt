package com.qlh.customviewinstruction.adpater

import android.widget.TextView
import android.widget.Toast
import com.chad.library.adapter.base.BaseViewHolder
import com.qlh.customviewinstruction.R
import com.qlh.sdk.myview.swipe.RecyclerCustomSwipeAdapter
import com.qlh.sdk.myview.swipe.SwipeLayout

/**
 *作者：QLH on 2019-04-18
 *描述：
 */
class SwipeRecycleAdapter(d:List<String>):RecyclerCustomSwipeAdapter<String,BaseViewHolder>(R.layout.item_swipe,d){
    override fun convert(helper: BaseViewHolder, item: String) {

        helper.setText(R.id.content,item)
        helper.getView<TextView>(R.id.btn).setOnClickListener { Toast.makeText(helper.itemView.context,"点击",Toast.LENGTH_SHORT).show() }

        helper.getView<SwipeLayout>(R.id.swipe_root).apply {
            showMode = SwipeLayout.ShowMode.LayDown  //PullOut
            this.isClickToClose = true
            mItemManger.bind(this,helper.layoutPosition)
        }
    }

    override fun getSwipeLayoutResourceId(position: Int) =   R.id.swipe_root
}