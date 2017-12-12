package com.tungnui.abccomputer.activity

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.MenuItem

import com.tungnui.abccomputer.data.constant.AppConstants
import com.tungnui.abccomputer.R
import com.tungnui.abccomputer.utils.ActivityUtils
import com.tungnui.abccomputer.utils.AdUtils
import kotlinx.android.synthetic.main.activity_order_success.*

class OrderSuccessActivity : BaseActivity() {
    private var mContext: Context?= null
    private var mActivity: Activity? = null
    private var orderId:String=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initVariable()
        initView()
        loadData()
        initToolbar()
        initListener()
    }

    private fun initVariable() {
        mContext = applicationContext
        mActivity = this@OrderSuccessActivity
    }

    private fun initView() {
        setContentView(R.layout.activity_order_success)

        initToolbar()
        enableBackButton()
        enableBackButton()
        setToolbarTitle(getString(R.string.order_successful))
    }


    private fun loadData() {

        val intent = intent
        if (intent.hasExtra(AppConstants.ORDER_ID)) {
            orderId = intent.getStringExtra(AppConstants.ORDER_ID)
        }

        tvOrderId.text = "Mã đơn hàng: " + orderId
    }

    private fun initListener() {
        btnClose.setOnClickListener {
            ActivityUtils.instance.invokeMainActivity(this@OrderSuccessActivity)
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                ActivityUtils.instance.invokeMainActivity(this@OrderSuccessActivity)
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }
}
