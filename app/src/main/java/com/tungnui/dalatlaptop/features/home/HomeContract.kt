package com.tungnui.dalatlaptop.features.home

import com.tungnui.dalatlaptop.models.Category
import com.tungnui.dalatlaptop.models.Product
import com.tungnui.dalatlaptop.utils.BasePresenter
import com.tungnui.dalatlaptop.utils.BaseView
import java.util.*

/**
 * Created by thanh on 27/09/2017.
 */
interface HomeContract{
    interface View:BaseView<Presenter>{
        fun showCategory(categories:List<Category>)
        fun showLoadingError()
        fun showNewestProduct(products:List<Product>)
        fun showSaleProduct(products:List<Product>)
    }
    interface Presenter:BasePresenter{
        fun loadCategory(page:Int=1, perPage:Int=15)
        fun loadNewestProduct()
        fun loadSaleProduct()
    }
}