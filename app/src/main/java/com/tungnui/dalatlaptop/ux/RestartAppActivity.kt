package com.tungnui.dalatlaptop.ux

import android.app.Activity
import android.content.Intent

import timber.log.Timber

class RestartAppActivity : Activity() {

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        finish()
    }

    override fun onResume() {
        super.onResume()
        startActivityForResult(Intent(this, SplashActivity::class.java), 0)
    }

    companion object {
        private val TAG = RestartAppActivity::class.java.simpleName
    }
}
