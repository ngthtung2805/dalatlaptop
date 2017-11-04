package com.tungnui.dalatlaptop.ux.fragments

import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.text.Html
import android.text.method.LinkMovementMethod
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.TextView

import com.facebook.share.model.ShareLinkContent
import com.facebook.share.widget.MessageDialog

import com.tungnui.dalatlaptop.CONST
import com.tungnui.dalatlaptop.MyApplication
import com.tungnui.dalatlaptop.R
import com.tungnui.dalatlaptop.listeners.OnSingleClickListener
import com.tungnui.dalatlaptop.models.Cart
import com.tungnui.dalatlaptop.utils.*
import com.tungnui.dalatlaptop.ux.MainActivity
import com.tungnui.dalatlaptop.ux.adapters.ProductImagesRecyclerAdapter
import com.tungnui.dalatlaptop.ux.adapters.RelatedProductsRecyclerAdapter
import com.tungnui.dalatlaptop.ux.dialogs.ProductImagesDialogFragment
import com.tungnui.dalatlaptop.api.ProductService
import com.tungnui.dalatlaptop.api.ServiceGenerator
import com.tungnui.dalatlaptop.models.Image
import com.tungnui.dalatlaptop.models.Product
import com.tungnui.dalatlaptop.utils.getFeaturedImage
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_product.*
import org.jetbrains.anko.support.v4.toast
import timber.log.Timber
import java.text.DecimalFormat

/**
 * Fragment shows a detail of the product.
 */
class ProductFragment : Fragment() {
    private var mCompositeDisposable: CompositeDisposable
    val productService: ProductService

    init {
        mCompositeDisposable = CompositeDisposable()
        productService = ServiceGenerator.createService(ProductService::class.java)
    }
    private  var product:Product? = null
    private lateinit var productImagesAdapter: ProductImagesRecyclerAdapter
    private lateinit var relatedProductsAdapter: RelatedProductsRecyclerAdapter
    private var scrollViewListener: ViewTreeObserver.OnScrollChangedListener? = null
    private  var productImages: List<Image>? = null
    private var productRelatedIds :List<Int>? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_product, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MainActivity.setActionBarTitle(getString(R.string.Product))
        prepareButtons()
        prepareProductImagesLayout()
        prepareScrollView()

