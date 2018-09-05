package com.klfront.control.WebGroup;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.webkit.ConsoleMessage;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;

//import com.klfront.control.Http.ITommEvent;
//import com.klfront.control.Http.TTommHttpDownloader;
import com.klfront.control.VirtualResource.VirtualResourceMgr;

import java.io.File;
import java.io.InputStream;
import java.util.EventObject;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 14-3-15.
 */
public class TTommWebViewGroup extends LinearLayout implements Animation.AnimationListener, ITommWebBrowser.ITommWebBrowserEvent
{
    private Stack<ITommWebBrowser> FList = null;
    private ITommWebBrowser FCurrWeb = null;
    private Context FContext = null;
    private IOnStateChangedEvent FOnStateChangedEvent = null;
//    private IWebViewAccessCamera FOnWebViewAccessCameraEvent = null;
    private String FLastErrorUrl = null;
    private boolean FIsClosing = false;
    private String FUserAgent = "";
    private String FCookies = "";

    public Object ExtTag = null;

    public TTommWebViewGroup(Context context)
    {
        this(context, null);
    }

    public TTommWebViewGroup(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        FContext = context;
        FList = new Stack<ITommWebBrowser>();

    }

    public void Init()
    {
        FCurrWeb = NewWindow();
    }


    public void SetCookies(String url, String cookies)
    {
        FCookies = cookies;
        for (int i = 0; i < FList.size(); i++)
        {
            ITommWebBrowser web = FList.get(i);
            web.SetCookie(url, cookies);
        }
    }

    public void ClearCookies(Context context)
    {
        CookieSyncManager.createInstance(context);
        CookieSyncManager.getInstance().startSync();
        CookieManager.getInstance().removeSessionCookie();
        CookieManager.getInstance().removeAllCookie();
        for (int i = 0; i < FList.size(); i++)
        {
            ITommWebBrowser web = FList.get(i);
            web.clearCache(true);
            web.clearHistory();
        }
    }

    public void SetOnStateChangedEvent(IOnStateChangedEvent event)
    {
        FOnStateChangedEvent = event;
    }


    public void RunJavascript(String js)
    {
        if (FCurrWeb != null)
        {
            FCurrWeb.RunJavascript(js);
        }
    }

    public void SetUserAgent(String agent)
    {
        FUserAgent = agent;
        for (int i = 0; i < FList.size(); i++)
        {
            ITommWebBrowser web = FList.get(i);
            web.SetUserAgent(agent);
        }
    }

    private void ChangeCurrentWebView()
    {
        if (FOnStateChangedEvent != null)
        {
            FOnStateChangedEvent.OnGetWebView(this, FCurrWeb);
        }
    }

    private ITommWebBrowser AddNewWebView(boolean show)
    {
        ITommWebBrowser web = null;
        web = new TTommWebBrowser(FContext);
        ((TTommWebBrowser) web).setDownloadListener(new DownloadListener()
        {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength)
            {
                //                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                //                request.allowScanningByMediaScanner();
                //                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED); //Notify client once download is completed!
                //                //request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
                //                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(url,contentDisposition,mimetype));
                //                DownloadManager dm = (DownloadManager) FContext.getSystemService(DOWNLOAD_SERVICE);
                //                dm.enqueue(request);
                final String dirPath = Environment.getExternalStorageDirectory().getPath() + "/Temp";
                File dir = new File(dirPath);
                if (!dir.exists())
                {
                    boolean isSucceed = dir.mkdirs();
                    if (!isSucceed)
                    {
                        return;
                    }
                }
                final String filePath = dirPath + "/" + URLUtil.guessFileName(url, contentDisposition, mimetype);
//                TTommHttpDownloader downloader = new TTommHttpDownloader();
//                downloader.AddTommEventListener(new ITommEvent()
//                {
//                    @Override
//                    public String GetEventArgType()
//                    {
//                        return TTommHttpDownloader.TOnDownloadFileComplete.class.getSimpleName();
//                    }
//
//                    @Override
//                    public void OnGetEvent(Object sender, EventObject e)
//                    {
//                        TTommHttpDownloader.TOnDownloadFileComplete complete = ((TTommHttpDownloader.TOnDownloadFileComplete) e);
//                        if (complete.IsSucess())
//                        {
//                            Intent intent = new Intent("qh.intent.action.DOWNLOAD_COMPLETE");
//                            intent.putExtra("uri", "file://" + filePath);
//                            FContext.sendBroadcast(intent);
//                        }
//                    }
//                });
//                downloader.DownloadFileAsync(url, filePath, null, null);
            }
        });

        web.setWebBrowserEventHandler(this);
        if (FUserAgent.equals(""))
        {
            FUserAgent = web.GetUserAgent();
        }

