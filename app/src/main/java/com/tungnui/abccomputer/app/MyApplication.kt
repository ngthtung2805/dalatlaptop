package com.tungnui.abccomputer.app

import android.app.Application
import android.util.Log

import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging


class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
        FirebaseMessaging.getInstance().subscribeToTopic("notification")
        val refreshedToken = FirebaseInstanceId.getInstance().token
        Log.d("==", "Refreshed token: " + refreshedToken)

    }

    companion object {
        val PACKAGE_NAME = MyApplication::class.java.`package`.name
        @get:Synchronized
        var instance: MyApplication? = null
            private set
    }
}
