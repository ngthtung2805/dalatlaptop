package com.tungnui.dalatlaptop.ux.login

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.tungnui.dalatlaptop.R
import com.tungnui.dalatlaptop.SettingsMy
import com.tungnui.dalatlaptop.api.CustomerServices
import com.tungnui.dalatlaptop.api.JSONAuthService
import com.tungnui.dalatlaptop.api.ServiceGenerator
import com.tungnui.dalatlaptop.libraryhelper.Utils
import com.tungnui.dalatlaptop.utils.checkTextInputLayoutValueRequirement
import com.tungnui.dalatlaptop.utils.getTextFromInputLayout
import com.tungnui.dalatlaptop.utils.hideKeyboard
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_login.*
import java.util.*

/**
 * Created by thanh on 06/11/2017.
 */
class LoginFragment : Fragment() {
    private var mCompositeDisposable: CompositeDisposable
    val jsonAuthService: JSONAuthService
    val customerService: CustomerServices

    init {
        mCompositeDisposable = CompositeDisposable()
        jsonAuthService = ServiceGenerator.createService(JSONAuthService::class.java)
        customerService = ServiceGenerator.createService(CustomerServices::class.java)
    }


    private lateinit var progressDialog: ProgressDialog
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressDialog = Utils.generateProgressDialog(activity, false)
        login_email_confirm.setOnClickListener {
            if (checkRequired()) {
                activity.hideKeyboard()
                logInWithEmail(login_email_email_wrapper.getTextFromInputLayout(), login_email_password_wrapper.getTextFromInputLayout())
            }
        }
    }

    fun checkRequired(): Boolean {
        val emailCheck = login_email_email_wrapper.checkTextInputLayoutValueRequirement("Tên đăng nhập không được bỏ trống")
        val passwordCheck = login_email_password_wrapper.checkTextInputLayoutValueRequirement("Mật khẩu không được bỏ trống")
        return if (emailCheck && passwordCheck) true else false
    }

    private fun logInWithEmail(email: String, password: String) {
        SettingsMy.userEmailHint = email
        progressDialog.show()
        val nonce = UUID.randomUUID().toString()
        val disposable = jsonAuthService.login(nonce, email, password)
                .subscribeOn((Schedulers.io()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ jsonAuthResponse ->
                    if (jsonAuthResponse.status == "ok") {
                        jsonAuthResponse.user.let { getCustomer(it?.id) }
                    } else {
                        progressDialog.cancel()
                        Toast.makeText(context, "Tên đăng nhập hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show()
                    }

                })
        mCompositeDisposable.add(disposable)

    }

    private fun getCustomer(customerId: Int?) {
        if (customerId == null)
            return;
        val disposable = customerService.single(customerId)
                .subscribeOn((Schedulers.io()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ customer ->
                    progressDialog.cancel()
                    (activity as LoginActivity).handleUserLogin(customer)
                },
                        { _ ->
                            progressDialog.cancel()
                        })
        mCompositeDisposable.add(disposable)
    }


}