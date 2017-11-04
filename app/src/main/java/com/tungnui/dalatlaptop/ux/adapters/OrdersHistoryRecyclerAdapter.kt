package com.tungnui.dalatlaptop.ux.adapters

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup

import java.util.ArrayList

import com.tungnui.dalatlaptop.R
import com.tungnui.dalatlaptop.models.Order
import com.tungnui.dalatlaptop.utils.formatPrice
import com.tungnui.dalatlaptop.utils.inflate
import kotlinx.android.synthetic.main.list_item_orders_history.view.*
import timber.log.Timber

class OrdersHistoryRecyclerAdapter( val listener: (Order) -> Unit) : RecyclerView.Adapter<OrdersHistoryRecyclerAdapter.ViewHolder>() {
    private val orders = ArrayList<Order>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.inflate(R.layout.list_item_orders_history))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.blind(orders[position],listener)
    }

    private fun getOrderItem(position: Int): Order {
        return orders[position]
    }

    override fun getItemCount(): Int {
        return orders.size
    }

    fun addOrders(orderList: List<Order>?) {
        if (orderList != null && !orderList.isEmpty()) {
            orders.addAll(orderList)
            notifyDataSetChanged()
        } else {
            Timber.e("Adding empty orders list.")
        }
    }
    fun clear() {
        orders.clear()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun blind(order:Order,listener: (Order) -> Unit)= with(itemView){
            order_history_item_id.text = order.id.toString()
            order_history_item_dateCreated.text = order.dateCreated
            order_history_item_totalPrice.text = order.total?.formatPrice()
            setOnClickListener { listener }
        }
     }
}
