/*
 * Copyright (c) 2017. http://hiteshsahu.com- All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * If you use or distribute this project then you MUST ADD A COPY OF LICENCE
 * along with the project.
 *  Written by Hitesh Sahu <hiteshkrsahu@Gmail.com>, 2017.
 */

/**
 *
 */
package com.tungnui.dalatlaptop.utils

import android.content.Context
import android.preference.PreferenceManager

/**
 * @author Hitesh
 */
class PreferenceHelper private constructor() {
    fun setBoolean(appContext: Context, key: String, value: Boolean?) {
        PreferenceManager.getDefaultSharedPreferences(appContext).edit()
                .putBoolean(key, value!!).apply()
    }

    fun setInteger(appContext: Context, key: String, value: Int) {

        PreferenceManager.getDefaultSharedPreferences(appContext).edit()
                .putInt(key, value).apply()
    }

    fun setFloat(appContext: Context, key: String, value: Float) {

        PreferenceManager.getDefaultSharedPreferences(appContext).edit()
                .putFloat(key, value).apply()
    }

    fun setString(appContext: Context, key: String, value: String) {

        PreferenceManager.getDefaultSharedPreferences(appContext).edit()
                .putString(key, value).apply()
    }

    // To retrieve values from shared preferences:

    fun getBoolean(appContext: Context, key: String,
                   defaultValue: Boolean?): Boolean {

        return PreferenceManager.getDefaultSharedPreferences(appContext)
                .getBoolean(key, defaultValue!!)
    }

    fun getInteger(appContext: Context, key: String, defaultValue: Int): Int {

        return PreferenceManager.getDefaultSharedPreferences(appContext)
                .getInt(key, defaultValue)
    }

    fun getString(appContext: Context, key: String, defaultValue: Float): Float {

        return PreferenceManager.getDefaultSharedPreferences(appContext)
                .getFloat(key, defaultValue)
    }

    fun getString(appContext: Context, key: String, defaultValue: String): String? {

        return PreferenceManager.getDefaultSharedPreferences(appContext)
                .getString(key, defaultValue)
    }

    fun setUserLoggedIn(UserLoggedIn: Boolean, appContext: Context) {

        setBoolean(appContext, USER_LOGGED_IN, UserLoggedIn)
    }

    fun isUserLoggedIn(appContext: Context): Boolean {

        return getBoolean(appContext, USER_LOGGED_IN, false)
    }

    companion object {
        val FIRST_TIME = "FirstTime"
        val WHATS_NEW_LAST_SHOWN = "whats_new_last_shown"
        val SUBMIT_LOGS = "CrashLogs"
        // Handle Local Caching of data for responsiveness
        val MY_CART_LIST_LOCAL = "MyCartItems"
        private val USER_LOGGED_IN = "isLoggedIn"
        val prefernceHelperInstace = PreferenceHelper()
    }

}
