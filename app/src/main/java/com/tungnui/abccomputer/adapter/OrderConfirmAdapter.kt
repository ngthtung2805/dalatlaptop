package com.tungnui.abccomputer.adapters

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.tungnui.abccomputer.R
import com.tungnui.abccomputer.models.Cart
import com.tungnui.abccomputer.utils.formatPrice
import com.tungnui.abccomputer.utils.inflate
import com.tungnui.abccomputer.utils.loadGlideImg
import kotlinx.android.synthetic.main.list_item_order_finish.view.*
import java.util.ArrayList

/**
 * Adapter handling list of cart items.
 */
class OrderConfirmAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val cartItems = ArrayList<Cart>()
    override fun getItemCount(): Int {
        return cartItems.size
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            ViewHolderProduct(parent.inflate(R.layout.list_item_order_finish))


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolderProduct).blind(cartItems[position])
    }

    fun refreshItems(carts: List<Cart>) {
            cartItems.clear()
            cartItems.addAll(carts)
            notifyDataSetChanged()
    }

    fun cleatCart() {
        cartItems.clear()
        notifyDataSetChanged()
    }


    class ViewHolderProduct(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun blind(cart: Cart) = with(itemView){
            order_finish_item_image.loadGlideImg(cart.image);
            order_finish_item_name.text = cart.productName
            order_finish_item_price.text = cart.price.toString().formatPrice()
            order_finish_item_quantity.text = "${cart.quantity}"
        }
    }


}