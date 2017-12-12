package com.tungnui.abccomputer.activity

import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.tungnui.abccomputer.R
import com.tungnui.abccomputer.adapter.OrderDetailAdapter
import com.tungnui.abccomputer.adapter.RecyclerDividerDecorator
import com.tungnui.abccomputer.api.OrderServices
import com.tungnui.abccomputer.api.ServiceGenerator
import com.tungnui.abccomputer.data.constant.AppConstants
import com.tungnui.abccomputer.model.OrderItem
import com.tungnui.abccomputer.models.Order
import com.tungnui.abccomputer.utils.DialogUtils
import com.tungnui.abccomputer.utils.formatPrice
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_order_detail.*
import kotlinx.android.synthetic.main.view_common_loader.*
import java.util.ArrayList

class OrderDetailActivity : BaseActivity() {
    private var orderService : OrderServices
    private var orderList: ArrayList<OrderItem>
    init {
        orderList = ArrayList()
        orderService = ServiceGenerator.createService(OrderServices::class.java)
    }
    private lateinit var productAdapter: OrderDetailAdapter
    private var orderId:Int=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = intent
        if (intent.hasExtra(AppConstants.KEY_ORDER_DETAIL)) {
            orderId = intent.getIntExtra(AppConstants.KEY_ORDER_DETAIL,0)
        }
        initView()
        initToolbar()
        initListener()
        loadData()
    }

   

    private fun initView() {
        setContentView(R.layout.activity_order_detail)
        initToolbar()
        setToolbarTitle("Chi tiết đơn hàng")
        enableBackButton()
        order_detail_recycler.addItemDecoration(RecyclerDividerDecorator(this))
        order_detail_recycler.itemAnimator = DefaultItemAnimator()
        order_detail_recycler.setHasFixedSize(true)
        order_detail_recycler.layoutManager = LinearLayoutManager(this)
        productAdapter = OrderDetailAdapter()
        order_detail_recycler.adapter = productAdapter

    }
    private fun loadData(){
        val progressDialog = DialogUtils.showProgressDialog(this@OrderDetailActivity, getString(R.string.msg_loading), false)
        Log.e("OrderList2",orderId.toString())
        var disposable =   orderService.single(orderId)
                .subscribeOn((Schedulers.io()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ item ->
                    item?.let{
                        productAdapter.refreshItems(item.lineItems!!)
                        tvTempPrice.text =item.total?.formatPrice()
                        tvTotalPrice.text=item.total?.formatPrice()
                        tvDiscount.text = item.shippingTotal?.formatPrice()
                        tvPaymentMethod.text= item.paymentMethodTitle
                        if(item.shippingTotal?.toDouble() == 0.0){
                            tvShippingMethod.text = "Nhận tại cửa hàng"
                            tvShippingFee.text="Miễn phí"
                        }
                        else{
                            tvShippingMethod.text = "Giao hàng tận nhà"
                            tvShippingFee.text=item.shippingTotal
                        }
                        if(item.status == "pending"){
                            footerView.visibility = View.VISIBLE
                        }else{
                            footerView.visibility = View.GONE
                        }
                    }
                    DialogUtils.dismissProgressDialog(progressDialog)
                },
                        { error ->
                            Log.e("OrderList",error.message)
                            DialogUtils.dismissProgressDialog(progressDialog)
                        })

    }


    private fun initListener() {
        btnCancelOrder.setOnClickListener { cancelOrder() }
    }


    private fun cancelOrder() {
        DialogUtils.showDialogPrompt(this, null, getString(R.string.cancel_order_item), getString(R.string.dialog_btn_yes), getString(R.string.dialog_btn_no), true) {
            // start progress dialog
            val progressDialog = DialogUtils.showProgressDialog(this, getString(R.string.msg_loading), false)
            var temp = Order(status = "cancelled")
            orderService.update(orderId,temp)
                    .subscribeOn((Schedulers.io()))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ item ->
                        DialogUtils.dismissProgressDialog(progressDialog)
                        if (item != null) {
                            loadData()

                        }
                    },
                            { error ->
                                showEmptyView()
                                info_text.text = getString(R.string.empty)
                                Toast.makeText(applicationContext, getString(R.string.failed), Toast.LENGTH_SHORT).show()
                            })
        }
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
}