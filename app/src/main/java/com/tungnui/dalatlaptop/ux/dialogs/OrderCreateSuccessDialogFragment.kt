package com.tungnui.dalatlaptop.ux.dialogs

import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.content.ContextCompat
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView

import com.tungnui.dalatlaptop.R
import com.tungnui.dalatlaptop.ux.MainActivity
import timber.log.Timber


/**
 * Dialog display "Thank you" screen after order is finished.
 */
class OrderCreateSuccessDialogFragment : DialogFragment() {

    private var sampleApplication = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        dialog.window!!.setWindowAnimations(R.style.dialogFragmentAnimation)
        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Timber.d("%s - OnCreateView", this.javaClass.simpleName)
        val view = inflater!!.inflate(R.layout.dialog_order_create_success, container, false)

        val okBtn = view.findViewById<View>(R.id.order_create_success_ok) as Button
        okBtn.setOnClickListener {
            if (activity is MainActivity)
                (activity as MainActivity).onDrawerHomeSelected()
            dismiss()
        }

        val title = view.findViewById<View>(R.id.order_create_success_title) as TextView
        val description = view.findViewById<View>(R.id.order_create_success_description) as TextView

        if (sampleApplication) {
            title.setText(R.string.This_is_a_sample_app)
            description.setTextColor(ContextCompat.getColor(context, R.color.textSecondary))
            description.setText(R.string.Sample_app_description)
        } else {
            title.setText(R.string.Thank_you_for_your_order)
            description.setTextColor(ContextCompat.getColor(context, R.color.colorAccent))
            description.text = Html.fromHtml(getString(R.string.Wait_for_sms_or_email_order_confirmation))
        }

        return view
    }

    companion object {
        fun newInstance(sampleApplication: Boolean): OrderCreateSuccessDialogFragment {
            val orderCreateSuccessDialogFragment = OrderCreateSuccessDialogFragment()
            orderCreateSuccessDialogFragment.sampleApplication = sampleApplication
            return orderCreateSuccessDialogFragment
        }
    }
}