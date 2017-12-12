package com.tungnui.abccomputer.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v4.view.ViewPager
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView

import com.tungnui.abccomputer.R
import com.tungnui.abccomputer.adapter.CategoryListAdapter
import com.tungnui.abccomputer.adapter.HomeProdListAdapter
import com.tungnui.abccomputer.adapter.ImageSliderAdapter
import com.tungnui.abccomputer.api.CategoryService
import com.tungnui.abccomputer.api.ProductService
import com.tungnui.abccomputer.api.ServiceGenerator
import com.tungnui.abccomputer.data.constant.AppConstants
import com.tungnui.abccomputer.data.sqlite.*
import com.tungnui.abccomputer.models.Category
import com.tungnui.abccomputer.models.Product
import com.tungnui.abccomputer.utils.ActivityUtils
import com.tungnui.abccomputer.utils.AdUtils
import com.tungnui.abccomputer.utils.AnalyticsUtils
import com.tungnui.abccomputer.utils.AppUtility
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.content_search_view.*
import kotlinx.android.synthetic.main.toolbar_main.*

import java.util.ArrayList
import java.util.Timer
import java.util.TimerTask

import me.relex.circleindicator.CircleIndicator

class MainActivity : BaseActivity() {
    private var mCompositeDisposable: CompositeDisposable
    val productService: ProductService
    val categoryService: CategoryService
    init {
        mCompositeDisposable = CompositeDisposable()
        productService = ServiceGenerator.createService(ProductService::class.java)
        categoryService = ServiceGenerator.createService(CategoryService::class.java)
    }

    // variables
    private var mContext: Context? = null
    private var mActivity: Activity? = null
    private lateinit var  categoryList: ArrayList<Category>
    private lateinit var featuredProdList: ArrayList<Product>
    private lateinit var recentProdList: ArrayList<Product>
    private lateinit var popularProdList: ArrayList<Product>
    private lateinit var sampleCategoryProducts: ArrayList<Product>
    private var categoryListAdapter: CategoryListAdapter? = null
    private var featuredProdListAdapter: HomeProdListAdapter? = null
    private var recentProdListAdapter: HomeProdListAdapter? = null
    private var popularProdListAdapter: HomeProdListAdapter? = null
    private var sampleCategoryAdapter: HomeProdListAdapter? = null

    // ui declaration
    private var rvCategory: RecyclerView? = null
    private var categoryParent: RelativeLayout? = null
    private var featureParent: RelativeLayout? = null
    private var recentParent: RelativeLayout? = null
    private var popularParent: RelativeLayout? = null
    private var sampleCatParent: RelativeLayout? = null
    private var featureProgress: ProgressBar? = null
    private var recentProgress: ProgressBar? = null
    private var popularProgress: ProgressBar? = null
    private var sampleProgress: ProgressBar? = null
    private var tvFeaturedListTitle: TextView? = null
    private var tvFeaturedListAll: TextView? = null
    private var tvRecentListTitle: TextView? = null
    private var tvRecentListAll: TextView? = null
    private var tvPopularListTitle: TextView? = null
    private var tvPopularListAll: TextView? = null
    private var tvSampleCategoryTitle: TextView? = null
    private var tvSampleCategoryAll: TextView? = null

