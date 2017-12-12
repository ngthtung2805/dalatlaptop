package com.tungnui.abccomputer.adapter


import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.travijuu.numberpicker.library.Enums.ActionEnum
import com.travijuu.numberpicker.library.Interface.ValueChangedListener
import com.tungnui.abccomputer.R
import com.tungnui.abccomputer.listeners.OnSingleClickListener
import com.tungnui.abccomputer.models.Cart
import com.tungnui.abccomputer.utils.formatPrice
import com.tungnui.abccomputer.utils.inflate
import com.tungnui.abccomputer.utils.loadGlideImg
import kotlinx.android.synthetic.main.item_cart_list.view.*

import java.util.ArrayList

class CartListAdapter( private val cartRecyclerInterface: CartRecyclerInterface) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val cartItems = ArrayList<Cart>()
    override fun getItemCount(): Int {
        return cartItems.size
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            ViewHolderProduct(parent.inflate(R.layout.item_cart_list))


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
            cart_product_image.loadGlideImg(cart.image);
            cart_product_name.text = cart.productName
            cart_product_price.text = cart.price.toString().formatPrice()
            cart.quantity?.let{cart_product_quantity.value = it}
            cart_product_quantity.setValueChangedListener(object : ValueChangedListener {
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
interface CartRecyclerInterface {
    fun onProductDelete(cart: Cart)
    fun onProductSelect(productId: Int)
    fun onQuantityChange(cart: Cart, quantity: Int, actionEnum: ActionEnum)
}
