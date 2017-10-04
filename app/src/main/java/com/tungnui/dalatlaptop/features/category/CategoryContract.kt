package com.tungnui.dalatlaptop.features.category

import android.content.Context
import com.tungnui.dalatlaptop.models.Category
import com.tungnui.dalatlaptop.models.Product
import com.tungnui.dalatlaptop.utils.BasePresenter
import com.tungnui.dalatlaptop.utils.BaseView
import com.tungnui.dalatlaptop.utils.EndlessRecyclerViewScrollListener

/**
 * Created by thanh on 27/09/2017.
 */
interface CategoryContract {
    interface View : BaseView<Presenter> {
        fun showCategory(categories:List<Category>)
        fun showLoadingError()
}
    interface Presenter: BasePresenter {
        fun loadCategory(page:Int=1, perPage:Int=15)
    }
}