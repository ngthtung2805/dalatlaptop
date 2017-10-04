package com.tungnui.dalatlaptop

import android.app.Application
import com.tungnui.dalatlaptop.utils.PreferenceHelper


import org.acra.ACRA
import org.acra.ReportField
import org.acra.ReportingInteractionMode
import org.acra.annotation.ReportsCrashes

@ReportsCrashes(mailTo = "ngthtung2805@gmail.com", customReportContent = arrayOf(
    ReportField.APP_VERSION_CODE,    ReportField.APP_VERSION_NAME,
    ReportField.ANDROID_VERSION,    ReportField.PHONE_MODEL,
    ReportField.CUSTOM_DATA,     ReportField.STACK_TRACE,
    ReportField.LOGCAT), mode = ReportingInteractionMode.TOAST, resToastText = R.string.crash_toast_text)
class AppController : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this

        // The following line triggers the initialization of ACRA for crash Log Reposrting
        if (PreferenceHelper.prefernceHelperInstace.getBoolean(
                this, PreferenceHelper.SUBMIT_LOGS, true)) {
            ACRA.init(this)
        }
    }

    companion object {
        val TAG = AppController::class.java.simpleName
        @get:Synchronized
        var instance: AppController? = null
            private set
    }

}
