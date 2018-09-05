package com.klfront.baseui.activity

import android.os.Bundle
import com.jude.swipbackhelper.SwipeBackHelper
import com.jude.swipbackhelper.SwipeBackPage


/**
 * Created by lujinlong on 2018/1/23.
 */

abstract class BackableActivity: BaseActivity()
{
    protected var swipePage: SwipeBackPage? = null

    override fun onCreate(savedInstanceState:Bundle?){
        super.onCreate(savedInstanceState)
        SwipeBackHelper.onCreate(this)
        swipePage = SwipeBackHelper.getCurrentPage(this)//获取当前页面
        swipePage!!.setSwipeBackEnable(true)//设置是否可滑动
                //.setSwipeEdge(200)//可滑动的范围。px。200表示为左边200px的屏幕
                //.setSwipeEdgePercent(0.2f)//可滑动的范围。百分比。0.2表示为左边20%的屏幕
                .setSwipeSensitivity(0.5f)//对横向滑动手势的敏感程度。0为迟钝 1为敏感
                .setClosePercent(0.7f)//触发关闭Activity百分比
                .setSwipeRelateEnable(false)//是否与下一级activity联动(微信效果)。默认关
                .setSwipeRelateOffset(500)//activity联动时的偏移量。默认500px。
                .setDisallowInterceptTouchEvent(false)//不抢占事件，默认关（事件将先由子View处理再由滑动关闭处理）
    }

    override fun onPostCreate(savedInstanceState: Bundle?)
    {
        super.onPostCreate(savedInstanceState)
        SwipeBackHelper.onPostCreate(this)
    }

    override fun onDestroy()
    {
        SwipeBackHelper.onDestroy(this)
        super.onDestroy()
    }
}
