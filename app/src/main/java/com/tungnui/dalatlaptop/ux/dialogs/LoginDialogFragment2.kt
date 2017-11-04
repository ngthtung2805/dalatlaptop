package com.tungnui.dalatlaptop.ux.dialogs


import android.accounts.AccountManager
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.TextInputLayout
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*

import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult

import org.json.JSONException
import org.json.JSONObject

import com.tungnui.dalatlaptop.BuildConfig
import com.tungnui.dalatlaptop.R
import com.tungnui.dalatlaptop.SettingsMy
import com.tungnui.dalatlaptop.interfaces.LoginDialogInterface
import com.tungnui.dalatlaptop.listeners.OnSingleClickListener
import com.tungnui.dalatlaptop.listeners.OnTouchPasswordListener
import com.tungnui.dalatlaptop.models.Customer
import com.tungnui.dalatlaptop.utils.JsonUtils
import com.tungnui.dalatlaptop.utils.MsgUtils
import com.tungnui.dalatlaptop.utils.Utils
import com.tungnui.dalatlaptop.ux.MainActivity
import com.tungnui.dalatlaptop.api.CustomerServices
import com.tungnui.dalatlaptop.api.JSONAuthService
import com.tungnui.dalatlaptop.api.ServiceGenerator
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.dialog_login.*
import timber.log.Timber
import java.util.*

/**
 * Dialog handles user login, registration and forgotten password function.
 */
class LoginDialogFragment2 : DialogFragment(), FacebookCallback<LoginResult> {
    private var mCompositeDisposable: CompositeDisposable
    val jsonAuthService: JSONAuthService
    val customerService: CustomerServices

    init {
        mCompositeDisposable = CompositeDisposable()
        jsonAuthService = ServiceGenerator.createService(JSONAuthService::class.java)
        customerService = ServiceGenerator.createService(CustomerServices::class.java)
    }

