package com.klfront.httputils;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;

import com.klfront.httputils.interfaces.IHttpEngine;
import com.klfront.httputils.interfaces.OnDownloadListener;
import com.klfront.httputils.interfaces.ResultCallback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by kl on 2018/4/11.
 */

public class OkHttpEngine implements IHttpEngine {
    private static OkHttpEngine mInstance;
    private OkHttpClient mOkHttpClient;
    private Handler mHandler;

    public static OkHttpEngine getInstance(Context context) {
        if (mInstance == null) {
            synchronized (OkHttpEngine.class) {
                if (mInstance == null) {
                    mInstance = new OkHttpEngine(context);
                }
            }
        }
        return mInstance;
    }

    private OkHttpEngine(Context context) {
        File sdcard = context.getExternalCacheDir();
        int cacheSize = 10 * 1024 * 1024;
//        ClearableCookieJar cookieJar = new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(mContext));
//        Https.SSLParams sslParams = Https.getSslSocketFactory(null, null, null);
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)//连接超时(单位:秒)
                .writeTimeout(20, TimeUnit.SECONDS)//写入超时(单位:秒)
                .readTimeout(20, TimeUnit.SECONDS)//读取超时(单位:秒)
                .cache(new Cache(sdcard.getAbsoluteFile(), cacheSize))
//                .cookieJar(cookieJar)//Cookies持久化
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                });
        mOkHttpClient = builder.build();
        mHandler = new Handler();
    }

    @Override
    public void getAsyn(String url, ResultCallback callback) {
        Request request = new Request.Builder().url(url).build();
        Call call = mOkHttpClient.newCall(request);
        dealResult(call, callback);
    }

    @Override
    public void getAsyn(String url, Map<String, String> params, ResultCallback callback) {
        url = getUrl(url, params);
        getAsyn(url, callback);
    }

    @Override
    public void getAsyn(String url, Map<String, String> params, Map<String, String> headerParams, ResultCallback callback) {
        url = getUrl(url, params);
        Headers headers = setHeaders(headerParams);
        Request request = new Request.Builder()
                .url(url)
                .headers(headers)
                .build();
        Call call = mOkHttpClient.newCall(request);
        dealResult(call, callback);
    }


    /**
     * @param url      请求地址
     * @param params   请求参数
     * @param callback 请求回调
     * @Description POST请求
     */
    @Override
    public void postAsyn(String url, Map<String, String> params, ResultCallback callback) {
        postAsyn(url, params, null, callback);
    }

    /**
     * @param url          请求地址
     * @param params       请求参数
     * @param headerParams 请求头参数
     * @param callback     请求回调
     * @Description POST请求
     */
    @Override
    public void postAsyn(String url, Map<String, String> params, Map<String, String> headerParams, ResultCallback callback) {
        Headers headers = setHeaders(headerParams);
        FormBody.Builder builder = new FormBody.Builder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            builder.add(entry.getKey(), entry.getValue());
        }
        RequestBody requestBodyPost = builder.build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBodyPost)
                .headers(headers)
                .build();
        Call call = mOkHttpClient.newCall(request);
        dealResult(call, callback);
    }

    @Override
    public void downloadFileAsyn(String url, String saveFilePath,Map<String, String> headerParams, OnDownloadListener listener) {
        Headers headers = setHeaders(headerParams);
        Request request = new Request.Builder().url(url).headers(headers).build();
        Call call = mOkHttpClient.newCall(request);
        dealFileResult(call, saveFilePath, listener);
    }

    @NonNull
    private String getUrl(String url, Map<String, String> params) {
        if (params == null || params.size() == 0) {
            return url;
        }

        StringBuilder urlBuilder;
        if (url.endsWith("?")) {
            url = url.substring(0, url.length() - 1);
        }
        if (url.contains("?")) {
            urlBuilder = new StringBuilder(url).append("?");
        } else {
            urlBuilder = new StringBuilder(url).append("&");
        }
        if (params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                urlBuilder
                        .append(entry.getKey())
                        .append("=")
                        .append(entry.getValue())
                        .append("&");
            }
        }
        return url;
    }

    /**
     * 设置请求头
     *
     * @param headersParams
     * @return
     */
    private Headers setHeaders(Map<String, String> headersParams) {
        Headers headers = null;
        okhttp3.Headers.Builder headersbuilder = new okhttp3.Headers.Builder();

        if (headersParams != null) {
            Iterator<String> iterator = headersParams.keySet().iterator();
            String key = "";
            while (iterator.hasNext()) {
                key = iterator.next().toString();
                headersbuilder.add(key, headersParams.get(key));
                Log.d("get http", "get_headers===" + key + "====" + headersParams.get(key));
            }
        }
        headers = headersbuilder.build();
        return headers;
    }

    private void dealResult(final Call call, final ResultCallback callback) {
        call.enqueue(new Callback() {
            @Override
            public void onFailure(final Call call, final IOException e) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onError(call.request().url().toString(), e);
                    }
                });
            }

            @Override
            public void onResponse(final Call call, Response response) {
                try {
                    final String result = response.body().string();
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (callback != null) {
                                callback.onResponse(result, null);
                            }
                        }
                    });
                } catch (final IOException e) {
                    e.printStackTrace();
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (callback != null) {
                                callback.onResponse("", e);
                            }
                        }
                    });
                }
            }
        });
    }

    private void dealFileResult(final Call call, final String saveFilePath, final OnDownloadListener listener) {
        call.enqueue(new Callback() {
            @Override
            public void onFailure(final Call call, final IOException e) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (listener != null) {
                            listener.onDownloadFailed(call.request().url().toString(), e);
                        }
                    }
                });
            }

            @Override
            public void onResponse(final Call call, Response response) {
                try {
                    InputStream is = null;//输入流
                    FileOutputStream fos = null;//输出流
                    is = response.body().byteStream();//获取输入流
                    long total = response.body().contentLength();//获取文件大小
                    if (is != null) {
                        Log.d("SettingPresenter", "onResponse: 不为空");
                        File file = new File(saveFilePath);// 设置路径
                        fos = new FileOutputStream(file);
                        byte[] buf = new byte[1024];
                        int ch = -1;
                        int progress = 0;
                        while ((ch = is.read(buf)) != -1) {
                            fos.write(buf, 0, ch);
                            progress += ch;
                            if (listener != null) {
                                listener.onProgressUpdate(progress);
                            }
                        }
                    }
                    fos.flush();
                    // 下载完成
                    if (fos != null) {
                        fos.close();
                    }
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (listener != null) {
                                listener.onDownloadSuccess(saveFilePath);
                            }
                        }
                    });
                } catch (final IOException e) {
                    e.printStackTrace();
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (listener != null) {
                                listener.onDownloadFailed(call.request().url().toString(), e);
                            }
                        }
                    });
                }
            }
        });
    }
}
