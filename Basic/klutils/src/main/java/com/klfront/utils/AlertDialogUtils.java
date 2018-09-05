package com.klfront.utils;

import android.content.DialogInterface;

import java.lang.reflect.Field;

/**
 * Created by L on 2017/4/27.
 */

public class AlertDialogUtils {

    //让dialog点击确定后 不自动消失
    public static void ForbidAutoCloseDialog(DialogInterface dialog) {
        try {
            Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
            field.setAccessible(true);
            field.set(dialog, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //强制关闭dialog
    public static void ForceCloseDialog(DialogInterface dialog) {
        try {
            Field field = dialog.getClass().getSuperclass()
                    .getDeclaredField("mShowing");
            field.setAccessible(true);
            field.set(dialog, true);
            dialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
