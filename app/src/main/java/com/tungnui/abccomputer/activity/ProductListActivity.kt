package com.tungnui.abccomputer.activity

import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.animation.*

import com.tungnui.abccomputer.R
import com.tungnui.abccomputer.adapter.ProductsGridAdapter
import com.tungnui.abccomputer.adapter.ProductsLinearAdapter
import com.tungnui.abccomputer.adapter.RecyclerMarginDecorator
import com.tungnui.abccomputer.api.CategoryService
import com.tungnui.abccomputer.api.ProductService
import com.tungnui.abccomputer.api.ServiceGenerator
import com.tungnui.abccomputer.data.constant.AppConstants
import com.tungnui.abccomputer.listener.EndlessRecyclerScrollListener
import com.tungnui.abccomputer.listeners.OnSingleClickListener
import com.tungnui.abccomputer.utils.ActivityUtils
import com.tungnui.abccomputer.utils.getNextUrl
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.content_product_list.*
import kotlinx.android.synthetic.main.toolbar_product_list.*
import java.io.UnsupportedEncodingException
import java.net.URLEncoder

class ProductListActivity : BaseActivity() {
    private var mCompositeDisposable: CompositeDisposable
    val productService: ProductService
    val categoryService: CategoryService

    init {
        mCompositeDisposable = CompositeDisposable()
        productService = ServiceGenerator.createService(ProductService::class.java)
        categoryService = ServiceGenerator.createService(CategoryService::class.java)
    }

    private var firstTimeSort = true
    private var categoryId: Int = 0
    private var type: String = ""
    private var searchQuery: String = ""
    private var productsNextLink: String? = null
    private lateinit var productsRecyclerLayoutManager: GridLayoutManager
    private lateinit var productGridAdapter: ProductsGridAdapter
    private lateinit var productLinearAdapter: ProductsLinearAdapter
    private var endlessRecyclerScrollListener: EndlessRecyclerScrollListener? = null
    private var filterParameters: String? = null
    private var toolbarOffset = -1
    private var isList = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        val intent = intent
        var categoryName: String = ""
        if (intent.hasExtra(AppConstants.CATEGORY_ID)) {
            categoryId = intent.getIntExtra(AppConstants.CATEGORY_ID, 0)
        }
        if (intent.hasExtra(AppConstants.CATEGORY_NAME)) {
            categoryName = intent.getStringExtra(AppConstants.CATEGORY_NAME)
        }
        if (intent.hasExtra(AppConstants.SEARCH_QUERY)) {
            searchQuery = intent.getStringExtra(AppConstants.SEARCH_QUERY)
        }
        if (intent.hasExtra(AppConstants.TYPE)) {
            type = intent.getStringExtra(AppConstants.TYPE)
        }

