package com.tungnui.dalatlaptop.ux.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

import com.tungnui.dalatlaptop.R
import com.tungnui.dalatlaptop.interfaces.RequestListener
import com.tungnui.dalatlaptop.libraryhelper.MsgUtils
import kotlinx.android.synthetic.main.dialog_discount_fragment.*
import timber.log.Timber


class DiscountDialogFragment : DialogFragment() {

    private var requestListener: RequestListener? = null


    private val isRequiredFieldsOk: Boolean
        get() {
            var discountCode = false

            if (discount_code_input_wrapper.editText == null || discount_code_input_wrapper.editText?.text.toString().equals("", ignoreCase = true)) {
                discount_code_input_wrapper.isErrorEnabled = true
                discount_code_input_wrapper.error = getString(R.string.Required_field)
            } else {
                Timber.d("Some fields are required.")
                discount_code_input_wrapper.isErrorEnabled = false
                discountCode = true
            }
            return discountCode
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.dialogNoTitle)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = object : Dialog(activity, theme) {
            override fun dismiss() {
                // Remove soft keyboard
                if (activity != null && view != null) {
                    val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view?.windowToken, 0)
                }

                requestListener = null

                super.dismiss()
            }
        }
        dialog.window.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        dialog.window.setWindowAnimations(R.style.dialogFragmentAnimation)
        dialog.setCanceledOnTouchOutside(false)
        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_discount_fragment, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        discount_code_confirm.setOnClickListener {
            if (isRequiredFieldsOk) {
                sendDiscountCode(discount_code_input_wrapper.editText)
            }
        }
        discount_code_close.setOnClickListener { dismiss() }
    }


    private fun sendDiscountCode(discountCodeInput: EditText?) {
        discount_code_progress.visibility =View.VISIBLE
        MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_MESSAGE, getString(R.string.Ok), MsgUtils.ToastLength.SHORT);
        if (requestListener != null)
            requestListener?.requestSuccess(0);

        // Don't have to hide progress, because of dismiss.
        dismiss();


    }

    override fun onStop() {
        super.onStop()
    }

    companion object {
        fun newInstance(requestListener: RequestListener): DiscountDialogFragment {
            val frag = DiscountDialogFragment()
            frag.requestListener = requestListener
            return frag
        }
    }
}
