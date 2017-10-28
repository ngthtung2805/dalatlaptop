package com.tungnui.dalatlaptop.ux.adapters

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.tungnui.dalatlaptop.models.Category
import java.util.ArrayList

import com.tungnui.dalatlaptop.R
import com.tungnui.dalatlaptop.utils.inflate
import kotlinx.android.synthetic.main.list_item_drawer_category.view.*

/**
 * Adapter handling list of drawer sub-items.
 */
class DrawerRecyclerAdapter(val listener: (Category) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val listCategory = ArrayList<Category>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            ViewHolderItemCategory(parent.inflate(R.layout.list_item_drawer_category))


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolderItemCategory).blind(listCategory[position],listener)
    }

    // This method returns the number of items present in the list
    override fun getItemCount(): Int {
        return listCategory.size
    }

    fun changeDrawerItems(categories: List<Category>) {
        listCategory.clear()
        listCategory.addAll(categories)
        notifyDataSetChanged()
    }


    // Provide a reference to the views for each data item
    class ViewHolderItemCategory(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun blind(item:Category,listener: (Category) -> Unit)=with(itemView){
            drawer_list_item_text.text = item.name
            drawer_list_item_text.setTextColor(ContextCompat.getColor(context, R.color.textPrimary))
            drawer_list_item_text.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            drawer_list_item_divider.visibility = View.GONE
            if (item.display == "subcategories") {
                drawer_list_item_indicator.visibility = View.VISIBLE
            } else {
                drawer_list_item_indicator.visibility = View.INVISIBLE
            }
            setOnClickListener { listener(item) }
        }
    }
}

