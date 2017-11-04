package com.tungnui.dalatlaptop.ux.fragments


import android.app.ProgressDialog
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper

import com.tungnui.dalatlaptop.CONST
import com.tungnui.dalatlaptop.MyApplication
import com.tungnui.dalatlaptop.R
import com.tungnui.dalatlaptop.api.ProductService
import com.tungnui.dalatlaptop.api.ServiceGenerator
import com.tungnui.dalatlaptop.listeners.OnSingleClickListener
import com.tungnui.dalatlaptop.utils.Utils
import com.tungnui.dalatlaptop.utils.loadImg
import com.tungnui.dalatlaptop.ux.MainActivity
import com.tungnui.dalatlaptop.ux.adapters.HomeProductRecyclerAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_banners.*
import org.jetbrains.anko.support.v4.progressDialog
import org.jetbrains.anko.support.v4.toast

/**
 * Provides "welcome" screen customizable from web administration. Often contains banners with sales or best products.
 */
class BannersFragment : Fragment() {
    private var mCompositeDisposable: CompositeDisposable
    val productService: ProductService
    init {
        mCompositeDisposable = CompositeDisposable()
        productService = ServiceGenerator.createService(ProductService::class.java)
    }
    private var progressDialog: ProgressDialog? = null


    private lateinit var newestRecyclerAdapter: HomeProductRecyclerAdapter
    private lateinit var saleRecycleAdapter:HomeProductRecyclerAdapter
    private lateinit var featuredRecycleAdapter:HomeProductRecyclerAdapter
    private var mAlreadyLoaded = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_banners, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MainActivity.setActionBarTitle(getString(R.string.app_name))
        progressDialog = Utils.generateProgressDialog(context,false)
        initCarousel()
        prepareAction()
       // prepareEmptyContent()
        // Don't reload data when return from backStack. Reload if a new instance was created or data was empty.
        if (savedInstanceState == null && !mAlreadyLoaded ||
                 saleRecycleAdapter.itemCount==0 || featuredRecycleAdapter.itemCount == 0
                || newestRecyclerAdapter.itemCount == 0) {
            mAlreadyLoaded = true

            // Prepare views and listeners
            prepareContentViews(true)
            loadData()
        } else {
            prepareContentViews(false)
            // Already loaded
        }
    }

    private fun prepareAction() {
        home_hot_sale_title.setOnClickListener {
            (activity as MainActivity).onCategorySelected("sale", "Khuyến mãi tốt nhất")
        }
        home_featured_title.setOnClickListener {
            (activity as MainActivity).onCategorySelected("featured", "Sản phẩm nổi bật nhất")
        }
        home_newest_title.setOnClickListener {
            (activity as MainActivity).onCategorySelected("newest", "Sản phẩm mới nhất")
        }
    }

    private fun prepareNewestAdapter(frestStart: Boolean) {
        if(frestStart){
        newestRecyclerAdapter = HomeProductRecyclerAdapter {
            product-> product.id?.let{(activity as MainActivity).onProductSelected(it)}
        }
        }
        home_newest_recycler.setHasFixedSize(true)
        home_newest_recycler.setLayoutManager(LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false))
        val snapHelperStart = GravitySnapHelper(Gravity.START)
        snapHelperStart.attachToRecyclerView(home_newest_recycler)
        home_newest_recycler.itemAnimator = DefaultItemAnimator()
        home_newest_recycler.adapter =  newestRecyclerAdapter
    }
    private fun prepareSaleAdapter(frestStart: Boolean) {
        if(frestStart) {
            saleRecycleAdapter = HomeProductRecyclerAdapter { product ->
                product.id?.let { (activity as MainActivity).onProductSelected(it) }
            }
        }
        home_sale_recycler.setHasFixedSize(true)
        home_sale_recycler.setLayoutManager(LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false))
        val snapHelperStart = GravitySnapHelper(Gravity.START)
        snapHelperStart.attachToRecyclerView(home_newest_recycler)
        home_sale_recycler.itemAnimator = DefaultItemAnimator()
        home_sale_recycler.adapter =  saleRecycleAdapter
    }
    private fun loadData(){
        loadFeatured()
        loadNewest()
        loadSale()
    }
    private fun prepareContentViews(frestStart:Boolean){
        if(frestStart)
        {
        prepareSaleAdapter(true)
        prepareNewestAdapter(true)
        prepareFeaturedProduct(true)
        }else{
            prepareSaleAdapter(false)
            prepareNewestAdapter(false)
            prepareFeaturedProduct(false)
        }
    }
    private fun prepareFeaturedProduct(frestStart: Boolean){
        if(frestStart){
        featuredRecycleAdapter = HomeProductRecyclerAdapter {
            product-> product.id?.let{(activity as MainActivity).onProductSelected(it)}
        }}
        home_featured_recycler.setHasFixedSize(true)
        home_featured_recycler.setLayoutManager(LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false))
        val snapHelperStart = GravitySnapHelper(Gravity.START)
        snapHelperStart.attachToRecyclerView(home_featured_recycler)
        home_featured_recycler.itemAnimator = DefaultItemAnimator()
        home_featured_recycler.adapter =  featuredRecycleAdapter
    }

    private fun loadFeatured() {
        progressDialog?.show()
        var disposable = productService.getFeatured()
                .subscribeOn((Schedulers.io()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    featuredRecycleAdapter.addProducts(response)
                    progressDialog?.cancel()
                },
                        { error ->
                            progressDialog?.cancel()
                            toast("Lỗi kết nối")
                        })
        mCompositeDisposable.add(disposable)
    }

    private fun loadSale() {
        progressDialog?.show()
        var disposable = productService.getSale()
                .subscribeOn((Schedulers.io()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    saleRecycleAdapter.addProducts(response)
                    progressDialog?.cancel()
                },
                        { error ->
                            progressDialog?.cancel()
                            toast("Lỗi kết nối")
                        })
        mCompositeDisposable.add(disposable)
    }

  /* private fun prepareEmptyContent() {
        banners_empty_action.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View) {
                // Just open drawer menu.
                val activity = activity
                if (activity is MainActivity) {
                    if (activity.drawerFragment != null)
                        activity.drawerFragment?.toggleDrawerMenu()
                }
            }
        })
    }*/
    private fun initCarousel() {
        var adImages = listOf(
                "https://dalatlaptop.tungnui.com/wp-content/uploads/2017/11/banner1.jpg",
                "https://dalatlaptop.tungnui.com/wp-content/uploads/2017/11/banner2.jpg",
                "https://dalatlaptop.tungnui.com/wp-content/uploads/2017/11/banner3.jpg")
        home_carousel_banner.setImageListener { position, imageView ->
            imageView.loadImg(adImages[position])
            imageView.scaleType = ImageView.ScaleType.FIT_XY

        }
        home_carousel_banner.setPageCount(adImages.size)
    }
    private fun loadNewest(){
        progressDialog?.show()
        val disposable = productService.getNewest()
                .subscribeOn((Schedulers.io()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                   newestRecyclerAdapter.addProducts(response)
                    progressDialog?.cancel()
                },
                        { error ->
                            progressDialog?.cancel()
                            toast("Lỗi kết nối")
                        })
        mCompositeDisposable.add(disposable)
    }


    override fun onStop() {
        if (progressDialog != null) {
            progressDialog?.cancel()
        }
        super.onStop()
    }
}
