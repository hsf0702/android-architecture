package com.klfront.control.VirtualResource;

import android.text.TextUtils;
import android.util.Log;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by dell on 2017/9/15.
 */

public class VirtualResourceMgr {
    private static List<VirtualDomain> FVirtualDomains = new ArrayList();
    private static String FLocalDirPath;

    public static void InitRemoteResource(String localDirPath) {
        FLocalDirPath = localDirPath;
    }

    public static void setVirtualDomains(String jsonData) {
        try {
            FVirtualDomains.clear();

            JSONArray array = new JSONArray(jsonData);
            for (int i= 0;i<array.length();i++)
            {
                VirtualDomain domain = new VirtualDomain();
                JSONObject obj =  array.getJSONObject(i);
                domain.Name = obj.getString("Name");

                JSONArray regex =  obj.getJSONArray("Regex");
                for(int k= 0 ;k<regex.length();k++)
                {
                   domain.Regex.add(regex.getString(k));
                }

                JSONArray items = obj.getJSONArray("Items");
                for(int m= 0 ;m<items.length();m++)
                {
                    VirtualResource resource = new VirtualResource();
                    JSONObject item =  items.getJSONObject(m);
                    resource.res = item.getString("res");
                    resource.version = item.getString("version");
                    domain.Items.add(resource);
                }

                FVirtualDomains.add(domain);
            }

        }catch (Exception e)
        {
            Log.e("setVirtualDomains",e.getMessage());
        }
    }