        val productId = arguments.getInt(PRODUCT_ID, 0)
        getProduct(productId)
    }

    /**
     * Prepare buttons views and listeners.
     *
     * @param view fragment base view.
     */
    private fun prepareButtons() {
        product_add_to_cart_progress.indeterminateDrawable.setColorFilter(ContextCompat.getColor(activity, R.color.textIconColorPrimary), PorterDuff.Mode.MULTIPLY)
        product_add_to_cart_layout.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View) {
                postProductToCart()
            }
        })

        product_send_to_a_friend.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View) {
                if (MyApplication.instance!!.isDataConnected) {
                    Timber.d("FragmentProductDetail share link clicked")
                    // send message with prepared content
                    try {
                        val messageDialog = MessageDialog(activity)
                        if (MessageDialog.canShow(ShareLinkContent::class.java)) {
                            /* val linkContent = ShareLinkContent.Builder()
                                     .setContentTitle(product!!.name)
                                     .setContentDescription(product!!.description)
                                     .setContentUrl(Uri.parse(product!!.url))
                                     .setImageUrl(Uri.parse(product!!.mainImage))
                                     .build()
                             messageDialog.show(linkContent)*/
                        } else {
                            Timber.e("FragmentProductDetail - APP is NOT installed")
                            val appPackageName = "com.facebook.orca" // getPackageName() from Context or Activity object
                            try {
                                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)))
                            } catch (anfe: android.content.ActivityNotFoundException) {
                                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)))
                            }

                        }
                    } catch (e: Exception) {
                        Timber.e(e, "Create share dialog exception")
                    }

                } else {
                    MsgUtils.showToast(activity, MsgUtils.TOAST_TYPE_NO_NETWORK, null, MsgUtils.ToastLength.SHORT)
                }
            }
        })
    }

    /**
     * Prepare product images and related products views, adapters and listeners.
     *
     * @param view fragment base view.
     */
    private fun prepareProductImagesLayout() {
        val linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        product_images_recycler_view.layoutManager = linearLayoutManager
        productImagesAdapter = ProductImagesRecyclerAdapter { position ->
            val imagesDialog = ProductImagesDialogFragment.newInstance(productImages, position)
            if (imagesDialog != null)
                imagesDialog.show(fragmentManager, ProductImagesDialogFragment::class.java.simpleName)
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

        // Prepare related products

        product_recommended_images_recycler.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        product_recommended_images_recycler.addItemDecoration(RecyclerMarginDecorator(context, RecyclerMarginDecorator.ORIENTATION.HORIZONTAL))
        relatedProductsAdapter = RelatedProductsRecyclerAdapter { product ->
            product.id?.let {
                if (activity is MainActivity) {
                    (activity as MainActivity).onProductSelected(it)
                }
            }
        }
    }


    /**
     * Prepare scroll view related animations and floating wishlist button.
     *
     * @param view fragment base view.
     */
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
                    //                    Timber.e("scrollY:" + scrollY + ". Alpha:" + alphaRatio);

                    if (alphaFull) {
                        if (alphaRatio <= 0.99) alphaFull = false
                    } else {
                        if (alphaRatio >= 0.9) alphaFull = true
                        product_background.alpha = alphaRatio
                    }
                } else {
                    Timber.e("Null productImagesScroll")
                }
            }
        }
    }

    /**
     * Load product data.
     *
     * @param productId id of product.
     */
    private fun getProduct(productId: Int) {
        var disposable = productService.single(productId)
                .subscribeOn((Schedulers.io()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    MainActivity.setActionBarTitle(response.name)
                    productRelatedIds = response.relatedIds
                    refreshScreenData(response)
                    product = response
                    productImages = response.images
                    response.images?.let { productImagesAdapter.addAll(it) }
                    setContentVisible(CONST.VISIBLE.CONTENT)
                },
                        { error ->
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
            product_name.text = product.name
            val formatter = DecimalFormat("#,###")
            if (product.onSale) {
                product_price_discount.text = "${formatter.format(product.salePrice?.toDouble())}đ"
                product_price.visibility = View.VISIBLE
                product_price.text = "${formatter.format(product.regularPrice?.toDouble())}đ"
                product_price.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG or Paint.ANTI_ALIAS_FLAG
                product_price_discount_percent.visibility = View.VISIBLE
                ifNotNull(product.regularPrice?.toDouble(),product.salePrice?.toDouble()){
                    price,sale->   product_price_discount_percent.text = Utils.calculateDiscountPercent(context, price ,sale )
                }
            } else {
                product_price.text = "${formatter.format(product.regularPrice?.toDouble())}đ"
                product_price.visibility = View.GONE
                product_price_discount_percent.visibility = View.GONE
            }
            if (product.description != null) {
                product_info.movementMethod = LinkMovementMethod.getInstance()
                product_info.text = Utils.safeURLSpanLinks(Html.fromHtml(product.description), activity)
            }
        } else {
            MsgUtils.showToast(activity, MsgUtils.TOAST_TYPE_INTERNAL_ERROR, getString(R.string.Internal_error), MsgUtils.ToastLength.LONG)
            Timber.e(RuntimeException(), "Refresh product screen with null product")
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
        val result = getString(R.string.Product) + " " + getString(R.string.added_to_cart)
        val snackbar = Snackbar.make(product_container, result, Snackbar.LENGTH_LONG)
                .setAction(R.string.Go_to_cart) {
                    if (activity is MainActivity)
                        (activity as MainActivity).onCartSelected()
                }
        val textView = snackbar.view.findViewById<TextView>(android.support.design.R.id.snackbar_text)
        textView.setTextColor(Color.WHITE)
        snackbar.show()
   }

    /**
     * Display content layout, progress bar or empty layout.
     *
     * @param visible enum value defining visible layout.
     */
    private fun setContentVisible(visible: CONST.VISIBLE) {
        if (product_empty_layout != null && product_scroll_layout != null && product_progress != null) {
            when (visible) {
                CONST.VISIBLE.EMPTY -> {
                    product_empty_layout.visibility = View.VISIBLE
                    product_scroll_layout.visibility = View.INVISIBLE
                    product_progress.visibility = View.GONE
                }
                CONST.VISIBLE.PROGRESS -> {
                    product_empty_layout.visibility = View.GONE
                    product_scroll_layout.visibility = View.INVISIBLE
                    product_progress.visibility = View.VISIBLE
                }
                else // Content
                -> {
                    product_empty_layout.visibility = View.GONE
                    product_scroll_layout.visibility = View.VISIBLE
                    product_progress.visibility = View.GONE
                }

            }
        } else {
            Timber.e(RuntimeException(), "Setting content visibility with null views.")
        }
    }

    override fun onResume() {
        product_scroll_layout.viewTreeObserver.addOnScrollChangedListener(scrollViewListener)
        super.onResume()
    }

    override fun onPause() {
        product_scroll_layout.viewTreeObserver.removeOnScrollChangedListener(scrollViewListener)
        super.onPause()
    }

    override fun onStop() {
        setContentVisible(CONST.VISIBLE.CONTENT)
        product_add_to_cart_image.visibility = View.VISIBLE
        product_add_to_cart_progress.visibility = View.INVISIBLE
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

