package com.tungnui.dalatlaptop.ux.adapters

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import java.util.ArrayList

import com.tungnui.dalatlaptop.R
import com.tungnui.dalatlaptop.views.ResizableImageViewHeight
import com.tungnui.dalatlaptop.models.Image
import com.tungnui.dalatlaptop.utils.inflate
import com.tungnui.dalatlaptop.utils.loadImg
import kotlinx.android.synthetic.main.list_item_product_image.view.*


class ProductImagesRecyclerAdapter(val listener: (Int) -> Unit): RecyclerView.Adapter<ProductImagesRecyclerAdapter.ViewHolder>() {
    private val productImages: MutableList<Image>
    init {
        productImages = ArrayList()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductImagesRecyclerAdapter.ViewHolder {
        return ViewHolder(parent.inflate(R.layout.list_item_product_image))
    }

    override fun onBindViewHolder(holder: ProductImagesRecyclerAdapter.ViewHolder, position: Int) {
       holder.blind(productImages[position],listener)
    }

    private fun getItem(position: Int): Image {
        return productImages[position]
    }

    override fun getItemCount(): Int {
        return productImages.size
    }

    fun add(position: Int, productImage: Image) {
        productImages.add(position, productImage)
        notifyItemInserted(position)
    }
    fun addAll(items:List<Image>){
        productImages.addAll(items)
        notifyDataSetChanged()
    }
    fun addLast(productImageUrl: Image) {
        productImages.add(productImages.size, productImageUrl)
        notifyItemInserted(productImages.size)
    }

    fun clearAll() {
        val itemCount = productImages.size

        if (itemCount > 0) {
            productImages.clear()
            notifyItemRangeRemoved(0, itemCount)
        }
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        internal var position: Int = 0

        fun blind(image:Image,listener: (Int) -> Unit)=with(itemView){
            (list_item_product_images_view as ResizableImageViewHeight).loadImg(image.src)
            setOnClickListener { listener(position) }
        }

        fun setPosition(position: Int) {
            this.position = position
        }

    }
}