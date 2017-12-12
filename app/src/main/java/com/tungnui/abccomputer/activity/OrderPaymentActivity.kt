package com.tungnui.abccomputer.activity


import android.os.Bundle

import android.view.MenuItem

import com.tungnui.abccomputer.data.constant.AppConstants
import com.tungnui.abccomputer.R

import com.tungnui.abccomputer.utils.ActivityUtils
import kotlinx.android.synthetic.main.activity_order_payment.*

import java.util.ArrayList


class OrderPaymentActivity : BaseActivity() {
    private var paymentMethod: String = "cod"
    private var shippingMethod: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initVariable()
        initView()
        initToolbar()
        initListener()

    }

    private fun initVariable() {
        val intent = intent
        if (intent.hasExtra(AppConstants.SHIPPING_METHOD)) {
            shippingMethod = intent.getStringExtra(AppConstants.SHIPPING_METHOD)
        }
    }

    private fun initView() {
        setContentView(R.layout.activity_order_payment)
        initToolbar()
        setToolbarTitle(getString(R.string.payment_method))
        enableBackButton()
    }


    private fun initListener() {
        btnPlaceOrder.setOnClickListener {
            ActivityUtils.instance.invokeOrderConfirm(this@OrderPaymentActivity, shippingMethod, paymentMethod)
        }
        rgPaymentMethod.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rdBank -> {
                    paymentMethod = getString(R.string.key_bank)
                }
                R.id.rdCashOnDelivery -> {
                    paymentMethod = getString(R.string.key_cash_on_delivery)
                }
            }
        }
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
