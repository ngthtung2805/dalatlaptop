package com.tungnui.abccomputer.activity

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast

import com.tungnui.abccomputer.data.constant.AppConstants
import com.tungnui.abccomputer.R
import com.tungnui.abccomputer.SettingsMy
import com.tungnui.abccomputer.adapter.OrderListAdapter
import com.tungnui.abccomputer.adapter.OrderListInterface
import com.tungnui.abccomputer.api.OrderServices
import com.tungnui.abccomputer.api.ServiceGenerator
import com.tungnui.abccomputer.data.preference.AppPreference
import com.tungnui.abccomputer.data.preference.PrefKey
import com.tungnui.abccomputer.listener.OnItemClickListener
import com.tungnui.abccomputer.model.LineItem
import com.tungnui.abccomputer.model.OrderItem
import com.tungnui.abccomputer.models.Order
import com.tungnui.abccomputer.network.helper.RequestCancelOrder
import com.tungnui.abccomputer.network.helper.RequestOrderItemList
import com.tungnui.abccomputer.network.helper.RequestOrderList
import com.tungnui.abccomputer.network.http.ResponseListener
import com.tungnui.abccomputer.utils.DialogUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_order_list.*
import kotlinx.android.synthetic.main.view_common_loader.*

import java.util.ArrayList


class OrderListActivity : BaseActivity() {
    private var orderService :OrderServices
    private var orderList: ArrayList<OrderItem>
    init {
        orderList = ArrayList()
        orderService = ServiceGenerator.createService(OrderServices::class.java)
    }
    private lateinit var orderListAdapter: OrderListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        loadOrderList()
    }

     private fun initView() {
        setContentView(R.layout.activity_order_list)
        initToolbar()
        enableBackButton()
         setToolbarTitle("Danh sách đơn hàng")
        initLoader()
        // init RecyclerView
        rvOrderList.setHasFixedSize(true)

        rvOrderList.layoutManager = LinearLayoutManager(this)
        orderListAdapter = OrderListAdapter(object: OrderListInterface{
            override fun onItemSelected(order: Order) {
                val intent = Intent(this@OrderListActivity, OrderDetailActivity::class.java)
                intent.putExtra(AppConstants.KEY_ORDER_DETAIL, order.id!!)
                startActivity(intent)
            }
            override fun onOrderCancel(order: Order) {
                cancelOrderDialog(order.id!!)
            }
        })
        rvOrderList.adapter = orderListAdapter
    }


    private fun loadOrderList() {
        val user = SettingsMy.getActiveUser()
        if (user!=null) {
            user.id?.let {
                showLoader()
             var disable=   orderService.getOrderByCustomer(it)
                        .subscribeOn((Schedulers.io()))
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ items ->
                            orderListAdapter.refreshItems(items)
                            hideLoader()
                           },
                                { error ->
                                    Log.e("OrderList", error.message)
                                    showEmptyView()
                                    info_text.text = getString(R.string.empty)
                                })
            }
        } else {
            showEmptyView()
            info_text.text = getString(R.string.register_to_show_order)
        }

    }



    private fun cancelOrderDialog(id: Int) {
        DialogUtils.showDialogPrompt(this, null, getString(R.string.cancel_order_item), getString(R.string.dialog_btn_yes), getString(R.string.dialog_btn_no), true) {
            // start progress dialog
            val progressDialog = DialogUtils.showProgressDialog(this, getString(R.string.msg_loading), false)
            var order = Order(status = "cancelled")
            orderService.update(id,order)
                    .subscribeOn((Schedulers.io()))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ item ->
                        DialogUtils.dismissProgressDialog(progressDialog)
                        if (item != null) {
                                 loadOrderList()

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
