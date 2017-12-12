package com.tungnui.abccomputer.activity

import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast

import com.tungnui.abccomputer.data.constant.AppConstants
import com.tungnui.abccomputer.R
import com.tungnui.abccomputer.SettingsMy
import com.tungnui.abccomputer.adapter.RecyclerDividerDecorator
import com.tungnui.abccomputer.adapters.OrderConfirmAdapter
import com.tungnui.abccomputer.api.CouponServices
import com.tungnui.abccomputer.api.OrderServices
import com.tungnui.abccomputer.api.ServiceGenerator
import com.tungnui.abccomputer.data.sqlite.*
import com.tungnui.abccomputer.listeners.OnSingleClickListener
import com.tungnui.abccomputer.models.LineItem
import com.tungnui.abccomputer.models.Order
import com.tungnui.abccomputer.dialog.DiscountDialogFragment
import com.tungnui.abccomputer.models.Coupon
import com.tungnui.abccomputer.utils.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_order_confirm.*
import kotlinx.android.synthetic.main.content_notification_details.*
import java.util.*

class OrderConfirmActivity : BaseActivity() {
    private var mCompositeDisposable: CompositeDisposable
    val orderService: OrderServices
    val couponService:CouponServices
    init {
        mCompositeDisposable = CompositeDisposable()
        orderService = ServiceGenerator.createService(OrderServices::class.java)
        couponService = ServiceGenerator.createService(CouponServices::class.java)
    }
    private var shippingMethod: String = ""
    private var paymentMethod: String = "cod"
    private var paymentMethodTitle: String = ""
    private lateinit var orderProductConfirmAdapter: OrderConfirmAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initVariable()
        initView()
        initToolbar()
        initListener()
        loadData()
    }

    private fun initVariable() {
        val intent = intent
        if (intent.hasExtra(AppConstants.SHIPPING_METHOD)) {
            shippingMethod = intent.getStringExtra(AppConstants.SHIPPING_METHOD)
        }
        if(intent.hasExtra(AppConstants.PAYMENT_METHOD)){
            paymentMethod = intent.getStringExtra(AppConstants.PAYMENT_METHOD)
        }
       paymentMethodTitle= when(paymentMethod){
            getString(R.string.key_bank)-> getString(R.string.title_bank)
            else-> getString(R.string.title_cash_on_delivery)
        }

    }

    private fun initView() {
        setContentView(R.layout.activity_order_confirm)
        initToolbar()
        setToolbarTitle(getString(R.string.finalize_order))
        enableBackButton()
        order_create_finish_recycler.addItemDecoration(RecyclerDividerDecorator(this))
        order_create_finish_recycler.itemAnimator = DefaultItemAnimator()
        order_create_finish_recycler.setHasFixedSize(true)
        order_create_finish_recycler.layoutManager = LinearLayoutManager(this)
        orderProductConfirmAdapter = OrderConfirmAdapter()
        order_create_finish_recycler.adapter = orderProductConfirmAdapter

    }
    private fun loadData(){
        orderProductConfirmAdapter.refreshItems(applicationContext.DbHelper.getAllCart())
        tvTempPrice.text =applicationContext.DbHelper.totalCart().toString().formatPrice()
        tvShippingFee.text ="Miễn phí"
        tvDiscount.text = applicationContext.DbHelper.totalDiscount().toString().formatPrice()
        tvTotalPrice.text=applicationContext.DbHelper.totalAfterDiscountCart().toString().formatPrice()
        tvPaymentMethod.text= paymentMethodTitle
        tvShippingMethod.text = shippingMethod
    }


    private fun initListener() {
        btnPlaceOrder.setOnClickListener { placeOrder() }
        tvApplyCoupon.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(view: View) {
                val discountDialog = DiscountDialogFragment.newInstance(object : DiscountDialogFragment.RequestListener {
                    override fun requestSuccess(couponCode: String) {
                       getCouponCode(couponCode)
                    }
                })
                discountDialog.show(supportFragmentManager, DiscountDialogFragment::class.java.simpleName)
            }
        })
    }
    private fun getCouponCode(couponCode:String){
        val progressDialog = DialogUtils.showProgressDialog(this, getString(R.string.msg_loading), false)
        var disposable = couponService.getCouponByCode(couponCode)
                .subscribeOn((Schedulers.io()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                     DialogUtils.dismissProgressDialog(progressDialog)
                    //Coupon tồn tài
                    var coupon= response[0]
                    var message =processCoupon(coupon)
                    Toast.makeText(applicationContext,message,Toast.LENGTH_LONG).show()
                    loadData()
                },
                        { error ->
                            //Coupon không tồn tại
                            Toast.makeText(applicationContext,"Mã giảm giá không tồn tại", Toast.LENGTH_SHORT).show()
                            DialogUtils.dismissProgressDialog(progressDialog)
                            Log.i("OrderConfirm", error.message);
                        })
        mCompositeDisposable.add(disposable)
    }

    private fun processCoupon(coupon: Coupon):String {
        var carts = applicationContext.DbHelper.getAllCart()
        var totalCart = applicationContext.DbHelper.totalCart()
        //Kiểm tra hạn sử dụng
        coupon.dateExpires?.let{
            val date = it.convertToDate()
            val currentDate = Date(System.currentTimeMillis());
            if(date!= null && date >date){
                 return "Mã giảm giá đã hết hạn sử dụng"
            }
        }
        coupon.minimumAmount?.let{
            var minimun = it.toDouble()
            if(totalCart < minimun)
                return "Mã giảm giá này chỉ áp dụng cho đơn hàng từ ${it.formatPrice()} trở lên"
        }
        ifNotNull(coupon.amount,coupon.discountType){
            amount, discountType->
            var amount = amount.toDouble()
            when(discountType){
                "percent"->{
                    for (c in carts){
                        c.discount = ((c.price!! * c.quantity!!)*amount/100).toInt()
                        c.beforeDiscount = c.price
                        c.afterDiscount = c.beforeDiscount!! - c.discount!!
                    }

                }

                "fixed_cart"-> {
                    var totalItem = applicationContext.DbHelper.totalCartItem()
                    var discoutPerItem = amount / totalItem
                    var i: Int = 0
                    var tempTotalDiscount = 0
                    for (c in carts) {
                        i++

                        c.discount = (discoutPerItem * c.quantity!!).toInt()
                        tempTotalDiscount += c.discount!!
                        c.beforeDiscount = c.price
                        c.afterDiscount = c.beforeDiscount!! - c.discount!!
                        if (i == carts.count()) {
                            c.discount = (amount - (tempTotalDiscount)).toInt()
                            c.beforeDiscount = c.price
                            c.afterDiscount = c.beforeDiscount!! - c.discount!!
                        }
                    }
                }
            }

            //Update cart
            applicationContext.DbHelper.clearAllCart()
            for (item in carts)
                applicationContext.DbHelper.addToCart(item)

        }
        return ""
    }

    private fun placeOrder() {
        var customer = SettingsMy.getActiveUser()
        val order= Order()
        val carts = applicationContext.DbHelper.getAllCart()
        order.customerId = customer?.id
        order.billing = customer?.billing
        order.paymentMethod = paymentMethod
        order.paymentMethodTitle = paymentMethodTitle
        val lineItems = ArrayList<LineItem>()
            for(item in carts){
                val lineItem = LineItem()
                lineItem.productId = item.productId
                lineItem.quantity = item.quantity
                lineItem.subtotal = item.beforeDiscount.toString()
                lineItem.total = item.afterDiscount.toString()
                lineItems.add(lineItem)
            }
            order.lineItems = lineItems
            Log.e("PlaceOrder2", Utils.getGsonParser().toJson(order))
            val progressDialog = DialogUtils.showProgressDialog(this@OrderConfirmActivity, getString(R.string.msg_loading), false)
                 val disposable = orderService.create(order)
                .subscribeOn((Schedulers.io()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    applicationContext.DbHelper.clearAllCart()
                     // postOderNote(order, lineItems)
                        ActivityUtils.instance.invokeOrder(this@OrderConfirmActivity, response.id.toString())
                },
                        { error ->
                            Log.e("PlaceOrder", error.message);
                            ActivityUtils.instance.invokeActivity(this@OrderConfirmActivity, MainActivity::class.java,true)
                            Toast.makeText(this@OrderConfirmActivity, getString(R.string.order_failed), Toast.LENGTH_LONG).show()
                        })
        DialogUtils.dismissProgressDialog(progressDialog)
       }

    // post product attribute as a note
   /* private fun postOderNote(order: OrderItem, arrayList: ArrayList<LineItem>) {

        var note = "Product attributes: "
        for (lineItem in arrayList) {
            note += lineItem.productName + "-" + lineItem.productAttribute + " | "
        }

        val requestOrderNote = RequestOrderNote(mContext, order.orderId)
        requestOrderNote.buildParams(note)
        requestOrderNote.execute()
    }*/

   /* override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                transactionIDInput!!.setText(data.getStringExtra("stripe_token"))
            } else if (resultCode == Activity.RESULT_CANCELED) {

            }
        }
    }*/

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
