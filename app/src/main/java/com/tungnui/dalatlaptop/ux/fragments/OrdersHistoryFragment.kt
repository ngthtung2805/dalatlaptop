package com.tungnui.dalatlaptop.ux.fragments

import android.app.ProgressDialog
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.tungnui.dalatlaptop.R
import com.tungnui.dalatlaptop.SettingsMy
import com.tungnui.dalatlaptop.api.EndPoints
import com.tungnui.dalatlaptop.api.ServiceGenerator
import com.tungnui.dalatlaptop.entities.Metadata
import com.tungnui.dalatlaptop.models.Order
import com.tungnui.dalatlaptop.utils.EndlessRecyclerScrollListener
import com.tungnui.dalatlaptop.utils.RecyclerMarginDecorator
import com.tungnui.dalatlaptop.utils.Utils
import com.tungnui.dalatlaptop.utils.getNextUrl
import com.tungnui.dalatlaptop.ux.MainActivity
import com.tungnui.dalatlaptop.ux.adapters.OrdersHistoryRecyclerAdapter
import com.tungnui.dalatlaptop.ux.dialogs.LoginExpiredDialogFragment
import com.tungnui.dalatlaptop.woocommerceapi.OrderServices
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_orders_history.*
import org.jetbrains.anko.support.v4.longToast
import org.jetbrains.anko.support.v4.toast
import timber.log.Timber

class OrdersHistoryFragment : Fragment() {
    private var mCompositeDisposable: CompositeDisposable
    val orderService: OrderServices
    init {
        mCompositeDisposable = CompositeDisposable()
        orderService = ServiceGenerator.createService(OrderServices::class.java)
    }
    private var progressDialog: ProgressDialog? = null

    /**
     * Request metadata containing urls for endlessScroll.
     */
    private val ordersMetadata: Metadata? = null
    private var order: Order? = null
    private var orderNextLink:String? = null

    private lateinit var ordersHistoryRecyclerAdapter: OrdersHistoryRecyclerAdapter
    private var endlessRecyclerScrollListener: EndlessRecyclerScrollListener? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_orders_history, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MainActivity.setActionBarTitle(getString(R.string.Order_history))
        progressDialog = Utils.generateProgressDialog(activity, false)
        prepareOrdersHistoryRecycler()
        loadOrders(null)
    }



    /**
     * Prepare content recycler. Create custom adapter and endless scroll.
     *
     * @param view root fragment view.
     */
    private fun prepareOrdersHistoryRecycler() {
        ordersHistoryRecyclerAdapter = OrdersHistoryRecyclerAdapter{
            val activity = activity
            (activity as? MainActivity)?.onOrderSelected(order)
        }
        orders_history_recycler.adapter = ordersHistoryRecyclerAdapter
        val layoutManager = LinearLayoutManager(orders_history_recycler.context)
        orders_history_recycler.layoutManager = layoutManager
        orders_history_recycler.itemAnimator = DefaultItemAnimator()
        orders_history_recycler.setHasFixedSize(true)
        orders_history_recycler.addItemDecoration(RecyclerMarginDecorator(resources.getDimensionPixelSize(R.dimen.base_margin)))

        endlessRecyclerScrollListener = object : EndlessRecyclerScrollListener(layoutManager) {
            override fun onLoadMore(currentPage: Int) {
                if (orderNextLink != null ) {
                    loadOrders(orderNextLink)
                } else {
                    Timber.d("CustomLoadMoreDataFromApi NO MORE DATA")
                }
            }
        }
        orders_history_recycler.addOnScrollListener(endlessRecyclerScrollListener)
    }

    private fun loadOrders(url: String?) {
        var user = SettingsMy.getActiveUser();
        var url:String? = url
        if (user != null) {
            progressDialog?.show();
            if (url == null) {
                ordersHistoryRecyclerAdapter.clear();
                url = EndPoints.ORDERS +"?customer=${user.id}"
            }
            var disposable = orderService.getAll(url)
                        .subscribeOn((Schedulers.io()))
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ response ->
                            response.body()?.let { ordersHistoryRecyclerAdapter.addOrders(it) }
                            orderNextLink = response.headers()["Link"]?.getNextUrl()
                            if (ordersHistoryRecyclerAdapter.getItemCount() > 0) {
                                order_history_empty.setVisibility(View.GONE);
                                order_history_content.setVisibility(View.VISIBLE);
                            } else {
                                order_history_empty.setVisibility(View.VISIBLE);
                                order_history_content.setVisibility(View.GONE);
                            }
                            progressDialog?.cancel()
                        },
                                { error ->
                                    progressDialog?.cancel()
                                })

             mCompositeDisposable.add(disposable)
                   } else {
            var loginExpiredDialogFragment = LoginExpiredDialogFragment();
            loginExpiredDialogFragment.show(getFragmentManager(), "loginExpiredDialogFragment");
        }
    }

    override fun onStop() {
        if (progressDialog != null) {
            // Hide progress dialog if exist.
            if (progressDialog!!.isShowing && endlessRecyclerScrollListener != null) {
                // Fragment stopped during loading data. Allow new loading on return.
                endlessRecyclerScrollListener!!.resetLoading()
            }
            progressDialog!!.cancel()
        }
        super.onStop()
    }

    override fun onDestroyView() {
        orders_history_recycler.clearOnScrollListeners()
        super.onDestroyView()
    }
}
