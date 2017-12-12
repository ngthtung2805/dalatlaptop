package com.tungnui.abccomputer.activity

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast

import com.tungnui.abccomputer.R
import com.tungnui.abccomputer.SettingsMy
import com.tungnui.abccomputer.api.CustomerServices
import com.tungnui.abccomputer.api.ServiceGenerator
import com.tungnui.abccomputer.api.ShippingZoneService
import com.tungnui.abccomputer.data.constant.AppConstants
import com.tungnui.abccomputer.data.preference.AppPreference
import com.tungnui.abccomputer.data.preference.PrefKey
import com.tungnui.abccomputer.data.sqlite.DbHelper
import com.tungnui.abccomputer.data.sqlite.totalCart
import com.tungnui.abccomputer.listener.ListDialogActionListener
import com.tungnui.abccomputer.model.LineItem
import com.tungnui.abccomputer.models.*
import com.tungnui.abccomputer.utils.ActivityUtils
import com.tungnui.abccomputer.utils.DialogUtils
import com.tungnui.abccomputer.utils.formatPrice
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_my_address.*
import kotlinx.android.synthetic.main.content_billing_address.*
import kotlinx.android.synthetic.main.content_shipping_address.*
import kotlinx.android.synthetic.main.fragment_drawer.*

import java.util.ArrayList

/**
 * Created by Nasir on 7/4/17.
 */

class MyAddressActivity : BaseActivity() {
    private lateinit var countryList: ArrayList<ShippingZone>
    private lateinit var stateList: ArrayList<ShippingLocation>
    private var mActivity: Activity? = null
    var shippingZoneService: ShippingZoneService

