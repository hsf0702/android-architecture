package com.klfront.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by Administrator on 14-4-4.
 */
public class NetUtils {

    public static boolean IsNetworkConnected(Context context) {
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        }
        // 获取NetworkInfo对象
        NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
        if (networkInfo != null && networkInfo.length > 0) {
            for (int i = 0; i < networkInfo.length; i++) {
                // 判断当前网络状态是否为连接状态
                if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }

    //不能在主线程使用 该方法连接超时5秒钟结果就不准确
    public static boolean isHostConnectable(String host, int port) {
        Socket socket = new Socket();
        try {
            socket.connect(new InetSocketAddress(host, port),5000);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public static boolean isHostConnectable(String host, int port,int minseconds) {
        Socket socket = new Socket();
        try {
            socket.connect(new InetSocketAddress(host, port),minseconds);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    //该方法不好用
    //判断ip是否可以连接 timeOut是超时时间
//    public static boolean isHostReachable(String host, int timeOut) {
//        try {
//            return InetAddress.getByName(host).isReachable(timeOut);
//        } catch (UnknownHostException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }
}
