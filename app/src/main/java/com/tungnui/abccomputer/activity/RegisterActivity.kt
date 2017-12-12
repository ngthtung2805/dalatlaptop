package com.tungnui.abccomputer.activity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.tungnui.abccomputer.R
import com.tungnui.abccomputer.SettingsMy
import com.tungnui.abccomputer.api.CustomerServices
import com.tungnui.abccomputer.api.ServiceGenerator
import com.tungnui.abccomputer.data.constant.AppConstants
import com.tungnui.abccomputer.listener.LoginListener
import com.tungnui.abccomputer.model.LineItem
import com.tungnui.abccomputer.models.Customer
import com.tungnui.abccomputer.utils.ActivityUtils
import com.tungnui.abccomputer.utils.AnalyticsUtils
import com.tungnui.abccomputer.utils.AppUtility
import com.tungnui.abccomputer.utils.DialogUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_register.*
import java.util.ArrayList

class RegisterActivity : BaseLoginActivity() {
    private var shouldOrder = false
    private var lineItems: ArrayList<LineItem>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initVar()
        initView()
        initListener()
    }

    private fun initVar() {
        lineItems = ArrayList()
        val intent = intent
        if (intent.hasExtra(AppConstants.KEY_LOGIN_ORDER)) {
            shouldOrder = intent.getBooleanExtra(AppConstants.KEY_LOGIN_ORDER, false)
        }
        if (intent.hasExtra(AppConstants.NAME)) {
            edtName.setText(intent.getStringExtra(AppConstants.NAME))
        }
        if (intent.hasExtra(AppConstants.EMAIL)) {
            edtEmail.setText(intent.getStringExtra(AppConstants.EMAIL))
        }
        //nalytics event trigger
        AnalyticsUtils.getAnalyticsUtils(applicationContext).trackEvent("Register Activity")
    }

    private fun initView() {
        setContentView(R.layout.activity_register)
        AppUtility.noInternetWarning(tvSkipRegister, applicationContext)
    }


    private fun initListener() {
        tvSkipRegister?.setOnClickListener {
            ActivityUtils.instance.invokeActivity(this@RegisterActivity, MainActivity::class.java, true)
            // analytics event trigger
            AnalyticsUtils.getAnalyticsUtils(applicationContext).trackEvent("Skipped login process")
        }
        btnBackToLogin.setOnClickListener { finish() }

        btnRegister.setOnClickListener { invokeRegister()}
        setLoginListener(object : LoginListener {
            override fun onRegisterResponse(customer: Customer) {
                handleRegister(customer)
            }

            override fun onLoginResponse(id: Int) {

            }

            override fun onLoginResponse(customer: Customer) {

            }


        })
    }
    private val isValidateInput: Boolean
        get() = if (!edtEmail?.text.toString().isEmpty() && !edtPassword?.text.toString().isEmpty() && !edtName?.text.toString().isEmpty()) {
            true
        } else
            false

    fun invokeRegister() {
        if(isValidateInput){
            var customer = Customer(firstName = edtName.text.toString(),email = edtEmail.text.toString(), password = edtPassword.text.toString())
             handleRegister(customer)
        }
        else{
            Toast.makeText(applicationContext, "Tên đăng nhập hoặc mật khẩu không được bỏ trống", Toast.LENGTH_SHORT).show()
        }
    }


    private fun handleRegister(customer: Customer) {
        val customerService = ServiceGenerator.createService(CustomerServices::class.java)
        var progressDialog = DialogUtils.showProgressDialog(this@RegisterActivity, getString(R.string.msg_loading), false)
        val disposable = customerService.create(customer)
                .subscribeOn((Schedulers.io()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ customer ->
                    SettingsMy.setActiveUser(customer)
                    whatNext()

                },
                        { _ ->
                            DialogUtils.dismissProgressDialog(progressDialog)
                        })
    }

     private fun whatNext() {
        if (shouldOrder) {
            ActivityUtils.instance.invokeOrderShipping(this@RegisterActivity)
        } else {
            ActivityUtils.instance.invokeActivity(this@RegisterActivity, MainActivity::class.java, true)
        }
    }

}