package com.klfront.appcore;

import android.support.multidex.MultiDexApplication;

import com.tuzhenlei.crashhandler.CrashHandler;

import java.io.Serializable;

/**
 * Created by kl on 2018/1/18.
 */

public class BaseApplication extends MultiDexApplication {
    public BaseApplication()
    {
        super();
//        CrashHandler.getInstance().init(this, true);
    }
}
