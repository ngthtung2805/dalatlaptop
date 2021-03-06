package com.tungnui.dalatlaptop.ux

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.facebook.appevents.AppEventsLogger

import com.tungnui.dalatlaptop.MyApplication
import com.tungnui.dalatlaptop.R
import com.tungnui.dalatlaptop.libraryhelper.Utils
import kotlinx.android.synthetic.main.activity_splash.*
import timber.log.Timber
class SplashActivity : AppCompatActivity() {
    private var activity: Activity? = null
    private lateinit var progressDialog: ProgressDialog
    private var layoutCreated = false
    private var windowDetached = false

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.tag(TAG)
        activity = this
       // init loading dialog
        progressDialog = Utils.generateProgressDialog(this, false)
        init()
    }



    private fun init() {
        // Check if data connected.
        if (MyApplication.instance?.isDataConnected == false) {
            progressDialog.hide()
            initSplashLayout()
            splash_content.visibility = View.VISIBLE
            splash_intro_screen.visibility = View.GONE
            splash_content_no_connection.visibility = View.VISIBLE
        } else {
            progressDialog.hide()
            startMainActivity(null)
        }
    }


    private fun initSplashLayout() {
        if (!layoutCreated) {
            setContentView(R.layout.activity_splash)
            splash_re_run_btn.setOnClickListener {
                progressDialog.show()
                Handler().postDelayed({ init() }, 600)
            }
            layoutCreated=true
        } else {
            Timber.d("%s screen is already created.", this.javaClass.simpleName)
        }
    }

      private fun startMainActivity(bundle: Bundle?) {
        val mainIntent = Intent(this@SplashActivity, MainActivity::class.java)
        startActivity(mainIntent)
        finish()
    }


    override fun onResume() {
        super.onResume()
        AppEventsLogger.activateApp(this)
    }

    override fun onPause() {
        super.onPause()
        AppEventsLogger.deactivateApp(this)
    }

    override fun onStop() {
        progressDialog.cancel()
        splash_intro_screen.visibility = View.GONE
        splash_content.visibility = View.VISIBLE
        super.onStop()
    }

    override fun onAttachedToWindow() {
        windowDetached = false
        super.onAttachedToWindow()
    }

    override fun onDetachedFromWindow() {
        windowDetached = true
        super.onDetachedFromWindow()
    }

    companion object {
        val REFERRER = "referrer"
        private val TAG = SplashActivity::class.java.simpleName
    }
}
