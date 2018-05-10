package com.adehehe.hqcommon

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 */
abstract class BaseFragmentV4 : Fragment() {
    protected var mRootView: View? = null

    /**
     * 在Fragment中单独定于Toolbar时使用
     */
    var mToolBar: Toolbar? = null
    open fun getActionBar(): Toolbar? = null

    /**
     * 在Fragment中设置标题
     */
    var mTitle: String = ""
        set(value) {
            field = value
            mFragmentChangeListener?.onTitleChanged(this, value)
        }

    fun onActivated(){
        // 当前Fragment处于活动状态
    }

    abstract fun getViewResourceId(): Int

    abstract fun initControls(view: View)

    var mFragmentChangeListener: OnFragmentChangeListener? = null

    interface OnFragmentChangeListener {
        fun onTitleChanged(fragment: BaseFragmentV4, title: String?)
        fun onFragmentAttached(fragment: BaseFragmentV4)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (mRootView != null) {
            val parent = mRootView!!.getParent()
            if (parent != null) {
                (parent as ViewGroup).removeView(mRootView)
            }
        }

        mRootView = inflater.inflate(getViewResourceId(), container, false)
        mToolBar = getActionBar()
        initControls(mRootView!!)
        if(mFragmentChangeListener!=null){
            mFragmentChangeListener!!.onFragmentAttached(this)
        }
        return mRootView
    }
}