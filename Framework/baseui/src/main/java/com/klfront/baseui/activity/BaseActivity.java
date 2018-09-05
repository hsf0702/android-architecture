package com.klfront.baseui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import com.githang.statusbar.StatusBarCompat;
import com.klfront.baseui.R;
import com.klfront.baseui.broadcastreceiver.NetBroadcastReceiver;
import com.klfront.baseui.widget.LoadingProgressDialog;

/**
 * Created on 2018/8/17.
 * @author lujinlong
 *
 */
public abstract class BaseActivity extends AppCompatActivity implements NetBroadcastReceiver.NetworkListener  {

    /**
     * 初始化参数
     * 考虑系统恢复Activity调用onCreate时从savedInstanceState中恢复参数的情况
     *
     * @param savedInstanceState
     */
    protected abstract void initParams(Bundle savedInstanceState);

    /**
     * 由子类设置资源ID
     *
     * @return
     */
    protected abstract int getLayoutResID();

    /**
     * 初始化视图
     */
    protected abstract void initContentView();


    /**
     * 初始化数据
     */
    protected abstract void initData();


    /**
     * 处理返回事件
     * 子类返回false 则表示交给父类处理
     *
     * @return 子类是否已处理
     */
    protected abstract boolean onGoBack();

    /**
     * 网络状态发生变化
     * @param status 网络状态 -1 网络断开，0 手机网络，1 Wifi网络
     */
    @Override
    public void onNetWorkChanged(int status) {
        // 子类需要监听网络变化的，可以重写该方法
    }

    /**
     * 加载提示框
     */
    private LoadingProgressDialog loadDialog = null;
    private BroadcastReceiver logoutReceiver;
    public static NetBroadcastReceiver.NetworkListener NETWORK_LISTENER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initParams(savedInstanceState);
        setContentView(getLayoutResID());
        initMaterialStatusBar(getColorPrimary());
        loadDialog = new LoadingProgressDialog(this, R.style.progress_dialog_loading, "加载中...");
        initContentView();
        initData();

        IntentFilter filter = new IntentFilter("action_logout");
        logoutReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                finish();
            }
        };
        registerReceiver(logoutReceiver, filter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 让当前活动的Activity监听网络变化
        NETWORK_LISTENER = this;
    }

    @Override
    protected void onPause() {
        NETWORK_LISTENER = null;
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (logoutReceiver != null) {
            unregisterReceiver(logoutReceiver);
        }
    }

    /**
     * 状态栏样式处理
     *
     * @param statusBarColor
     */
    protected void initMaterialStatusBar(@ColorInt int statusBarColor) {
        // SDK 19 20 下的特殊处理
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
                &&Build.VERSION.SDK_INT<Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        StatusBarCompat.setStatusBarColor(this, statusBarColor);
    }

    /**
     * 覆盖setSupportActionBar的默认实现
     *
     * @param toolbarId
     */
    protected void setSupportActionBar(@IdRes int toolbarId) {
        Toolbar toolbar = findViewById(toolbarId);
        setSupportActionBar(toolbar);
    }

    /**
     * 覆盖setSupportActionBar的默认实现
     *
     * @param toolbar
     */
    @Override
    public void setSupportActionBar(@Nullable Toolbar toolbar) {
        super.setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {   // 显示返回键
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setElevation(0);
        }
    }

    /**
     * 处理toolbar上的返回键
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (!onGoBack()) {
                supportFinishAfterTransition();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 处理返回键
     * 三星，OPPO等机型手机上有不执行onBackPressed()的情况，因此统一在onKeyDown中处理
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (!onGoBack()) {
                supportFinishAfterTransition();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    //==========================Helper====================================//

    /**
     * 启动活动
     * @param clazz
     * @param bundle
     */
    protected void startActivity(Class clazz, Bundle bundle) {
        Intent intent = new Intent(this, clazz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    /**
     * 启动活动 需要处理返回结果
     * @param clazz
     * @param requestCode
     * @param bundle
     */
    protected void startActivityForResult(Class clazz, int requestCode, Bundle bundle) {
        Intent intent = new Intent(this, clazz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
    }

    /**
     * 弹出Toast消息
     * @param resId 消息字符串资源Id
     */
    protected void showToast(@StringRes int resId) {
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
    }

    /**
     * 弹出Toast消息
     * @param msg 消息内容
     */
    protected void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 显示加载提示框
     */
    protected void showLoadDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (loadDialog != null) {
                    loadDialog.show();
                }
            }
        });
    }

    /**
     * 隐藏加载提示框
     */
    protected void hideLoadDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (loadDialog != null && loadDialog.isShowing()) {
                    loadDialog.dismiss();
                }
            }
        });
    }

    /**
     * 获取主题颜色
     *
     * @return
     */
    protected int getColorPrimary() {
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        return typedValue.data;
    }

    /**
     * 获取深色的主题颜色
     *
     * @return
     */
    protected int getDarkColorPrimary() {
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimaryDark, typedValue, true);
        return typedValue.data;
    }
}
