package com.tungnui.dalatlaptop.ux.adapters

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout

import com.tungnui.dalatlaptop.models.Image

import com.tungnui.dalatlaptop.R
import com.tungnui.dalatlaptop.views.TouchImageView
import com.tungnui.dalatlaptop.utils.loadImg

/**
 * Simple images pager adapter. Uses [com.tungnui.dalatlaptop.views.TouchImageView] for zooming single images.
 */
class ProductImagesPagerAdapter(private val context: Context, private val images: List<Image>) : PagerAdapter() {
    override fun getCount(): Int {
        return this.images.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val imgDisplay: TouchImageView
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val viewLayout = inflater.inflate(R.layout.layout_fullscreen_image, container, false)
        imgDisplay = viewLayout.findViewById<View>(R.id.fullscreen_image) as TouchImageView
        imgDisplay.loadImg(images[position].src)
        container.addView(viewLayout)
        return viewLayout
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as RelativeLayout)
    }
}