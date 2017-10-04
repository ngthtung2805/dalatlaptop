package com.tungnui.dalatlaptop.features.product

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tungnui.dalatlaptop.R
import com.tungnui.dalatlaptop.api.ProductService
import com.tungnui.dalatlaptop.api.ServiceGenerator
import com.tungnui.dalatlaptop.features.adapter.ProductEndlessAdapter
import com.tungnui.dalatlaptop.models.Product
import com.tungnui.dalatlaptop.utils.EndlessRecyclerViewScrollListener
import kotlinx.android.synthetic.main.product_fragment.*
import com.tungnui.dalatlaptop.utils.inflate


/**
 * Created by thanh on 23/09/2017.
 */
class ProductFragment : Fragment(), ProductContract.View {

    override lateinit var presenter: ProductContract.Presenter
    private val listAdapter = ProductEndlessAdapter()
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        product_list.setHasFixedSize(true)
        var linearLayout = LinearLayoutManager(getContext())
        product_list.layoutManager = linearLayout
        product_list.itemAnimator = DefaultItemAnimator()
        product_list.adapter = listAdapter
        presenter = ProductPresenter(ServiceGenerator.createService(ProductService::class.java), this)
        presenter.loadProducts()
        var scrollListener = object : EndlessRecyclerViewScrollListener(linearLayout) {
            override fun onLoadMore(page: Int, totalItemsCount: Int) {
                presenter.loadProducts(page)
            }
        }
        product_list.addOnScrollListener(scrollListener)

    }

    override fun showProducts(products: List<Product>) {
        listAdapter.addProduct(products)
    }

    override fun showLoadingProductError() {

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
        return container?.inflate(R.layout.product_fragment)

    }

    companion object {
        fun newInstance() = ProductFragment()
    }


}