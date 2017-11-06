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
    private var isUpdate:Boolean=false
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_order_create_add_address, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressDialog = Utils.generateProgressDialog(activity, false)
        val startBundle = arguments
        if (startBundle != null) {
            isUpdate = startBundle.getBoolean(IS_UPDATE,false)
        }
        if(isUpdate){
            val user = SettingsMy.getActiveUser()
            user?.let{
                Utils.setTextToInputLayout(order_create_add_name_wrapper,it.firstName)
                Utils.setTextToInputLayout(order_create_add_street_wrapper,it.billing?.address1 )
                Utils.setTextToInputLayout(order_create_add_city_wrapper,it.billing?.city )
                Utils.setTextToInputLayout(order_create_add_province_wrapper,it.billing?.state )
                Utils.setTextToInputLayout(order_create_add_email_wrapper,it.billing?.email )
                Utils.setTextToInputLayout(order_create_add_phone_wrapper,it.billing?.phone)
            }
        }
        order_create_add_save.setOnClickListener {
            if(checkRequired()){
                val billing = Billing()
                billing.firstName = Utils.getTextFromInputLayout(order_create_add_name_wrapper)
                billing.lastName = ""
                billing.address1= Utils.getTextFromInputLayout(order_create_add_street_wrapper)
                billing.city = Utils.getTextFromInputLayout(order_create_add_city_wrapper)
                billing.state = Utils.getTextFromInputLayout(order_create_add_province_wrapper)
                billing.phone = Utils.getTextFromInputLayout(order_create_add_phone_wrapper)
                billing.email = Utils.getTextFromInputLayout(order_create_add_email_wrapper)
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
        val nameCheck = Utils.checkTextInputLayoutValueRequirement(order_create_add_name_wrapper,"Vui lòng nhập họ tên")
        val addressCheck = Utils.checkTextInputLayoutValueRequirement(order_create_add_street_wrapper, "Vui lòng nhập địa chỉ")
        val cityCheck = Utils.checkTextInputLayoutValueRequirement(order_create_add_city_wrapper, "Vui lòng chọn quận/huyện")
        val provinceCheck = Utils.checkTextInputLayoutValueRequirement(order_create_add_province_wrapper, "Vui lòng chọn tỉnh/thành phố")
        val emailCheck = Utils.checkTextInputLayoutValueRequirement(order_create_add_email_wrapper, "Vui lòng nhập email")
        val phoneCheck = Utils.checkTextInputLayoutValueRequirement(order_create_add_phone_wrapper, "Vui lòng nhập số điện thoại")
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
    companion object {
        private val IS_UPDATE = "is_update"
        fun newInstance(isUpdate: Boolean): OrderCreateAddAddressFragment {
            val args = Bundle()
            args.putBoolean(IS_UPDATE, isUpdate)
            val fragment = OrderCreateAddAddressFragment()
            fragment.arguments = args
            return fragment
        }
    }

}