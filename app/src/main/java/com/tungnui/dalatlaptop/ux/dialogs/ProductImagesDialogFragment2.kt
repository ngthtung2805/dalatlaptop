package com.tungnui.dalatlaptop.ux.dialogs

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.tungnui.dalatlaptop.models.Image

import java.util.ArrayList

import com.tungnui.dalatlaptop.R
import com.tungnui.dalatlaptop.ux.adapters.ProductImagesPagerAdapter
import com.tungnui.dalatlaptop.views.loopViewPager.LoopViewPager
import kotlinx.android.synthetic.main.dialog_product_detail_images.*

class ProductImagesDialogFragment2 : DialogFragment() {
    private var images: List<Image> = ArrayList<Image>()
    private var defaultPosition = 0
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
            window.setLayout(width, height)
            window.setWindowAnimations(R.style.dialogFragmentAnimation)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_product_detail_images, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var mPagerAdapter = ProductImagesPagerAdapter(activity, images)
        var loopViewPager = view?.findViewById<LoopViewPager>(R.id.dialog_product_detail_images_pager)
        //loopViewPager.adapter = mPagerAdapter

        if (defaultPosition > 0 && defaultPosition < images.size)
            dialog_product_detail_images_pager.currentItem = defaultPosition
        else
            dialog_product_detail_images_pager.currentItem = 0

        dialog_product_detail_images_left.setOnClickListener {
            val position = dialog_product_detail_images_pager.currentItem
            dialog_product_detail_images_pager.setCurrentItem(position - 1, true)
        }

        dialog_product_detail_images_right.setOnClickListener {
            val position = dialog_product_detail_images_pager.currentItem
            dialog_product_detail_images_pager.setCurrentItem(position + 1, true)
        }
        pager_close.setOnClickListener { dismiss() }
    }
    companion object {
        fun newInstance(images: List<Image>?, defaultPosition: Int): ProductImagesDialogFragment2? {
            if (images == null || images.isEmpty()) return null
            val frag = ProductImagesDialogFragment2()
            frag.images = images
            frag.defaultPosition = defaultPosition
            return frag
        }
    }
}
