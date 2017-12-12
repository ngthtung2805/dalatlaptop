package com.tungnui.abccomputer.adapter

import android.graphics.Paint
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.tungnui.abccomputer.R
import com.tungnui.abccomputer.SettingsMy
import com.tungnui.abccomputer.models.Product
import com.tungnui.abccomputer.utils.*
import kotlinx.android.synthetic.main.list_item_product_linear.view.*
import java.util.ArrayList

/**
 * Adapter handling list of product items.
 */
class ProductsLinearAdapter( val listener: (Product) -> Unit) : RecyclerView.Adapter<ProductsLinearAdapter.ViewHolder>() {
    private val products = ArrayList<Product>()
    override fun getItemCount(): Int {
        return products.size
    }

    fun addProducts(productList: List<Product>) {
        products.addAll(productList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsLinearAdapter.ViewHolder =
          ViewHolder(parent.inflate(R.layout.list_item_product_linear))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.blind(products[position],listener)
    }

    fun clear() {
        products.clear()
    }



    // Provide a reference to the views for each data item
    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        fun blind(item: Product, listener: (Product) -> Unit)=with(itemView){
            var user = SettingsMy.getActiveUser()
            product_item_name.text = item.name
            product_item_image.loadGlideImg(item.images?.getFeaturedImage()?.src)
            if(user != null && user.role =="administrator"){
                layoutAdmin.visibility= View.VISIBLE
                layoutUser.visibility = View.GONE
                capital_price.text = "Giá vốn: ${item.metaData?.getCapitalPrice()?.formatPrice()}"
                regular_price.text = "Giá bán: ${item.regularPrice?.formatPrice()}"
                sale_price.text = "Giá KM: ${item.price?.formatPrice()}"
                stock_quantity.text ="Tồn :${item.stockQuantity.toString()}"
                sale_quantity.text = "Bán :${item.stockQuantity.toString()}"
            }else{
                layoutAdmin.visibility= View.GONE
                layoutUser.visibility = View.VISIBLE
                product_item_name.text = item.name
                product_item_image.loadGlideImg(item.images?.getFeaturedImage()?.src)
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
            }
            setOnClickListener { listener(item) }
        }
    }
}