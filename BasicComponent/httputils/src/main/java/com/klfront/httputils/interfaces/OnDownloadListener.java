package com.klfront.httputils.interfaces;

public interface OnDownloadListener {
    /**
     * 下载成功
     */
    void onDownloadSuccess(String path);
    /**
     * 下载进度
     * @param progress
     */
    void onProgressUpdate(int progress);
    /**
     * 下载失败
     */
    void onDownloadFailed(String url,Exception e);
}
