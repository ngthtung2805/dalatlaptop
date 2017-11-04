package com.tungnui.dalatlaptop.ux.fragments

import android.app.ProgressDialog
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.tungnui.dalatlaptop.CONST
import com.tungnui.dalatlaptop.MyApplication
import com.tungnui.dalatlaptop.R

import com.tungnui.dalatlaptop.utils.Utils
import com.tungnui.dalatlaptop.ux.MainActivity

class SettingsFragment : Fragment() {

    private var progressDialog: ProgressDialog? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MainActivity.setActionBarTitle(getString(R.string.Settings))

        progressDialog = Utils.generateProgressDialog(activity, false)




    }



    override fun onStop() {
        if (progressDialog != null) progressDialog!!.cancel()
        super.onStop()
    }
}
