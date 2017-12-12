package com.tungnui.abccomputer.activity

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.util.Log
import android.view.MenuItem
import android.view.View

import com.tungnui.abccomputer.R
import com.tungnui.abccomputer.adapter.AttributeAdapter
import com.tungnui.abccomputer.adapter.ProductSliderAdapter
import com.tungnui.abccomputer.adapter.ReviewListAdapter
import com.tungnui.abccomputer.api.ProductService
import com.tungnui.abccomputer.api.ServiceGenerator
import com.tungnui.abccomputer.data.constant.AppConstants
import com.tungnui.abccomputer.data.sqlite.*
import com.tungnui.abccomputer.listener.OnItemClickListener
import com.tungnui.abccomputer.model.ProductAttribute
import com.tungnui.abccomputer.model.ProductReview
import com.tungnui.abccomputer.models.Cart
import com.tungnui.abccomputer.models.Image
import com.tungnui.abccomputer.models.Product
import com.tungnui.abccomputer.network.helper.RequestProductReviews
import com.tungnui.abccomputer.utils.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_product_details.*
import kotlinx.android.synthetic.main.content_product_details.*

import java.util.ArrayList

import me.relex.circleindicator.CircleIndicator

class ProductDetailsActivity : BaseActivity() {
    private var mCompositeDisposable: CompositeDisposable
    val productService: ProductService

    init {
        mCompositeDisposable = CompositeDisposable()
        productService = ServiceGenerator.createService(ProductService::class.java)
    }

    // init variables
    private var mContext: Context? = null
    private var mActivity: Activity? = null

    private var quantityCounter = 1

    // recycler view
    private var reviewList: ArrayList<ProductReview>? = null
    private var reviewListAdapter: ReviewListAdapter? = null
    private var reviewLytManager: LinearLayoutManager? = null

    // init view


    private var mPager: ViewPager? = null
    private var productSliderAdapter: ProductSliderAdapter? = null
    private var product: Product? = null
    private var addedToWishList: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initVariable()
        initView()
        initToolbar()
        loadData()
        initListener()
    }

    private fun initVariable() {
        mContext = applicationContext
        mActivity = this@ProductDetailsActivity
        reviewList = ArrayList()
    }

    private fun initView() {
        setContentView(R.layout.activity_product_details)

        initToolbar()
        enableBackButton()
        initLoader()

        reviewLytManager = LinearLayoutManager(mActivity)
        rvReviews.layoutManager = reviewLytManager
        reviewListAdapter = ReviewListAdapter(reviewList)
        reviewListAdapter!!.registerAdapterDataObserver(RVEmptyObserver(rvReviews, emptyView))
        rvReviews.adapter = reviewListAdapter

        wvDescription?.settings?.textZoom = 85
        wvDescription?.setBackgroundColor(Color.TRANSPARENT)
        wvDescription?.isVerticalScrollBarEnabled = false
    }


    private fun loadData() {
        val intent = intent
        val productId = intent.getIntExtra(AppConstants.PRODUCT_ID, AppConstants.VALUE_ZERO)
        if (productId > 0) {
            // Load product details
            updateWishButton(productId)
            var disposable = productService.single(productId)
                    .subscribeOn((Schedulers.io()))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ response ->
                        Log.i("frodo", response.name)
                        product = response
                        loadProductPhoto(response.images)
                        // set other properties
                        tvProductName.text = response.name
                        tvShortDescription.text = Html.fromHtml(response.shortDescription)
                        tvOrderCount.text = response.totalSales.toString() + " lần"
                        if (response.onSale) {
                            tvRegularPrice.text = response.regularPrice.toString()
                            tvSalesPrice.text = response.salePrice?.formatPrice()
                            tvRegularPrice.paintFlags = tvRegularPrice!!.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                        } else {

                            tvRegularPrice.visibility = View.INVISIBLE
                            tvSalesPrice.text = response.regularPrice?.formatPrice()
                        }

                        wvDescription.loadData(response.description, "text/html; charset=utf-8", "UTF-8")
                        tvAverageRating.text = response.averageRating.toString()
                        tvRatingCount.text = resources.getString(R.string.total_review) + response.ratingCount.toString() + ")"
                        ratingBar.rating = response.averageRating!!.toFloat()
                        hideLoader()
                    }
                            ,
                            { error ->
                                hideLoader()
                                Log.i("abc", "Loi");
                            })
            mCompositeDisposable.add(disposable)

            // Load product reviews data
            val productReviews = RequestProductReviews(mActivity, productId)
            productReviews.setResponseListener { data ->
                if (data != null) {
                    reviewList!!.addAll(data as ArrayList<ProductReview>)
                    if (reviewList!!.size > 0) {
                        reviewListAdapter!!.notifyDataSetChanged()
                    }
                }
            }
            productReviews.execute()
        }
    }


    private fun initListener() {
        btnAddToCart.setOnClickListener {
            // Add to cart list
            applicationContext.DbHelper.addToCart(Cart(
                    productId = product?.id,
                    price = product?.price?.toInt(),
                    productName = product?.name,
                    quantity = 1,
                    image = product?.images?.getFeaturedImage()?.src,
                    discount = 0,
                    beforeDiscount = product?.price?.toInt(),
                    afterDiscount = product?.price?.toInt()

            ))
            AppUtility.showToast(mContext, getString(R.string.added_to_cart))
        }

        cartList.setOnClickListener { ActivityUtils.instance.invokeActivity(this@ProductDetailsActivity, CartListActivity::class.java, false) }

        addWishList.setOnClickListener { toggleWishList() }

        searchButton.setOnClickListener { ActivityUtils.instance.invokeSearchActivity(this@ProductDetailsActivity, AppConstants.EMPTY_STRING) }

    }

    private fun loadProductPhoto(images: ArrayList<Image>?) {
        if (images != null && !images.isEmpty()) {
            productSliderAdapter = ProductSliderAdapter(mContext, images)
            mPager = findViewById<View>(R.id.vpImageSlider) as ViewPager
            mPager?.adapter = productSliderAdapter
            val indicator = findViewById<View>(R.id.sliderIndicator) as CircleIndicator
            indicator.setViewPager(mPager)
            productSliderAdapter?.setItemClickListener(object :OnItemClickListener{
                override fun onItemListener(view: View?, position: Int) {
                    ActivityUtils.instance.invokeImage(this@ProductDetailsActivity, images[position].src!!)
                }
            })
        }
    }

    private fun updateWishButton(prodId: Int) {
        addedToWishList = applicationContext.DbHelper.isAlreadyWished(prodId)
        if (addedToWishList) {
            addWishList.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.ic_wish))
        } else {
            addWishList.setImageDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.ic_un_wish))
        }
    }


    override fun onBackPressed() {
        super.onBackPressed()
        this.finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }


    private fun toggleWishList() {
        product?.let {
            if (!addedToWishList) {
                applicationContext.DbHelper.insertWishItem(it)
            } else {
                applicationContext.DbHelper.deleteWishItemById(it.id!!)

            }
            updateWishButton(it.id!!)
        }
    }
}
