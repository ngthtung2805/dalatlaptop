package com.tungnui.dalatlaptop.ux.adapters

import android.graphics.Paint
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup

import java.util.ArrayList

import com.tungnui.dalatlaptop.R
import com.tungnui.dalatlaptop.models.Product
import com.tungnui.dalatlaptop.utils.formatPrice
import com.tungnui.dalatlaptop.utils.getFeaturedImage
import com.tungnui.dalatlaptop.utils.inflate
import com.tungnui.dalatlaptop.utils.loadImg
import kotlinx.android.synthetic.main.list_item_products.view.*
import java.text.DecimalFormat

/**
 * Adapter handling list of product items.
 */
class ProductsRecyclerAdapter(val listener: (Product) -> Unit) : RecyclerView.Adapter<ProductsRecyclerAdapter.ViewHolder>() {
    private val products = ArrayList<Product>()
    override fun getItemCount(): Int {
        return products.size
    }

    fun addProducts(productList: List<Product>) {
        products.addAll(productList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsRecyclerAdapter.ViewHolder =
         ViewHolder(parent.inflate(R.layout.list_item_products))


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.blind(products[position],listener)
    }

    fun clear() {
        products.clear()
    }



    // Provide a reference to the views for each data item
    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        fun blind(item:Product,listener: (Product) -> Unit)=with(itemView){
            product_item_name.text = item.name
            product_item_image.loadImg(item.images?.getFeaturedImage()?.src)
            item.averageRating?.toFloat()?.let{product_item_rating.rating = it }
            product_item_rating_count.text = "(" + item.ratingCount + ")"
            if(item.onSale){
                product_item_price.visibility = View.VISIBLE
                product_item_regular_price.visibility = View.VISIBLE
                product_item_price.text = item.price?.formatPrice()
                product_item_regular_price.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG or Paint.ANTI_ALIAS_FLAG
                product_item_regular_price.text = item.regularPrice?.formatPrice()
            }else{
                product_item_price.visibility = View.VISIBLE
                product_item_regular_price.visibility = View.GONE
                product_item_price.text =item.price?.formatPrice()
            }
             setOnClickListener { listener(item) }
        }
    }
}
