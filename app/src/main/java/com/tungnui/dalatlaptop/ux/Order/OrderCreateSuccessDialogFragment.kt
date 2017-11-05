package com.tungnui.dalatlaptop.ux.Order

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.tungnui.dalatlaptop.R
import kotlinx.android.synthetic.main.dialog_order_create_success.*

class OrderCreateSuccessDialogFragment : DialogFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setStyle(STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth)
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.dialogFullscreen)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window.setWindowAnimations(R.style.dialogFragmentAnimation)
        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_order_create_success, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
      /*  toolbar.setTitle("Thông báo đơn hàng")
        (getActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        val actionBar = (getActivity() as AppCompatActivity).supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeButtonEnabled(true)
            actionBar.setHomeAsUpIndicator(android.R.drawable.ic_menu_close_clear_cancel)
        }
        setHasOptionsMenu(true)*/
        order_success_continous_shopping.setOnClickListener {
            if (activity is OrderActivity)
                (activity as OrderActivity).onOrderSuccessContinousShopping()
            dismiss()
        }
    }

}