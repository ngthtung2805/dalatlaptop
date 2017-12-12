package com.tungnui.abccomputer.activity

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import com.tungnui.abccomputer.R
import com.tungnui.abccomputer.utils.ActivityUtils
import com.tungnui.abccomputer.utils.AppUtility
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {

    // init variables
    private var mContext: Context? = null
    private var mActivity: Activity? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        initVariables()
        setContentView(R.layout.activity_splash)
    }

    override fun onResume() {
        super.onResume()
        initFunctionality()
    }

    private fun initFunctionality() {
        if (AppUtility.isNetworkAvailable(mContext)) {
            Handler().postDelayed({
                ActivityUtils.instance.invokeActivity(this@SplashActivity, MainActivity::class.java, true) }, SPLASH_DURATION.toLong())
        } else {
            AppUtility.noInternetWarning(splashBody, mContext)
        }
    }

    private fun initVariables() {
        mActivity = this@SplashActivity
        mContext = mActivity?.applicationContext
    }

    companion object {
        private val SPLASH_DURATION = 2500
    }
}