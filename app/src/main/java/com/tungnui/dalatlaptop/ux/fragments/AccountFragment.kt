package com.tungnui.dalatlaptop.ux.fragments

import android.app.ProgressDialog
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.tungnui.dalatlaptop.R
import com.tungnui.dalatlaptop.SettingsMy
import com.tungnui.dalatlaptop.interfaces.LoginDialogInterface
import com.tungnui.dalatlaptop.listeners.OnSingleClickListener
import com.tungnui.dalatlaptop.models.Customer
import com.tungnui.dalatlaptop.utils.Utils
import com.tungnui.dalatlaptop.ux.MainActivity
import com.tungnui.dalatlaptop.api.CustomerServices
import com.tungnui.dalatlaptop.api.ServiceGenerator
import com.tungnui.dalatlaptop.ux.login.LoginActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_account.*
import timber.log.Timber

/**
 * Fragment provides the account screen with options such as logging, editing and more.
 */
class AccountFragment : Fragment() {
    private var mCompositeDisposable: CompositeDisposable
    val customerService: CustomerServices

    init {
        mCompositeDisposable = CompositeDisposable()
        customerService = ServiceGenerator.createService(CustomerServices::class.java)
    }
    private lateinit var pDialog: ProgressDialog
    private var mAlreadyLoaded = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MainActivity.setActionBarTitle(getString(R.string.Profile))
        pDialog = Utils.generateProgressDialog(activity, false)



        account_update.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View) {
                /*if (activity is MainActivity)
                    (activity as MainActivity).onAccountEditSelected()*/
            }
        })

        account_my_orders.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View) {
                if (activity is MainActivity)
                    (activity as MainActivity).onOrdersHistory()
            }
        })

        account_settings.setOnClickListener {
            val activity = activity
            if (activity != null && activity is MainActivity)
                (getActivity() as MainActivity).startSettingsFragment()
        }
        account_dispensing_places.setOnClickListener {
           /* val shippingDialogFragment = ShippingDialogFragment.newInstance { }
            shippingDialogFragment.show(fragmentManager, "shippingDialogFragment")*/
        }

        account_login_logout_btn.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View) {
                if (SettingsMy.getActiveUser() != null) {
                    LoginActivity.logoutUser()
                    refreshScreen(null)
                } else {
                   /* val loginDialogFragment = LoginDialogFragment.newInstance (object:LoginDialogInterface{
                        override fun successfulLoginOrRegistration(customer: Customer) {
                            refreshScreen(customer)
                            MainActivity.updateCartCountNotification()
                        }
                    })
                    loginDialogFragment.show(fragmentManager, LoginDialogFragment::class.java.simpleName)*/
                }
            }
        })


        val user = SettingsMy.getActiveUser()
        if (user != null) {
            Timber.d("user: %s", user.toString())
            // Sync user data if fragment created (not reuse from backstack)
            if (savedInstanceState == null && !mAlreadyLoaded) {
                mAlreadyLoaded = true
                syncUserData(user.id)
            } else {
                refreshScreen(user)
            }
        } else {
            refreshScreen(null)
        }
    }
    private fun syncUserData(customerId:Int?) {
        pDialog.show()
        if (customerId == null)
            return;
        var disposable = customerService.single(customerId)
                .subscribeOn((Schedulers.io()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ customer ->
                    pDialog.cancel()
                    refreshScreen(customer)
                },
                        { _ ->
                            pDialog.cancel()
                        })
        mCompositeDisposable.add(disposable)
    }

    private fun refreshScreen(customer: Customer?) {

        if (customer == null) {
            account_login_logout_btn.text = getString(R.string.Log_in)
            account_user_info.visibility = View.GONE
            account_update.visibility = View.GONE
            account_my_orders.visibility = View.GONE
        } else {
            account_login_logout_btn.text = getString(R.string.Log_out)
            account_user_info.visibility = View.VISIBLE
            account_update.visibility = View.VISIBLE
            account_my_orders.visibility = View.VISIBLE

            account_name.text = customer.username

           /* var address: String? = customer.
            address = appendCommaText(address, user.houseNumber, false)
            address = appendCommaText(address, user.city, true)
            address = appendCommaText(address, user.zip, true)*/

           // account_address.text = address
            account_email.text = customer.email
           // account_phone.text = customer.
        }
    }

    private fun appendCommaText(result: String?, append: String?, addComma: Boolean): String? {
        var result = result
        if (result != null && !result.isEmpty()) {
            if (append != null && !append.isEmpty()) {
                if (addComma)
                    result += getString(R.string.format_comma_prefix, append)
                else
                    result += getString(R.string.format_space_prefix, append)
            }
            return result
        } else {
            return append
        }
    }

    override fun onStop() {
        super.onStop()
    }
}
