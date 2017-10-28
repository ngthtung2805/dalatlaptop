package com.tungnui.dalatlaptop.ux.adapters

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker

import java.util.ArrayList

import com.tungnui.dalatlaptop.R
import com.travijuu.numberpicker.library.Enums.ActionEnum
import com.travijuu.numberpicker.library.Interface.ValueChangedListener
import com.tungnui.dalatlaptop.entities.cart.CartDiscountItem
import com.tungnui.dalatlaptop.interfaces.CartRecyclerInterface
import com.tungnui.dalatlaptop.listeners.OnSingleClickListener
import com.tungnui.dalatlaptop.models.Cart
import com.tungnui.dalatlaptop.utils.inflate
import com.tungnui.dalatlaptop.utils.loadImg
import kotlinx.android.synthetic.main.list_item_cart_product.view.*


/**
 * Adapter handling list of cart items.
 */
class CartRecyclerAdapter( private val cartRecyclerInterface: CartRecyclerInterface) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val cartItems = ArrayList<Cart>()
    private val cartDiscountItems = ArrayList<CartDiscountItem>()
    companion object {

        private val TYPE_ITEM_PRODUCT = 0
        private val TYPE_ITEM_DISCOUNT = 1
    }
    override fun getItemCount(): Int {
        return cartItems.size + cartDiscountItems.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < cartItems.size)
            TYPE_ITEM_PRODUCT
        else
            TYPE_ITEM_DISCOUNT
    }

    private fun getCartDiscountItem(position: Int): Cart {
        return cartItems[position - cartItems.size]
    }

    private fun getCartProductItem(position: Int): Cart {
        return cartItems[position]
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            when (viewType) {
                TYPE_ITEM_DISCOUNT -> ViewHolderDiscount(parent.inflate(R.layout.list_item_cart_discount))
                else-> ViewHolderProduct(parent.inflate(R.layout.list_item_cart_product))
            }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolderProduct-> holder.blind(cartItems[position], cartRecyclerInterface)
            is ViewHolderDiscount -> holder.blind(cartItems[position], cartRecyclerInterface)
        }
    }

    fun refreshItems(carts: List<Cart>) {
            cartItems.clear()
            cartDiscountItems.clear()
            cartItems.addAll(carts)
         //   cartDiscountItems.addAll(carts)
            notifyDataSetChanged()
    }

    fun cleatCart() {
        cartItems.clear()
        cartDiscountItems.clear()
        notifyDataSetChanged()
    }


    // Provide a reference to the views for each data item
    class ViewHolderDiscount(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun blind(cart:Cart, cartRecyclerInterface: CartRecyclerInterface) = with(itemView){
          /* cart_discount_name.text = cart.discount.name
            cart_discount_value.text = cartDiscountItem.discount.valueFormatted
            cart_discount_delete.setOnClickListener(object : OnSingleClickListener() {
                override fun onSingleClick(v: View) {
                    cartRecyclerInterface.onDiscountDelete(cartDiscountItem)
                }
            })*/
        }
     }

    // Provide a reference to the views for each data item
    class ViewHolderProduct(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun blind(cart: Cart, cartRecyclerInterface: CartRecyclerInterface) = with(itemView){
            cart_product_image.loadImg(cart.image);
            cart_product_name.text = cart.productName
            cart_product_price.text = cart.price.toString()
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
