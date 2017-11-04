package com.tungnui.dalatlaptop.ux.adapters

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker

import java.util.ArrayList

import com.tungnui.dalatlaptop.R
import com.travijuu.numberpicker.library.Enums.ActionEnum
import com.travijuu.numberpicker.library.Interface.ValueChangedListener
import com.tungnui.dalatlaptop.interfaces.CartRecyclerInterface
import com.tungnui.dalatlaptop.listeners.OnSingleClickListener
import com.tungnui.dalatlaptop.models.Cart
import com.tungnui.dalatlaptop.utils.formatPrice
import com.tungnui.dalatlaptop.utils.inflate
import com.tungnui.dalatlaptop.utils.loadImg
import kotlinx.android.synthetic.main.list_item_cart_product.view.*


/**
 * Adapter handling list of cart items.
 */
class CartRecyclerAdapter( private val cartRecyclerInterface: CartRecyclerInterface) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val cartItems = ArrayList<Cart>()
    override fun getItemCount(): Int {
        return cartItems.size
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            ViewHolderProduct(parent.inflate(R.layout.list_item_cart_product))


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolderProduct).blind(cartItems[position], cartRecyclerInterface)
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
        fun blind(cart: Cart, cartRecyclerInterface: CartRecyclerInterface) = with(itemView){
            cart_product_image.loadImg(cart.image);
            cart_product_name.text = cart.productName
            cart_product_price.text = cart.price.toString().formatPrice()
            cart.quantity?.let{cart_product_quantity.value = it}
            cart_product_quantity.setValueChangedListener(object :ValueChangedListener{
                override fun valueChanged(value: Int, action: ActionEnum) {
                        cartRecyclerInterface.onQuantityChange(cart,value,action)
                }
            })
            cart_product_delete.setOnClickListener(object : OnSingleClickListener() {
                override fun onSingleClick(v: View) {
                    cartRecyclerInterface.onProductDelete(cart)
                }
            })

        }
    }


}
