package com.tungnui.dalatlaptop.ux.Order

import android.app.ProgressDialog
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tungnui.dalatlaptop.R
import com.tungnui.dalatlaptop.SettingsMy
import com.tungnui.dalatlaptop.api.OrderService
import com.tungnui.dalatlaptop.api.ServiceGenerator
import com.tungnui.dalatlaptop.libraryhelper.RecyclerDividerDecorator
import com.tungnui.dalatlaptop.libraryhelper.Utils
import com.tungnui.dalatlaptop.models.*
import com.tungnui.dalatlaptop.utils.*
import com.tungnui.dalatlaptop.ux.adapters.OrderCreateFinishAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_order_create_finish.*
import org.jetbrains.anko.support.v4.toast


class OrderCreateFinishFragment : Fragment() {
    private var mCompositeDisposable: CompositeDisposable
    val orderService: OrderService

    init {
        mCompositeDisposable = CompositeDisposable()
        orderService = ServiceGenerator.createService(OrderService::class.java)
    }

    private lateinit var progressDialog: ProgressDialog
    private var paymentMethod:String= ""
    private var note:String=""
    private lateinit var orderFinishAdapter:OrderCreateFinishAdapter
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_order_create_finish, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressDialog = Utils.generateProgressDialog(activity, false)
        (activity as OrderActivity).title = "Xác nhận"
        val startBundle = arguments
        if (startBundle != null) {
            note = startBundle.getString(NOTE, "")
            paymentMethod = startBundle.getString(PAYMENT_METHOD, "")
        }
        prepareOrderRecycler()
        refreshLayout()
        order_create_step3_order.setOnClickListener{
            postOrder(getOrder())
        }
    }

    private  fun getOrder():Order{
        val order= Order()
        val customer = SettingsMy.getActiveUser()
        val carts = context.cartHelper.getAll();
        order.customerId = customer?.id
        order.customerNote = note
        order.billing = customer?.billing
        order.paymentMethod = paymentMethod
        order.paymentMethodTitle = if(paymentMethod == "cod") "Thanh toán khi nhận hàng" else "Thanh toán qua thẻ ATM nội địa/Internet Banking"
        val lineItems = ArrayList<LineItem>()
        for(item in carts){
            val lineItem = LineItem()
            lineItem.productId = item.productId
            lineItem.quantity = item.quantity
            lineItems.add(lineItem)
        }
        order.lineItems = lineItems
        return order
    }
    private  fun postOrder(order:Order){
        progressDialog.show()
        val disposable = orderService.create(order)
                .subscribeOn((Schedulers.io()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    context.cartHelper.clearAll()
                    progressDialog.cancel()
                    val dialog = OrderCreateSuccessDialogFragment()
                    dialog.show(fragmentManager, OrderCreateSuccessDialogFragment::class.java.simpleName)
                },
                        { error ->
                            toast("Lỗi cập nhật!")
                            progressDialog.cancel()
                        })

        mCompositeDisposable.add(disposable)
    }
    fun refreshLayout(){
        val user = SettingsMy.getActiveUser()
        user?.let {
            val billing = it.billing
            order_create_finish_name.text = it.firstName
            order_create_finish_address.text = billing?.address1
            order_create_finish_phone.text= billing?.phone
            order_create_finish_payment_method.text = when(paymentMethod){
                "bacs"->"Thẻ ATM nội địa/Internet Banking"
                else->"Thanh toán tiền mặt khi nhận hàng"
            }
            orderFinishAdapter.refreshItems(context.cartHelper.getAll())
            order_create_finish_price.text= context.cartHelper.total().toString().formatPrice()
        }
    }
    private fun prepareOrderRecycler() {
        order_create_finish_recycler.addItemDecoration(RecyclerDividerDecorator(activity))
        order_create_finish_recycler.itemAnimator = DefaultItemAnimator()
        order_create_finish_recycler.setHasFixedSize(true)
        order_create_finish_recycler.layoutManager = LinearLayoutManager(activity)
        orderFinishAdapter = OrderCreateFinishAdapter()
        order_create_finish_recycler.adapter = orderFinishAdapter
    }



    override fun onStop() {
        super.onStop()
        progressDialog.cancel()
    }
    companion object {
        private val PAYMENT_METHOD = "payment_method"
        private val NOTE = "note"
        fun newInstance(note:String,payment: String): OrderCreateFinishFragment {
            val args = Bundle()
            args.putString(PAYMENT_METHOD, payment)
            args.putString(NOTE, note)
            val fragment = OrderCreateFinishFragment()
            fragment.arguments = args
            return fragment
        }
    }

}