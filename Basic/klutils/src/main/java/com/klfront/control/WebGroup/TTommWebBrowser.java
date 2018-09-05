package com.klfront.control.WebGroup;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.GeolocationPermissions;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;


import com.klfront.control.VirtualResource.VirtualResourceMgr;

import java.io.InputStream;

public class TTommWebBrowser extends WebView implements ITommWebBrowser {
    private Object FTagEx = null;
    private boolean FIsBusy = false;
    private String FWebTitle = "";  //标题
    private boolean FIsLoaded = false;
    private int FLoadedPercent = 0;
    private Context FContext = null;
    private ITommWebBrowserEvent FWebEvent = null;
    private Object FJsObject = null;

    public String GetWebTitle() {
        if (FWebTitle == "") {
            FWebTitle = "无标题";
        }
        return FWebTitle;
    }

    public int GetLoadPercent() {
        return FLoadedPercent;
    }

    @Override
    public void setJavaScriptEnabled(boolean enabled) {
        getSettings().setJavaScriptEnabled(enabled);
    }

    @Override
    public void setCacheMode(int cacheMode) {
        getSettings().setCacheMode(cacheMode);
    }

    @Override
    public void setAppCachePath(String appCacheDir) {
        getSettings().setAppCachePath(appCacheDir);
    }

    @Override
    public void setAppCacheMaxSize(int cacheMaxSize) {
        getSettings().setAppCacheMaxSize(cacheMaxSize);
    }

    @Override
    public void setAppCacheEnabled(boolean enabled) {
        getSettings().setAppCacheEnabled(enabled);
    }

    @Override
    public void setBuiltInZoomControls(boolean enabled) {
        getSettings().setBuiltInZoomControls(enabled);
    }

    @Override
    public void setSupportZoom(boolean enabled) {
        getSettings().setSupportZoom(enabled);
    }

    @Override
    public void setSavePassword(boolean enabled) {
        getSettings().setSavePassword(enabled);
    }

    @Override
    public void setUseWideViewPort(boolean enabled) {
        getSettings().setUseWideViewPort(enabled);
    }

    public String GetUserAgent() {
        return getSettings().getUserAgentString();
    }

    public void SetUserAgent(String useragent) {
        getSettings().setUserAgentString(useragent);
    }

    public Object GetJsInterface() {
        return FJsObject;
    }

    public void SetJsInterface(Object jsobj) {
        FJsObject = jsobj;
    }


    public Object GetTagEx() {
        return FTagEx;
    }

    public void SetTagEx(Object o) {
        FTagEx = o;
    }

    public String GetCookie(String url) {
        CookieSyncManager.createInstance(getContext());
        CookieManager cookieManager = CookieManager.getInstance();
        return cookieManager.getCookie(url);
    }

    public void SetCookie(String url, String cookie) {
        CookieSyncManager.createInstance(getContext());
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setCookie(url, cookie);
        CookieSyncManager.getInstance().sync();
    }

    @Override
    public boolean GetIsBusy() {
        return FIsBusy;
    }

    @Override
    public void SetIsBusy(boolean b) {
        FIsBusy = b;
    }

    @Override
    public void RunJavascript(String script) {
        loadUrl(String.format("javascript:%s", script));
    }

    @Override
    public void RunJavaScript(String script, ValueCallback<String> callback) {
        if (Build.VERSION.SDK_INT >= 19) {
            evaluateJavascript(script, callback);
        } else {
            RunJavascript("console.log('sdk version too lower.');");
        }
    }

    @Override
    public void AddJavaScriptFunction(String scriptfunc) {
        String js = "var head = document.getElementsByTagName('head').item(0);" + "var newjs = document.createElement('script');" + String.format("newjs.text = \"%s\";", scriptfunc) + "head.appendChild(newjs);";
        RunJavascript(js);
    }

    @Override
    public void loadUrl(String url) {
        super.loadUrl(url);
        FIsBusy = true;
        FIsLoaded = false;
    }

    @Override
    public boolean IsLoaded() {
        return FIsLoaded;
    }

    public TTommWebBrowser(Context context) {
        super(context);
        FContext = context;
        super.setWebChromeClient(new TWebBrowserClient());
        super.setWebViewClient(new TWebViewClient());
        WebSettings settings = getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setSavePassword(false);
        settings.setSaveFormData(false);
        settings.setSupportMultipleWindows(true);
        settings.setAllowFileAccess(true);
        settings.setPluginState(WebSettings.PluginState.ON);
        settings.setGeolocationEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);

