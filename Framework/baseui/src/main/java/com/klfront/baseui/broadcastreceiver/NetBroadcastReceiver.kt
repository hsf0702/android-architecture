package com.klfront.baseui.broadcastreceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager

import com.klfront.baseui.activity.BaseActivity


class NetBroadcastReceiver : BroadcastReceiver() {

    var event = BaseActivity.NETWORK_LISTENER

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ConnectivityManager.CONNECTIVITY_ACTION) {
            val netWorkState = getNetWorkState(context)
            // 接口回调传过去状态的类型
            event?.onNetWorkChanged(netWorkState)
        }
    }

    // 自定义接口
    interface NetworkListener {
        fun onNetWorkChanged(status: Int)
    }

    companion object {
        /**
         * 没有连接网络
         */
        private val NETWORK_NONE = -1
        /**
         * 移动网络
         */
        private val NETWORK_MOBILE = 0
        /**
         * 无线网络
         */
        private val NETWORK_WIFI = 1

        fun getNetWorkState(context: Context): Int {
            // 得到连接管理器对象
            val connectivityManager = context
                    .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                if (activeNetworkInfo.type == ConnectivityManager.TYPE_WIFI) {
                    return NETWORK_WIFI
                } else if (activeNetworkInfo.type == ConnectivityManager.TYPE_MOBILE) {
                    return NETWORK_MOBILE
                }
            } else {
                return NETWORK_NONE
            }
            return NETWORK_NONE
        }
    }
}