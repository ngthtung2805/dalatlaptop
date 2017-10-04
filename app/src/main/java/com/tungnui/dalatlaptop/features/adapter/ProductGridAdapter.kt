package com.tungnui.dalatlaptop.features.adapter

import android.graphics.Paint
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.tungnui.dalatlaptop.R
import com.tungnui.dalatlaptop.models.Product
import com.tungnui.dalatlaptop.utils.inflate
import com.tungnui.dalatlaptop.utils.loadImg
import kotlinx.android.synthetic.main.product_grid_item.view.*
import java.text.DecimalFormat

/**
 * Created by thanh on 27/09/2017.
 */
class ProductGridAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var items: ArrayList<Product>

    init {
        items = ArrayList<Product>()
    }

    override fun getItemCount(): Int = items.size
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? =
            ProductViewHolder(parent.inflate(R.layout.product_grid_item))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ProductViewHolder).bind(items[position])
    }

    override fun getItemViewType(position: Int): Int = items[position].getViewType()
    fun addProducts(products: List<Product>) {
        items.addAll(products)
        notifyDataSetChanged()
    }

    private class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(item: Product) = with(itemView) {
            imgProduct.loadImg(item.images.let { it?.get(0)?.src })
            txtProductName.text = item.name
            productRating.rating = item.averageRating?.toFloat() ?: 0.0f
            txtRatingCount.text = "(${item.ratingCount ?: 0})"
            val formatter = DecimalFormat("#,###")
            txtSalePrice.text = formatter.format(item.price?.toDouble()) + "đ"
            if (item.onSale) {
                txtProductPrice.text = "${formatter.format(item.regularPrice?.toDouble())}đ"
                txtProductPrice.paintFlags = txtProductPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            }
        }
    }
}