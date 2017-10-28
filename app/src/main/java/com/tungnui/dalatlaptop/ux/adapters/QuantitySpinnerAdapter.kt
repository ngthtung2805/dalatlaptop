package com.tungnui.dalatlaptop.ux.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

import com.tungnui.dalatlaptop.R
import com.tungnui.dalatlaptop.entities.product.ProductQuantity
import timber.log.Timber

/**
 * Simple arrayAdapter for quantity selection.
 */
class QuantitySpinnerAdapter(context: Context, private val quantities: List<ProductQuantity>) : ArrayAdapter<ProductQuantity>(context, layoutID, quantities) {
    private val layoutInflater: LayoutInflater

    init {
        this.layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun getCount(): Int {
        return quantities.size
    }

    override fun getItem(position: Int): ProductQuantity? {
        return quantities[position]
    }

    override fun getItemId(position: Int): Long {
        return quantities[position].quantity.toLong()
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position, convertView, parent)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position, convertView, parent)
    }

    private fun getCustomView(position: Int, convertView: View?, parent: ViewGroup): View {
        var v = convertView
        val holder: ListItemHolder

        if (v == null) {
            v = layoutInflater.inflate(layoutID, parent, false)
            holder = ListItemHolder()
            holder.text = v!!.findViewById<View>(R.id.text) as TextView
            v.tag = holder
        } else {
            holder = v.tag as ListItemHolder
        }

        if (getItem(position) != null) {
            holder.text!!.text = getItem(position)!!.value
        } else {
            Timber.e("Received null value in %s", this.javaClass.simpleName)
        }

        return v
    }

    internal class ListItemHolder {
        var text: TextView? = null
    }

    companion object {
        private val layoutID = R.layout.spinner_item_simple_text
    }

}