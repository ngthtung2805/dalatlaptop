package com.tungnui.dalatlaptop.features.category

import com.tungnui.dalatlaptop.api.CategoryService
import com.tungnui.dalatlaptop.api.ProductService
import com.tungnui.dalatlaptop.models.Product
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

/**
 * Created by thanh on 27/09/2017.
 */
class CategoryPresenter(val categoryService: CategoryService, val categoryView: CategoryContract.View) : CategoryContract.Presenter {
    private var mCompositeDisposable: CompositeDisposable

    init {
        categoryView.presenter = this
        mCompositeDisposable = CompositeDisposable()
    }

    override fun loadCategory(page: Int, perPage: Int) {
        var disposable = categoryService.paging(page)
                .subscribeOn((Schedulers.io()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ items ->
                    categoryView.showCategory(items)
                },
                        { _ ->
                            categoryView.showLoadingError()
                        })
        mCompositeDisposable.add(disposable)
    }


    override fun subscribe() {
        loadCategory()
    }

    override fun unsubcribe() {
        mCompositeDisposable.clear();
    }

}