        web.setCacheMode(WebSettings.LOAD_DEFAULT);
        web.setAppCacheMaxSize(1024 * 10240);
        web.setAppCacheEnabled(true);
        web.setBuiltInZoomControls(true);
        web.setSupportZoom(true);
        web.setSavePassword(false);
        web.setUseWideViewPort(true);
        web.setJavaScriptEnabled(true);

        if(FOnStateChangedEvent != null)
        {
            FOnStateChangedEvent.OnNewWebViewCreated(this, web);
        }


        Object objinterface = null;
        if (FOnStateChangedEvent != null)
        {
            objinterface = FOnStateChangedEvent.OnCreateJsInterface(this, web);
        }

        if (objinterface != null)
        {
            web.SetJsInterface(objinterface);
        }

        if (!FUserAgent.equals(""))
        {
            web.SetUserAgent(FUserAgent);
        }

        FList.push(web);
        for (int i = 0; i < this.getChildCount(); i++)
        {
            this.getChildAt(i).setVisibility(View.GONE);
        }
        if (show)
        {
            addView(web.GetTargetWebView(), new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        }

        FCurrWeb = web;
        ChangeCurrentWebView();
        return web;
    }

    private boolean RemoveCurrWebView()
    {
        if (FList.size() > 1)
        {   //不能关闭所有的web,最少要保留一个
            if (!FIsClosing)
            {
                FIsClosing = true;
                TranslateAnimation right_out_anim = new TranslateAnimation(0, getWidth(), 0, 0);
                right_out_anim.setDuration(300);
                right_out_anim.setAnimationListener(this);
                startAnimation(right_out_anim);
            }
            return true;
        }
        return false;
    }


    public ITommWebBrowser NewWindow()
    {
        return NewWindow(null);
    }

    public ITommWebBrowser NewWindow(String url)
    {
        FCurrWeb = AddNewWebView(true);
        if (FCurrWeb != null && url != null)
        {
            FCurrWeb.SetCookie(url, FCookies);
            FCurrWeb.loadUrl(url);
        }
        return FCurrWeb;
    }

    public void CloseWindow()
    {
        if (RemoveCurrWebView())
        {

        }
        else
        {//处理内蒙反馈的：退到最后一个页面js调用closeWindows没有关掉界面的问题
            if (FList.size() == 1)
            {
                if (!FIsClosing)
                {
                    FIsClosing = false;
                    FList.pop();
                    removeView(FCurrWeb.GetTargetWebView());
                    FCurrWeb = null;
                    //关掉Webview接客户端界面
                    Intent intent = new Intent("QhCloseWebViewWindow");
                    FContext.sendBroadcast(intent);
                }
            }
        }
    }

    public void CloseWindow(ITommWebBrowser web)
    {
        if (FCurrWeb == web)
        {
            CloseWindow();
        }
        else
        {
            removeView(web.GetTargetWebView());
        }
    }

    public boolean Back()
    {
        if (FCurrWeb != null)
        {
            if (FCurrWeb.canGoBack())
            {
                FCurrWeb.goBack();
                return true;
            }
            else
            {
                return RemoveCurrWebView();
            }
        }
        return false;
    }


    private void FireBeforeLoadEvent(String url)
    {
        if (FOnStateChangedEvent != null)
        {
            FOnStateChangedEvent.OnBeforeLoad(this, url);
        }
    }

    private void FireAfterLoadEvent(String title,String url)
    {
        if (FOnStateChangedEvent != null)
        {
            FOnStateChangedEvent.OnWebViewLoaded(this,title, url);
        }
    }


    private void FireLoadErrorEvent(ITommWebBrowser web, Object view, int errorCode, String description, String url)
    {
        if (FOnStateChangedEvent != null)
        {
            FOnStateChangedEvent.OnLoadError(this, web, errorCode, description, url);
        }
    }

    private void FireOnCloseWindowEvent(ITommWebBrowser web)
    {
        if (FOnStateChangedEvent != null)
        {
            FOnStateChangedEvent.OnNeedCloseWebView(this, web);
        }
    }


    private boolean FireProcessUrlEvent(ITommWebBrowser web, String url)
    {
        if (FOnStateChangedEvent != null)
        {
            return FOnStateChangedEvent.OnProcessUrl(this, web, url);
        }
        return false;
    }

    private boolean FireOnGetCameraEvent(ITommWebBrowser web, Object uploadMsg, Object params)
    {
        if (FOnStateChangedEvent != null)
        {
            FOnStateChangedEvent.OnGetCamera(this, web, uploadMsg, params);
            return true;
        }
        return false;
    }

    public ITommWebBrowser GetCurrWebView()
    {
        return FCurrWeb;
    }