    init {
        shippingZoneService = ServiceGenerator.createService(ShippingZoneService::class.java)
    }
    private val isValidateInput: Boolean
        get() = if (!edtUserName?.text.toString().isEmpty() && !edtPhoneNumber?.text.toString().isEmpty()
                && !edtAddress?.text.toString().isEmpty()) {
            true
        } else false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initVariable()
        initView()
        loadData()
        initListener()
    }

    private fun initVariable() {
        countryList = ArrayList<ShippingZone>()
        stateList = ArrayList<ShippingLocation>()
        mActivity = this@MyAddressActivity
    }

    private fun initView() {
        setContentView(R.layout.activity_my_address)
        initToolbar()
        enableBackButton()
        setToolbarTitle(getString(R.string.menu_my_address))
    }

    private fun initListener() {
        spinnerCountry?.setOnClickListener {
            showProvinceListDialog(mActivity, getString(R.string.country), countryList, ListDialogActionListener { position ->
                val selectedCountry = countryList[position].name
                tvCountry?.text = selectedCountry
                // load related states
                statePicker(countryList[position].id!!)
            })
        }
        spinnerCountryShip?.setOnClickListener { showProvinceListDialog(mActivity, getString(R.string.country), countryList, ListDialogActionListener {
            position -> tvCountryShip!!.text = countryList[position].name
        }) }

        spinnerState!!.setOnClickListener { showDistrictListDialog(mActivity, getString(R.string.state), stateList, ListDialogActionListener { position -> tvStateName.text = stateList[position].code }) }
        spinnerStateShip!!.setOnClickListener { showDistrictListDialog(mActivity, getString(R.string.state), stateList, ListDialogActionListener { position -> tvStateNameShip.text = stateList[position].code }) }

        chkShipToAddress!!.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                // hide shipping address
                lytShippingAddress.visibility = View.GONE
            } else {
                lytShippingAddress.visibility = View.VISIBLE
            }
        }


        btnUpdateAddress?.setOnClickListener {
            if (isValidateInput) {
                var customer = storeUserInput()
                var currentUser = SettingsMy.getActiveUser()
                var id:Int=0;
                if(currentUser!= null && currentUser.id!= null)
                    id = currentUser.id!!
                editCustomer(id,customer)
            } else {
                Toast.makeText(applicationContext, "Vui lòng điền đầy đủ các trường", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun storeUserInput() :Customer {
        var billing = Billing(
                firstName = edtFirstName.text.toString(),
                address1 = edtAddress.text.toString(),
                city = tvStateName.text.toString(),
                state = tvCountry.text.toString(),
                phone = edtPhoneNumber.text.toString()
        )
        lateinit var shipping:Shipping
        if(chkShipToAddress.isChecked){
            shipping = Shipping(
                    firstName = edtFirstNameShip.text.toString(),
                    address1 = edtAddressShip.text.toString(),
                    city = tvStateNameShip.text.toString(),
                    state = tvCountryShip.text.toString()
            )
        }else{
            shipping = Shipping(
                    firstName = edtFirstName.text.toString(),
                    address1 = edtAddress.text.toString(),
                    city = tvStateName.text.toString(),
                    state = tvCountry.text.toString()
            )
        }


        var customer = Customer(
                firstName = edtFirstName.text.toString(),
                email= edtEmail.text.toString(),
                username = edtUserName.text.toString(),
                billing = billing,
                shipping = shipping
        )
        return customer
    }

    // load ui data from preference
    private fun loadData() {
        val customer = SettingsMy.getActiveUser()
        // preserve value into UI
        customer?.let {
            edtFirstName.setText(it.firstName)
            edtEmail.setText(it.email)
            edtUserName.setText(it.username)
            edtAddress.setText(it.billing?.address1)
            tvStateName.text = it.billing?.city
            tvCountry.text = it.billing?.state
            edtPhoneNumber.setText(it.billing?.phone)
            edtFirstNameShip?.setText(it.shipping?.firstName)
            edtLastNameShip.setText(it.shipping?.lastName)
            edtAddressShip.setText(it.shipping?.address1)
            tvStateNameShip.text = it.shipping?.city
            tvCountryShip.text = it.shipping?.state
        }
        // load countries
        countryPicker()
    }

    private fun countryPicker() {
        val progressDialog = DialogUtils.showProgressDialog(mActivity, getString(R.string.msg_loading), false)
        shippingZoneService.getProvince()
                .subscribeOn((Schedulers.io()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ items ->
                    if (!countryList.isEmpty()) {
                        countryList.clear()
                    }
                    countryList.addAll(items)
                    if (countryList.size > AppConstants.VALUE_ZERO) {
                        tvCountry.text = countryList[AppConstants.INDEX_ZERO].name
                        tvCountryShip.text = countryList[AppConstants.INDEX_ZERO].name

                    }

                    DialogUtils.dismissProgressDialog(progressDialog)
                },
                        { error ->
                            Log.e("MyAddressActivity", error.message)
                            DialogUtils.dismissProgressDialog(progressDialog)
                        })
    }


    private fun statePicker(provinceId: Int) {
        val progressDialog = DialogUtils.showProgressDialog(mActivity, getString(R.string.msg_loading), false)
        shippingZoneService.getDistrict(provinceId)
                .subscribeOn((Schedulers.io()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ items ->
                    if (!stateList.isEmpty()) {
                        stateList.clear()
                    }
                    stateList.addAll(items)
                    if (stateList.size > AppConstants.VALUE_ZERO) {
                        tvStateName.text = stateList[AppConstants.INDEX_ZERO].code
                        tvStateNameShip.text = stateList[AppConstants.INDEX_ZERO].code
                    }
                    DialogUtils.dismissProgressDialog(progressDialog)
                },
                        { error ->
                            Log.e("DrawerFragment", error.message)
                            DialogUtils.dismissProgressDialog(progressDialog)
                        })
    }


    private fun editCustomer(id:Int,customer: Customer) {
        // start loading progress dialog
        val progressDialog = DialogUtils.showProgressDialog(mActivity, getString(R.string.msg_loading), false)
        var customerService = ServiceGenerator.createService(CustomerServices::class.java)
        customerService.update(id, customer)
                .subscribeOn((Schedulers.io()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ data ->
                    Toast.makeText(applicationContext, "Cập nhật thành công.", Toast.LENGTH_SHORT).show()
                    SettingsMy.setActiveUser(data)
                    val returnIntent = Intent()
                    setResult(Activity.RESULT_OK, returnIntent)
                    finish()
                },
                        { error ->
                            Log.e("Address",error.message)
                            Toast.makeText(applicationContext, "Cập nhật không thành công!", Toast.LENGTH_SHORT).show()
                        })
        DialogUtils.dismissProgressDialog(progressDialog)
    }

     override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    fun showProvinceListDialog(activity: Activity?, title: String?, list: ArrayList<ShippingZone>?, listDialogActionListener: ListDialogActionListener?) {
        val alertDialogBuilder = AlertDialog.Builder(activity)
        if (title != null) {
            alertDialogBuilder.setTitle(title)
        }

        val countryArr = arrayOfNulls<String>(list!!.size)
        for (i in list.indices) {
            countryArr[i] = list[i].name
        }

        alertDialogBuilder.setItems(countryArr) { dialog, which ->
            listDialogActionListener?.onItemSelected(which)
            dialog.dismiss()
        }
        alertDialogBuilder.show()
    }
    fun showDistrictListDialog(activity: Activity?, title: String?, list: ArrayList<ShippingLocation>?, listDialogActionListener: ListDialogActionListener?) {
        val alertDialogBuilder = AlertDialog.Builder(activity)
        if (title != null) {
            alertDialogBuilder.setTitle(title)
        }

        val countryArr = arrayOfNulls<String>(list!!.size)
        for (i in list.indices) {
            countryArr[i] = list[i].code
        }

        alertDialogBuilder.setItems(countryArr) { dialog, which ->
            listDialogActionListener?.onItemSelected(which)
            dialog.dismiss()
        }
        alertDialogBuilder.show()
    }
}
