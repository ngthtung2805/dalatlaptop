package com.tungnui.dalatlaptop.ux.Order

import android.app.ProgressDialog
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tungnui.dalatlaptop.R
import com.tungnui.dalatlaptop.libraryhelper.Utils
import kotlinx.android.synthetic.main.fragment_order_create_address.*
import kotlinx.android.synthetic.main.fragment_order_create_payment.*
import org.jetbrains.anko.sdk25.coroutines.onCheckedChange


class OrderCreatePaymentFragment : Fragment() {
    private lateinit var progressDialog: ProgressDialog
    private var note =""
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_order_create_payment, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressDialog = Utils.generateProgressDialog(activity, false)
        val startBundle = arguments
        if (startBundle != null) {
            note = startBundle.getString(NOTE, "")
        }
        order_create_payment_rd_cod.onCheckedChange { buttonView, isChecked ->
            if (isChecked) {
                order_create_payment_rd_bacs.isChecked = false
            }
        }
        order_create_payment_rd_bacs.onCheckedChange { buttonView, isChecked ->
            if (isChecked) {
                order_create_payment_rd_cod.isChecked = false
            }
        }
        order_create_step2_next.setOnClickListener {
            var paymentMethod = if(order_create_payment_rd_cod.isChecked) "cod" else "bacs"
            (activity as OrderActivity).onNextStep3(note,paymentMethod)
        }
    }

    override fun onStop() {
        super.onStop()
        progressDialog.cancel()
    }

    companion object {
        private val NOTE = "note"
        fun newInstance(note: String): OrderCreatePaymentFragment {
            val args = Bundle()
            args.putString(NOTE, note)
            val fragment = OrderCreatePaymentFragment()
            fragment.arguments = args
            return fragment
        }
    }
}