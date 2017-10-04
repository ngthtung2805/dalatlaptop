package com.tungnui.dalatlaptop.features.home

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.*
import com.tungnui.dalatlaptop.R
import com.tungnui.dalatlaptop.models.Category
import com.tungnui.dalatlaptop.models.Product
import com.tungnui.dalatlaptop.utils.inflate
import com.tungnui.dalatlaptop.utils.loadImg
import kotlinx.android.synthetic.main.home_fragment.*
import android.widget.ImageView
import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper
import com.tungnui.dalatlaptop.HomeActivity
import com.tungnui.dalatlaptop.api.CategoryService
import com.tungnui.dalatlaptop.api.ProductService
import com.tungnui.dalatlaptop.api.ServiceGenerator
import com.tungnui.dalatlaptop.features.adapter.CategoryAdapter
import com.tungnui.dalatlaptop.features.adapter.ProductGridAdapter
import com.tungnui.dalatlaptop.features.category.CategoryFragment


/**
 * Created by thanh on 23/09/2017.
 */
class HomeFragment : Fragment(), HomeContract.View {
    override lateinit var presenter: HomeContract.Presenter
    private val categoryAdapter = CategoryAdapter()
    private val newestProductAdapter = ProductGridAdapter()
    private val saleProductAdapter=ProductGridAdapter()
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        presenter = HomePresenter(ServiceGenerator.createService(CategoryService::class.java),
                ServiceGenerator.createService(ProductService::class.java),this).apply {
            // Load previously saved state, if available.
            if (savedInstanceState != null) {

            }
        }
        initCarousel()
        initCategory()
        initNewestProduct()
        initSaleProduct()
        txtViewAll.setOnClickListener{
            (activity as HomeActivity).showFragment(CategoryFragment())
        }
    }

    private fun initSaleProduct() {
        rcvSaleProduct.setHasFixedSize(true)
        rcvSaleProduct.layoutManager=GridLayoutManager(context,2)
        rcvSaleProduct.adapter= saleProductAdapter
        presenter.loadSaleProduct()
        rcvSaleProduct.setOnTouchListener ({
            _: View, _: MotionEvent ->  true
        })
    }

    private fun initNewestProduct() {
        rcvNewestProduct.setHasFixedSize(true)
        rcvNewestProduct.layoutManager = GridLayoutManager(context,2)
        rcvNewestProduct.adapter = newestProductAdapter
        presenter.loadNewestProduct()
    }

    override fun showSaleProduct(products: List<Product>) {
       saleProductAdapter.addProducts(products)
    }
    override fun showNewestProduct(products: List<Product>) {
        newestProductAdapter.addProducts(products)
        Log.i("abc", products.get(0).toString())
    }

    private fun initCategory() {
        rcvCategory.setHasFixedSize(true)
        rcvCategory.setLayoutManager(LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false))
        val snapHelperStart = GravitySnapHelper(Gravity.START)
        snapHelperStart.attachToRecyclerView(rcvCategory)
        rcvCategory.itemAnimator = DefaultItemAnimator()
        rcvCategory.adapter = categoryAdapter
        presenter.loadCategory()
    }

    private fun initCarousel() {
        var adImages = listOf(
                "https://dalatlaptop.com/images/slider/image-slide-1.png",
                "https://dalatlaptop.com/images/slider/image-slide-2.png",
                "https://dalatlaptop.com/images/slider/image-slide-3.png",
                "https://dalatlaptop.com/images/slider/image-slide-4.png"
        )
        var carouselView = carouselView
        carouselView.setPageCount(adImages.size)

        carouselView.setImageListener { position, imageView ->
            imageView.loadImg(adImages[position])
            imageView.scaleType = ImageView.ScaleType.FIT_XY

        }

    }

    override fun showCategory(categories: List<Category>) {
        categoryAdapter.addCategories(categories)
    }

    override fun showLoadingError() {

    }


    override fun onPause() {
        super.onPause()
        presenter.unsubcribe()
    }

    override fun onResume() {
        super.onResume()
        // presenter.start()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return container?.inflate(R.layout.home_fragment)

    }

    companion object {
        fun newInstance() = HomeFragment()
    }
}