    //获取虚拟Url 本地的以file://开头
    public static String GetVirtualUrl(final String rawUrl) {
        if (IsInVirtualDomain(rawUrl)) {
            String virtual = GetLocalUrl(rawUrl);
            if (!TextUtils.isEmpty(virtual)) {
                //替换为本地路径
                return virtual;
            } else {
                //使用原url，并从服务端下载下次使用
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        DownloadResouceFile(rawUrl);
                    }
                }).start();
                return rawUrl;
            }
        } else {
            //否则使用原url
            return rawUrl;
        }
    }

    public static boolean IsInVirtualDomain(String rawUrl) {
        for (VirtualDomain domain : FVirtualDomains) {
            for (String domainUrl : domain.Regex) {
                if (rawUrl.startsWith(domainUrl)) {
                    return true;
                }
            }
        }
        return false;
    }

    //比较时，获取本地版本号
    public static String getLocalResVersion(final String localUrl) {
        String localVersionUrl = localUrl + ".version";
        File file = new File(localVersionUrl);
        String version = "0";
        if (file.exists()) {
            try {
                InputStream instream = new FileInputStream(file);
                if (instream != null) {
                    InputStreamReader inputreader = new InputStreamReader(instream);
                    BufferedReader buffreader = new BufferedReader(inputreader);
                    version = buffreader.readLine(); //分行读取
                    instream.close();
                }
            } catch (Exception e) {
                Log.e("getLocalResVersion",e.getMessage());
            }
        }
        return version==null?"0":version;
    }

    //下载保存时 获取版本号保存
    public static String getRemoteResVersion(final String rawUrl) {
        for (VirtualDomain domain : FVirtualDomains) {
            for (String domainUrl : domain.Regex) {
                if (rawUrl.startsWith(domainUrl)) {
                    for (VirtualResource res : domain.Items) {
                        if (rawUrl.equals(domainUrl +res.res)) {
                            return res.version;
                        }
                    }
                }
            }
        }
        return "";
    }

    public static String getRemoteVirtualDomainName(String rawUrl)
    {
        for (VirtualDomain domain : FVirtualDomains) {
            for (String domainUrl : domain.Regex) {
                if (rawUrl.startsWith(domainUrl)) {
                   return domain.Name;
                }
            }
        }
        return "";
    }

    public static List<String> getRemoteVirtualDomainRegex(String rawUrl)
    {
        List<String> list = new ArrayList<>();
        for (VirtualDomain domain : FVirtualDomains) {
            for (String domainUrl : domain.Regex) {
                list.add(domainUrl);
            }
        }
        return list;
    }

    /**
     * 获取本地Url
     *
     * @param rawUrl
     * @return 本地有，版本与远程的一致 或远程没提供版本，则返回本地；
     *         本地没有或者与远程版本不一致 要下载（下载）
     */
    public static String GetLocalUrl(String rawUrl) {
        String res="";
        String localUrl = replaceUrl(rawUrl);
        File file = new File(localUrl);
        if (file.exists()) {
            String localversion = getLocalResVersion(localUrl);
            String remoteVersion = getRemoteResVersion(rawUrl);
            if (remoteVersion == ""||localversion.equals(remoteVersion)) {
                res = "[static]" + file.getAbsolutePath();
            }
        }
        return res;
    }

    private static String replaceUrl(String rawUrl) {
        String localFolderPath = FLocalDirPath+ "/"+ getRemoteVirtualDomainName(rawUrl)+"/";
        List<String> listDomainUrl = getRemoteVirtualDomainRegex(rawUrl);
        String localUrl =rawUrl;
        for(String domainUrl:listDomainUrl)
        {
            localUrl = localUrl.replace(domainUrl, localFolderPath);
        }
        return localUrl;
    }

    public static void DownloadResouceFile(final String rawUrl) {
        String localUrl = replaceUrl(rawUrl);
        final String resVersionPath = localUrl + ".version";
        File file = new File(localUrl);
        if (file.exists()) {
            file.delete();
        }

        DownloadFile(rawUrl, localUrl, new IQhAPICallEvent<Boolean>() {
            @Override
            public void OnGetResult(Boolean issuccess) {
                //下载完 写入版本号
                if(rawUrl.endsWith("vue-min.js"))
                {
                    Log.d("GetVirtualUrl","获取版本");
                }
                String resVersion = getRemoteResVersion(rawUrl);
                if(resVersion=="")
                {
                    resVersion ="0";
                }

                File versionFile = new File(resVersionPath);
                versionFile.getParentFile().mkdirs();
                if (versionFile.exists()) {
                    versionFile.delete();
                }
                try {
                    versionFile.createNewFile();
                    FileOutputStream out = new FileOutputStream(versionFile); // 输出文件路径
                    out.write(resVersion.getBytes());
                    out.close();
                } catch (IOException e) {
                    Log.e("writeFile", e.getMessage());
                }
            }

            @Override
            public void OnGetError(Exception err) {
                Log.e("download", err==null?"":err.getMessage());
            }
        });
    }


    /**
     * Http请求下载文件
     *
     * @param url
     * @param savefile
     * @param callback
     */

    public static void DownloadFile(String url, String savefile, final IQhAPICallEvent callback) {
//        TTommHttpDownloader downloader = new TTommHttpDownloader();
//        downloader.AddTommEventListener(new ITommEvent() {
//            @Override
//            public String GetEventArgType() {
//                return TTommHttpDownloader.TOnDownloadFileComplete.class.getSimpleName();
//            }
//
//            @Override
//            public void OnGetEvent(Object sender, EventObject e) {
//                TTommHttpDownloader.TOnDownloadFileComplete arg = (TTommHttpDownloader.TOnDownloadFileComplete) e;
//                boolean isSucess = arg.IsSucess();
//                if (isSucess) {
//                    callback.OnGetResult(isSucess);
//                } else {
//                    callback.OnGetError(arg.GetError()==null?new Exception("下载失败"):arg.GetError());
//                }
//            }
//        });
//        Map<String, String> headers = new HashMap<>();
//        headers.put("User-Agent", System.getProperty("http.agent"));
//        headers.put("Accept-Language", Locale.getDefault().toString());
//        downloader.DownloadFileAsync(url, savefile, null, headers);
    }


    public static void ClearCache()
    {
        deleteDir(FLocalDirPath);
    }

    //删除文件夹和文件夹里面的文件
    public static void deleteDir(final String dirPath) {
        if(TextUtils.isEmpty(dirPath))
        {
            return;
        }

        File dir = new File(dirPath);
        if(dir.exists()) {
            deleteDirWithFile(dir);
        }
    }

    public static void deleteDirWithFile(File dir) {
        if (dir == null || !dir.exists() || !dir.isDirectory())
            return;
        for (File file : dir.listFiles()) {
            if (file.isFile())
                file.delete(); // 删除所有文件
            else if (file.isDirectory())
                deleteDirWithFile(file); // 递规的方式删除文件夹
        }
        dir.delete();// 删除目录本身
    }

    public interface IQhAPICallEvent<T> {
        void OnGetResult(T obj);

        void OnGetError(Exception err);
    }
}
