package com.tungnui.dalatlaptop.ux.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment

import com.tungnui.dalatlaptop.R
import com.tungnui.dalatlaptop.ux.MainActivity
import com.tungnui.dalatlaptop.ux.fragments.BannersFragment
import timber.log.Timber

class LoginExpiredDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        Timber.d("%s - OnCreateView", this.javaClass.simpleName)

        val builder = AlertDialog.Builder(activity, R.style.myAlertDialogStyle)
        builder.setTitle(R.string.Oops_login_expired)
        builder.setMessage(R.string.Your_session_has_expired_Please_log_in_again)

        builder.setPositiveButton(R.string.Ok) { dialog, which ->
            if (activity is MainActivity)
                (activity as MainActivity).onDrawerHomeSelected()
            dialog.dismiss()
        }

        return builder.create()
    }
}
