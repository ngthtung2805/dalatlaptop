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
import com.tungnui.dalatlaptop.api.CustomerServices
import com.tungnui.dalatlaptop.api.JSONAuthService
import com.tungnui.dalatlaptop.api.ServiceGenerator
import com.tungnui.dalatlaptop.interfaces.LoginDialogInterface
import com.tungnui.dalatlaptop.models.Customer
import com.tungnui.dalatlaptop.libraryhelper.Utils
import com.tungnui.dalatlaptop.utils.checkTextInputLayoutValueRequirement
import com.tungnui.dalatlaptop.utils.getTextFromInputLayout
import com.tungnui.dalatlaptop.utils.hideKeyboard
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_register.*

/**
 * Created by thanh on 06/11/2017.
 */
class RegisterFragment : Fragment() {
    private var mCompositeDisposable: CompositeDisposable
    val jsonAuthService: JSONAuthService
    val customerService: CustomerServices

    init {
        mCompositeDisposable = CompositeDisposable()
        jsonAuthService = ServiceGenerator.createService(JSONAuthService::class.java)
        customerService = ServiceGenerator.createService(CustomerServices::class.java)
    }

    private var loginDialogInterface: LoginDialogInterface? = null
    private lateinit var progressDialog: ProgressDialog
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressDialog = Utils.generateProgressDialog(activity, false)
        login_registration_confirm.setOnClickListener {
            if (checkRequired()){

                val name =login_registration_name_wrapper.getTextFromInputLayout()
                val email = login_registration_email_wrapper.getTextFromInputLayout()
                val password = login_registration_password_wrapper.getTextFromInputLayout()
                registerNewUser(name, email, password)
                activity.hideKeyboard()
            }
        }
    }

    fun checkRequired(): Boolean {
        val nameCheck = login_registration_name_wrapper.checkTextInputLayoutValueRequirement("Vui lòng nhập họ và tên")
        val emailCheck = login_registration_email_wrapper.checkTextInputLayoutValueRequirement("Vui lòng nhập email của bạn")
        val passwordCheck = login_registration_password_wrapper.checkTextInputLayoutValueRequirement("Vui lòng nhập mật khẩu")
        return if (emailCheck && passwordCheck && nameCheck) true else false
    }




    private fun registerNewUser(name: String, email: String, password: String) {
        progressDialog.show()
        val customer = Customer(firstName = name, email = email, password = password)
        val disposable = customerService.create(customer)
                .subscribeOn((Schedulers.io()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ customer ->
                    Toast.makeText(context, "Đăng kí thành công", Toast.LENGTH_SHORT).show()
                    progressDialog.cancel()
                    (activity as LoginActivity).handleUserLogin(customer)
                },
                        { _ ->
                            progressDialog.cancel()
                        })
        mCompositeDisposable.add(disposable)
    }


}