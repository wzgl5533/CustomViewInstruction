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
	       implementation 'com.github.wzgl5533:CustomViewInstruction:1.0'
	}
```
### 模块分类

**1、输入框**
  * ClearEditText：可以自己自定义右边删除图标以及它的宽高
  
  <com.qlh.sdk.myview.edittext.MyClearEditText
        android:layout_width="match_parent"
        android:layout_height="50dp"
        app:edt_delete_icon_width="@dimen/d20"//删除图标宽度
        app:edt_delete_icon_height="@dimen/d20"//删除图标高度
        android:drawableRight="@drawable/delete"/>//删除图标


**2、图片**
* CircleImageView：带边框的圆形图片

 <com.qlh.sdk.myview.imageview.CircleImageView
        android:layout_width="@dimen/d100"
        android:layout_height="@dimen/d100"
        app:civ_border_width="@dimen/d1"//描边宽度
        app:civ_border_color="#FF0000"//描边颜色
        app:civ_border_overlay="true"//描边是否浮在图片上面，true：会遮盖图片的边缘；false:不会遮盖图片，但图片会往里面缩小
        app:civ_fill_color="#00FF00"//填充空白颜色
        android:src="@drawable/live_room_bg"/>


