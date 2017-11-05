package com.tungnui.dalatlaptop.ux.adapters

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.travijuu.numberpicker.library.Enums.ActionEnum
import com.travijuu.numberpicker.library.Interface.ValueChangedListener
import com.tungnui.dalatlaptop.R
import com.tungnui.dalatlaptop.interfaces.CartRecyclerInterface
import com.tungnui.dalatlaptop.listeners.OnSingleClickListener
import com.tungnui.dalatlaptop.models.Cart
import com.tungnui.dalatlaptop.utils.formatPrice
import com.tungnui.dalatlaptop.utils.inflate
import com.tungnui.dalatlaptop.utils.loadImg
import kotlinx.android.synthetic.main.list_item_order_finish.view.*
import java.util.ArrayList

/**
 * Adapter handling list of cart items.
 */
class OrderCreateFinishAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
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
            order_finish_item_image.loadImg(cart.image);
            order_finish_item_name.text = cart.productName
            order_finish_item_price.text = cart.price.toString().formatPrice()
            order_finish_item_quantity.text = "${cart.quantity}"
        }
    }


}