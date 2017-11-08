package com.tungnui.dalatlaptop.ux.dialogs

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageView

import com.tungnui.dalatlaptop.R
import com.tungnui.dalatlaptop.models.Image
import com.tungnui.dalatlaptop.ux.adapters.ProductImagesPagerAdapter

import java.util.ArrayList

import timber.log.Timber

class ProductImagesDialogFragment : DialogFragment() {
    private var images: List<Image>? = null
    private var defaultPosition = 0

    private var imagesPager: ViewPager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.dialogFullscreen)
    }

    override fun onStart() {
        super.onStart()
        val d = dialog
        if (d != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            val window = d.window
            window!!.setLayout(width, height)
            window.setWindowAnimations(R.style.dialogFragmentAnimation)
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Timber.d("%s - OnCreateView", this.javaClass.simpleName)
        val view = inflater!!.inflate(R.layout.dialog_product_detail_images, container, false)

        imagesPager = view.findViewById<View>(R.id.dialog_product_detail_images_pager) as ViewPager

        // Prepare endless image adapter
        val mPagerAdapter = ProductImagesPagerAdapter(activity, images!!)
        imagesPager!!.adapter = mPagerAdapter

        if (defaultPosition > 0 && defaultPosition < images!!.size)
            imagesPager!!.currentItem = defaultPosition
        else
            imagesPager!!.currentItem = 0

        val goLeft = view.findViewById<View>(R.id.dialog_product_detail_images_left) as ImageView
        goLeft.setOnClickListener {
            val position = imagesPager!!.currentItem
            imagesPager!!.setCurrentItem(position - 1, true)
        }

        val goRight = view.findViewById<View>(R.id.dialog_product_detail_images_right) as ImageView
        goRight.setOnClickListener {
            val position = imagesPager!!.currentItem
            imagesPager!!.setCurrentItem(position + 1, true)
        }

        val cancel = view.findViewById<View>(R.id.pager_close) as Button
        cancel.setOnClickListener { dismiss() }

        return view
    }

    companion object {

        fun newInstance(images: List<Image>?, defaultPosition: Int): ProductImagesDialogFragment? {
            if (images == null || images.isEmpty()) return null
            val frag = ProductImagesDialogFragment()
            frag.images = images
            frag.defaultPosition = defaultPosition
            return frag
        }
    }
}
