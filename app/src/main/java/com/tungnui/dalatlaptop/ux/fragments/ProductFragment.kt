package com.tungnui.dalatlaptop.ux.fragments

import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.text.Html
import android.text.method.LinkMovementMethod
import android.util.DisplayMetrics
import android.view.*
import android.widget.TextView
import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper

import com.tungnui.dalatlaptop.CONST
import com.tungnui.dalatlaptop.R
import com.tungnui.dalatlaptop.api.ProductReviewService
import com.tungnui.dalatlaptop.models.Cart
import com.tungnui.dalatlaptop.utils.*
import com.tungnui.dalatlaptop.ux.MainActivity
import com.tungnui.dalatlaptop.ux.adapters.ProductImagesRecyclerAdapter
import com.tungnui.dalatlaptop.ux.adapters.RelatedProductsRecyclerAdapter
import com.tungnui.dalatlaptop.api.ProductService
import com.tungnui.dalatlaptop.api.ServiceGenerator
import com.tungnui.dalatlaptop.libraryhelper.Utils
import com.tungnui.dalatlaptop.models.Image
import com.tungnui.dalatlaptop.models.Product
import com.tungnui.dalatlaptop.utils.getFeaturedImage
import com.tungnui.dalatlaptop.ux.adapters.ProductReviewRecyclerAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_product.*
import org.jetbrains.anko.support.v4.toast


class ProductFragment : Fragment() {
    private var mCompositeDisposable: CompositeDisposable
    val productService: ProductService
    val productReviewService:ProductReviewService

