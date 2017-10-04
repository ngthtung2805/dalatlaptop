package com.tungnui.dalatlaptop.features.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.tungnui.dalatlaptop.R
import com.tungnui.dalatlaptop.models.Category
import com.tungnui.dalatlaptop.models.Product
import com.tungnui.dalatlaptop.utils.inflate
import com.tungnui.dalatlaptop.utils.loadImg
import kotlinx.android.synthetic.main.product_item.view.*
import java.text.DecimalFormat

/**
 * Created by thanh on 27/09/2017.
 */
class ProductAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var items: ArrayList<Product>
    init {
        items = ArrayList<Product>()
    }

    override fun getItemCount(): Int = items.size
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? =
            ProductViewHolder(parent.inflate(R.layout.product_item))

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
            img_thumbnail.loadImg(item.images.let { it?.get(0)?.src })
            name.text = item.name
            val formatter = DecimalFormat("#,###")
            price.text = formatter.format(item.price?.toDouble())
        }
    }
}