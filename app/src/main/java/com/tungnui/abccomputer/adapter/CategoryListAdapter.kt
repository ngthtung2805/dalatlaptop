package com.tungnui.abccomputer.adapter


import android.support.v7.widget.RecyclerView
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.tungnui.abccomputer.R
import com.tungnui.abccomputer.models.Category
import com.tungnui.abccomputer.utils.loadGlideImg
import kotlinx.android.synthetic.main.item_category.view.*

import java.util.ArrayList

class CategoryListAdapter(private val dataList: ArrayList<Category>,val listener: (Category) -> Unit) : RecyclerView.Adapter<CategoryListAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun blind(category:Category,listener: (Category) -> Unit)=with(itemView){
            tvCategoryName.text = Html.fromHtml(category.name)
            ivProductImage.loadGlideImg(category.image?.src)
            // listener
            if (category.isSelected) {
                selectedBoarderView.setBackgroundResource(R.drawable.bg_selected_img)
            } else {
                selectedBoarderView.setBackgroundResource(0)
            }
            setOnClickListener { listener(category) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.blind(dataList[position],listener );
    }


    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun getItemViewType(position: Int): Int {
        return 0
    }
}
