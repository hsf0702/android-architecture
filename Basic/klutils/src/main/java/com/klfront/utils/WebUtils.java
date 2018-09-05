package com.klfront.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Created by lujinlong on 2018/1/6.
 */

public class WebUtils
{
    public static void OpenLink(Context context, String url)
    {
        Uri uri = Uri.parse(url);
        if (uri != null)
        {
            try
            {
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
            catch (Exception err)
            {
            }
        }
    }

    public static void RunApp(Context context, String packageName)
    {
        try
        {
            Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
        catch (Exception err)
        {
        }
    }
}
