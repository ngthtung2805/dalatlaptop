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
import kotlinx.android.synthetic.main.list_item_home.view.*
import java.text.DecimalFormat


class HomeProductRecyclerAdapter(val listener: (Product) -> Unit) : RecyclerView.Adapter<HomeProductRecyclerAdapter.ViewHolder>() {
    private val products = ArrayList<Product>()
    override fun getItemCount(): Int {
        return products.size
    }

    fun addProducts(productList: List<Product>) {
        products.addAll(productList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeProductRecyclerAdapter.ViewHolder =
            ViewHolder(parent.inflate(R.layout.list_item_home))


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.blind(products[position],listener)
    }

    fun clear() {
        products.clear()
    }




    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        fun blind(item:Product,listener: (Product) -> Unit)=with(itemView){
            home_product_item_name.text = item.name
            home_product_item_image.loadImg(item.images?.getFeaturedImage()?.src)
            item.averageRating?.toFloat()?.let{home_product_item_rating.rating = it }
            home_product_item_rating_count.text = "(" + item.ratingCount + ")"
            if(item.onSale){
                home_product_item_price.visibility = View.VISIBLE
                home_product_item_regular_price.visibility = View.VISIBLE
                home_product_item_regular_price.text = item.regularPrice?.formatPrice()
                home_product_item_regular_price.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG or Paint.ANTI_ALIAS_FLAG
                  home_product_item_price.text = item.salePrice?.formatPrice()
            }else{
                home_product_item_price.visibility = View.VISIBLE
                home_product_item_regular_price.visibility = View.GONE
                home_product_item_price.text = item.price?.formatPrice()
            }
            setOnClickListener { listener(item) }
        }
    }
}
