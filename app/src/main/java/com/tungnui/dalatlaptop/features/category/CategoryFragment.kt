package com.tungnui.dalatlaptop.features.category

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tungnui.dalatlaptop.HomeActivity
import com.tungnui.dalatlaptop.R
import com.tungnui.dalatlaptop.api.CategoryService
import com.tungnui.dalatlaptop.api.ServiceGenerator
import com.tungnui.dalatlaptop.features.adapter.CategoryEndlessAdapter
import com.tungnui.dalatlaptop.models.Category
import com.tungnui.dalatlaptop.utils.EndlessRecyclerViewScrollListener
import com.tungnui.dalatlaptop.utils.inflate
import kotlinx.android.synthetic.main.category_fragment.*


/**
 * Created by thanh on 23/09/2017.
 */
class CategoryFragment : Fragment(), CategoryContract.View {
    companion object {
        fun newInstance() = CategoryFragment()
    }
    override lateinit var presenter: CategoryContract.Presenter
    private val listAdapter = CategoryEndlessAdapter()
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        rcvCategory.setHasFixedSize(true)
        var linearLayout = GridLayoutManager(getContext(),2)
        rcvCategory.layoutManager = linearLayout
        rcvCategory.itemAnimator = DefaultItemAnimator()
        rcvCategory.adapter = listAdapter
        presenter = CategoryPresenter(ServiceGenerator.createService(CategoryService::class.java), this)
        presenter.loadCategory()
        var scrollListener = object : EndlessRecyclerViewScrollListener(linearLayout) {
            override fun onLoadMore(page: Int, totalItemsCount: Int) {
                presenter.loadCategory(page)
            }
        }
        rcvCategory.addOnScrollListener(scrollListener)
    }

    override fun showCategory(categories: List<Category>) {
        listAdapter.addItems(categories)
    }

    override fun showLoadingError() {

    }

    override fun onPause() {
        super.onPause()
        presenter.unsubcribe()
    }

    override fun onResume() {
        super.onResume()
        (activity as HomeActivity).setToolbarTitle(R.string.app_name)
        (activity as HomeActivity).enableNavigationIcon()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return container?.inflate(R.layout.category_fragment)
    }





}