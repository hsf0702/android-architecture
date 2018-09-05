package com.klfront.control.WebGroup;

import android.graphics.Bitmap;
import android.graphics.Picture;
import android.os.Message;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.ValueCallback;

import java.io.InputStream;

/**
 * Created by Administrator on 2014/8/25.
 */
public interface ITommWebBrowser
{
    void setWebBrowserEventHandler(ITommWebBrowserEvent event);

    View GetTargetWebView();

    void RunJavascript(String js);

    void RunJavaScript(String js, ValueCallback<String> callback);

    void SetUserAgent(String ua);

    String GetUserAgent();

    void addJavascriptInterface(Object obj, String name);

    void loadUrl(String url);

    boolean canGoBack();

    void goBack();

    boolean IsLoaded();

    int GetLoadPercent();

    boolean canGoForward();

    void goForward();

    void reload();

    void stopLoading();

    String getTitle();

    Picture capturePicture();

    Bitmap getDrawingCache();

    Object getSettings();

    void setJavaScriptEnabled(boolean enabled);

    void setCacheMode(int cacheMode);

    void setAppCachePath(String appCacheDir);

    void setAppCacheMaxSize(int cacheMaxSize);

    void setAppCacheEnabled(boolean enabled);

    void setBuiltInZoomControls(boolean enabled);

    void setSupportZoom(boolean enabled);

    void setSavePassword(boolean enabled);

    void setUseWideViewPort(boolean enabled);

    void clearCache(boolean aa);

    void clearHistory();

    void AddJavaScriptFunction(String scriptfunc);

    Object GetTagEx();

    void SetTagEx(Object o);

    String GetCookie(String url);

    void SetCookie(String url, String cookie);

    boolean GetIsBusy();

    void SetIsBusy(boolean b);

    Object GetJsInterface();
    void SetJsInterface(Object jsobj);



    public interface ITommWebBrowserEvent
    {
        void onProgressChanged(ITommWebBrowser web, Object view, int newProgress);

        void onReceivedTitle(ITommWebBrowser web, Object view, String title);

        void onCloseWindow(ITommWebBrowser web);

        ITommWebBrowser onCreateWindow(ITommWebBrowser web, Object view, boolean dialog, boolean userGesture, Message resultMsg);

        void onReceivedError(ITommWebBrowser web, Object view, int errorCode, String description, String failingUrl);

        boolean shouldOverrideUrlLoading(ITommWebBrowser web, Object view, String url);

        void onPageStarted(ITommWebBrowser web, Object view, String url, Bitmap favicon);

        void onPageFinished(ITommWebBrowser web, Object view, String url);

        boolean OnGetCamera(ITommWebBrowser web, Object uploadMsg, Object params);

        void OnConsoleMessage(ITommWebBrowser web, ConsoleMessage consoleMessage);

        void OnShowJsAlert(ITommWebBrowser web, String url, String text);

        InputStream OnGetLocalResources(String url);


    }

}
