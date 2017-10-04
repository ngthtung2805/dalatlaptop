package com.tungnui.dalatlaptop.features.product

import android.content.Context
import com.tungnui.dalatlaptop.models.Product
import com.tungnui.dalatlaptop.utils.BasePresenter
import com.tungnui.dalatlaptop.utils.BaseView
import com.tungnui.dalatlaptop.utils.EndlessRecyclerViewScrollListener

/**
 * Created by thanh on 27/09/2017.
 */
interface ProductContract{
    interface View : BaseView<Presenter> {
        fun showProducts(products:List<Product>)
        fun showLoadingProductError()
}
    interface Presenter: BasePresenter {
        fun loadProducts(page:Int=1)
    }
}