    public boolean IsLoaded()
    {
        if (FCurrWeb != null)
        {
            return FCurrWeb.IsLoaded();
        }
        return true;
    }

    public int GetLoadPercent()
    {
        if (FCurrWeb != null)
        {
            return FCurrWeb.GetLoadPercent();
        }
        return 0;
    }

    public void Forward()
    {
        if (FCurrWeb != null)
        {
            if (FCurrWeb.canGoForward())
            {
                FCurrWeb.goForward();
            }
        }
    }

    public void Refresh()
    {
        if (FCurrWeb != null)
        {
            VirtualResourceMgr.ClearCache();//清理本地缓存
            FCurrWeb.reload();
        }
    }

    public void ShowLoadErrorMessage(String url, String errorurl)
    {
        if (FCurrWeb != null)
        {
            FLastErrorUrl = url;
            FCurrWeb.loadUrl(errorurl);
        }
    }

    public void Retry()
    {
        if (FCurrWeb != null && FLastErrorUrl != null)
        {
            FCurrWeb.loadUrl(FLastErrorUrl);
        }
    }

    public void Navigate(String url)
    {
        if (FCurrWeb == null)
        {
            NewWindow(url);
        }
        else
        {
            FCurrWeb.loadUrl(url);
        }
    }

    public void Navigate(String url, String args)
    {
        if (args != null)
        {
            if (url.indexOf("?") < 0)
            {
                url = String.format("%s?%s", url, args.replace("%26", "&"));
            }
            else
            {
                url = String.format("%s&%s", url, args.replace("%26", "&"));
            }
        }
        Navigate(url);
    }


    public void Stop()
    {
        if (FCurrWeb != null)
        {
            FCurrWeb.stopLoading();
        }
    }

    public String getTitle()
    {
        if (FCurrWeb != null)
        {
            return FCurrWeb.getTitle();
        }
        return null;
    }

    @Override
    public void onAnimationStart(Animation animation)
    {

    }

    public Bitmap GetSnapshot(int width, int height)
    {
        if (FCurrWeb != null)
        {
            return FCurrWeb.getDrawingCache();

            /*
            Picture snapShot =  FCurrWeb.capturePicture();
            try
            {
                Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bmp);
                canvas.drawPicture(snapShot, new Rect(0,0,width,height));
                return bmp;
            }
            catch (Exception err)
            {
            }
            */
        }
        return null;
    }

    @Override
    public void onAnimationEnd(Animation animation)
    {
        if (FCurrWeb != null)
        {
            FIsClosing = false;
            FList.pop();
            removeView(FCurrWeb.GetTargetWebView());
            FCurrWeb = null;

            if (FList.size() > 0)
            {
                FCurrWeb = FList.peek();
                ChangeCurrentWebView();
                setTitle(FCurrWeb.getTitle());
                FCurrWeb.GetTargetWebView().setVisibility(View.VISIBLE);
            }
        }

    }

    @Override
    public void onAnimationRepeat(Animation animation)
    {

    }

    private void setTitle(String title)
    {
        if (FOnStateChangedEvent != null)
        {
            FOnStateChangedEvent.OnTitleChanged(this, title);
        }
    }

    private void setProgress(ITommWebBrowser web, int progress)
    {
        if (FOnStateChangedEvent != null)
        {
            FOnStateChangedEvent.OnGetLoadProgress(this, web, progress);
        }
    }



    @Override
    public void onProgressChanged(ITommWebBrowser web, Object view, int newProgress)
    {
        setProgress(web, newProgress);
    }

    @Override
    public void onReceivedTitle(ITommWebBrowser web, Object view, String title)
    {
        setTitle(title);
    }

    @Override
    public void onCloseWindow(ITommWebBrowser web)
    {
        FireOnCloseWindowEvent(web);
        CloseWindow();
    }

