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
import com.tungnui.dalatlaptop.models.Billing
import com.tungnui.dalatlaptop.models.Customer
import com.tungnui.dalatlaptop.models.Shipping
import com.tungnui.dalatlaptop.utils.Utils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_order_create_add_address.*
import org.jetbrains.anko.support.v4.toast

/**
 * Fragment allowing the user to create order.
 */
class OrderCreateAddAddressFragment : Fragment() {
    private var mCompositeDisposable: CompositeDisposable
    val customerService: CustomerServices
    init {
        mCompositeDisposable = CompositeDisposable()
        customerService = ServiceGenerator.createService(CustomerServices::class.java)
    }
    private lateinit var progressDialog: ProgressDialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_order_create_add_address, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressDialog = Utils.generateProgressDialog(activity, false)
        (activity as OrderActivity).title = "Thêm địa chỉ"
        order_create_add_save.setOnClickListener {
            if(checkRequired()){
                val billing = Billing()
                billing.firstName = Utils.getTextFromInputLayout(order_create_add_name_wrapper)
                billing.lastName = ""
                billing.address1= Utils.getTextFromInputLayout(order_create_add_street_wrapper)
                billing.city = Utils.getTextFromInputLayout(order_create_add_city_wrapper)
                billing.state = Utils.getTextFromInputLayout(order_create_add_province_wrapper)
                billing.phone = Utils.getTextFromInputLayout(order_create_add_phone_wrapper)
                updateBilling(billing)
            }
        }
    }
    fun updateBilling(billing: Billing){
        progressDialog.show()
        val user = SettingsMy.getActiveUser()
        user?.let{
            val data = Customer(billing = billing)
            val disposable =  customerService.update(it.id!!, data)
                    .subscribeOn((Schedulers.io()))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ response ->
                        SettingsMy.setActiveUser(response)
                        progressDialog.cancel()
                        (activity as OrderActivity).onBackPressed()
                    },
                            { error ->
                                toast("Lỗi cập nhật!")
                                progressDialog.cancel()
                            })

            mCompositeDisposable.add(disposable)
        }
    }
    fun checkRequired():Boolean{
        var nameCheck = Utils.checkTextInputLayoutValueRequirement(order_create_add_name_wrapper,"Vui lòng nhập họ tên")
        var addressCheck = Utils.checkTextInputLayoutValueRequirement(order_create_add_street_wrapper, "Vui lòng nhập địa chỉ")
        var cityCheck = Utils.checkTextInputLayoutValueRequirement(order_create_add_city_wrapper, "Vui lòng chọn quận/huyện")
        var provinceCheck = Utils.checkTextInputLayoutValueRequirement(order_create_add_province_wrapper, "Vui lòng chọn tỉnh/thành phố")
        var emailCheck = Utils.checkTextInputLayoutValueRequirement(order_create_add_email_wrapper, "Vui lòng nhập email")
        var phoneCheck = Utils.checkTextInputLayoutValueRequirement(order_create_add_phone_wrapper, "Vui lòng nhập số điện thoại")
        if (nameCheck && addressCheck && provinceCheck && cityCheck && phoneCheck && emailCheck) {
            return true
        } else {
            return false
        }
    }

    override fun onStop() {
        super.onStop()
        progressDialog.cancel()
    }


}