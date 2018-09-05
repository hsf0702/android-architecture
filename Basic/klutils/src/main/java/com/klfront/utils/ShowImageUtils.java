package com.klfront.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import java.util.UUID;

/**
 * Created by L on 2016/4/19.
 */
public class ShowImageUtils {

    /**
     * 使用系统图片查看器打开图片
     * @param ctx
     * @param largeUrl http://或file://或content://
     */
    public static void ShowRawImage(final Context ctx, String largeUrl) {
        Uri uri = null;
        if ("file".equalsIgnoreCase(uri.getScheme()))
        {
            //要在应用间共享文件，您应发送一项 content://URI，并授予 URI 临时访问权限。而进行此授权的最简单方式是使用 FileProvider类
            File file = new File(largeUrl.replace("file://",""));
            if(file.exists()) {
                uri = UriUtils.GetUriForFileByFileProvider(ctx, file);
            }else
            {
                return;
            }
        }
        else
        {
            uri = Uri.parse(largeUrl);
        }
        ShowImage(ctx,uri);
    }

    /**
     * 使用系统图片查看器打开图片
     * @param ctx
     * @param file
     */
    public static void ShowLocalImage(final Context ctx, File file) {
        if(file.exists()) {
            Uri uri = UriUtils.GetUriForFileByFileProvider(ctx, file);
            ShowImage(ctx,uri);
        }else
        {
            Toast.makeText(ctx, "文件不存在",Toast.LENGTH_SHORT).show();
        }
    }

    public static void ShowImage(Context ctx,Uri uri)
    {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//Android 7.0 要求的权限
        intent.setDataAndType(uri, "image/*");
        ctx.startActivity(intent);
    }
}
