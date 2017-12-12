package com.tungnui.abccomputer.activity

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.TextView

import com.tungnui.abccomputer.data.constant.AppConstants
import com.tungnui.abccomputer.R

class NotificationContentActivity : AppCompatActivity() {
    private var mToolbar: Toolbar? = null
    private var titleView: TextView? = null
    private var messageView: TextView? = null
    private var title: String? = null
    private var message: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        initVeritable()
        initFunctionality()
        initListeners()
    }

    private fun initVeritable() {
        val extras = intent.extras
        title = extras!!.getString(AppConstants.BUNDLE_KEY_TITLE)
        message = extras.getString(AppConstants.BUNDLE_KEY_MESSAGE)
    }

    private fun initView() {
        setContentView(R.layout.activity_notification_details)
        mToolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(mToolbar)
        supportActionBar?.setTitle(getString(R.string.notification_details))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        titleView = findViewById<TextView>(R.id.title)
        messageView = findViewById<TextView>(R.id.message)


    }


    private fun initFunctionality() {
        titleView?.text = title
        messageView?.text = message
    }

    private fun initListeners() {

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }
}
