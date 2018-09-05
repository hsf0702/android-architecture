package com.klfront.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.StringRes;


/**
 * Created by L on 2017/4/10.
 */

public class UIUtils {

    public static final int DELAY = 1000;
    private static long lastClickTime = 0;
    public static boolean isFastClick(){
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClickTime > DELAY) {
            lastClickTime = currentTime;
            return false;
        }else{
            lastClickTime = currentTime;
            return true;
        }
    }

}
