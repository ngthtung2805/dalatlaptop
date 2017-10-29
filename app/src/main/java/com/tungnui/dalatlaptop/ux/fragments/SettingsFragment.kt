package com.tungnui.dalatlaptop.ux.fragments

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Spinner

import com.tungnui.dalatlaptop.CONST
import com.tungnui.dalatlaptop.MyApplication
import com.tungnui.dalatlaptop.R

import com.tungnui.dalatlaptop.listeners.OnSingleClickListener
import com.tungnui.dalatlaptop.utils.Utils
import com.tungnui.dalatlaptop.ux.MainActivity
import com.tungnui.dalatlaptop.ux.dialogs.LicensesDialogFragment
import kotlinx.android.synthetic.main.fragment_settings.*
import timber.log.Timber

class SettingsFragment : Fragment() {

    private var progressDialog: ProgressDialog? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MainActivity.setActionBarTitle(getString(R.string.Settings))

        progressDialog = Utils.generateProgressDialog(activity, false)

        settings_licenses_layout.setOnClickListener {
            val df = LicensesDialogFragment()
            df.show(fragmentManager, LicensesDialogFragment::class.java.simpleName)
        }

        settings_privacy_policy.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(view: View) {
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse("http://openshop.io/privacy-policy.html")
                startActivity(i)
            }
        })
    }



    override fun onStop() {
        MyApplication.getInstance().cancelPendingRequests(CONST.SETTINGS_REQUESTS_TAG)
        if (progressDialog != null) progressDialog!!.cancel()
        super.onStop()
    }
}
