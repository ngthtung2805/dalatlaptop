package com.tungnui.dalatlaptop.features.product

import com.tungnui.dalatlaptop.api.ProductService
import com.tungnui.dalatlaptop.models.Product
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

/**
 * Created by thanh on 27/09/2017.
 */
class ProductPresenter(val productService: ProductService, val productView: ProductContract.View) : ProductContract.Presenter {
     private var mCompositeDisposable: CompositeDisposable

    init {
        productView.presenter = this
        mCompositeDisposable = CompositeDisposable()
    }

    override fun subscribe() {
        loadProducts()
    }

    override fun unsubcribe() {
        mCompositeDisposable.clear();
    }

    override fun loadProducts(page: Int) {
        mCompositeDisposable.clear()
        var disposable = productService.paging(page)
                .subscribeOn((Schedulers.io()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ products ->
                    processProduct(products)
                },
                        { _ ->
                            productView.showLoadingProductError()
                        })
        mCompositeDisposable.add(disposable)
    }

    private fun processProduct(products: List<Product>) {
        if (!products.isEmpty())
            productView.showProducts(products)
    }


}