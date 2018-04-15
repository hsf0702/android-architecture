package com.klfront.httputils.interfaces;

import java.util.Map;

public interface IHttpEngine {
    public void getAsyn(String url, ResultCallback callback);
    public void getAsyn(String url, Map<String, String> params, ResultCallback callback);
    public void getAsyn(String url, Map<String, String> params, Map<String, String> headers, ResultCallback callback);

    public void postAsyn(String url, Map<String, String> params, ResultCallback callback);
    public void postAsyn(String url, Map<String, String> params, Map<String, String> headers, ResultCallback callback);

    public void downloadFileAsyn(String url,String saveFilePath,Map<String, String> headers,OnDownloadListener listener);
}
