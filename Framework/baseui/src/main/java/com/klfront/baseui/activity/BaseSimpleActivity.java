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

public abstract class BaseSimpleActivity extends BaseActivity {
    @Override
    protected void initParams(Bundle savedInstanceState) {
        // 空实现
    }

    @Override
    protected void initData() {
        // 空实现
    }
}
