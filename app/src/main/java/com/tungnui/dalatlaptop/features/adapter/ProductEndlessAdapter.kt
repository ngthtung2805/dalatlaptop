package com.tungnui.dalatlaptop.features.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.tungnui.dalatlaptop.R
import com.tungnui.dalatlaptop.models.Product
import com.tungnui.dalatlaptop.utils.adapter.AdapterConstants
import com.tungnui.dalatlaptop.utils.adapter.ViewType
import com.tungnui.dalatlaptop.utils.inflate
import com.tungnui.dalatlaptop.utils.loadImg
import kotlinx.android.synthetic.main.loading.view.*
import kotlinx.android.synthetic.main.product_item.view.*
import java.text.DecimalFormat

/**
 * Created by thanh on 27/09/2017.
 */
class ProductEndlessAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var items: ArrayList<ViewType>
    private var loadingItem = object : ViewType {
        override fun getViewType(): Int {
            return AdapterConstants.LOADING
        }
    }

    init {
        items = ArrayList<ViewType>()
        items.add(loadingItem)
    }

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? =
            when (viewType) {
                AdapterConstants.ITEM -> ProductViewHolder(parent.inflate(R.layout.product_item))
                AdapterConstants.LOADING -> LoadingViewHolder(parent.inflate(R.layout.loading))
                else -> null
            }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is LoadingViewHolder -> holder.processBar.isIndeterminate = true
            is ProductViewHolder -> holder.bind(items[position] as Product)
        }
    }

    override fun getItemViewType(position: Int): Int = items[position].getViewType()
    fun addProduct(products: List<Product>) {
                  val initPosition = items.size - 1
            items.removeAt(initPosition)
            notifyItemRemoved(initPosition)
            items.addAll(products)
            items.add(loadingItem)
            notifyItemChanged(initPosition, items.size + 1)

    }


    fun clearAndAddProduct(news: List<Product>) {
        items.clear()
        notifyItemRangeRemoved(0, getLastPosition())

        items.addAll(news)
        items.add(loadingItem)
        notifyItemRangeInserted(0, items.size)
    }

    fun getProduct(): List<Product?> {
        return items
                .filter { it.getViewType() == AdapterConstants.ITEM }
                .map { it as Product }
    }

    private fun getLastPosition() = if (items.lastIndex == -1) 0 else items.lastIndex

    private class LoadingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var processBar = view.loading
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