    init {
        mCompositeDisposable = CompositeDisposable()
        productService = ServiceGenerator.createService(ProductService::class.java)
        productReviewService = ServiceGenerator.createService(ProductReviewService::class.java)
    }
    private  var product:Product? = null
    private lateinit var productImagesAdapter: ProductImagesRecyclerAdapter
    private lateinit var relatedProductsAdapter: RelatedProductsRecyclerAdapter
    private lateinit var customerReviewsAdapter:ProductReviewRecyclerAdapter
    private var scrollViewListener: ViewTreeObserver.OnScrollChangedListener? = null
    private  var productImages: List<Image>? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_product, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MainActivity.setActionBarTitle(getString(R.string.Product))
        prepareButtons()
        prepareProductImagesLayout()
        prepareScrollView()
        prepareRecommendAdapter()
        prepareCustomerReviewAdapter()
        val productId = arguments.getInt(PRODUCT_ID, 0)
        getProduct(productId)
        getCustomerReview(productId)
    }

    private fun getCustomerReview(productId: Int) {
        val disposable = productReviewService.getReview(productId)
                .subscribeOn((Schedulers.io()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    customerReviewsAdapter.refreshItems(response)
                },
                        { error ->
                            toast(error.message!!)
                            setContentVisible(CONST.VISIBLE.EMPTY)
                            toast("Lỗi kết nối")
                        })
        mCompositeDisposable.add(disposable)


    }


    private fun prepareButtons() {
        product_add_to_cart.setOnClickListener{
            postProductToCart()
        }
    }

    private fun prepareProductImagesLayout() {
        val linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        product_images_recycler_view.layoutManager = linearLayoutManager
        productImagesAdapter = ProductImagesRecyclerAdapter { position ->
            //val imagesDialog = ProductImagesDialogFragment.newInstance(productImages, position)
           // if (imagesDialog != null)
             //   imagesDialog.show(fragmentManager, ProductImagesDialogFragment2::class.java.simpleName)
        }
        product_images_recycler_view.adapter = productImagesAdapter

        val params = product_images_recycler_view.layoutParams
        val dm = activity.resources.displayMetrics
        val densityDpi = dm.densityDpi

        // For small screen even smaller images.
        if (densityDpi <= DisplayMetrics.DENSITY_MEDIUM) {
            params.height = (dm.heightPixels * 0.4).toInt()
        } else {
            params.height = (dm.heightPixels * 0.48).toInt()
        }
    }
    private fun prepareCustomerReviewAdapter(){
        customerReviewsAdapter = ProductReviewRecyclerAdapter()
        product_customer_review_recycler.setHasFixedSize(true)
        product_customer_review_recycler.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
        product_customer_review_recycler.itemAnimator = DefaultItemAnimator()
        product_customer_review_recycler.adapter = customerReviewsAdapter
    }

    private fun prepareRecommendAdapter() {
            relatedProductsAdapter = RelatedProductsRecyclerAdapter {
                product ->  product.id?.let {
                    if (activity is MainActivity) {
                        (activity as MainActivity).onProductSelected(it)
                    }
                }
            }

        product_recommended_recycler.setHasFixedSize(true)
        product_recommended_recycler.setLayoutManager(LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false))
        val snapHelperStart = GravitySnapHelper(Gravity.START)
        snapHelperStart.attachToRecyclerView(product_recommended_recycler)
        product_recommended_recycler.itemAnimator = DefaultItemAnimator()
        product_recommended_recycler.adapter =  relatedProductsAdapter
    }


    private fun prepareScrollView() {
        scrollViewListener = object : ViewTreeObserver.OnScrollChangedListener {
            private var alphaFull = false
            override fun onScrollChanged() {
                val scrollY = product_scroll_layout.scrollY
                if (product_images_recycler_view != null) {
                    val alphaRatio: Float
                    if (product_images_recycler_view.height > scrollY) {
                        product_images_recycler_view.translationY = (scrollY / 2).toFloat()
                        alphaRatio = scrollY.toFloat() / product_images_recycler_view.height
                    } else {
                        alphaRatio = 1f
                    }

                    if (alphaFull) {
                        if (alphaRatio <= 0.99) alphaFull = false
                    } else {
                        if (alphaRatio >= 0.9) alphaFull = true
                        product_background.alpha = alphaRatio
                    }
                }
            }
        }
    }


    private fun getProduct(productId: Int) {
        var disposable = productService.single(productId)
                .subscribeOn((Schedulers.io()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    MainActivity.setActionBarTitle(response.name)
                    refreshScreenData(response)
                    product = response
                    productImages = response.images
                    response.images?.let { productImagesAdapter.addAll(it) }
                    getRecommendProducts(response.relatedIds!!)
                    setContentVisible(CONST.VISIBLE.CONTENT)
                },
                        { error ->
                            toast(error.message!!)
                            setContentVisible(CONST.VISIBLE.EMPTY)
                            toast("Lỗi kết nối")
                        })
        mCompositeDisposable.add(disposable)

    }
    private fun getRecommendProducts(related: List<Int>) {
        var disposable = productService.getRecommentProducts(related)
                .subscribeOn((Schedulers.io()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ products ->
                    for (prod in products) {
                        relatedProductsAdapter.addLast(prod)
                    }
                },
                        { error ->
                            setContentVisible(CONST.VISIBLE.EMPTY)
                            toast("Lỗi kết nối")
                        })
        mCompositeDisposable.add(disposable)
    }
    private fun refreshScreenData(product: Product?) {
        if (product != null) {
            product_name?.text = product.name
            if (product.onSale) {
                product_price?.text = product.salePrice.toString().formatPrice()
                product_regular_price?.text = product.regularPrice.toString().formatPrice()
                product_regular_price?.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG or Paint.ANTI_ALIAS_FLAG
                product_price_discount_percent?.visibility = View.VISIBLE

                ifNotNull(product.regularPrice?.toDouble(),product.salePrice?.toDouble()){
                    price,sale->   product_price_discount_percent?.text = Utils.calculateDiscountPercent(context, price ,sale )
                }
            } else {
                product_price?.text = product.regularPrice.toString().formatPrice()
                product_regular_price?.visibility = View.INVISIBLE
                product_price_discount_percent?.visibility = View.INVISIBLE
            }
            product_rating_count?.text="(${product.ratingCount} nhận xét)"
            product.averageRating?.let{product_rating.rating = it.toFloat()}
            if (product.description != null) {
                product_info?.movementMethod = LinkMovementMethod.getInstance()
                product_info?.text = Html.fromHtml(product.description)
            }
        }
    }




    private fun postProductToCart() {
        context.cartHelper.addToCart(Cart(
                productId = product?.id,
                price = product?.price?.toInt(),
                productName = product?.name,
                quantity = 1,
                image = product?.images?.getFeaturedImage()?.src
        ))
        MainActivity.updateCartCountNotification()
        val result ="Sản phẩm được thêm vào giỏ hàng"
        val snackbar = Snackbar.make(product_container, result, Snackbar.LENGTH_LONG)
                .setAction(R.string.Go_to_cart) {
                    if (activity is MainActivity)
                        (activity as MainActivity).onCartSelected()
                }
        val textView = snackbar.view.findViewById<TextView>(android.support.design.R.id.snackbar_text)
        textView.setTextColor(Color.WHITE)
        snackbar.show()
   }

     private fun setContentVisible(visible: CONST.VISIBLE) {
        if (product_empty_layout != null && product_scroll_layout != null && product_progress != null) {
            when (visible) {
                CONST.VISIBLE.EMPTY -> {
                    product_empty_layout?.visibility = View.VISIBLE
                    product_scroll_layout?.visibility = View.INVISIBLE
                    product_progress?.visibility = View.GONE
                }
                CONST.VISIBLE.PROGRESS -> {
                    product_empty_layout?.visibility = View.GONE
                    product_scroll_layout?.visibility = View.INVISIBLE
                    product_progress?.visibility = View.VISIBLE
                }
                else // Content
                -> {
                    product_empty_layout?.visibility = View.GONE
                    product_scroll_layout?.visibility = View.VISIBLE
                    product_progress?.visibility = View.GONE
                }

            }
        }
    }

    override fun onResume() {
        product_scroll_layout?.viewTreeObserver?.addOnScrollChangedListener(scrollViewListener)
        super.onResume()
    }

    override fun onPause() {
        product_scroll_layout?.viewTreeObserver?.removeOnScrollChangedListener(scrollViewListener)
        super.onPause()
    }

    override fun onStop() {
        setContentVisible(CONST.VISIBLE.CONTENT)
        super.onStop()
    }

    companion object {
        private val PRODUCT_ID = "product_id"
        fun newInstance(productId: Int): ProductFragment {
            val args = Bundle()
            args.putInt(PRODUCT_ID, productId)
            val fragment = ProductFragment()
            fragment.arguments = args
            return fragment
        }
    }
}