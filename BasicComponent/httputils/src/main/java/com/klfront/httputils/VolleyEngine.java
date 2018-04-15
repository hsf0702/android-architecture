package com.klfront.httputils;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.klfront.httputils.interfaces.IHttpEngine;
import com.klfront.httputils.interfaces.OnDownloadListener;
import com.klfront.httputils.interfaces.ResultCallback;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class VolleyEngine implements IHttpEngine {

    private static VolleyEngine mInstance;
    private RequestQueue mQueue = null;
    private Handler mHandler;

    public static VolleyEngine getInstance(Context context) {
        if (mInstance == null) {
            synchronized (OkHttpEngine.class) {
                if (mInstance == null) {
                    mInstance = new VolleyEngine(context);
                }
            }
        }
        return mInstance;
    }

    private VolleyEngine(Context context) {
        mQueue = Volley.newRequestQueue(context.getApplicationContext());
        mHandler = new Handler();
    }

    @Override
    public void getAsyn(String url, final ResultCallback callback) {
        StringRequest request = getStringRequest(url, callback);
        mQueue.add(request);
    }

    @Override
    public void getAsyn(String url, Map<String, String> params, ResultCallback callback) {
        url = getUrl(url, params);
        getAsyn(url, callback);
    }

    @Override
    public void getAsyn(String url, Map<String, String> params, Map<String, String> headers, ResultCallback callback) {
        url = getUrl(url, params);
        StringRequest request = getStringRequest(url, headers, callback);
        mQueue.add(request);
    }

    @Override
    public void postAsyn(String url, Map<String, String> params, final ResultCallback callback) {
        JsonObjectRequest request = getJsonObjectRequest(url, params, callback);
        mQueue.add(request);
    }

    @Override
    public void postAsyn(String url, Map<String, String> params, Map<String, String> headers, final ResultCallback callback) {
        JsonObjectRequest request = getJsonObjectRequest(url, params, headers, callback);
        mQueue.add(request);
    }

    @Override
    public void downloadFileAsyn(final String url, final String saveFilePath, Map<String, String> headers, final OnDownloadListener listener) {
        try {
            URL web = new URL(url);
            HttpURLConnection conn = null;
            conn = (HttpURLConnection) web.openConnection();
            if (headers != null) {
                Iterator<Map.Entry<String, String>> it = headers.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<String, String> entry = (Map.Entry<String, String>) it.next();
                    conn.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }

            File SaveFile = new File(saveFilePath);
            if (SaveFile.exists()) {
                SaveFile.delete();
            }
            SaveFile.getParentFile().mkdirs();
            SaveFile.createNewFile();

            FileOutputStream fos = new FileOutputStream(saveFilePath, true);
            InputStream is = conn.getInputStream();

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
            conn.disconnect();
            fos.close();
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    listener.onDownloadSuccess(saveFilePath);
                }
            });
        } catch (Exception e) {
            listener.onDownloadFailed(url,e);
        }
    }

    @NonNull
    private String getUrl(String url, Map<String, String> params) {
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

    private StringRequest getStringRequest(final String url, final ResultCallback callback) {
        return getStringRequest(url, new HashMap<String, String>(), callback);
    }

    private StringRequest getStringRequest(final String url, final Map<String, String> headers, final ResultCallback callback) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String response) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                callback.onResponse(response, null);
                            }
                        });
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(final VolleyError error) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onError(url, error);
                    }
                });
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map = super.getHeaders();
                map.putAll(headers);
                return map;
            }
        };
        return stringRequest;
    }


    private JsonObjectRequest getJsonObjectRequest(final String url, final Map<String, String> params, final ResultCallback callback) {
        return getJsonObjectRequest(url,params,null,callback);
    }

    private JsonObjectRequest getJsonObjectRequest(final String url, final Map<String, String> params, final Map<String, String> headers, final ResultCallback callback) {
        JsonObjectRequest req = new JsonObjectRequest(url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(final JSONObject response) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                callback.onResponse(response.toString(), null);
                            }
                        });
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(final VolleyError error) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onResponse(null, error);
                        }
                    });
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                if(headers==null){
                    return super.getHeaders();
                }
                return headers;
            }
        };

        return req;
    }
}
