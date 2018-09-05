package com.klfront.appcore.upgrade;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.klfront.appcore.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by L on 2015/12/17.
 */
public class UpgradeMgr {
    private Context mContext;

    /* 下载包保存路径 */
    private static String saveFileName = null;

    private String updateMsg = "是否下载更新?";

    private Dialog downloadDialog;
    /* 进度条与通知ui刷新的handler和msg常量 */
    private ProgressBar mProgress;
    private TextView mTvProgress;

    private static final int DOWN_UPDATE = 1;
    private static final int DOWN_OVER = 2;
    private int progress;

    private Thread downLoadThread;

    private boolean interceptFlag = false;

    private String mVersion = null;
    private String mDesc = null;
    private String mDownloadUrl = null;

    public UpgradeMgr(Context context, String version, String desc, String downloadUrl) {
        this.mContext = context;
        this.mVersion = version;
        this.mDesc = desc;
        this.mDownloadUrl = downloadUrl;
        saveFileName = (mContext.getCacheDir())+ "temp.apk";
    }

    //外部接口让主Activity调用
    public void updateApk() {
        if(TextUtils.isEmpty(mDownloadUrl)){
            return;
        }
        showNoticeDialog();
    }


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DOWN_UPDATE:
                    mProgress.setProgress(progress);
                    mTvProgress.setText(String.valueOf(progress) + "%");
                    break;
                case DOWN_OVER:
                    installApk();
                    break;
                default:
                    break;
            }
        }
    };

    private void showNoticeDialog() {
        //对话框设置
        new AlertDialog.Builder(mContext).setTitle("检测到新版本(V" + mVersion + ")")
                .setMessage(mDesc + "\r\n\r\n" + updateMsg).setPositiveButton("下载", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                showDownloadDialog();
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setCancelable(false).show();
    }

    private void showDownloadDialog() {
        final LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.progress, null);
        mProgress = (ProgressBar) v.findViewById(R.id.progress);
        mTvProgress = (TextView) v.findViewById(R.id.tvProgress);
        mTvProgress.setVisibility(View.VISIBLE);

        downloadDialog = new AlertDialog.Builder(mContext).setTitle("软件版本更新").setView(v).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                interceptFlag = true;
            }
        }).setCancelable(false).create();
        downloadDialog.setCancelable(false);
        downloadDialog.show();
        downloadApk();
    }

    private Runnable mDownApkRunnable = new Runnable() {
        @Override
        public void run() {
            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                try {
                    URL url = new URL(mDownloadUrl);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.connect();
                    int length = conn.getContentLength();
                    String a = String.valueOf(length);
                    InputStream is = conn.getInputStream();


                    File ApkFile = new File(saveFileName);
                    FileOutputStream fos = new FileOutputStream(ApkFile);

                    int count = 0;
                    byte buf[] = new byte[1024];
                    do {
                        int numread = is.read(buf);
                        count += numread;
                        progress = (int) (((float) count / length) * 100);
                        //更新进度
                        mHandler.sendEmptyMessage(DOWN_UPDATE);
                        if (numread <= 0) {
                            //下载完成通知安装
                            mHandler.sendEmptyMessage(DOWN_OVER);
                            downloadDialog.dismiss();
                            break;
                        }
                        fos.write(buf, 0, numread);
                    } while (!interceptFlag);//点击取消就停止下载.

                    fos.close();
                    is.close();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    downloadDialog.dismiss();
                } catch (IOException e) {
                    e.printStackTrace();
                    downloadDialog.dismiss();
                }
            } else {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext, "您没有存储的权限，请检查您的设置", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    };

    /**
     * 下载apk
     */
    private void downloadApk() {
        downLoadThread = new Thread(mDownApkRunnable);
        downLoadThread.start();
    }

    /**
     * 安装apk
     */
    private void installApk() {
        File apkfile = new File(saveFileName);
        if (!apkfile.exists()) {
            return;
        }
        //Uri uri = FileProviderUtils.GetUriForFileByFileProvider(mContext,apkfile);//解析包时出错
        //i.setDataAndType(Uri.fromFile(apkfile), "application/vnd.android.package-archive");//本应用保存的文件不用GetUriForFileByFileProvider(拍照等第三方保存的才这么用)，直接使用Uri.fromFile(apkfile)
        //Uri uri = Uri.fromFile(apkfile);//华为mate8解析包时出错
        //Uri uri = Uri.parse(apkfile.getPath());//华为mate8解析包时出错
        try {
            if (saveFileName != null) {
                if (saveFileName.endsWith(".apk")) {
                    Intent install = new Intent(Intent.ACTION_VIEW);
                    install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    if (Build.VERSION.SDK_INT >= 24) {//判读版本是否在7.0以上
                        //Uri apkUri = FileProvider.getUriForFile(context, "com.dafangya.app.pro.fileprovider", file);//在AndroidManifest中的android:authorities值
                        File file = new File(saveFileName);
                        Uri apkUri = null;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            apkUri = FileProvider.getUriForFile(mContext, "com.klfront.project.fileprovider", file);
                        } else {
                            apkUri = Uri.fromFile(file);
                        }

                        install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//添加这一句表示对目标应用临时授权该Uri所代表的文件
                        install.setDataAndType(apkUri, "application/vnd.android.package-archive");
                    } else {
                        install.setDataAndType(Uri.fromFile(apkfile), "application/vnd.android.package-archive");
                    }
                    mContext.startActivity(install);
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
            }
        } catch (Exception e) {
            Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

}


