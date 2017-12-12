package com.tungnui.abccomputer.adapter

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

import com.tungnui.abccomputer.R
import com.tungnui.abccomputer.listener.OnItemClickListener
import com.tungnui.abccomputer.models.Image
import com.tungnui.abccomputer.utils.loadGlideImg

import java.util.ArrayList

class ProductSliderAdapter(private val mContext: Context?, var images: ArrayList<Image>) : PagerAdapter() {

    private val inflater: LayoutInflater

    // Listener
    private var mListener: OnItemClickListener? = null

    init {
        inflater = LayoutInflater.from(mContext)
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun getCount(): Int {
        return images.size
    }

    override fun instantiateItem(view: ViewGroup, position: Int): Any {

        val imageLayout = inflater.inflate(R.layout.item_product_image_slider, view, false)
        val imageView = imageLayout.findViewById<View>(R.id.image) as ImageView
        imageView.loadGlideImg(images[position].src)
        view.addView(imageLayout)
        imageView.setOnClickListener {
            if (mListener != null) {
                mListener!!.onItemListener(view, position)
            }
        }

        return imageLayout
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    fun setItemClickListener(mListener: OnItemClickListener) {
        this.mListener = mListener
    }

}