        setToolbarTitle(categoryName)
        prepareRecyclerAdapter()
        prepareProductRecycler()
        getProducts(null)


    }

    private fun prepareRecyclerAdapter() {
        productGridAdapter = ProductsGridAdapter() { product ->
            product.id?.let {
                ActivityUtils.instance.invokeProductDetails(this@ProductListActivity, it)
            }
        }
        productLinearAdapter = ProductsLinearAdapter() { product ->
            product.id?.let {
                ActivityUtils.instance.invokeProductDetails(this@ProductListActivity, it)
            }
        }
    }

    private fun prepareProductRecycler() {
        category_products_recycler.addItemDecoration(RecyclerMarginDecorator(this@ProductListActivity, RecyclerMarginDecorator.ORIENTATION.BOTH))
        category_products_recycler.itemAnimator = DefaultItemAnimator()
        category_products_recycler.setHasFixedSize(true)
        productsRecyclerLayoutManager = GridLayoutManager(this@ProductListActivity, 1)
        productsRecyclerLayoutManager = GridLayoutManager(this@ProductListActivity, 2)
        category_products_recycler.layoutManager = productsRecyclerLayoutManager
        endlessRecyclerScrollListener = object : EndlessRecyclerScrollListener(productsRecyclerLayoutManager) {
            override fun onLoadMore(currentPage: Int) {
                if (productsNextLink != null) {
                    getProducts(productsNextLink)
                } else {

                }
            }
        }
        category_products_recycler.addOnScrollListener(endlessRecyclerScrollListener)
        category_products_recycler.adapter = productGridAdapter

        viewToggle.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View) {
                if (isList) {
                    isList = false
                    viewToggle.setImageResource(R.drawable.ic_grid);
                    category_products_recycler.adapter = productGridAdapter
                    animateRecyclerLayoutChange(2)
                } else {
                    isList = true
                    viewToggle.setImageResource(R.drawable.ic_list);
                    category_products_recycler.adapter = productLinearAdapter
                    animateRecyclerLayoutChange(1)
                }
            }
        })
    }

    private fun animateRecyclerLayoutChange(layoutSpanCount: Int) {
        val fadeOut = AlphaAnimation(1f, 0f)
        fadeOut.interpolator = DecelerateInterpolator()
        fadeOut.duration = 400
        fadeOut.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}

            override fun onAnimationRepeat(animation: Animation) {}

            override fun onAnimationEnd(animation: Animation) {
                productsRecyclerLayoutManager.spanCount = layoutSpanCount
                productsRecyclerLayoutManager.requestLayout()
                val fadeIn = AlphaAnimation(0f, 1f)
                fadeIn.interpolator = AccelerateInterpolator()
                fadeIn.duration = 400
                category_products_recycler.startAnimation(fadeIn)
            }
        })
        category_products_recycler.startAnimation(fadeOut)
    }


    private fun getProducts(url: String?) {
        var url = url
        category_load_more_progress.visibility = View.VISIBLE
        if (url == null) {
            if (endlessRecyclerScrollListener != null)
                endlessRecyclerScrollListener?.clean()
            if (isList) {
                productLinearAdapter.clear()
            } else {
                productGridAdapter.clear()
            }
            url = "https://abccomputer.tungnui.com/wp-json/wc/v2/products"
            // Build request url
            if (searchQuery != "") {
                var newSearchQueryString: String
                try {
                    newSearchQueryString = URLEncoder.encode(searchQuery, "UTF-8")
                } catch (e: UnsupportedEncodingException) {
                    newSearchQueryString = URLEncoder.encode(searchQuery)
                }
                url += "?search=" + newSearchQueryString
            } else if (categoryId != 0) {
                url += "?category=$categoryId"
            } else if (type != "") {
                when (type) {
                    "featured" -> url += "?featured=true"
                    "sale" -> url += "?on_sale=true"
                    "newest" -> url += "?order=desc&order_by=date"
                }
            }
            // Add filters parameter if exist
            if (filterParameters != null && !filterParameters!!.isEmpty()) {
                url += filterParameters
            }

            /*  val sortItem = category_sort_spinner.selectedItem as SortItem
              if (sortItem != null) {
                  url = url + "&orderby=" + sortItem.value
              }*/
        }
        Log.i("URL", url)

        var disposable = url?.let {
            productService.getAll(it)
                    .subscribeOn((Schedulers.io()))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ response ->
                        firstTimeSort = false
                        response.body()?.let {
                            productLinearAdapter.addProducts(it)
                            productGridAdapter.addProducts(it)
                        }
                        productsNextLink = response.headers()["Link"]?.getNextUrl()
                        checkEmptyContent()
                        category_load_more_progress?.let { it.visibility = View.INVISIBLE }
                    },
                            { error ->
                                category_load_more_progress?.let { it.visibility = View.INVISIBLE }
                                checkEmptyContent()
                            })
        }
        disposable?.let { mCompositeDisposable.add(it) }
    }

    private fun checkEmptyContent() {
        var adapter = if (isList) productLinearAdapter else productGridAdapter
        if (adapter.itemCount > 0) {
            category_products_empty?.let { it.visibility = View.INVISIBLE }
            category_products_recycler?.let { it.visibility = View.VISIBLE }
        } else {
            category_products_empty?.let { it.visibility = View.VISIBLE }
            category_products_recycler?.let { it.visibility = View.INVISIBLE }
        }
    }

    private fun initView() {
        setContentView(R.layout.activity_product_list)
        initToolbar()
        enableBackButton()
        //setToolbarTitle(title!!)
        //initLoader()
        val fadeIn = AnimationUtils.loadAnimation(applicationContext, R.anim.fade_in_slowed)
        val fadeOut = AnimationUtils.loadAnimation(applicationContext, android.R.anim.fade_out)
        prepareRecyclerAdapter()

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
}
