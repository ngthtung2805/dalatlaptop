package com.tungnui.abccomputer.adapter


import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.tungnui.abccomputer.data.constant.AppConstants
import com.tungnui.abccomputer.R
import com.tungnui.abccomputer.models.Product
import com.tungnui.abccomputer.utils.AppUtility
import com.tungnui.abccomputer.utils.formatPrice
import com.tungnui.abccomputer.utils.getFeaturedImage
import com.tungnui.abccomputer.utils.loadGlideImg
import kotlinx.android.synthetic.main.item_rectangle.view.*

import java.util.ArrayList

class HomeProdListAdapter(private val dataList: ArrayList<Product>,val listener: (Product) -> Unit) : RecyclerView.Adapter<HomeProdListAdapter.ViewHolder>() {
    private var displaySize = 0

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun blind(product:Product,listener: (Product) -> Unit) = with(itemView){
            product.name?.let{tvProductName.text = AppUtility.showHtml(it)}
            if(product.onSale){
                price.text = product.salePrice?.formatPrice()
            }else{
                price.text =  product.regularPrice?.formatPrice()
            }
            ivProductImage.loadGlideImg(product.images?.getFeaturedImage()?.src)
            setOnClickListener { listener(product) }
        }
       }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_rectangle, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.blind(dataList[position],listener)
    }

    override fun getItemCount(): Int {
        return if (displaySize == AppConstants.VALUE_ZERO || displaySize > dataList.size) {
            dataList.size
        } else {
            displaySize
        }
    }

    override fun getItemViewType(position: Int): Int {
        return 0
    }

    fun setDisplayCount(numberOfEntries: Int) {
        displaySize = numberOfEntries
        notifyDataSetChanged()
    }
}