    private lateinit var callbackManager: CallbackManager
    private var loginDialogInterface: LoginDialogInterface? = null
    private lateinit var progressDialog: ProgressDialog
    private var actualFormState = FormState.BASE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.dialogFullscreen)
        progressDialog = Utils.generateProgressDialog(activity, false)
    }

    override fun onStart() {
        super.onStart()
      /*  val d = dialog
        if (d != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            val window = d.window
            window.setLayout(width, height)
            window.setWindowAnimations(R.style.dialogFragmentAnimation)
            d.setOnKeyListener(DialogInterface.OnKeyListener { dialog, keyCode, event ->
                if (BuildConfig.DEBUG)
                    Timber.d("onKey: %d (Back=%d). Event:%d (Down:%d, Up:%d)", keyCode, KeyEvent.KEYCODE_BACK, event.action,
                            KeyEvent.ACTION_DOWN, KeyEvent.ACTION_UP)
                if (keyCode == KeyEvent.KEYCODE_BACK && event.repeatCount == 0) {
                    when (actualFormState) {
                        LoginDialogFragment.FormState.REGISTRATION -> {
                            if (event.action == KeyEvent.ACTION_UP) {
                                setVisibilityOfRegistrationForm(false)
                            }
                            return@OnKeyListener true
                        }
                        LoginDialogFragment.FormState.FORGOTTEN_PASSWORD -> {
                            if (event.action == KeyEvent.ACTION_UP) {
                                setVisibilityOfEmailForgottenForm(false)
                            }
                            return@OnKeyListener true
                        }
                        else -> return@OnKeyListener false
                    }
                }
                false
            })
        }*/
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_login2, container, false)

    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        callbackManager = CallbackManager.Factory.create()
       // prepareLoginFormNavigation()
       // prepareInputBoxes()
        //prepareActionButtons()
    }

    private fun prepareInputBoxes() {
        val registrationPassword = login_registration_password_wrapper.editText
        registrationPassword?.setOnTouchListener(OnTouchPasswordListener(registrationPassword))


        // Login email form
        val loginEmail = login_email_email_wrapper.editText
        loginEmail?.setText(SettingsMy.userEmailHint)
        val emailPassword = login_email_password_wrapper.editText
        if (emailPassword != null) {
            emailPassword.setOnTouchListener(OnTouchPasswordListener(emailPassword))
            emailPassword.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_SEND || actionId == 124) {
                    invokeLoginWithEmail()
                    return@OnEditorActionListener true
                }
                false
            })
        }

        val emailForgottenPassword = login_email_forgotten_email_wrapper.editText
        emailForgottenPassword?.setText(SettingsMy.userEmailHint)

        // Simple accounts whisperer.
        val accounts = AccountManager.get(activity).accounts
        val addresses = arrayOfNulls<String>(accounts.size)
        for (i in accounts.indices) {
            addresses[i] = accounts[i].name
            Timber.e("Sets autocompleteEmails: %s", accounts[i].name)
        }

        val emails = ArrayAdapter<String>(activity, android.R.layout.simple_dropdown_item_1line, addresses)
        login_registration_email_text_auto.setAdapter(emails)
    }

    private fun prepareLoginFormNavigation() {
        login_form_registration_btn.setOnClickListener {
            setVisibilityOfRegistrationForm(true)
            login_form_registration_btn.visibility = View.INVISIBLE
        }
        login_registration_close_button.setOnClickListener {
            // Slow to display ripple effect
            Handler().postDelayed({
                setVisibilityOfRegistrationForm(false)
                login_form_registration_btn.visibility = View.VISIBLE
            }, 200)
        }

        login_email_forgotten_password.setOnClickListener { setVisibilityOfEmailForgottenForm(true) }
        login_email_forgotten_back_button.setOnClickListener {
            // Slow to display ripple effect
            Handler().postDelayed({ setVisibilityOfEmailForgottenForm(false) }, 200)
        }
    }

    private fun prepareActionButtons() {
        login_form_skip.setOnClickListener {
            //                if (loginDialogInterface != null) loginDialogInterface.skipLogin();
            dismiss()
        }

        // FB login
        login_form_facebook.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View) {
                invokeFacebookLogin()
            }
        })

        login_email_confirm.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View) {
                invokeLoginWithEmail()
            }
        })

        login_registration_confirm.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View) {
                invokeRegisterNewUser()
            }
        })

        login_email_forgotten_confirm.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View) {
                invokeResetPassword()
            }
        })
    }
    private fun invokeLoginWithEmail() {
        hideSoftKeyboard()
        if (isRequiredFields(login_email_email_wrapper, login_email_password_wrapper)) {
            logInWithEmail(login_email_email_wrapper.editText, login_email_password_wrapper.editText)
        }
    }
    /**
     * Check if editTexts are valid view and if user set all required fields.
     *
     * @return true if ok.
     */
    private fun isRequiredFields(emailWrapper: TextInputLayout?, passwordWrapper: TextInputLayout?): Boolean {
        if (emailWrapper == null || passwordWrapper == null) {
            Timber.e(RuntimeException(), "Called isRequiredFields with null parameters.")
            MsgUtils.showToast(activity, MsgUtils.TOAST_TYPE_INTERNAL_ERROR, null, MsgUtils.ToastLength.LONG)
            return false
        } else {
            val email = emailWrapper.editText
            val password = passwordWrapper.editText
            if (email == null || password == null) {
                Timber.e(RuntimeException(), "Called isRequiredFields with null editTexts in wrappers.")
                MsgUtils.showToast(activity, MsgUtils.TOAST_TYPE_INTERNAL_ERROR, null, MsgUtils.ToastLength.LONG)
                return false
            } else {
                var isEmail = false
                var isPassword = false

                if (email.text.toString().equals("", ignoreCase = true)) {
                    emailWrapper.isErrorEnabled = true
                    emailWrapper.error = getString(R.string.Required_field)
                } else {
                    emailWrapper.isErrorEnabled = false
                    isEmail = true
                }

                if (password.text.toString().equals("", ignoreCase = true)) {
                    passwordWrapper.isErrorEnabled = true
                    passwordWrapper.error = getString(R.string.Required_field)
                } else {
                    passwordWrapper.isErrorEnabled = false
                    isPassword = true
                }

                if (isEmail && isPassword) {
                    return true
                } else {
                    Timber.e("Some fields are required.")
                    return false
                }
            }
        }
    }
    private fun invokeFacebookLogin() {
        LoginManager.getInstance().registerCallback(callbackManager, this)
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"))
    }

    private fun invokeRegisterNewUser() {
        hideSoftKeyboard()
        if (isRegisterRequiredFields(login_registration_firstname_wrapper,
                login_registration_lastname_wrapper,
                login_registration_email_wrapper,
                login_registration_password_wrapper,
                login_registration_repassword_wrapper)) {
            registerNewUser(login_registration_firstname_wrapper.editText,
                    login_registration_lastname_wrapper.editText,
                    login_registration_email_wrapper.editText,
                    login_registration_password_wrapper.editText)
        }
    }
    private fun isRegisterRequiredFields(firstNameWrapper: TextInputLayout,
                                         lastNameWrapper: TextInputLayout,
                                         emailWrapper: TextInputLayout,
                                         passwordWrapper: TextInputLayout,
                                         repasswordWrapper: TextInputLayout): Boolean {
        val email = emailWrapper.editText
        val password = passwordWrapper.editText
        val firstname = firstNameWrapper.editText
        val lastname = lastNameWrapper.editText
        val rePassword = repasswordWrapper.editText
        if (email == null || password == null || firstname == null || lastname == null || rePassword == null) {
            return false
        }
        var isEmail = false
        var isPassword = false
        var isFirstName = false
        var isLastName = false
        var isRepassword = false
        var isRe = false
        if (firstname.text.toString().equals("", ignoreCase = true)) {
            firstNameWrapper.isErrorEnabled = true
            firstNameWrapper.error = getString(R.string.Required_field)
        } else {
            login_registration_firstname_wrapper.isErrorEnabled = false
            isFirstName = true
        }
        if (lastname.text.toString().equals("", ignoreCase = true)) {
            lastNameWrapper.isErrorEnabled = true
            lastNameWrapper.error = getString(R.string.Required_field)
        } else {
            login_registration_lastname_wrapper.isErrorEnabled = false
            isLastName = true
        }
        if (email.text.toString().equals("", ignoreCase = true)) {
            emailWrapper.isErrorEnabled = true
            emailWrapper.error = getString(R.string.Required_field)
        } else {
            login_registration_email_wrapper.isErrorEnabled = false
            isEmail = true
        }

        if (password.text.toString().equals("", ignoreCase = true)) {
            passwordWrapper.isErrorEnabled = true
            passwordWrapper.error = getString(R.string.Required_field)
        } else {
            login_registration_password_wrapper.isErrorEnabled = false
            isPassword = true
        }
        if (rePassword.text.toString().equals("", ignoreCase = true)) {
            repasswordWrapper.isErrorEnabled = true
            repasswordWrapper.error = getString(R.string.Required_field)
        } else {
            repasswordWrapper.isErrorEnabled = false
            isRepassword = true
        }

        if(!rePassword.text.toString().equals(password.text.toString())){
            repasswordWrapper.isErrorEnabled = true
            repasswordWrapper.error = getString(R.string.Not_password_repassword)
        }
        else{
            login_registration_repassword_wrapper.isErrorEnabled = false
            isRe = true
        }

        if (isEmail && isPassword && isFirstName && isLastName &&isRepassword && isRe) {
            return true
        }

        return false
    }

    private fun registerNewUser(editTextFirstName: EditText?, editTextLastName: EditText?,
                                editTextEmail: EditText?, editTextPassword: EditText?) {
        progressDialog.show()
        var customer = Customer(firstName = editTextFirstName?.text.toString(),
                lastName = editTextLastName?.text.toString(),
                email=editTextEmail?.text.toString(),
                password = editTextPassword?.text.toString())
        var disposable = customerService.create(customer)
                .subscribeOn((Schedulers.io()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ customer ->
                    Toast.makeText(context,"Đăng kí thành công. Đăng nhập ngay", Toast.LENGTH_SHORT).show()
                    handleUserLogin(customer)
                },
                        { _ ->
                            progressDialog.cancel()
                        })
        mCompositeDisposable.add(disposable)
    }

    private fun logInWithEmail(editTextEmail: EditText?, editTextPassword: EditText?) {
        SettingsMy.userEmailHint = editTextEmail?.text.toString()
        progressDialog.show()
        var email = editTextEmail?.text.toString()
        var password = editTextPassword?.text.toString()
        var nonce = UUID.randomUUID().toString()
        var disposable = jsonAuthService.login(nonce, email,password)
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
        var disposable = customerService.single(customerId)
                .subscribeOn((Schedulers.io()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ customer ->
                    handleUserLogin(customer)
                },
                        { _ ->
                            progressDialog.cancel()
                        })
        mCompositeDisposable.add(disposable)
    }


    private fun handleUserLogin(customer: Customer) {
        progressDialog.cancel()
        SettingsMy.setActiveUser(customer)
        MainActivity.invalidateDrawerMenuHeader()

        if (loginDialogInterface != null) {
            loginDialogInterface?.successfulLoginOrRegistration(customer)
        } else {
            Timber.e("Interface is null")
            MsgUtils.showToast(activity, MsgUtils.TOAST_TYPE_INTERNAL_ERROR, null, MsgUtils.ToastLength.SHORT)
        }
        dismiss()
    }

    private fun invokeResetPassword() {
        val emailForgottenPasswordEmail = login_email_forgotten_email_wrapper.editText
        if (emailForgottenPasswordEmail == null || emailForgottenPasswordEmail.text.toString().equals("", ignoreCase = true)) {
            login_email_forgotten_email_wrapper.isErrorEnabled = true
            login_email_forgotten_email_wrapper.error = getString(R.string.Required_field)
        } else {
            login_email_forgotten_email_wrapper.isErrorEnabled = false
            resetPassword(emailForgottenPasswordEmail)
        }
    }

    private fun resetPassword(emailOfForgottenPassword: EditText) {
        progressDialog!!.show()

        val jo = JSONObject()
        try {
            jo.put(JsonUtils.TAG_EMAIL, emailOfForgottenPassword.text.toString().trim { it <= ' ' })
        } catch (e: JSONException) {
            Timber.e(e, "Parse resetPassword exception")
            MsgUtils.showToast(activity, MsgUtils.TOAST_TYPE_INTERNAL_ERROR, null, MsgUtils.ToastLength.SHORT)
            return
        }

        if (BuildConfig.DEBUG) Timber.d("Reset password email: %s", jo.toString())
    }

    private fun hideSoftKeyboard() {
        if (activity != null && view != null) {
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view!!.windowToken, 0)
        }
    }

    private fun setVisibilityOfRegistrationForm(setVisible: Boolean) {
        if (setVisible) {
            actualFormState = FormState.REGISTRATION
            login_registration_close_button.visibility= View.VISIBLE
            login_registration_form.visibility = View.VISIBLE
            login_email_form.visibility = View.INVISIBLE
        } else {
            actualFormState = FormState.EMAIL
            login_registration_form.visibility = View.INVISIBLE
            login_registration_close_button.visibility= View.INVISIBLE
            login_email_form.visibility = View.VISIBLE
            hideSoftKeyboard()
        }
    }

    private fun setVisibilityOfEmailForm(setVisible: Boolean) {
        if (setVisible) {
            actualFormState = FormState.EMAIL
            login_form_skip.visibility = View.VISIBLE
            login_email_form.visibility = View.VISIBLE
        } else {
            actualFormState = FormState.BASE
            login_email_form.visibility = View.INVISIBLE
            login_form_skip.visibility = View.INVISIBLE
            hideSoftKeyboard()
        }
    }

    private fun setVisibilityOfEmailForgottenForm(setVisible: Boolean) {
        if (setVisible) {
            actualFormState = FormState.FORGOTTEN_PASSWORD
            login_email_form.visibility = View.INVISIBLE
            login_email_forgotten_form.visibility = View.VISIBLE
        } else {
            actualFormState = FormState.EMAIL
            login_email_form.visibility = View.VISIBLE
            login_email_forgotten_form.visibility = View.INVISIBLE
        }
        hideSoftKeyboard()
    }

    /**
     * Check if editTexts are valid view and if user set all required fields.
     *
     * @return true if ok.
     */

    override fun onStop() {
        super.onStop()
    }

    override fun onDetach() {
        loginDialogInterface = null
        super.onDetach()
    }

    override fun onSuccess(loginResult: LoginResult?) {
        Timber.d("FB login success")
        if (loginResult == null) {
            Timber.e("Fb login succeed with null loginResult.")
            handleNonFatalError(getString(R.string.Facebook_login_failed), true)
        } else {
            Timber.d("Result: %s", loginResult.toString())
            val request = GraphRequest.newMeRequest(loginResult.accessToken
            ) { `object`, response ->
                if (response != null && response.error == null) {
                    verifyUserOnApi(`object`, loginResult.accessToken)
                } else {
                    Timber.e("Error on receiving user profile information.")
                    if (response != null && response.error != null) {
                        Timber.e(RuntimeException(), "Error: %s", response.error.toString())
                    }
                    handleNonFatalError(getString(R.string.Receiving_facebook_profile_failed), true)
                }
            }
            val parameters = Bundle()
            parameters.putString("fields", "id,name,email,gender")
            request.parameters = parameters
            request.executeAsync()
        }
    }

    override fun onCancel() {
        Timber.d("Fb login canceled")
    }

    override fun onError(e: FacebookException) {
        Timber.e(e, "Fb login error")
        handleNonFatalError(getString(R.string.Facebook_login_failed), false)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        callbackManager.onActivityResult(requestCode, resultCode, data)

    }

    /**
     * Volley request that sends FB_ID and FB_ACCESS_TOKEN to API
     */
    private fun verifyUserOnApi(userProfileObject: JSONObject, fbAccessToken: AccessToken) {
        val jo = JSONObject()
        try {
            jo.put(JsonUtils.TAG_FB_ID, userProfileObject.getString("id"))
            jo.put(JsonUtils.TAG_FB_ACCESS_TOKEN, fbAccessToken.token)
        } catch (e: JSONException) {
            Timber.e(e, "Exception while parsing fb user.")
            MsgUtils.showToast(activity, MsgUtils.TOAST_TYPE_INTERNAL_ERROR, null, MsgUtils.ToastLength.LONG)
            return
        }

        progressDialog!!.show()
        /*      val verifyFbUser = GsonRequest(Request.Method.POST, url, jo.toString(), User::class.java,
                      Response.Listener { response ->
                          Timber.d(MSG_RESPONSE, response.toString())
                          handleUserLogin(response)
                      }, Response.ErrorListener { error ->
                  if (progressDialog != null) progressDialog!!.cancel()
                  MsgUtils.logAndShowErrorMessage(activity, error)
                  LoginDialogFragment.logoutUser()
              }, fragmentManager, null)*/
    }

    /**
     * Handle errors, when user have identity at least.
     * Show error message to user.
     */
    private fun handleNonFatalError(message: String, logoutFromFb: Boolean) {
        if (logoutFromFb) {
            LoginDialogFragment.logoutUser()
        }
        if (activity != null)
            MsgUtils.showToast(activity, MsgUtils.TOAST_TYPE_MESSAGE, message, MsgUtils.ToastLength.LONG)
    }

    private enum class FormState {
        BASE, REGISTRATION, EMAIL, FORGOTTEN_PASSWORD
    }

    companion object {

        val MSG_RESPONSE = "response: %s"

        /**
         * Creates dialog which handles user login, registration and forgotten password function.
         *
         * @param loginDialogInterface listener receiving login/registration results.
         * @return new instance of dialog.
         */
        fun newInstance(loginDialogInterface: LoginDialogInterface): LoginDialogFragment {
            val frag = LoginDialogFragment()
            return frag
        }

        fun logoutUser() {
            val fbManager = LoginManager.getInstance()
            fbManager?.logOut()
            SettingsMy.setActiveUser(null)
            MainActivity.updateCartCountNotification()
            MainActivity.invalidateDrawerMenuHeader()
        }
    }
}


