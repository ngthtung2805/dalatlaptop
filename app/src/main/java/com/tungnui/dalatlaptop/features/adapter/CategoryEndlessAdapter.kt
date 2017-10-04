package com.tungnui.dalatlaptop.features.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.tungnui.dalatlaptop.R
import com.tungnui.dalatlaptop.models.Category
import com.tungnui.dalatlaptop.models.Product
import com.tungnui.dalatlaptop.utils.adapter.AdapterConstants
import com.tungnui.dalatlaptop.utils.adapter.ViewType
import com.tungnui.dalatlaptop.utils.inflate
import com.tungnui.dalatlaptop.utils.loadImg
import kotlinx.android.synthetic.main.category_item.view.*
import kotlinx.android.synthetic.main.loading.view.*
import java.text.DecimalFormat

/**
 * Created by thanh on 27/09/2017.
 */
class CategoryEndlessAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
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
                AdapterConstants.ITEM -> CategoryViewHolder(parent.inflate(R.layout.category_item))
                AdapterConstants.LOADING -> LoadingViewHolder(parent.inflate(R.layout.loading))
                else -> null
            }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is LoadingViewHolder -> holder.processBar.isIndeterminate = true
            is CategoryViewHolder -> holder.bind(items[position] as Category)
        }
    }

    override fun getItemViewType(position: Int): Int = items[position].getViewType()

    fun addItems(categories: List<Category>) {
                  val initPosition = items.size - 1
            items.removeAt(initPosition)
            notifyItemRemoved(initPosition)
            items.addAll(categories)
            items.add(loadingItem)
            notifyItemChanged(initPosition, items.size + 1)

    }

    private class LoadingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var processBar = view.loading
    }

    private class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(item: Category) = with(itemView) {
            imgCategory.loadImg(item.image?.src)
            txtCategoryName.text = item.name
                  }
    }
}