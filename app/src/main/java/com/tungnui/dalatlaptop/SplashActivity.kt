package com.tungnui.dalatlaptop

import android.os.Bundle
import android.os.Handler
import android.support.v4.app.FragmentActivity
import kotlinx.android.synthetic.main.activity_splash.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.content.Intent
import android.view.animation.Animation.AnimationListener


/**
 * Created by thanh on 24/09/2017.
 */
class SplashActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Handler().postDelayed({ endSplash() }, 3000)
    }

    private fun endSplash() {
       var animation = AnimationUtils.loadAnimation(this,R.anim.logo_animation_back)
        logo_img.startAnimation(animation)
        animation.setAnimationListener(object : AnimationListener {
            override fun onAnimationEnd(arg0: Animation) {
                val intent = Intent(applicationContext,HomeActivity::class.java)
                startActivity(intent)
                finish()
            }
            override fun onAnimationRepeat(arg0: Animation) {}
            override fun onAnimationStart(arg0: Animation) {}
        })
    }
}