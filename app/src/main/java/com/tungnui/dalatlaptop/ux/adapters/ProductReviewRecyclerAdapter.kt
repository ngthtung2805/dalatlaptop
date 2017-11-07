package com.tungnui.dalatlaptop.ux.adapters

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.travijuu.numberpicker.library.Enums.ActionEnum
import com.travijuu.numberpicker.library.Interface.ValueChangedListener
import com.tungnui.dalatlaptop.R
import com.tungnui.dalatlaptop.interfaces.CartRecyclerInterface
import com.tungnui.dalatlaptop.listeners.OnSingleClickListener
import com.tungnui.dalatlaptop.models.Cart
import com.tungnui.dalatlaptop.models.ProductReview
import com.tungnui.dalatlaptop.utils.inflate
import kotlinx.android.synthetic.main.list_item_customer_review.view.*
import java.util.ArrayList

/**
 * Adapter handling list of cart items.
 */
class ProductReviewRecyclerAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val productReviews = ArrayList<ProductReview>()
    override fun getItemCount(): Int {
        return productReviews.size
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            ViewHolderProduct(parent.inflate(R.layout.list_item_customer_review))


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolderProduct).blind(productReviews[position])
    }

    fun refreshItems(proRes: List<ProductReview>) {
        productReviews.clear()
        productReviews.addAll(proRes)
        notifyDataSetChanged()
    }

    fun cleatCart() {
        productReviews.clear()
        notifyDataSetChanged()
    }


    class ViewHolderProduct(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun blind(productReview: ProductReview) = with(itemView){
            productReview.rating?.let{list_item_customer_review_rating.rating=it.toFloat()}
            list_item_customer_review_date.text = productReview.dateCreated
            list_item_customer_review_name.text = productReview.name
            list_item_customer_review_content.text = productReview.review
        }
    }


}