    @Override
    public ITommWebBrowser onCreateWindow(ITommWebBrowser web, Object view, boolean dialog, boolean userGesture, Message resultMsg)
    {
        ITommWebBrowser oldweb = web;
        FCurrWeb = AddNewWebView(false);
        Animation a = new TranslateAnimation(0, -getWidth(), 0, 0);
        a.setDuration(300);
        oldweb.GetTargetWebView().setAnimation(a);
        a.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationStart(Animation animation)
            {

            }

            @Override
            public void onAnimationEnd(Animation animation)
            {
                addView(FCurrWeb.GetTargetWebView(), new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            }

            @Override
            public void onAnimationRepeat(Animation animation)
            {

            }
        });
        oldweb.GetTargetWebView().startAnimation(a);
        return FCurrWeb;
    }

    @Override
    public void onReceivedError(ITommWebBrowser web, Object view, int errorCode, String description, String failingUrl)
    {
        FireLoadErrorEvent(web, view, errorCode, description, failingUrl);
    }

    @Override
    public boolean shouldOverrideUrlLoading(ITommWebBrowser web, Object view, String url)
    {
        return FireProcessUrlEvent(web, url);
    }


    @Override
    public void onPageStarted(ITommWebBrowser web, Object view, String url, Bitmap favicon)
    {
        FireBeforeLoadEvent(url);
    }

    @Override
    public void onPageFinished(ITommWebBrowser web, Object view, String url)
    {
        if(view instanceof WebView) {
            FireAfterLoadEvent(((WebView) view).getTitle(), url);
        }else
        {
            FireAfterLoadEvent("", url);
        }
    }

    @Override
    public boolean OnGetCamera(ITommWebBrowser web, Object uploadMsg, Object params)
    {
        return FireOnGetCameraEvent(web, uploadMsg, params);
    }

    @Override
    public void OnConsoleMessage(ITommWebBrowser browser, ConsoleMessage consoleMessage)
    {
        if (consoleMessage.message() == null)
        {
            return;
        }
        if (FOnStateChangedEvent != null)
        {
            FOnStateChangedEvent.OnConsoleMessage(consoleMessage);
        }
        ConsoleMessage.MessageLevel messageLevel = consoleMessage.messageLevel();
        switch (messageLevel)
        {
            case DEBUG:
            case LOG:
            case TIP:
                String msg = consoleMessage.message();
                Pattern p = Pattern.compile("native:(.*?)@(.*?)\\((.*?)\\)");
                Matcher matcher = p.matcher(msg);
                if (matcher.matches())
                {
                    String func = matcher.group(1);
                    String methodid = matcher.group(2);
                    String args = matcher.group(3);
                    if (FOnStateChangedEvent != null)
                    {
                        FOnStateChangedEvent.OnNativeMethodCall(TTommWebViewGroup.this, browser, methodid, func, args);
                    }
                }
                else
                {
                    Log.d("[qhplatform]", String.format("WebView Javascript Message:Source %s,第%d行, %s", consoleMessage.sourceId(), consoleMessage.lineNumber(), consoleMessage.message()));
                }
                break;
            case ERROR:
                Log.e("[qhplatform]", String.format("WebView Javascript Error:Source %s,第%d行, %s", consoleMessage.sourceId(), consoleMessage.lineNumber(), consoleMessage.message()));
                break;
            default:
                Log.d("[qhplatform]", String.format("WebView Javascript Message:Source %s,第%d行, %s", consoleMessage.sourceId(), consoleMessage.lineNumber(), consoleMessage.message()));
                break;
        }
    }

    @Override
    public void OnShowJsAlert(ITommWebBrowser web, String url, String text)
    {
        if (FOnStateChangedEvent != null)
        {
            FOnStateChangedEvent.OnShowJsAlert(this, web, url, text);
        }
    }

    @Override
    public InputStream OnGetLocalResources(String url)
    {
        if (FOnStateChangedEvent != null)
        {
            return FOnStateChangedEvent.OnGetLocalResources(url);
        }
        return null;
    }

    public boolean CanBack()
    {
        if (FCurrWeb != null)
        {
            if (FCurrWeb.canGoBack())
            {
                return true;
            }
        }
        return FList.size() > 1;
    }

    public boolean HasMultiPages()
    {
        if (FCurrWeb != null)
        {
            if (FCurrWeb.canGoBack())
            {
                return FList.size() > 2;
            }
        }
        return false;
    }

    public interface IOnStateChangedEvent
    {

        void OnNewWebViewCreated(Object sender, ITommWebBrowser web);

        void OnTitleChanged(Object sender, String title);

        void OnGetLoadProgress(Object sender, ITommWebBrowser web, int progress);

        void OnGetWebView(Object sender, ITommWebBrowser web);

        //void OnWebViewLoaded(Object sender, String url);//返回上一页时 需要在这里更新标题
        void OnWebViewLoaded(Object sender,String title, String url);

        void OnBeforeLoad(Object sender, String url);

        void OnLoadError(Object sender, ITommWebBrowser web, int errorCode, String description, String url);

        void OnNeedCloseWebView(Object sender, ITommWebBrowser web);

        boolean OnGetCamera(Object sender, ITommWebBrowser web, Object uploadMsg, Object params);

        void OnNativeMethodCall(Object sender, ITommWebBrowser webBrowser, String mehtodid, String method, String args);

        void OnConsoleMessage(ConsoleMessage consoleMessage);

        void OnShowJsAlert(Object sender, ITommWebBrowser web, String url, String text);

        boolean OnProcessUrl(Object sender, ITommWebBrowser web, String url);

        InputStream OnGetLocalResources(String url);

        Object OnCreateJsInterface(Object sender, ITommWebBrowser web);
    }

}
