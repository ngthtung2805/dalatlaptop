package com.tungnui.abccomputer.adapter


import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.tungnui.abccomputer.R
import com.tungnui.abccomputer.data.constant.AppConstants
import com.tungnui.abccomputer.models.Order
import com.tungnui.abccomputer.utils.convertStatusToVN
import com.tungnui.abccomputer.utils.formatPrice
import com.tungnui.abccomputer.utils.inflate
import com.tungnui.abccomputer.utils.parseDate
import kotlinx.android.synthetic.main.item_order_list.view.*

import java.util.ArrayList

class OrderListAdapter(val orderListInterface: OrderListInterface) : RecyclerView.Adapter<OrderListAdapter.ViewHolder>() {
    private val dataList: ArrayList<Order> = ArrayList<Order>()
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun blind(item:Order, orderListInterface: OrderListInterface)=with(itemView) {
            tvOrderNumber.text = context.resources.getString(R.string.order_id) + item.id
            tvPrice.text = item.total.toString().formatPrice()
            tvOrderDate.text = item.dateCreated?.parseDate()
            tvOrderStatus.text = context.resources.getString(R.string.status) + item.status?.convertStatusToVN()
            when (item.status) {
                AppConstants.ORDER_STATUS_PENDING -> {
                    cancelOrder.visibility = View.VISIBLE
                    tvOrderStatus.setTextColor(ContextCompat.getColor(context, R.color.colorPending))
                }
                AppConstants.ORDER_STATUS_COMPLETED -> {
                    cancelOrder.visibility = View.INVISIBLE
                    tvOrderStatus.setTextColor(ContextCompat.getColor(context, R.color.colorCompleted))
                }
                else -> {
                    cancelOrder.visibility = View.INVISIBLE
                    tvOrderStatus.setTextColor(ContextCompat.getColor(context, R.color.colorProcessing))

                }
            }
            setOnClickListener{orderListInterface.onItemSelected(item)}
            cancelOrder.setOnClickListener { orderListInterface.onOrderCancel(item) }

        }
      }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.inflate(R.layout.item_order_list))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.blind(dataList[position],orderListInterface)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun getItemViewType(position: Int): Int {
        return 0
    }
    fun refreshItems(orders:List<Order>){
        dataList.clear()
        dataList.addAll(orders)
        notifyDataSetChanged()
    }
}
interface OrderListInterface{
    fun onItemSelected(order:Order)
    fun onOrderCancel(order:Order)
}
