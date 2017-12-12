package com.tungnui.abccomputer.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.tungnui.abccomputer.R
import com.tungnui.abccomputer.models.Cart
import com.tungnui.abccomputer.models.LineItem
import com.tungnui.abccomputer.utils.formatPrice
import com.tungnui.abccomputer.utils.inflate
import com.tungnui.abccomputer.utils.loadGlideImg
import kotlinx.android.synthetic.main.list_item_order_finish.view.*
import java.util.ArrayList

class OrderDetailAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val items = ArrayList<LineItem>()
    override fun getItemCount(): Int {
        return items.size
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            ViewHolderProduct(parent.inflate(R.layout.item_order_detail))


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolderProduct).blind(items[position])
    }

    fun refreshItems(item: List<LineItem>) {
            items.clear()
            items.addAll(item)
            notifyDataSetChanged()
    }

    fun cleatCart() {
        items.clear()
        notifyDataSetChanged()
    }


    class ViewHolderProduct(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun blind(item: LineItem) = with(itemView){
            order_finish_item_name.text = item.name
            order_finish_item_price.text = item.price.toString().formatPrice()
            order_finish_item_quantity.text = "${item.quantity}"
        }
    }


}