        setDrawingCacheEnabled(true);

        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        String dir = context.getDir("cache", Context.MODE_PRIVATE).getPath();
        settings.setGeolocationDatabasePath(dir);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            setWebContentsDebuggingEnabled(true);
        }
        this.requestFocus(View.FOCUS_DOWN);
        this.requestFocusFromTouch();

		/*
        setOnTouchListener(new OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event) 
			{
				if(event.getAction() == MotionEvent.ACTION_UP)
				{
					v.requestFocus();
				}
				return false;
			}
			
		});
		*/
    }

    @Override
    public void setWebBrowserEventHandler(ITommWebBrowserEvent event) {
        FWebEvent = event;
    }

    @Override
    public View GetTargetWebView() {
        return TTommWebBrowser.this;
    }

    public class TWebBrowserClient extends WebChromeClient {
        @Override
        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
            if (FWebEvent != null) {
                FWebEvent.OnConsoleMessage(TTommWebBrowser.this, consoleMessage);
            }
            return super.onConsoleMessage(consoleMessage);
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            FLoadedPercent = newProgress;
            if (FWebEvent != null) {
                FWebEvent.onProgressChanged(TTommWebBrowser.this, view, newProgress);
            }
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            //String a = FTagEx.toString();
            FWebTitle = title;
            if (FWebEvent != null) {
                FWebEvent.onReceivedTitle(TTommWebBrowser.this, view, title);
            }
        }

        @Override
        public void onReceivedIcon(WebView view, Bitmap icon) {

        }

        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            int a = 0;
            a++;
        }


        @Override
        public void onCloseWindow(WebView window) {
            if (FWebEvent != null) {
                FWebEvent.onCloseWindow(TTommWebBrowser.this);
            }
            super.onCloseWindow(window);
        }


        @Override
        public boolean onCreateWindow(WebView view, boolean dialog, boolean userGesture, Message resultMsg) {
            ITommWebBrowser newweb = null;
            if (FWebEvent != null) {
                newweb = FWebEvent.onCreateWindow(TTommWebBrowser.this, view, dialog, userGesture, resultMsg);
            }

            if (newweb != null) {
                WebView childView = (WebView) newweb.GetTargetWebView();
                if (childView != null) {
                    WebViewTransport transport = (WebViewTransport) resultMsg.obj;
                    transport.setWebView(childView);
                    resultMsg.sendToTarget();
                    childView.requestFocus();
                    return true;
                }
            }
            return false;
        }


        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            result.cancel();

            /*
            AlertDialog.Builder msgbox = new AlertDialog.Builder(view.getContext());
            msgbox.setTitle("来自网页的消息");
            msgbox.setMessage(message);
            msgbox.setPositiveButton("确定", null);
            msgbox.show();
            */
            if (FWebEvent != null) {
                FWebEvent.OnShowJsAlert(TTommWebBrowser.this, url, message);
            }
            return true;
        }

        @Override
        public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setTitle("请确认");
            builder.setMessage(message);
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    result.confirm();
                }
            });
            builder.setNeutralButton("取消", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    result.cancel();
                }
            });
            builder.setCancelable(false);
            builder.create();
            builder.show();
            return true;
        }

		/*
        @Override
		public WebView shouldCreateWindow(WebView view)
		{
			return view;  
		}
		*/

        public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
            return false;
        }


        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
            if (FWebEvent != null) {
                FWebEvent.OnGetCamera(TTommWebBrowser.this, filePathCallback, fileChooserParams);
                return true;
            }
            return false;
        }


        // openFileChooser for Android 3.0+
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
            if (FWebEvent != null) {
                FWebEvent.OnGetCamera(TTommWebBrowser.this, uploadMsg, acceptType);
            }
        }

        // openFileChooser for Android < 3.0
        public void openFileChooser(ValueCallback<Uri> uploadMsg) {
            openFileChooser(uploadMsg, "");
        }

        //openFileChooser for other Android versions
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {

            openFileChooser(uploadMsg, acceptType);
        }


        @Override
        public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
            callback.invoke(origin, true, false);
        }

    }

    public class TWebViewClient extends WebViewClient {
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            WebResourceResponse webresp = null;//retrun null 浏览器就会继续请求远程服务器

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                url = VirtualResourceMgr.GetVirtualUrl(url);
                //http://localhost/android_asset/error.png

                if (url.startsWith("http://localhost:20487") || url.startsWith("[static]")) {
                    String resurl = url.replace("http://localhost:20487/", "");
                    String mimetype = "text/html";
                    if (resurl.contains(".css")) {
                        mimetype = "text/css";
                    } else if (resurl.contains(".js")) {
                        mimetype = "application/x-javascript";
                    } else if (resurl.contains(".png")) {
                        mimetype = "image/png";
                    } else if (resurl.contains(".gif")) {
                        mimetype = "image/gif";
                    } else if (resurl.contains(".jpg")) {
                        mimetype = "image/jpg";
                    }
                    try {
                        InputStream inputs = null;
                        if (FWebEvent != null) {
                            inputs = FWebEvent.OnGetLocalResources(resurl);
                        }
                        webresp = new WebResourceResponse(mimetype, "utf-8", inputs);
                        //todo: inputs没有办法调用close方法,会造成内存泄漏吗???
                    } catch (Exception err) {
                        Log.e("[qhplatform]", String.format("Can not open resource file:%s", resurl));
                    }
                }
            }
            return webresp;
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                String url = request.getUrl().toString();
                url = VirtualResourceMgr.GetVirtualUrl(url);
                if (url.startsWith("http://localhost:20487") || url.startsWith("[static]")) {
                    WebResourceResponse webresp = null;
                    String resurl = url.replace("http://localhost:20487/", "");
                    String mimetype = "text/html";
                    if (resurl.contains(".css")) {
                        mimetype = "text/css";
                    } else if (resurl.contains(".js")) {
                        mimetype = "application/x-javascript";
                    } else if (resurl.contains(".png")) {
                        mimetype = "image/png";
                    } else if (resurl.contains(".gif")) {
                        mimetype = "image/gif";
                    } else if (resurl.contains(".jpg")) {
                        mimetype = "image/jpg";
                    }
                    try {
                        InputStream inputs = null;
                        if (FWebEvent != null) {
                            inputs = FWebEvent.OnGetLocalResources(resurl);
                        }
                        webresp = new WebResourceResponse(mimetype, "utf-8", inputs);
                    } catch (Exception err) {
                        Log.e("[qhplatform]", String.format("Can not open resource file:%s", resurl));
                    }
                    return webresp;
                } else {
                    return super.shouldInterceptRequest(view, request);
                }
            } else {
                return super.shouldInterceptRequest(view, request);
            }
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            if (FWebEvent != null) {
                FWebEvent.onReceivedError(TTommWebBrowser.this, view, errorCode, description, failingUrl);
            }
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, android.net.http.SslError error) {
            handler.proceed();
        }

        //通过location.href打开的页面 通过open跳转到新页面，依次走shouldOverrideUrlLoading和onPageStarted。//可通过return true拦截 只走shouldOverrideUrlLoading
        //通过location.href打开的页面 再通过location.href 新页面，依次走shouldOverrideUrlLoading和onPageStarted。//可通过return true拦截 只走shouldOverrideUrlLoading

        //***避免使用*** 通过window.open打开的页面 再通过location.href 跳转到weiclassviewer://这样的页面，只走onPageStarted ===（注意：但是有的手机通过open打开的页面（或者通过open打开父级页面，然后location.href打开的本页面），再通过location.href 或a标签跳转到新页，自定义的协议拦截有时失败）。

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            boolean ret = false;
            if (FWebEvent != null) {
                ret = FWebEvent.shouldOverrideUrlLoading(TTommWebBrowser.this, view, url);
            }
            if (url.startsWith("qianhe://")) {
                return true;//拦截 不走onPageStarted
            } else if (url.startsWith("weiclassviewer://")) {
                return true;//拦截 不走onPageStarted
            }
            return ret;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            boolean ret = false;
            if (FWebEvent != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    String url = request.getUrl().toString();
                    if (url.startsWith("qianhe://")) {
                        if (FWebEvent != null) {
                            FWebEvent.onPageStarted(TTommWebBrowser.this, view, url, null);
                        }
                        return true;
                    } else if (url.startsWith("weiclassviewer://")) {
                        if (FWebEvent != null) {
                            FWebEvent.onPageStarted(TTommWebBrowser.this, view, url, null);
                        }
                        return true;
                    } else {
                        ret = FWebEvent.shouldOverrideUrlLoading(TTommWebBrowser.this, view, request.getUrl().toString());
                    }
                }
            }
            return ret;
        }


        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            if (url.startsWith("qianhe://")) {
                view.stopLoading();
                //                view.goBack();
            } else if (url.startsWith("weiclassviewer://")) {
                view.stopLoading();
                //                view.goBack();
            }
            if (FWebEvent != null) {
                FWebEvent.onPageStarted(TTommWebBrowser.this, view, url, favicon);
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            FIsLoaded = true;
            if (FWebEvent != null) {
                FWebEvent.onPageFinished(TTommWebBrowser.this, view, url);
            }
        }

    }

}
