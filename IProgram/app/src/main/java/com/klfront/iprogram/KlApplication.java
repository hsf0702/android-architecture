
package com.klfront.iprogram;

import android.content.Context;

import com.klfront.appcore.BaseApplication;
import com.mob.MobSDK;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

public class KlApplication extends BaseApplication {
    private RefWatcher refWatcher;
    public KlApplication(){
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // 发布应用时 注释以下一行
        refWatcher= initLeakCanary();
        MobSDK.init(this);
    }

    private RefWatcher  initLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return RefWatcher.DISABLED;
        }
        return LeakCanary.install(this);
    }

    public static RefWatcher getRefWatcher(Context context) {
        KlApplication leakApplication = (KlApplication) context.getApplicationContext();
        return leakApplication.refWatcher;
    }
}
