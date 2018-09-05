package com.klfront.control.WebGroup;

import android.net.Uri;
import android.webkit.ValueCallback;

/**
 * Created by Administrator on 2014/8/25.
 */
public interface IWebViewAccessCamera
{
    public void OnGetCamera(Object sender, ValueCallback<Uri> uploadMsg, String acceptType);
}