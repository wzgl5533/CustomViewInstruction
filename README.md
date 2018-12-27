### CustomViewInstruction

### 简介

本库拆分于CustomView库，主要用于自己的项目，收集很多优秀开源的组件，对其进行封装和归纳，方便使用，再此感谢，各位小伙伴也可以使用或者修改维护自己的库，喜欢的可以打个星。

### 使用
1. 添加依赖

Add it in your project build.gradle at the end of repositories:
```
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
Add it in your module build.gradle
```
	dependencies {
	       implementation 'com.github.wzgl5533:CustomViewInstruction:1.3.1'
	}
```
### 模块分类

**1、输入框**
  * ClearEditText：可以自己自定义右边删除图标以及它的宽高
  ```
  <com.qlh.sdk.myview.edittext.MyClearEditText
        android:layout_width="match_parent"
        android:layout_height="50dp"
        app:edt_delete_icon_width="@dimen/d20"//删除图标宽度
        app:edt_delete_icon_height="@dimen/d20"//删除图标高度
        android:drawableRight="@drawable/delete"/>//删除图标
```
![ClearEditText](https://github.com/wzgl5533/CustomViewInstruction/blob/master/myview/screenshot/MyClearEditText.gif)

**2、图片**
* CircleImageView：带边框的圆形图片
```
 <com.qlh.sdk.myview.imageview.CircleImageView
        android:layout_width="@dimen/d100"
        android:layout_height="@dimen/d100"
        app:civ_border_width="@dimen/d1"//描边宽度
        app:civ_border_color="#FF0000"//描边颜色
        app:civ_border_overlay="true"//描边是否浮在图片上面，true：会遮盖图片的边缘；false:不会遮盖图片，但图片会往里面缩小
        app:civ_fill_color="#00FF00"//填充空白颜色
        android:src="@drawable/live_room_bg"/>
```
![CircleImageView](https://github.com/wzgl5533/CustomViewInstruction/blob/master/myview/screenshot/CircleImageView.jpg)

**3、数字按钮**
* AmountView:防购物车增减按钮
```
<com.qlh.sdk.myview.number.AmountView
        android:layout_width="wrap_content"
        android:layout_height="@dimen/d100"
        app:btnWidth="@dimen/d100"//左右2边+-按钮的宽度
        app:btnTextSize="@dimen/d20"//中间TextView的文字大小
        app:btnRightBg="@mipmap/ic_launcher"//右边按钮背景
        app:numTvWidth="@dimen/d100"//中间TextView的宽度
        app:numTextSize="@dimen/d10"//左右2边+-按钮的文字大小
        app:NumTvColor="#FF0000"//中间文本颜色
        app:btnLeftBg="@drawable/live_room_bg"//左边按钮背景
        app:btnTvColor="#FF0000"//按钮文本颜色
        app:numTvBg="@color/colorPrimary"//输入框背景
        app:maxValue="20"//能加到的最小值
        app:minValue="10"//能减到的最小值
        app:minValueTip="已经是最小值了~~"//减到的最小值提示
        app:maxValueTip="已经是最大值了~~"/>//加到的最小值提示
	app:step="1"//步长值
	/>
	
```
![AmountView](https://github.com/wzgl5533/CustomViewInstruction/blob/master/myview/screenshot/AmountView.gif)

**4、倒计时按钮**
* MyTimeButton：倒计时按钮
```
<com.qlh.sdk.myview.button.MyTimeButton
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:padding="@dimen/d10"
        app:textBefore="获取验证码"//点击前文本
        app:textAfter="秒后重新获取验证码"//点击后文本
        app:timeLength="10"//总时长
	/>
```
![MyTimeButton](https://github.com/wzgl5533/CustomViewInstruction/blob/master/myview/screenshot/MyTimeButton.gif)

**5、范围选择**
* RangeBarView：价格范围
```
<com.qlh.sdk.myview.rangebar.RangeBarView
        android:id="@+id/view_range_bar"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:rect_line_default_color="@color/grey_CD"//轨道线框默认颜色
        app:rect_line_checked_color="@color/blue_275D9D"//轨道线框选中颜色
        app:left_circle_solid_color="@color/white"//左边滑块填充颜色
        app:left_circle_stroke_color="@color/grey_CD"//左边滑块描边颜色
        app:right_circle_solid_color="@color/white"//右边滑块填充颜色
        app:right_circle_stroke_color="@color/grey_CD"//右边滑块描边颜色
        app:circle_stroke_width="2dp"//滑块描边宽度
        app:circle_radius="10dp"//滑块半径
        app:rect_line_height="3dp"//轨道线框高度
        app:range_text_size="@dimen/d10"//刻度范围文字大小
        app:range_text_color="#FF0000"//刻度范围文字颜色
        app:rect_price_desc_dialog_color="#FF0000"//价格显示对话框颜色,包括底部三角形颜色
        app:rect_price_desc_dialog_corner_radius="@dimen/d10"//价格显示对话框圆角半径
        app:rect_price_desc_dialog_width="@dimen/d50"//价格显示对话框宽度
        app:rect_price_desc_text_color="#00ff00"//价格对话框文字颜色
        app:rect_price_desc_text_size="@dimen/d15"//价格对话框文字大小
        app:rect_price_desc_space_to_progress="@dimen/d10"//价格对话框离轨道垂直距离
        app:view_text_space="@dimen/d20"//刻度文本离轨道垂直距离
        android:layout_marginLeft="20dp"
        android:layout_marginRight="20dp"
        />
```
![RangeBarView](https://github.com/wzgl5533/CustomViewInstruction/blob/master/myview/screenshot/RangeBarView.gif)

**6、自定义相机（解决拍照旋转问题）***
* CameraView：提供简洁的拍照画面，可以自定义操作界面，亦可结合[自定义裁剪框使用](https://github.com/wzgl5533/CropView)
```
<com.qlh.sdk.myview.camera.CameraView
    android:id="@+id/cam"
    android:layout_width="match_parent"
    android:layout_height="match_parent"/>
```
![CameraView](https://github.com/wzgl5533/CustomViewInstruction/blob/master/myview/screenshot/CameraView.jpg)

**7、自定义签名***
* SignatureView：自定义签名
```
<com.qlh.sdk.myview.view.SignatureView
        android:id="@+id/sign_view"
        android:layout_width="match_parent"
        android:layout_height="200dp"
        app:sv_canvas_color="#FFFFFF"
        app:sv_paint_color="@color/red_d10773"
        app:sv_paint_width="4dp"/>
```
![SignatureView](https://github.com/wzgl5533/CustomViewInstruction/blob/master/myview/screenshot/SignatureView.jpg)

**8、自定义文本切换器***
* TextSwitchButton：文本切换器
```
<com.qlh.sdk.myview.button.TextSwitchButton
        android:id="@+id/tsb"
        android:layout_width="100dp"
        android:layout_height="30dp"
        app:tsb_bg_color="@color/grey_CC"
        app:tsb_btn_color="@color/blue_275D9D"
        app:tsb_left_text="出车"
        app:tsb_right_text="收车"
        app:tsb_shape="round"
        app:tsb_text_size="@dimen/d13"
        app:tsb_corner_radius="@dimen/d15"
        app:tsb_btn_width="@dimen/d50"
        app:tsb_text_normal_color="@color/blue_4499ff"
        app:tsb_text_selected_color="@color/red_d10773" />
```
![TextSwitchButton](https://github.com/wzgl5533/CustomViewInstruction/blob/master/myview/screenshot/TextSwitchButton.gif)
