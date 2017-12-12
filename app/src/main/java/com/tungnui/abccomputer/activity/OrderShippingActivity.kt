package com.tungnui.abccomputer.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast

import com.tungnui.abccomputer.data.constant.AppConstants
import com.tungnui.abccomputer.R
import com.tungnui.abccomputer.SettingsMy
import com.tungnui.abccomputer.utils.ActivityUtils
import kotlinx.android.synthetic.main.activity_order_shipping.*

class OrderShippingActivity : BaseActivity() {
    private var shippingMethod:String = "Nhận hàng tại cửa hàng"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        initToolbar()
        initListener()
        loadData()
    }

    private fun loadData(){
        var customer = SettingsMy.getActiveUser()
        customer?.let {
            tvName.text = customer.firstName
            tvAddress.text = customer.billing?.address1
            tvDistrict.text = customer.billing?.city
            tvProvince.text = customer.billing?.state
        }
    }

    private fun initView() {
        setContentView(R.layout.activity_order_shipping)
        initToolbar()
        setToolbarTitle(getString(R.string.shipping_method))
        enableBackButton()
    }


    private fun initListener() {
       rgShippingMethod.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rdOffice -> {
                    shippingMethod =getString(R.string.title_shipping_office)
                }
                R.id.rdHome -> {
                    shippingMethod = getString(R.string.title_shipping_home)
                }
            }
        }
        btnNextToPay.setOnClickListener {
            ActivityUtils.instance.invokeOrderPayment(this@OrderShippingActivity,shippingMethod)
        }
        btnUpdateAddress.setOnClickListener{
            val intent = Intent(this@OrderShippingActivity, MyAddressActivity::class.java)
            startActivityForResult(intent,AppConstants.REQUEST_UPDATE_ADDRESS_CODE)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode ==AppConstants.REQUEST_UPDATE_ADDRESS_CODE){
            if(resultCode == Activity.RESULT_OK){
                loadData()
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
