package com.klfront.baseui.activity

import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import android.os.Bundle
import android.os.Handler
import android.support.annotation.ColorInt
import android.support.annotation.IdRes
import android.support.annotation.Nullable
import android.support.v4.content.ContextCompat
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.widget.Toast
import com.githang.statusbar.StatusBarCompat
import com.klfront.baseui.R
import com.klfront.baseui.broadcastreceiver.NetBroadcastReceiver
import com.klfront.baseui.widget.LoadingProgressDialog

/**
 */
abstract class BaseActivity: AppCompatActivity(), NetBroadcastReceiver.NetEvent
{
    companion object {
        var networkEvent: NetBroadcastReceiver.NetEvent? = null
    }

    protected var context: Context? = null
    protected var toolBar: Toolbar? = null
    protected val handler = Handler()
    /**
     * 加载提示框
     */
    private var loadDialog: LoadingProgressDialog? = null

    protected abstract fun setContentView()

    protected open fun setupActivity()
    {
        initMaterialStatusBar()
    }

    protected open fun setupActionbar(@IdRes toolbarId: Int)
    {
        toolBar = findViewById(toolbarId)
        setSupportActionBar(toolBar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
    }

    protected open fun initMaterialStatusBar()
    {
        initMaterialStatusBar(ContextCompat.getColor(this, R.color.colorPrimary))
    }

    protected open fun initMaterialStatusBar(@ColorInt statusBarColor: Int)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        } else {
            //android 4.2以下
        }
        StatusBarCompat.setStatusBarColor(this, statusBarColor)
    }


    override fun onCreate(@Nullable savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        //使用AlerDialog的时候，不能传applicationContext 会报错You need to use a Theme.AppCompat theme (or descendant) with this activity.”
        //静态类或单例时，不能使用context，会持有Activity不释放导致内存泄漏 得使用context!!.applicationContext
        context = this
        // 让当前活动的Activity监听网络变化
        networkEvent = this

        setContentView()

        loadDialog = LoadingProgressDialog(this, R.style.progress_dialog_loading, "加载中。。。")

        setupActivity()


    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean
    {
        when (item!!.getItemId())
        {
            android.R.id.home -> supportFinishAfterTransition()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        supportFinishAfterTransition()
    }


    /**
     * 跳转页面
     *
     * @param clz         跳转的Activity类
     */
    fun startActivity(clz: Class<*>) {
        startActivity(Intent(this, clz))
    }

    /**
     * 跳转页面
     *
     * @param clz         跳转的Activity类
     * @param requestCode 请求码
     */
    fun startActivityForResult(clz: Class<*>, requestCode: Int) {
        startActivityForResult(Intent(this, clz), requestCode)
    }

    /**
     * 跳转页面
     *
     * @param clz    跳转的目的Activity类
     * @param bundle 跳转所携带的信息
     */
    fun startActivity(clz: Class<*>, bundle: Bundle?) {
        val intent = Intent(this, clz)
        if (bundle != null) {
            intent.putExtra("bundle", bundle)
        }
        startActivity(intent)
    }

    /**
     * 跳转页面
     *
     * @param clz         跳转的Activity类
     * @param bundle      跳转所携带的信息
     * @param requestCode 请求码
     */
    fun startActivityForResult(clz: Class<*>, requestCode: Int, bundle: Bundle?) {
        val intent = Intent(this, clz)
        if (bundle != null) {
            intent.putExtra("bundle", bundle)
        }
        startActivityForResult(intent, requestCode)
    }

    fun showToast(message: String) {
        runOnUiThread {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    fun showToast(messageId: Int) {
        runOnUiThread {
            Toast.makeText(context, messageId, Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 显示加载提示框
     */
    fun showLoadDialog() {
        runOnUiThread {  loadDialog?.show()}
    }

    /**
     * 隐藏加载提示框
     */
    fun hideLoadDialog() {
        runOnUiThread {
            loadDialog?.let {
                if (loadDialog != null && loadDialog!!.isShowing()) {
                    loadDialog!!.dismiss()
                }
            }
        }
    }

    /**
     * @param status -1 网络断开，0 手机网络，1 Wifi网络
     */
    override fun onNetWorkChanged(status: Int) {
        // 子类需要监听网络变化的 可以重写该方法
    }
}