package com.tungnui.dalatlaptop.features.home

import com.tungnui.dalatlaptop.api.CategoryService
import com.tungnui.dalatlaptop.api.ProductService
import com.tungnui.dalatlaptop.models.Category
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

/**
 * Created by thanh on 27/09/2017.
 */
class HomePresenter(val categoryService: CategoryService, val productService: ProductService, val homeView: HomeContract.View) : HomeContract.Presenter {

    override fun loadSaleProduct() {
        var disposable = productService.getSale(1, 4)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ items ->
                    homeView.showSaleProduct(items)
                }, { _ ->
                    homeView.showLoadingError()
                })
        mCompositeDisposable.add(disposable)
    }

    override fun loadNewestProduct() {
        var disposable = productService.getNewest(1, 4)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ items ->
                    homeView.showNewestProduct(items)
                }, { _ -> homeView.showLoadingError() })
        mCompositeDisposable.add(disposable)
    }

    override fun subscribe() {
        loadCategory()
    }

    override fun unsubcribe() {
        mCompositeDisposable.clear()
    }

    override fun loadCategory(page: Int, perPage: Int) {
        var disposable = categoryService.paging(page)
                .subscribeOn((Schedulers.io()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ items ->
                    homeView.showCategory(items)
                },
                        { _ ->
                            homeView.showLoadingError()
                        })
        mCompositeDisposable.add(disposable)
    }

    private var mCompositeDisposable: CompositeDisposable

    init {
        homeView.presenter = this
        mCompositeDisposable = CompositeDisposable()
    }
}