package com.tungnui.abccomputer.activity

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient

import com.tungnui.abccomputer.data.constant.AppConstants
import com.tungnui.abccomputer.R
import kotlinx.android.synthetic.main.activity_webpage.*

class WebPageActivity : BaseActivity() {
    private var pageTitle = AppConstants.EMPTY_STRING
    private var url = AppConstants.EMPTY_STRING

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initVariable()
        initView()
        initToolbar()
        initFunctionality()
    }

    private fun initVariable() {
        val intent = intent
        if (intent.hasExtra(AppConstants.BUNDLE_KEY_TITLE)) {
            pageTitle = intent.getStringExtra(AppConstants.BUNDLE_KEY_TITLE)
        }
        if (intent.hasExtra(AppConstants.BUNDLE_KEY_URL)) {
            url = intent.getStringExtra(AppConstants.BUNDLE_KEY_URL)
        }
    }

    private fun initView() {
        setContentView(R.layout.activity_webpage)
        initToolbar()
        enableBackButton()
        setToolbarTitle(pageTitle)
        initLoader()
    }


    private fun initFunctionality() {

        val webSetting = webView.settings
        webSetting.javaScriptEnabled = true
        webView!!.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(webView: WebView, webUrl: String): Boolean {

                webView.loadUrl(webUrl)
                return true
            }

            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap) {
                showLoader()
            }

            override fun onPageFinished(view: WebView, url: String) {
                hideLoader()
            }

        }

        // load web view
        webView!!.loadUrl(url)
    }

}
