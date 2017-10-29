package com.tungnui.dalatlaptop.ux.adapters

import android.graphics.Paint
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup

import com.tungnui.dalatlaptop.models.Product

import java.util.ArrayList

import com.tungnui.dalatlaptop.R
import com.tungnui.dalatlaptop.views.ResizableImageViewHeight
import com.tungnui.dalatlaptop.utils.getFeaturedImage
import com.tungnui.dalatlaptop.utils.inflate
import com.tungnui.dalatlaptop.utils.loadImg
import kotlinx.android.synthetic.main.list_item_recommended_products.view.*

/**
 * Adapter handling list of related products.
 */
class RelatedProductsRecyclerAdapter(val listener: (Product)->Unit) : RecyclerView.Adapter<RelatedProductsRecyclerAdapter.ViewHolder>() {
    private val relatedProducts: MutableList<Product>
    init {
        relatedProducts = ArrayList()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RelatedProductsRecyclerAdapter.ViewHolder {
        return ViewHolder(parent.inflate(R.layout.list_item_recommended_products))
    }

    override fun onBindViewHolder(holder: RelatedProductsRecyclerAdapter.ViewHolder, position: Int) {
        holder.blind(relatedProducts[position],listener)
    }

    private fun getItem(position: Int): Product? {
        return relatedProducts[position]
    }

    override fun getItemCount(): Int {
        return relatedProducts.size
    }

    /**
     * Add the product to the list.
     *
     * @param position list position where item should be added.
     * @param product  item to add.
     */
    fun add(position: Int, product: Product) {
        relatedProducts.add(position, product)
        notifyItemInserted(position)
    }
    fun addProducts(items:List<Product>){
        relatedProducts.addAll(items)
        notifyDataSetChanged()
    }
    /**
     * Add the product at the end of the list.
     *
     * @param product item to add.
     */
    fun addLast(product: Product) {
        relatedProducts.add(relatedProducts.size, product)
        notifyItemInserted(relatedProducts.size)
    }

    // Provide a reference to the views for each data item
    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        internal var position: Int = 0
            fun blind(item:Product,listener: (Product) -> Unit)=with(itemView){
                (list_item_recommended_products_image as ResizableImageViewHeight).loadImg(item.images?.getFeaturedImage()?.src)
                list_item_recommended_products_name.text = item.name
                val pr = item.price
                val dis = item.salePrice
                if (pr == dis) {
                    list_item_recommended_products_price.visibility = View.VISIBLE
                    list_item_recommended_products_discount.visibility = View.GONE
                    list_item_recommended_products_price.setText(item.priceHtml)
                    //list_item_recommended_products_price.paintFlags = item
                    list_item_recommended_products_price.setTextColor(ContextCompat.getColor(context, R.color.textPrimary))
                } else {
                    list_item_recommended_products_price.visibility = View.VISIBLE
                    list_item_recommended_products_discount.visibility = View.VISIBLE
                    list_item_recommended_products_price.setText(item.priceHtml)
                    list_item_recommended_products_price.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG or Paint.ANTI_ALIAS_FLAG
                    list_item_recommended_products_price.setTextColor(ContextCompat.getColor(context, R.color.textSecondary))
                    list_item_recommended_products_discount.setText(item.salePrice)
                }
                setOnClickListener { listener(item) }
            }
               fun setPosition(position: Int) {
            this.position = position
        }
    }
}
