package com.klfront.control.WebGroup;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.widget.Toast;


import com.klfront.utils.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by L on 2016/6/1.
 */
public class WebViewActivity extends AppCompatActivity implements TTommWebViewGroup.IOnStateChangedEvent {
    private Toolbar FTitleBar = null;
    TTommWebViewGroup FWebGroup;
    Uri mCapturedImageURI;
    private Object mUploadMessage;
    private static final int FILECHOOSER_RESULTCODE = 2888;
    private static final int FILE_SELECT_CODE = 3900;
    private static final int IMAGE_SELECT_CODE = 3901;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.webviewer);
        } catch (Exception e) {
            Log.e("setContentView", "error");
        }
        String url = getIntent().getStringExtra("url");

        FTitleBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(FTitleBar);
        setTitle("");
        FTitleBar.setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FWebGroup = (TTommWebViewGroup) findViewById(R.id.webView);
        FWebGroup.SetOnStateChangedEvent(this);
        FWebGroup.Navigate(url);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (OnGoBack()) {

            } else {
                ExitApp();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (OnGoBack()) {

        } else {
            ExitApp();
        }
    }

    protected boolean OnGoBack() {
        //需要配合assets下的qhplatform.js使用。页面用法：qhplatform.On("GoBack", function(){//code; return true;});
        String js = "if (typeof (qhplatform) == 'object' && typeof(qhplatform.FireEvent)=='function'){try{qhplatform.FireEvent('GoBack');}catch (e){}}else{console.log('native:OnGoBack@OnGoBack14377307892612853([true])');}";
        FWebGroup.RunJavascript(js);
        return true;
    }

    @Override
    public void OnNewWebViewCreated(Object sender, ITommWebBrowser web) {

    }

    @Override
    public void OnBeforeLoad(Object sender, String url) {

    }

    @Override
    public void OnTitleChanged(Object sender, String title) {
        FTitleBar.setTitle(title);
    }

    @Override
    public void OnGetLoadProgress(Object sender, ITommWebBrowser web, int progress) {

    }

    @Override
    public void OnGetWebView(Object sender, ITommWebBrowser web) {

    }

    @Override
    public void OnWebViewLoaded(Object sender, String title, String url) {
        FTitleBar.setTitle(title);
    }


    @Override
    public void OnLoadError(Object sender, ITommWebBrowser web, int errorCode, String description, String url) {

    }

    @Override
    public void OnNeedCloseWebView(Object sender, ITommWebBrowser web) {

    }

    @Override
    public boolean OnGetCamera(Object sender, ITommWebBrowser web, Object uploadMsg, Object params) {
        ShowChooseFileDialog(sender, web, uploadMsg, params);
        return true;
    }

    @Override
    public void OnNativeMethodCall(Object sender, ITommWebBrowser webBrowser, String methodid, String method, String args) {
        Log.d("[qhplatform]", String.format("OnNativeMethodCall,method=%s,args=%s", method, args));
        if (args == null) {
            Log.d("[qhplatform]", "OnNativeMethodCall error, args=null , the args must be not null!");
            return;
        }

        if (webBrowser != null) {
            QhJsInterface jsobj = (QhJsInterface) webBrowser.GetJsInterface();
            JSONArray array = new JSONArray();
            try {
                array = new JSONArray(args);
            } catch (Exception err) {
                err.printStackTrace();
            }

            Method[] methods = new Method[0];
            try {
                methods = jsobj.getClass().getMethods();
            } catch (Exception e) {
                e.printStackTrace();
            }
            for (Method m : methods) {
                //检查是否有@JavascriptInterface标签
                boolean isannotationed = false;
                Annotation[] annotations = m.getDeclaredAnnotations();
                for (Annotation a : annotations) {
                    if (a instanceof JavascriptInterface) {
                        isannotationed = true;
                        break;
                    }
                }
                if (!isannotationed) {
                    continue;
                }

                //检查是否有@JavascriptInterface标签

                if (m.getName().equals(method)) {
                    Class[] argtypes = m.getParameterTypes();
                    if (array.length() == argtypes.length)  //函数的参数个数必须一致
                    {
                        try {
                            List<Object> callargs = new ArrayList<Object>();
                            for (int i = 0; i < array.length(); i++) {
                                if (array.get(i) == JSONObject.NULL) {
                                    callargs.add(null);
                                } else {
                                    callargs.add(array.get(i));
                                }
                            }
                            Object ret = m.invoke(jsobj, callargs.toArray());//调用实际的函数
                            if (!m.getReturnType().equals(Void.TYPE)) {
                                RunPlatformCallBack(webBrowser, methodid, ret);
                                //调用回调函数
                            }
                        } catch (Exception err) {
                            Log.e("[qhplatform]", err.getMessage());
                        }
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void OnConsoleMessage(ConsoleMessage consoleMessage) {

    }

    @Override
    public void OnShowJsAlert(Object sender, ITommWebBrowser web, String url, String text) {

    }

    @Override
    public boolean OnProcessUrl(Object sender, ITommWebBrowser web, String url) {
        return false;
    }

    @Override
    public InputStream OnGetLocalResources(String url) {
        InputStream stream = null;
        if (!TextUtils.isEmpty(url)) {
            try {
                if (url.equals("qhplatform.js")) {
                    String js = getFromAssets("qhplatform.js");
                    js += GetQhplatformJs();
                    byte[] data = js.getBytes();
                    stream = new ByteArrayInputStream(data);
                } else {
                    if (url.startsWith("[system]")) {
                        url = url.replace("[system]", "");
                        File file = new File(url);
                        stream = new FileInputStream(file);
                    } else {
                        stream = getAssets().open(url);
                    }
                }
            } catch (Exception err) {
                Log.e("qhplatform.js", err.getMessage());
            }
        }
        return stream;
    }

    private String getFromAssets(String fileName) {
        String Result = "";
        try {
            InputStreamReader inputReader = new InputStreamReader(getAssets().open(fileName));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line = "";
            while ((line = bufReader.readLine()) != null)
                Result += line + "\r\n";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result;
    }

    public static String GetQhplatformJs() {
        String js = "";
        Method[] methods = QhJsInterface.class.getMethods();
        for (Method method : methods) {
            JavascriptInterface jsobj = method.getAnnotation(JavascriptInterface.class);
            if (jsobj != null) {
                js += GetMethodJs(method) + "\r\n\r\n";
            }
        }
        return js;
    }

    private static String GetMethodJs(Method method) {
        String js = String.format("qhplatform.%s = function (", method.getName());
        int cbargindex = -1;

        Annotation[][] annotations = method.getParameterAnnotations();
        for (int i = 0; i < annotations.length; i++) {
            if (annotations[i].length > 0) {
                for (Annotation an : annotations[i]) {
                    if (an instanceof ICallBackArg) {
                        cbargindex = i;
                        break;
                    }
                }
            }
        }

        Class<?>[] params = method.getParameterTypes();
        for (int i = 0; i < params.length; i++) {
            js += String.format(i == params.length - 1 ? "arg%s" : "arg%s,", i);
        }
        if (!method.getReturnType().equals(Void.TYPE)) {
            js += String.format("%scallback", params.length > 0 ? "," : "");
        }
        js += ")\r\n";
        js += String.format("{\r\n");
        if (cbargindex >= 0) {
            js += String.format("var methodid = \"$%s_\" + (new Date()).getTime() + parseInt(Math.random() * 10000);\n" + "    this.CallBackMethods.add(methodid, arg%s);\n" + "    var func = this.CallBackMethods.items(methodid);\n", method.getName(), cbargindex);
        }
        js += String.format("this.CallNativeMethod(\"%s\"", method.getName());
        if (params.length > 0) {
            js += ",";
        }
        for (int i = 0; i < params.length; i++) {
            if (i != cbargindex) {
                js += String.format(i == params.length - 1 ? "arg%s" : "arg%s,", i);
            } else {
                js += String.format(i == params.length - 1 ? "%s" : "%s,", "methodid");
            }
        }
        if (!method.getReturnType().equals(Void.TYPE)) {
            js += ",callback";
        }
        js += ");";
        js += "\r\n}";
        return js;
    }

    @Override
    public Object OnCreateJsInterface(Object sender, ITommWebBrowser web) {
        return new QhJsInterface(FWebGroup, web);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            //屏蔽返回功能
            if (!FWebGroup.Back()) {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FILE_SELECT_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Bundle bb = data.getExtras();
                String name = bb.getString("Path");
                long size = bb.getLong("Size");
                String cbid = bb.getString("CallBackId");
                if (name != null && cbid != null) {
                    try {
                        JSONObject fileobj = new JSONObject();
                        fileobj.put("Path", name);
                        fileobj.put("Size", size);
                        ITommWebBrowser web = FWebGroup.GetCurrWebView();
                        RunPlatformCallBack(web, cbid, fileobj);
                    } catch (Exception err) {
                    }
                }
            }
        } else if (requestCode == FILECHOOSER_RESULTCODE) {
            if (mUploadMessage == null) {
                return;
            }

            Uri result = null;
            try {
                if (resultCode != RESULT_OK) {
                    result = null;
                } else {
                    // retrieve from the private variable if the intent is null
                    result = data == null ? mCapturedImageURI : data.getData();

                    File file = new File(mCapturedImageURI.getPath());
                    if (data == null) {
                        if (file.exists()) {
                            result = Uri.fromFile(file);
                        } else {
                            Toast.makeText(getApplicationContext(), "file not found", Toast.LENGTH_LONG).show();
                            mUploadMessage = null;  //return前必须设置为null,否则不能重新选择
                            return;
                        }
                    }
                }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "activity :" + e, Toast.LENGTH_LONG).show();
            }

            //实在没办法判断mUploadMessage的类型了，用最笨的办法解决吧
            try {
                ((ValueCallback<Uri>) mUploadMessage).onReceiveValue(result);
            } catch (Exception err) {
                err.printStackTrace();
            }

            try {
                ((ValueCallback<Uri[]>) mUploadMessage).onReceiveValue(new Uri[]{result});
            } catch (Exception err) {
                err.printStackTrace();
            }
            mUploadMessage = null;
        } else if (requestCode == FILE_SELECT_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                // Get the Uri of the selected file
                Bundle bb = data.getExtras();
                String name = bb.getString("Path");
                long size = bb.getLong("Size");
                String cbid = bb.getString("CallBackId");
                if (name != null && cbid != null) {
                    try {
                        JSONObject fileobj = new JSONObject();
                        fileobj.put("Path", name);
                        fileobj.put("Szie", size);
                        ITommWebBrowser web = FWebGroup.GetCurrWebView();
                        RunPlatformCallBack(web, cbid, fileobj);
                    } catch (Exception err) {
                    }
                }
            }
        } else if (requestCode == IMAGE_SELECT_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                if (mCapturedImageURI != null) {

                    Uri result = data == null ? mCapturedImageURI : data.getData();
                    String url = result.toString();
                    if (url.startsWith("content:")) {
                        url = getFilePathFromContentUri(result, getContentResolver());
                    }
                    try {
                        JSONObject fileobj = new JSONObject();
                        fileobj.put("Path", url);
                        fileobj.put("Size", -1);
                        if (FWebGroup.ExtTag != null) {
                            RunPlatformCallBack(FWebGroup.GetCurrWebView(), FWebGroup.ExtTag.toString(), fileobj);
                        }
                    } catch (Exception err) {
                    }
                }
            }
        }

    }

    public static String getFilePathFromContentUri(Uri uri, ContentResolver contentResolver) {
        String filePath;
        String[] filePathColumn = {MediaStore.MediaColumns.DATA};

        Cursor cursor = contentResolver.query(uri, filePathColumn, null, null, null);
        // 也可用下面的方法拿到cursor
        //Cursor cursor = this.context.managedQuery(selectedVideoUri, filePathColumn, null, null, null);

        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        filePath = cursor.getString(columnIndex);
        cursor.close();
        return filePath;
    }


    private void ShowChooseFileDialog(Object sender, ITommWebBrowser web, Object uploadMsg, Object params) {
        mUploadMessage = uploadMsg;
        try {
            //Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            //i.addCategory(Intent.CATEGORY_OPENABLE);
            Intent i = new Intent(Intent.ACTION_PICK);
            i.setType("image/*");

            Intent chooserIntent = Intent.createChooser(i, "Image Chooser");

            //File imageStorageDir = new File(Environment.getExternalStorageDirectory(), "YTYC");
            File imageStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "YTYC");
            if (!imageStorageDir.exists()) {
                boolean isSuccess = imageStorageDir.mkdirs();
                if (!isSuccess) {
                    imageStorageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                }
            }
            File file = new File(imageStorageDir + File.separator + "IMG_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
            mCapturedImageURI = Uri.fromFile(file);  //存储图片文件的Uri

            final Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Parcelable[]{captureIntent});

            // On select image call onActivityResult method of activity
            startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE);
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), "Exception:" + e, Toast.LENGTH_LONG).show();
        }

    }

    public static void RunPlatformCallBack(ITommWebBrowser web, String func, Object args) {
        String retstr = GetJsStringArgs(args);
        String jsfunc = String.format("qhplatform.CallFromNative('%s',%s)", func, retstr);
        web.RunJavascript(jsfunc);
    }

    public static String GetJsStringArgs(Object args) {
        String retstr = null;
        if (args instanceof JSONArray || args instanceof JSONObject) {
            retstr = args.toString();
        } else {
            JSONArray obj = new JSONArray();
            obj.put(args);
            retstr = obj.toString();
        }
        return retstr;
    }

    private void DoGoBack() {
        boolean isbacked = FWebGroup.Back();
        if (isbacked) {

        } else {
            ExitApp();
        }
    }

    private void ExitApp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(WebViewActivity.this);
        builder.setTitle("退出提示").setMessage("您确定要退出系统吗？").setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //清除缓存
                FWebGroup.ClearCookies(WebViewActivity.this);
                dialog.dismiss();
                finish();
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(0);
            }
        }).create().show();
    }

    public class QhJsInterface {
        private ITommWebBrowser FWebBrowser = null;
        private TTommWebViewGroup FWebGroup = null;

        public QhJsInterface(TTommWebViewGroup webgroup, ITommWebBrowser webbrowser) {
            FWebBrowser = webbrowser;
            FWebGroup = webgroup;
        }

        public QhJsInterface() {
        }

        @JavascriptInterface
        public void OnGoBack(boolean cangoback) {
            if (cangoback) {
                WebViewActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        WebViewActivity.this.DoGoBack();
                    }
                });
            }
        }

        @JavascriptInterface
        public String GetDeviceName() {
            return "Android";
        }

        @JavascriptInterface
        public String GetDeviceModel() {
            return String.format("%s;%s;%s", Build.MANUFACTURER, Build.MODEL, Build.SERIAL);
        }

        @JavascriptInterface
        public void ShowToast(final String msg) {
            WebViewActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(WebViewActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
            });
        }

        @JavascriptInterface
        public void ClearHistory() {
//            FEventHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                    FWebBrowser.clearHistory();
//                }
//            });
        }

    }
}
