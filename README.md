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
	       implementation 'com.github.wzgl5533:CustomViewInstruction:1.2'
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
