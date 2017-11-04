package com.tungnui.dalatlaptop.ux.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.squareup.picasso.Picasso

import com.tungnui.dalatlaptop.R
import com.tungnui.dalatlaptop.models.Order
import com.tungnui.dalatlaptop.views.ResizableImageView
import timber.log.Timber

/**
 * Adapter handling list of order items.
 */
class OrderRecyclerAdapter
/**
 * Creates an adapter that handles a list of order items.
 *
 * @param context activity context.
 */
(private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var layoutInflater: LayoutInflater? = null
    private var order: Order? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (layoutInflater == null)
            layoutInflater = LayoutInflater.from(parent.context)
        if (viewType == TYPE_ITEM_ORDER) {
            val view = layoutInflater!!.inflate(R.layout.list_item_order_product_image, parent, false)
            return ViewHolderOrderProduct(view)
        } else {
            val view = layoutInflater!!.inflate(R.layout.list_item_order_header, parent, false)
            return ViewHolderHeader(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolderOrderProduct) {

          /*  Picasso.with(context).load(order!!.products[position - 1].variant.mainImage)
                    .fit().centerInside()
                    .placeholder(R.drawable.placeholder_loading)
                    .error(R.drawable.placeholder_error)
                    .into(holder.productImage)*/

        } else if (holder is ViewHolderHeader) {

            /*holder.orderId.text = order!!.remoteId
            holder.orderName.text = order!!.name
            holder.orderDateCreated.text = Utils.parseDate(order!!.dateCreated)
            holder.orderTotal.text = order!!.totalFormatted
            holder.orderShippingMethod.text = order!!.shippingName
            holder.orderShippingPrice.text = order!!.shippingPriceFormatted*/
        } else {
            Timber.e(RuntimeException(), "Unknown holder type.")
        }
    }

    // This method returns the number of items present in the list
    override fun getItemCount(): Int {
        return if (order != null) {
           /* if (order!!.products != null && order!!.products.size > 0) {
                order!!.products.size + 1 // the number of items in the list, +1 for header view.
            } else {
                1
            }*/
            0
        } else {
            0
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0)
            TYPE_HEADER
        else
            TYPE_ITEM_ORDER
    }


    /**
     * Add item to list, and notify dataSet changed.
     *
     * @param order item to add.
     */
    fun addOrder(order: Order?) {
        if (order != null) {
            this.order = order
            notifyDataSetChanged()
        } else {
            Timber.e("Setting null order object.")
        }
    }

    // Provide a reference to the views for each data item
    class ViewHolderOrderProduct(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var productImage: ResizableImageView

        init {
            productImage = itemView.findViewById<View>(R.id.list_item_product_images_view) as ResizableImageView
        }
    }

    class ViewHolderHeader(headerView: View) : RecyclerView.ViewHolder(headerView) {

        var orderId: TextView
        var orderName: TextView
        var orderDateCreated: TextView
        var orderTotal: TextView
        var orderShippingMethod: TextView
        var orderShippingPrice: TextView

        init {
            orderId = headerView.findViewById<View>(R.id.list_item_order_header_id) as TextView
            orderName = headerView.findViewById<View>(R.id.list_item_order_header_name) as TextView
            orderDateCreated = headerView.findViewById<View>(R.id.list_item_order_header_dateCreated) as TextView
            orderTotal = headerView.findViewById<View>(R.id.list_item_order_header_total) as TextView
            orderShippingMethod = headerView.findViewById<View>(R.id.list_item_order_header_shipping_method) as TextView
            orderShippingPrice = headerView.findViewById<View>(R.id.list_item_order_header_shipping_price) as TextView
        }
    }

    companion object {

        private val TYPE_HEADER = 0
        private val TYPE_ITEM_ORDER = 1
    }
}