package com.klfront.baseui.activity

/**
 */
abstract class AppActivity : BaseActivity()
{
    protected var appId = ""
    protected var appServerUrl = ""
    protected var apiKey = ""
    protected var appName = ""

    override fun setupActivity()
    {
        apiKey = intent.getStringExtra("apikey") ?: ""
        appId = intent.getStringExtra("appId") ?: ""
        appName = intent.getStringExtra("appName") ?: ""
        appServerUrl = intent.getStringExtra("url") ?: ""

        super.setupActivity()
    }
}