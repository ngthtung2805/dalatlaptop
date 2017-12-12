package com.tungnui.abccomputer.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast

import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import com.facebook.GraphRequest
import com.facebook.accountkit.Account
import com.facebook.accountkit.AccountKit
import com.facebook.accountkit.AccountKitCallback
import com.facebook.accountkit.AccountKitError
import com.facebook.accountkit.AccountKitLoginResult
import com.facebook.accountkit.ui.AccountKitActivity
import com.facebook.accountkit.ui.AccountKitConfiguration
import com.facebook.accountkit.ui.LoginType
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.GoogleApiClient
import com.tungnui.abccomputer.R
import com.tungnui.abccomputer.SettingsMy
import com.tungnui.abccomputer.api.CustomerServices
import com.tungnui.abccomputer.api.JSONAuthService
import com.tungnui.abccomputer.api.ServiceGenerator
import com.tungnui.abccomputer.models.Customer
import com.tungnui.abccomputer.listener.LoginListener
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login.*

import java.util.*

open class BaseLoginActivity : AppCompatActivity() {
    val jsonAuthService: JSONAuthService
    init {
        jsonAuthService = ServiceGenerator.createService(JSONAuthService::class.java)
    }

    private var loginListener: LoginListener? = null
    private var callbackManager: CallbackManager? = null
    private var btnLoginGPlus: SignInButton? = null
    private var mGoogleApiClient: GoogleApiClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FacebookSdk.sdkInitialize(this.applicationContext)
    }

    private fun grantPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.READ_PHONE_STATE, Manifest.permission.RECEIVE_SMS), PERMISSION_REQ)

            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_REQ) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                invokePhoneLogin()
            } else {
                Toast.makeText(this@BaseLoginActivity, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }


    fun invokePhoneLogin() {
        if (grantPermission()) {
            val intent = Intent(this, AccountKitActivity::class.java)
            val configurationBuilder = AccountKitConfiguration.AccountKitConfigurationBuilder(
                    LoginType.PHONE,
                    AccountKitActivity.ResponseType.TOKEN)


            // enable receive auto sms
            configurationBuilder.setReadPhoneStateEnabled(true)
            configurationBuilder.setReceiveSMS(true)
            configurationBuilder.setDefaultCountryCode("BD")

            intent.putExtra(
                    AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
                    configurationBuilder.build())
            startActivityForResult(intent, FB_REQUEST_CODE)
        }
    }
    private val isValidateInput: Boolean
        get() = if (!edtEmail?.text.toString().isEmpty() && !edtPassword?.text.toString().isEmpty()) {
            true
        } else
            false

    fun invokeEmailLogin() {
        if(isValidateInput){
            var email = edtEmail.text.toString()
            var password = edtPassword.text.toString()
            val nonce = UUID.randomUUID().toString()
            val disposable = jsonAuthService.login(nonce, email, password)
                    .subscribeOn((Schedulers.io()))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ jsonAuthResponse ->
                        if (jsonAuthResponse.status == "ok") {
                            jsonAuthResponse.user.let {
                                 loginListener?.onLoginResponse(it?.id!!)
                            }
                        } else {
                              Toast.makeText(applicationContext, "Tên đăng nhập hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show()
                        }

                    })
        }
        else{
            Toast.makeText(applicationContext, "Tên đăng nhập hoặc mật khẩu không được bỏ trống", Toast.LENGTH_SHORT).show()
        }
    }

    fun initFbLogin() {
        btnLoginFacebook.visibility = View.VISIBLE
        btnLoginFacebook.setReadPermissions("email")

        callbackManager = CallbackManager.Factory.create()

        // Callback registration
        btnLoginFacebook.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                getUserInfo(loginResult.accessToken)
            }

            override fun onCancel() {
                Toast.makeText(this@BaseLoginActivity, "Cancelled", Toast.LENGTH_SHORT).show()
            }

            override fun onError(error: FacebookException) {
                Toast.makeText(this@BaseLoginActivity, "Cancelled", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun initGoogleLogin() {
        btnLoginGPlus = findViewById<View>(R.id.btnLoginGPlus) as SignInButton
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
        mGoogleApiClient = GoogleApiClient.Builder(this)
                .enableAutoManage(this) { Toast.makeText(this@BaseLoginActivity, "Failed", Toast.LENGTH_SHORT).show() }
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build()

        btnLoginGPlus?.setOnClickListener {
            val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
            startActivityForResult(signInIntent, GOOGLE_REQUEST_CODE)
        }

    }

    fun setGoogleButtonText(buttonText: String) {
        if (btnLoginGPlus != null) {
            for (i in 0 until btnLoginGPlus!!.childCount) {
                val v = btnLoginGPlus!!.getChildAt(i)

                if (v is TextView) {
                    v.text = buttonText
                    return
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FB_REQUEST_CODE) { // confirm that this response matches your request
            val loginResult = data.getParcelableExtra<AccountKitLoginResult>(AccountKitLoginResult.RESULT_KEY)
            if (loginResult.error != null) {
                Toast.makeText(this@BaseLoginActivity, "Failed", Toast.LENGTH_SHORT).show()
            } else if (loginResult.wasCancelled()) {
                Toast.makeText(this@BaseLoginActivity, "Cancelled", Toast.LENGTH_SHORT).show()
            } else {
                getUserInfo()
            }
        } else if (requestCode == GOOGLE_REQUEST_CODE) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if (result.isSuccess) {
                getUserInfo(result)
            } else {
                Toast.makeText(this@BaseLoginActivity, "Failed", Toast.LENGTH_SHORT).show()
            }
        } else {
            callbackManager?.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun getUserInfo(result: GoogleSignInResult) {

        val account = result.signInAccount
        if (loginListener != null) {
            val uri = account!!.photoUrl
            var photo = ""
            if (uri != null) {
                photo = uri.toString()
            }
            var customer = Customer(firstName = account.displayName, email = account.email,avatarUrl = photo )
            if(loginListener!=null)
                loginListener?.onLoginResponse(customer)
        }

    }

    private fun getUserInfo() {

        AccountKit.getCurrentAccount(object : AccountKitCallback<Account> {
            override fun onSuccess(account: Account) {
                val accountKitId = account.id

                // Get phone number
                val phoneNumber = account.phoneNumber
                var phoneNumberString: String? = null
                if (phoneNumber != null) {
                    phoneNumberString = phoneNumber.toString()
                    Log.e("AccKit", "Phone: " + phoneNumberString)
                }

                // Get email
                val email = account.email

                var customer = Customer(firstName = "name", email = email)
                if(loginListener!=null)
                    loginListener?.onLoginResponse(customer)
            }

            override fun onError(error: AccountKitError) {
                Log.e("AccKit", "Error: " + error.toString())
            }
        })

    }

    private fun getUserInfo(accessToken: AccessToken) {
        val request = GraphRequest.newMeRequest(
                accessToken
        ) { `object`, response ->
            try {
                val id = `object`.getString("id")
                val name = `object`.getString("name")
                val email = `object`.getString("email")
                val profilePic = "https://graph.facebook.com/$id/picture?width=200&height=150"
               var customer = Customer(firstName = name, email = email,avatarUrl = profilePic)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        val parameters = Bundle()
        parameters.putString("fields", "id,name,email")
        request.parameters = parameters
        request.executeAsync()

    }





    fun setLoginListener(loginListener: LoginListener) {
        this.loginListener = loginListener
    }

    companion object {
        private val FB_REQUEST_CODE = 99
        private val PERMISSION_REQ = 100
        private val GOOGLE_REQUEST_CODE = 101

    }

}
