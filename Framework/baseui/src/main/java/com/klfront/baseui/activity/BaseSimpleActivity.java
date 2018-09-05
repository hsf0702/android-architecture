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
 * Created by lujinlong on 2018/8/17.
 */

public abstract class BaseSimpleActivity extends AppCompatActivity implements NetBroadcastReceiver.NetworkListener {

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

        setContentView(getLayoutResID());
        initMaterialStatusBar(getColorPrimary());

        loadDialog = new LoadingProgressDialog(this, R.style.progress_dialog_loading, "加载中...");

        initContentView();

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        } else {
            //android 4.2以下
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
//                finish();
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
                //finish();
                supportFinishAfterTransition();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    //==========================Helper====================================//

    protected void startActivity(Class clazz, Bundle bundle) {
        Intent intent = new Intent(this, clazz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    protected void startActivityForResult(Class clazz, int requestCode, Bundle bundle) {
        Intent intent = new Intent(this, clazz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
    }

    protected void showToast(@StringRes int resId) {
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
    }

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
     * 获取主题颜色
     *
     * @return
     */
    protected int getDarkColorPrimary() {
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimaryDark, typedValue, true);
        return typedValue.data;
    }
}
