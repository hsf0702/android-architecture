package com.klfront.utils;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;

import java.io.File;

/**
 * Created by dell on 2017/7/17.
 */

public class FileProviderUtils {
    /**
     * 使用FileProvider方式获取文件的Uri
     * @param context
     * @param file 文件
     * @return
     */
    public static Uri GetUriForFileByFileProvider(Context context, File file) {
        Uri uri = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //uri = FileProvider.getUriForFile(context, ((IQhContext) context.getApplicationContext()).GetFileProvider(), file);
        } else {
            uri = Uri.fromFile(file);
        }
        return uri;
    }
}
