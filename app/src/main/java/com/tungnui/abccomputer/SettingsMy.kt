package com.tungnui.abccomputer

import android.content.Context
import android.content.SharedPreferences
import com.tungnui.abccomputer.app.MyApplication
import com.tungnui.abccomputer.models.Customer
import com.tungnui.abccomputer.utils.Utils

object SettingsMy {
    val PREF_ACTIVE_USER = "pref_active_user"
    val PREF_USER_EMAIL = "pref_user_email"
    val REGISTRATION_COMPLETE = "registrationComplete"
    private val TAG = SettingsMy::class.java.simpleName
    private var activeUser: Customer? = null
    private var sharedPref: SharedPreferences? = null

    var userEmailHint: String
        get() {
            val prefs = settings
            val userEmail = prefs.getString(PREF_USER_EMAIL, "")
            return userEmail
        }
        set(userEmail) {
            putParam(PREF_USER_EMAIL, userEmail)
        }

      val settings: SharedPreferences
        get() {
            if (sharedPref == null) {
                sharedPref = MyApplication.instance?.getSharedPreferences(MyApplication.PACKAGE_NAME, Context.MODE_PRIVATE)
            }
            return sharedPref!!
        }
    fun isUserLogin():Boolean=if(getActiveUser()== null)false else true
    fun getActiveUser(): Customer? {
        if (activeUser != null) {
            return activeUser
        } else {
            val prefs = settings
            val json = prefs.getString(PREF_ACTIVE_USER, "")
            if (json.isEmpty() || "null" == json) {
                return null
            } else {
                activeUser = Utils.getGsonParser().fromJson(json, Customer::class.java)
                return activeUser
            }
        }
    }


    fun setActiveUser(customer: Customer?) {
        SettingsMy.activeUser = customer
        val json = Utils.getGsonParser().toJson(SettingsMy.activeUser)
        val editor = settings.edit()
        editor.putString(PREF_ACTIVE_USER, json)
        editor.apply()
    }

    private fun putParam(key: String, value: String): Boolean {
        val editor = settings.edit()
        editor.putString(key, value)
        return editor.commit()
    }
}
