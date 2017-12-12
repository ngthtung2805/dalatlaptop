package com.tungnui.abccomputer.activity

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.TextView

import com.google.android.gms.ads.AdView
import com.tungnui.abccomputer.adapter.NotificationAdapter
import com.tungnui.abccomputer.data.constant.AppConstants
import com.tungnui.abccomputer.data.sqlite.NotificationDBController
import com.tungnui.abccomputer.listener.OnItemClickListener
import com.tungnui.abccomputer.model.NotificationModel
import com.tungnui.abccomputer.R
import com.tungnui.abccomputer.utils.ActivityUtils
import com.tungnui.abccomputer.utils.AdUtils

import java.util.ArrayList

class NotificationActivity : AppCompatActivity() {
    private var mToolbar: Toolbar? = null
    private var recyclerView: RecyclerView? = null
    private var mAdapter: NotificationAdapter? = null
    private var dataList: ArrayList<NotificationModel>? = null

    private var emptyView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
       super.onCreate(savedInstanceState)
        initVars()
        initialView()
        initFunctionality()
        initialListener()
    }

    private fun initVars() {
        dataList = ArrayList()
    }

    private fun initialView() {
        setContentView(R.layout.activity_notification)
        mToolbar = findViewById<View>(R.id.toolbar) as Toolbar
        emptyView = findViewById<View>(R.id.emptyView) as TextView
        setSupportActionBar(mToolbar)
        supportActionBar?.title = getString(R.string.notifications)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        //productList
        recyclerView = findViewById<View>(R.id.recycler_view) as RecyclerView
        mAdapter = NotificationAdapter(this@NotificationActivity, dataList)
        recyclerView!!.layoutManager = LinearLayoutManager(this@NotificationActivity)
        recyclerView!!.adapter = mAdapter
    }

    private fun initFunctionality() {
        val notifyController = NotificationDBController(applicationContext)
        notifyController.open()
        dataList?.addAll(notifyController.allNotification)
        notifyController.close()

        if (dataList != null && !dataList!!.isEmpty()) {
            emptyView?.visibility = View.GONE
            mAdapter?.notifyDataSetChanged()
        } else {
            emptyView?.visibility = View.VISIBLE
        }
    }

    private fun initialListener() {
        mToolbar?.setNavigationOnClickListener { finish() }
        mAdapter?.setItemClickListener { view, position ->
            when(dataList!![position].notificationType){
                AppConstants.NOTIFY_TYPE_MESSAGE ->
                        ActivityUtils.instance.invokeNotifyContentActivity(this@NotificationActivity,dataList!![position].title, dataList!![position].message)
            AppConstants.NOTIFY_TYPE_PRODUCT->
                ActivityUtils.instance.invokeProductDetails(this@NotificationActivity, dataList!![position].productId)
                AppConstants.NOTIFY_TYPE_URL ->
                if (dataList!![position].url != null && !dataList!![position].url.isEmpty()) {
                    ActivityUtils.instance.invokeWebPageActivity(this@NotificationActivity, resources.getString(R.string.app_name), dataList!![position].url)
                }
            }
            updateStatus(dataList!![position].id)
        }

    }

    private fun updateStatus(id: Int) {
        val notifyController = NotificationDBController(applicationContext)
        notifyController.open()
        notifyController.updateStatus(id, true)
        notifyController.close()
    }

    override fun onResume() {
        super.onResume()
    }
}
