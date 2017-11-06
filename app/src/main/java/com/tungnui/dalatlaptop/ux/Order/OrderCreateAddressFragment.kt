package com.tungnui.dalatlaptop.ux.Order

import android.app.ProgressDialog
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tungnui.dalatlaptop.R
import com.tungnui.dalatlaptop.SettingsMy
import com.tungnui.dalatlaptop.api.CustomerServices
import com.tungnui.dalatlaptop.api.ServiceGenerator
import com.tungnui.dalatlaptop.api.ShippingMethodService
import com.tungnui.dalatlaptop.models.Billing
import com.tungnui.dalatlaptop.models.Customer
import com.tungnui.dalatlaptop.models.Shipping
import com.tungnui.dalatlaptop.utils.Utils
import com.tungnui.dalatlaptop.utils.getNextUrl
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_category.*
import kotlinx.android.synthetic.main.fragment_order_create_address.*
import org.jetbrains.anko.noButton
import org.jetbrains.anko.sdk25.coroutines.onCheckedChange
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.yesButton


/**
 * Fragment allowing the user to create order.
 */
class OrderCreateAddressFragment : Fragment() {
    private var mCompositeDisposable: CompositeDisposable
    val customerService: CustomerServices

    init {
        mCompositeDisposable = CompositeDisposable()
        customerService = ServiceGenerator.createService(CustomerServices::class.java)
    }

    private lateinit var progressDialog: ProgressDialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_order_create_address, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressDialog = Utils.generateProgressDialog(activity, false)
        refreshLayout()
        order_create_address_add.setOnClickListener {
            (activity as OrderActivity).onAddAddress()
        }
        order_create_address_delete.setOnClickListener {
            alert("", "Bạn muốn xóa địa chỉ này?") {
                yesButton { deleteAddress() }
                noButton {}
            }.show()
        }
        order_create_address_update.setOnClickListener {
            (activity as OrderActivity).onAddAddress(isUpdate = true)
        }
        order_create_address_rd1.onCheckedChange { buttonView, isChecked ->
            if (isChecked) {
                order_create_address_rd2.isChecked = false
            }
        }
        order_create_address_rd2.onCheckedChange { buttonView, isChecked ->
            if (isChecked) {
                order_create_address_rd1.isChecked = false
            }
        }
        order_create_step1_next.setOnClickListener {
            var note:String=""
            if(order_create_address_rd2.isChecked)
                note= "Nhận hàng tại cửa hàng"
            (activity as OrderActivity).onNextStep2(note)
        }
    }

    fun deleteAddress() {
        progressDialog.show()
        val user = SettingsMy.getActiveUser()
        user?.let {
            val data = Customer(billing = Billing("", "", "", "", "", "", "", "", "", "", ""),
                    shipping = Shipping("", "", "", "", "", "", "", "", ""))
            val disposable = customerService.update(it.id!!, data)
                    .subscribeOn((Schedulers.io()))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ response ->
                        SettingsMy.setActiveUser(response)
                        refreshLayout()
                        progressDialog.cancel()
                    },
                            { error ->
                                toast("Lỗi cập nhật!")
                                progressDialog.cancel()
                            })

            mCompositeDisposable.add(disposable)
        }

    }

    fun refreshLayout() {
        val user = SettingsMy.getActiveUser()
        if (user?.billing?.firstName == "" || user?.billing?.address1 == "") {
            order_create_address_layout.visibility = View.GONE
            order_create_address_add.visibility = View.VISIBLE
        } else {
            order_create_address_layout.visibility = View.VISIBLE
            order_create_address_add.visibility = View.GONE
            var shipAddress = user?.billing
            shipAddress?.let {
                order_create_address_name.text = "${shipAddress.firstName}"
                order_create_address_phone.text = shipAddress.phone
                order_create_address_address.text = shipAddress.address1
            }
        }

    }


    override fun onStop() {
        super.onStop()
        progressDialog.cancel()
    }


}