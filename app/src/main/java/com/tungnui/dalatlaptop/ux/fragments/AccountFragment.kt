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
    private lateinit var pDialog: ProgressDialog

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
                    (activity as MainActivity).onAccountSelected()
                }
            }
        })


        val user = SettingsMy.getActiveUser()
        if (user != null) {
            refreshScreen(user)
        }    else {
            refreshScreen(null)
        }
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

            account_name.text = customer.firstName
            account_email.text = customer.email
            account_phone.text = customer.billing?.phone
        }
    }

}