    // view pager image slider
    private var mPager: ViewPager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
          super.onCreate(savedInstanceState)
        initVariable()
        initView()
        initListener()
        loadData()
        instance = this
    }

    override fun onResume() {
        super.onResume()
        loadCartCounter()
        loadNotificationCounter()


        // load full screen ad
        AdUtils.getInstance(mContext).loadFullScreenAd(mActivity)
    }

    private fun initVariable() {
        mContext = applicationContext
        mActivity = this@MainActivity
        categoryList = ArrayList()
        featuredProdList = ArrayList()
        recentProdList = ArrayList()
        popularProdList = ArrayList()
        sampleCategoryProducts = ArrayList()

        // analytics event trigger
        AnalyticsUtils.getAnalyticsUtils(mContext).trackEvent("Main Activity")
    }

    private fun initView() {

        // set parent view
        setContentView(R.layout.activity_main)

        // initiate drawer and toolbar
        initToolbar()
        initDrawer();
        initLoader()

        // cart counter


        // category list ui
        rvCategory = lytCategoryList.findViewById<View>(R.id.homeRecyclerView) as RecyclerView
        categoryParent = lytCategoryList.findViewById<View>(R.id.parentPanel) as RelativeLayout
        lytCategoryList.findViewById<View>(R.id.lytListHeader).visibility = View.GONE
        lytCategoryList.findViewById<View>(R.id.sectionProgress).visibility = View.GONE


        // featured list ui
        val rvFeaturedList = lytFeaturedList.findViewById<View>(R.id.homeRecyclerView) as RecyclerView
        tvFeaturedListTitle = lytFeaturedList.findViewById<View>(R.id.tvListTitle) as TextView
        tvFeaturedListAll = lytFeaturedList.findViewById<View>(R.id.tvSeeAll) as TextView
        featureParent = lytFeaturedList.findViewById<View>(R.id.parentPanel) as RelativeLayout
        featureProgress = lytFeaturedList.findViewById<View>(R.id.sectionProgress) as ProgressBar
        //rvFeaturedList.addItemDecoration(new GridSpacingItemDecoration(2, (int) getResources().getDimension(R.dimen.pad_margin_2dp), false));

        // recent list ui
        val rvRecentList = lytRecentList.findViewById<View>(R.id.homeRecyclerView) as RecyclerView
        tvRecentListTitle = lytRecentList.findViewById<View>(R.id.tvListTitle) as TextView
        tvRecentListAll = lytRecentList.findViewById<View>(R.id.tvSeeAll) as TextView
        recentParent = lytRecentList.findViewById<View>(R.id.parentPanel) as RelativeLayout
        recentProgress = lytRecentList.findViewById<View>(R.id.sectionProgress) as ProgressBar

        // popular list ui
        val rvPopularList = lytPopularList.findViewById<View>(R.id.homeRecyclerView) as RecyclerView
        tvPopularListTitle = lytPopularList.findViewById<View>(R.id.tvListTitle) as TextView
        tvPopularListAll = lytPopularList.findViewById<View>(R.id.tvSeeAll) as TextView
        popularParent = lytPopularList.findViewById<View>(R.id.parentPanel) as RelativeLayout
        popularProgress = lytPopularList.findViewById<View>(R.id.sectionProgress) as ProgressBar


        // init category RecyclerView
        rvCategory?.setHasFixedSize(true)
        rvCategory?.layoutManager = LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false)
        categoryListAdapter = CategoryListAdapter(categoryList){
            category -> ActivityUtils.instance.invokeProducts(
                activity = this@MainActivity,
                categoryId = category.id!!,
                title = category.name!!)
        }
        rvCategory?.adapter = categoryListAdapter

        // init featured product RecyclerView
        rvFeaturedList.layoutManager = GridLayoutManager(mActivity, COLUMN_SPAN_COUNT)
        featuredProdListAdapter = HomeProdListAdapter(featuredProdList){
            product->
            val productId = product.id ?: 0
            if (productId > 0) {
                val intent = Intent(mActivity, ProductDetailsActivity::class.java)
                intent.putExtra(AppConstants.PRODUCT_ID, productId)
                startActivity(intent)
            }
        }
        rvFeaturedList.adapter = featuredProdListAdapter

        // init recent product RecyclerView
        rvRecentList.layoutManager = GridLayoutManager(mActivity, COLUMN_SPAN_COUNT)
        recentProdListAdapter = HomeProdListAdapter(recentProdList){
            product ->
            product.id?.let{
                val intent = Intent(mActivity, ProductDetailsActivity::class.java)
                intent.putExtra(AppConstants.PRODUCT_ID, it)
                startActivity(intent)
            }
        }
        rvRecentList.adapter = recentProdListAdapter

        // init popular product RecyclerView
        rvPopularList.layoutManager = GridLayoutManager(mActivity, COLUMN_SPAN_COUNT)
        popularProdListAdapter = HomeProdListAdapter(popularProdList){
            product ->
            product.id?.let{
                val intent = Intent(mActivity, ProductDetailsActivity::class.java)
                intent.putExtra(AppConstants.PRODUCT_ID, it)
                startActivity(intent)
            }
        }
        rvPopularList.adapter = popularProdListAdapter



        AppUtility.noInternetWarning(findViewById(R.id.parentPanel), mContext)
        if (!AppUtility.isNetworkAvailable(mContext)) {
            showEmptyView()
        }
    }

    private fun loadData() {
        var disposable = categoryService.getCategory()
                .subscribeOn((Schedulers.io()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    Log.e("main",response.joinToString())
                    if (response != null) {
                        if (!categoryList.isEmpty()) {
                            categoryList.clear()
                        }
                        categoryList.addAll(response as ArrayList<Category>)

                        if (!categoryList.isEmpty()) {
                            // load slider images
                            categoryParent?.visibility = View.VISIBLE
                            categoryListAdapter?.notifyDataSetChanged()
                            loadCategorySlider()
                            hideLoader()
                        }
                    }
                },
                        { error ->
                            Log.e("main",error.message)
                        })
        mCompositeDisposable.add(disposable)


        // Load featured products
        disposable = productService.getFeatured(AppConstants.INITIAL_PAGE_NUMBER, AppConstants.MAX_PER_PAGE)
                .subscribeOn((Schedulers.io()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    if (response != null) {
                        featuredProdList.addAll(response)
                        if (!featuredProdList.isEmpty()) {
                            featureParent?.visibility = View.VISIBLE
                            tvFeaturedListTitle?.text = "SẢN PHẨM NỔI BẬT (" + featuredProdList!!.size + ")"   // set list title
                            featuredProdListAdapter?.setDisplayCount(AppConstants.HOME_ITEM_MAX)                                    // set display item limit
                            featuredProdListAdapter?.notifyDataSetChanged()
                        }
                    }
                    featureProgress!!.visibility = View.GONE
                    hideLoader()
                },
                        { error ->

                        })
        mCompositeDisposable.add(disposable)


        // Load recent products
        disposable = productService.getNewest(AppConstants.INITIAL_PAGE_NUMBER, AppConstants.MAX_PER_PAGE)
                .subscribeOn((Schedulers.io()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    if (response != null) {
                        recentProdList.addAll(response)

                        if (!recentProdList.isEmpty()) {
                            recentParent!!.visibility = View.VISIBLE
                            tvRecentListTitle!!.text = "SẢN PHẨM MỚI NHẤT"
                            recentProdListAdapter!!.setDisplayCount(AppConstants.HOME_ITEM_MAX)
                            recentProdListAdapter!!.notifyDataSetChanged()
                        }
                    }
                    recentProgress!!.visibility = View.GONE
                    hideLoader()
                },
                        { error ->

                        })
        mCompositeDisposable.add(disposable)

        // Load popular product
        disposable = productService.getSale(AppConstants.INITIAL_PAGE_NUMBER, AppConstants.MAX_PER_PAGE)
                .subscribeOn((Schedulers.io()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    if (response != null) {
                        popularProdList.addAll(response)

                        if (!popularProdList.isEmpty()) {
                            popularParent?.visibility = View.VISIBLE
                            tvPopularListTitle?.text = "KHUYẾN MÃI HOT NHẤT"
                            popularProdListAdapter?.setDisplayCount(AppConstants.HOME_ITEM_MAX)
                            popularProdListAdapter?.notifyDataSetChanged()
                        }
                    }
                    popularProgress?.visibility = View.GONE
                    hideLoader()
                },
                        { error ->

                        })
        mCompositeDisposable.add(disposable)



/*
        // Load tech categories products as sample category
        val requestCatProduct = RequestProducts(mActivity, AppConstants.INITIAL_PAGE_NUMBER, AppConstants.MAX_PER_PAGE, AppConstants.CATEGORY_TEC_PRODUCTS_ID, AppConstants.TYPE_CATEGORY)
        requestCatProduct.setResponseListener { data ->
            if (data != null) {
                sampleCategoryProducts!!.addAll(data as ArrayList<ProductDetail>)

                if (!sampleCategoryProducts!!.isEmpty()) {
                    sampleCatParent!!.visibility = View.VISIBLE
                    tvSampleCategoryTitle!!.text = "TECH DISCOVERY (" + sampleCategoryProducts!!.size + ")"
                    sampleCategoryAdapter!!.setDisplayCount(AppConstants.HOME_ITEM_MAX)
                    sampleCategoryAdapter!!.notifyDataSetChanged()
                }
            }
            sampleProgress!!.visibility = View.GONE
            hideLoader()
        }
        requestCatProduct.execute()*/
        // set cart counter
        loadCartCounter()
    }

    private fun initListener() {

       /* recentProdListAdapter!!.setItemClickListener { view, position ->
            val productId = recentProdList!![position].id
            if (productId > 0) {
                val intent = Intent(mActivity, ProductDetailsActivity::class.java)
                intent.putExtra(AppConstants.PRODUCT_ID, productId)
                startActivity(intent)
            }
        }

        popularProdListAdapter!!.setItemClickListener { view, position ->
            val productId = popularProdList!![position].id
            if (productId > 0) {
                val intent = Intent(mActivity, ProductDetailsActivity::class.java)
                intent.putExtra(AppConstants.PRODUCT_ID, productId)
                startActivity(intent)
            }
        }
        sampleCategoryAdapter!!.setItemClickListener { view, position ->
            val productId = sampleCategoryProducts!![position].id
            if (productId > 0) {
                val intent = Intent(mActivity, ProductDetailsActivity::class.java)
                intent.putExtra(AppConstants.PRODUCT_ID, productId)
                startActivity(intent)
            }
        }*/
        // toolbar cart action listener
        imgToolbarCart.setOnClickListener {
            ActivityUtils.instance.invokeActivity(this@MainActivity, CartListActivity::class.java, false);
        }
        // toolbar notification action listener
        imgNotification.setOnClickListener {
            ActivityUtils.instance.invokeActivity(this@MainActivity, NotificationActivity::class.java, false);
        }

        // search icon at home action listener
        ivSearchIcon.setOnClickListener {
            if (edtSearchProduct.text.toString().isEmpty()) {
                AppUtility.showToast(mContext, getString(R.string.type_something))
            } else {
                ActivityUtils.instance.invokeSearchActivity(this@MainActivity, edtSearchProduct.text.toString())
            }
        }


        edtSearchProduct.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                if (edtSearchProduct.text.toString().isEmpty()) {
                    AppUtility.showToast(mContext, getString(R.string.type_something))
                } else {
                    ActivityUtils.instance.invokeSearchActivity(this@MainActivity, edtSearchProduct.text.toString())
                }
                return@OnEditorActionListener true
            }
            false
        })

        // See all listener
        tvFeaturedListAll?.setOnClickListener {
            ActivityUtils.instance.invokeProducts(this@MainActivity,title =  getString(R.string.featured_items),type="featured") }

        tvRecentListAll?.setOnClickListener {
            ActivityUtils.instance.invokeProducts(this@MainActivity,title=getString(R.string.recent_items), type="newest") }

        tvPopularListAll?.setOnClickListener { ActivityUtils.instance.invokeProducts(this@MainActivity, title=getString(R.string.popular_items), type="sale") }

    }

    private fun loadCategorySlider() {
        val imageList = ArrayList<String>()
        for (i in categoryList.indices) {
            imageList.add(categoryList[i].image?.src!!)
        }
        val imageSliderAdapter = ImageSliderAdapter(mContext, imageList)
        mPager = findViewById<View>(R.id.vpImageSlider) as ViewPager
        mPager!!.adapter = imageSliderAdapter
        val indicator = findViewById<View>(R.id.sliderIndicator) as CircleIndicator
        indicator.setViewPager(mPager)

        // Auto start of viewpager
        val handler = Handler()
        val Update = Runnable {
            var setPosition = mPager!!.currentItem + 1
            if (setPosition == imageList.size) {
                setPosition = AppConstants.VALUE_ZERO
            }
            mPager!!.setCurrentItem(setPosition, true)
            try {
                rvCategory!!.smoothScrollToPosition(setPosition)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        //  Auto animated timer
        val swipeTimer = Timer()
        swipeTimer.schedule(object : TimerTask() {
            override fun run() {
                handler.post(Update)
            }
        }, 3000, 3000)


        // view page on item click listener
        imageSliderAdapter.setItemClickListener { view, position -> ActivityUtils.instance.invokeProducts(
                this@MainActivity,categoryList[position].id!!, categoryList[position].name!!)
        }

        mPager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                setCategoryItemSelection(position)
            }

            override fun onPageSelected(position: Int) {

            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })
    }


    private fun loadCartCounter() {
        var total = applicationContext.DbHelper.totalCartItem()
        if(total == 0){
            tvCartCounter.visibility = View.GONE
        }else{
            tvCartCounter.visibility = View.VISIBLE
            tvCartCounter.text = total.toString()
        }
    }

    private fun loadNotificationCounter() {
        try {
            val notifyController = NotificationDBController(mContext)
            notifyController.open()
            val notifyList = notifyController.unreadNotification
            notifyController.close()

            if (notifyList.isEmpty()) {
                tvNotificationCounter.visibility = View.GONE
            } else {
                tvNotificationCounter.visibility = View.VISIBLE
                tvNotificationCounter.text = notifyList.size.toString()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            tvNotificationCounter.visibility = View.GONE
        }

    }

    private fun setCategoryItemSelection(selectedIndex: Int) {
        rvCategory?.smoothScrollToPosition(selectedIndex)
        for (pageModel in categoryList) {
            pageModel.isSelected = false
        }
        categoryList[selectedIndex].isSelected = true
        categoryListAdapter?.notifyDataSetChanged()
    }

    companion object {
        private val COLUMN_SPAN_COUNT = 2
            @get:Synchronized
            private var instance: MainActivity? = null
            fun invalidateDrawerMenuHeader() {
                val instance = MainActivity.instance
                if (instance != null) {
                    instance.invalidateHeader()
                }
            }
    }
}
