package com.tungnui.abccomputer.activity

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.tungnui.abccomputer.R
import com.tungnui.abccomputer.SettingsMy
import com.tungnui.abccomputer.api.CustomerServices
import com.tungnui.abccomputer.api.ServiceGenerator
import com.tungnui.abccomputer.data.constant.AppConstants
import com.tungnui.abccomputer.models.Customer
import com.tungnui.abccomputer.model.LineItem
import com.tungnui.abccomputer.listener.LoginListener
import com.tungnui.abccomputer.utils.ActivityUtils
import com.tungnui.abccomputer.utils.AnalyticsUtils
import com.tungnui.abccomputer.utils.AppUtility
import com.tungnui.abccomputer.utils.DialogUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login.*

import java.util.ArrayList

class LoginActivity : BaseLoginActivity() {

    // initialize variables
    private var mContext: Context? = null
    private var mActivity: Activity? = null

    private var shouldOrder = false
    private var lineItems: ArrayList<LineItem>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initVar()
        initView()
        initFbLogin()
        initGoogleLogin()
        setGoogleButtonText(getString(R.string.google_cap))
        initListener()
    }

    private fun initVar() {
        // variables instance
        mContext = applicationContext
        mActivity = this@LoginActivity

        lineItems = ArrayList()

        val intent = intent
        if (intent.hasExtra(AppConstants.KEY_LINE_ITEM_LIST)) {
            lineItems = intent.getParcelableArrayListExtra(AppConstants.KEY_LINE_ITEM_LIST)
        }
        if (intent.hasExtra(AppConstants.KEY_LOGIN_ORDER)) {
            shouldOrder = intent.getBooleanExtra(AppConstants.KEY_LOGIN_ORDER, false)
        }

        // analytics event trigger
        AnalyticsUtils.getAnalyticsUtils(mContext).trackEvent("Login Activity")
    }

    private fun initView() {
        setContentView(R.layout.activity_login)
        AppUtility.noInternetWarning(tvSkipLogin, mContext)
    }


    private fun initListener() {
        btnGoRegister.setOnClickListener{
            ActivityUtils.instance.invokeActivity(this@LoginActivity,RegisterActivity::class.java, false)
        }
        tvSkipLogin?.setOnClickListener {
            ActivityUtils.instance.invokeActivity(this@LoginActivity, MainActivity::class.java, true)
            // analytics event trigger
            AnalyticsUtils.getAnalyticsUtils(mContext).trackEvent("Skipped login process")
        }

        btnLoginEmail.setOnClickListener { invokeEmailLogin() }

        setLoginListener(object : LoginListener {
            override fun onRegisterResponse(customer: Customer) {

            }

            override fun onLoginResponse(id: Int) {
                handleLogin(id)
            }

            override fun onLoginResponse(customer: Customer) {
                handleLogin(customer)
            }
        })
    }
    private fun handleLogin(customer:Customer){
        var progressDialog = DialogUtils.showProgressDialog(this@LoginActivity, getString(R.string.msg_loading), false)
        val customerService = ServiceGenerator.createService(CustomerServices::class.java)
        customerService.getByEmail(customer.email!!)
                .subscribeOn((Schedulers.io()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ items ->
                    val customer = items[0];
                    SettingsMy.setActiveUser(customer)
                    whatNext()
                },
                        { error ->
                            ActivityUtils.instance.invokeRegisterActivity(this@LoginActivity,customer.firstName!!, customer.email!!, shouldOrder)
                        })
    }

    private fun handleLogin(id:Int){
        val customerService = ServiceGenerator.createService(CustomerServices::class.java)
        val progressDialog = DialogUtils.showProgressDialog(this, getString(R.string.msg_loading), false)
        customerService.single(id)
                .subscribeOn((Schedulers.io()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ item ->
                    SettingsMy.setActiveUser(item)
                    whatNext()
                    DialogUtils.dismissProgressDialog(progressDialog)
                },
                        { error ->
                            Toast.makeText(applicationContext, "Không thể đăng nhập", Toast.LENGTH_SHORT).show()
                            Log.e("ERROR", error.message)
                            DialogUtils.dismissProgressDialog(progressDialog)
                        })
    }


     private fun whatNext() {
        if (shouldOrder) {
            ActivityUtils.instance.invokeOrderShipping(this@LoginActivity)
        } else {
            ActivityUtils.instance.invokeActivity(this@LoginActivity, MainActivity::class.java, true)
        }
    }

}
