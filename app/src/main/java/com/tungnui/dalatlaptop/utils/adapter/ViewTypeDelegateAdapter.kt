package com.tungnui.dalatlaptop.utils.adapter

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup

/**
 * Created by thanh on 23/09/2017.
 */
interface ViewTypeDelegateAdapter{
    fun onCreateViewHolder(parent:ViewGroup):RecyclerView.ViewHolder
    fun onBlindViewHolder(holder:RecyclerView.ViewHolder, item:ViewType)
}