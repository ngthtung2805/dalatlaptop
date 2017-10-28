package com.tungnui.dalatlaptop.ux.adapters


import android.app.Activity
import android.widget.ArrayAdapter

import java.util.ArrayList

import com.tungnui.dalatlaptop.R
import com.tungnui.dalatlaptop.entities.SortItem

class SortSpinnerAdapter(activity: Activity) : ArrayAdapter<SortItem>(activity, R.layout.spinner_item_sort) {
    private val sortItemList = ArrayList<SortItem>()

    init {
        this.setDropDownViewResource(R.layout.spinner_item_sort_dropdown)
        val newest = SortItem("newest", activity.getString(R.string.Whats_new))
        sortItemList.add(newest)
        val popularity = SortItem("popularity", activity.getString(R.string.Recommended))
        sortItemList.add(popularity)
        val priceDesc = SortItem("price_DESC", activity.getString(R.string.Highest_price))
        sortItemList.add(priceDesc)
        val priceAsc = SortItem("price_ASC", activity.getString(R.string.Lowest_price))
        sortItemList.add(priceAsc)

    }

    override fun getCount(): Int {
        return sortItemList.size
    }

    override fun getItem(position: Int): SortItem? {
        return sortItemList[position]
    }
}