package com.tungnui.dalatlaptop

import android.app.Application
import android.content.Context
import android.content.res.Resources
import android.net.ConnectivityManager
import android.support.multidex.MultiDexApplication
import com.android.volley.RequestQueue
import com.facebook.FacebookSdk


class MyApplication : MultiDexApplication() {

    private var mRequestQueue: RequestQueue? = null

    //Kiểm tra kết nối mang
    val isDataConnected: Boolean
        get() {
            val connectMan = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetworkInfo = connectMan.activeNetworkInfo
            return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting
        }

    val isWiFiConnection: Boolean
        get() {
            val connectMan = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetworkInfo = connectMan.activeNetworkInfo
            return activeNetworkInfo != null && activeNetworkInfo.type == ConnectivityManager.TYPE_WIFI
        }


    override fun onCreate() {
        super.onCreate()
        instance = this
        FacebookSdk.sdkInitialize(this)
    }

    companion object {
        val PACKAGE_NAME = MyApplication::class.java.`package`.name
        var APP_VERSION = "0.0.0"
        var ANDROID_ID = "0000000000000000"
        @get:Synchronized
        var instance: MyApplication? = null
            private set
    }
}
