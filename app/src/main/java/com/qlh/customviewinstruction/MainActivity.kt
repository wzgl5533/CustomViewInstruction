package com.qlh.customviewinstruction

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        view_range_bar.setRectLineHeight(10)
        view_range_bar.setDialogToProgressSpace(50)
        view_range_bar.setRangeTextToProgressSpace(100)
        view_range_bar.setDialogColor(resources.getColor(R.color.blue_275D9D))
        view_range_bar.setDialogWidth(100)
        view_range_bar.setDialogCornerRadius(20)
    }
}
