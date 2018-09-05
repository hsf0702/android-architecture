package com.klfront.baseui.activity

import android.os.Bundle

/**
 */
abstract class AppActivity : BaseActivity()
{
    protected var appId = ""
    protected var appServerUrl = ""
    protected var apiKey = ""
    protected var appName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        apiKey = intent.getStringExtra("apikey") ?: ""
        appId = intent.getStringExtra("appId") ?: ""
        appName = intent.getStringExtra("appName") ?: ""
        appServerUrl = intent.getStringExtra("url") ?: ""
    }
}