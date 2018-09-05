package com.klfront.httputils.interfaces;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by kl on 2018/4/11.
 */

public interface ResultCallback {
    void onError(String requestUrl,Exception e);
    void onResponse(String responseBody,Exception e);
}
