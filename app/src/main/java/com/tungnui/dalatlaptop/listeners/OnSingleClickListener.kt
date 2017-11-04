package com.tungnui.dalatlaptop.listeners

import android.os.SystemClock
import android.view.View


abstract class OnSingleClickListener : View.OnClickListener {

    private var mLastClickTime: Long = 0
    abstract fun onSingleClick(view: View)

    override fun onClick(v: View) {
        if (SystemClock.uptimeMillis() - mLastClickTime < MIN_CLICK_INTERVAL)
            return
        mLastClickTime = SystemClock.uptimeMillis()

        onSingleClick(v)
    }

    companion object {
        private val MIN_CLICK_INTERVAL: Long = 800
    }

}
