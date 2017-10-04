package com.tungnui.dalatlaptop.features.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.tungnui.dalatlaptop.R
import com.tungnui.dalatlaptop.models.Category
import com.tungnui.dalatlaptop.utils.inflate
import com.tungnui.dalatlaptop.utils.loadImg
import kotlinx.android.synthetic.main.category_item.view.*

/**
 * Created by thanh on 27/09/2017.
 */
class CategoryAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var items: ArrayList<Category>
    init {
        items = ArrayList<Category>()
    }

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? =
            CategoryViewHolder(parent.inflate(R.layout.category_item))


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as CategoryViewHolder).bind(items[position])
     }

    override fun getItemViewType(position: Int): Int = items[position].getViewType()
    fun addCategories(categories: List<Category>) {
       items.addAll(categories)
        notifyDataSetChanged()
    }
    private class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(item: Category) = with(itemView) {
            imgCategory.loadImg(item.image?.src)
            txtCategoryName.text = item.name
        }
    }
}