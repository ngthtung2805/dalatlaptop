package com.tungnui.dalatlaptop.ux.fragments

import android.os.Bundle

import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView

import java.io.UnsupportedEncodingException
import java.net.URLEncoder

import com.tungnui.dalatlaptop.R
import com.tungnui.dalatlaptop.api.EndPoints
import com.tungnui.dalatlaptop.entities.filtr.Filters
import com.tungnui.dalatlaptop.interfaces.FilterDialogInterface
import com.tungnui.dalatlaptop.listeners.OnSingleClickListener
import com.tungnui.dalatlaptop.utils.EndlessRecyclerScrollListener
import com.tungnui.dalatlaptop.utils.MsgUtils
import com.tungnui.dalatlaptop.utils.RecyclerMarginDecorator
import com.tungnui.dalatlaptop.ux.MainActivity
import com.tungnui.dalatlaptop.ux.adapters.ProductsRecyclerAdapter
import com.tungnui.dalatlaptop.ux.dialogs.FilterDialogFragment
import com.tungnui.dalatlaptop.api.ProductService
import com.tungnui.dalatlaptop.api.ServiceGenerator
import com.tungnui.dalatlaptop.utils.getNextUrl
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_category.*
import timber.log.Timber

/**
 * Fragment handles various types of product lists.
 * Also allows displaying the search results.
 */
class CategoryFragment : Fragment() {
    private var mCompositeDisposable: CompositeDisposable
    val productService: ProductService
    init {
        mCompositeDisposable = CompositeDisposable()
        productService = ServiceGenerator.createService(ProductService::class.java)
    }
    /**
     * Prevent the sort selection callback during initialization.
     */
    private var firstTimeSort = true
    private var categoryId: Int = 0
    /**
     * Search string. The value is set only if the fragment is launched in order to searching.
     */
    private var searchQuery: String? = null

    /**
     * Request metadata containing URLs for endlessScroll.
     */
    private var productsNextLink: String? = null


    private lateinit var productsRecyclerLayoutManager: GridLayoutManager
    private lateinit var productsRecyclerAdapter: ProductsRecyclerAdapter
    private var endlessRecyclerScrollListener: EndlessRecyclerScrollListener? = null

    // Filters parameters
    private var filters: Filters? = null
    private var filterParameters: String? = null


    // Properties used to restore previous state
    private var toolbarOffset = -1
    private var isList = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_category, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fadeIn = AnimationUtils.loadAnimation(context, R.anim.fade_in_slowed)
        val fadeOut = AnimationUtils.loadAnimation(context, android.R.anim.fade_out)
        category_switch_layout_manager.inAnimation = fadeIn
        category_switch_layout_manager.outAnimation = fadeOut
        prepareRecyclerAdapter()
        val startBundle = arguments
        if (startBundle != null) {
            categoryId = startBundle.getInt(CATEGORY_ID, 0)
            var categoryName = startBundle.getString(CATEGORY_NAME, "")
            searchQuery = startBundle.getString(SEARCH_QUERY, null)
            if (toolbarOffset != -1) category_appbar_layout.offsetTopAndBottom(toolbarOffset)
            category_appbar_layout.addOnOffsetChangedListener { _, i -> toolbarOffset = i }
            MainActivity.setActionBarTitle(categoryName)
            category_filter_button.setOnClickListener(object : OnSingleClickListener() {
                override fun onSingleClick(view: View) {
                    if (filters == null) {
                        MsgUtils.showToast(activity, MsgUtils.TOAST_TYPE_MESSAGE, getString(R.string.Filter_unavailable), MsgUtils.ToastLength.SHORT)
                    } else {
                        val filterDialogFragment = FilterDialogFragment.newInstance(filters, object : FilterDialogInterface {
                            override fun onFilterSelected(newFilterUrl: String) {
                                filterParameters = newFilterUrl
                                category_filter_button.setImageResource(R.drawable.filter_selected)
                                getProducts(null)
                            }

                            override fun onFilterCancelled() {
                                filterParameters = null
                                category_filter_button.setImageResource(R.drawable.filter_unselected)
                                getProducts(null)
                            }
                        })
                   /*     if (filterDialogFragment != null)
                            filterDialogFragment.show(fragmentManager, "filterDialogFragment")
                        else {
                            MsgUtils.showToast(activity, MsgUtils.TOAST_TYPE_INTERNAL_ERROR, null, MsgUtils.ToastLength.SHORT)
                        }*/
                    }
                }
            })


            if (filterParameters != null && !filterParameters!!.isEmpty()) {
                category_filter_button.setImageResource(R.drawable.filter_selected)
            } else {
                category_filter_button.setImageResource(R.drawable.filter_unselected)
            }

            // Opened first time (not form backstack)
            if (productsRecyclerAdapter.itemCount == 0) {
                prepareRecyclerAdapter()
                prepareProductRecycler()
                prepareSortSpinner()
                getProducts(null)
            } else {
                prepareProductRecycler()
                prepareSortSpinner()
            }
        } else {
            MsgUtils.showToast(activity, MsgUtils.TOAST_TYPE_INTERNAL_ERROR, getString(R.string.Internal_error), MsgUtils.ToastLength.LONG)
            Timber.e(RuntimeException(), "Run category fragment without arguments.")
        }
    }



    /**
     * Prepare content recycler. Create custom adapter and endless scroll.
     *
     * @param view root fragment view.
     */
    private fun prepareProductRecycler() {
        category_products_recycler.addItemDecoration(RecyclerMarginDecorator(activity, RecyclerMarginDecorator.ORIENTATION.BOTH))
        category_products_recycler.itemAnimator = DefaultItemAnimator()
        category_products_recycler.setHasFixedSize(true)
        category_switch_layout_manager.setFactory { ImageView(context) }
        if (isList) {
            category_switch_layout_manager.setImageResource(R.drawable.grid_off)
            productsRecyclerLayoutManager = GridLayoutManager(activity, 1)
        } else {
            category_switch_layout_manager.setImageResource(R.drawable.grid_on)
            // TODO A better solution would be to dynamically determine the number of columns.
            productsRecyclerLayoutManager = GridLayoutManager(activity, 2)
        }
        category_products_recycler.layoutManager = productsRecyclerLayoutManager
        endlessRecyclerScrollListener = object : EndlessRecyclerScrollListener(productsRecyclerLayoutManager) {
            override fun onLoadMore(currentPage: Int) {
                Timber.e("Load more")
                if (productsNextLink != null) {
                    getProducts(productsNextLink)
                } else {
                    Timber.d("CustomLoadMoreDataFromApi NO MORE DATA")
                }
            }
        }
        category_products_recycler.addOnScrollListener(endlessRecyclerScrollListener)
        category_products_recycler.adapter = productsRecyclerAdapter

        category_switch_layout_manager.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View) {
                if (isList) {
                    isList = false
                    category_switch_layout_manager.setImageResource(R.drawable.grid_on)
                    animateRecyclerLayoutChange(2)
                } else {
                    isList = true
                    category_switch_layout_manager.setImageResource(R.drawable.grid_off)
                    animateRecyclerLayoutChange(1)
                }
            }
        })
    }

    private fun prepareRecyclerAdapter() {
        productsRecyclerAdapter = ProductsRecyclerAdapter(){
            product-> product.id?.let {  (activity as MainActivity).onProductSelected(it) }

        }
    }

    /**
     * Animate change of rows in products recycler LayoutManager.
     *
     * @param layoutSpanCount number of rows to display.
     */
    private fun animateRecyclerLayoutChange(layoutSpanCount: Int) {
        val fadeOut = AlphaAnimation(1f, 0f)
        fadeOut.interpolator = DecelerateInterpolator()
        fadeOut.duration = 400
        fadeOut.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}

            override fun onAnimationRepeat(animation: Animation) {}

            override fun onAnimationEnd(animation: Animation) {
                productsRecyclerLayoutManager.spanCount = layoutSpanCount
                productsRecyclerLayoutManager.requestLayout()
                val fadeIn = AlphaAnimation(0f, 1f)
                fadeIn.interpolator = AccelerateInterpolator()
                fadeIn.duration = 400
                category_products_recycler.startAnimation(fadeIn)
            }
        })
        category_products_recycler.startAnimation(fadeOut)
    }



    private fun prepareSortSpinner() {
       /* val sortSpinnerAdapter = SortSpinnerAdapter(activity)
        category_sort_spinner.adapter = sortSpinnerAdapter
        category_sort_spinner.onItemSelectedListener = null
        category_sort_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            private var lastSortSpinnerPosition = -1

            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                if (firstTimeSort) {
                    firstTimeSort = false
                    return
                }
                Timber.d("Selected pos: %d", position)

                if (position != lastSortSpinnerPosition) {
                    Timber.d("OnItemSelected change")
                    lastSortSpinnerPosition = position
                    getProducts(null)
                } else {
                    Timber.d("OnItemSelected no change")
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                Timber.d("OnNothingSelected - no change")
            }
        }*/
    }


    /**
     * Endless content loader. Should be used after views inflated.
     *
     * @param url null for fresh load. Otherwise use URLs from response metadata.
     */
    private fun getProducts(url: String?) {
        var url = url
        category_load_more_progress.visibility = View.VISIBLE
        if (url == null) {
            if (endlessRecyclerScrollListener != null)
                endlessRecyclerScrollListener?.clean()
            productsRecyclerAdapter.clear()
            url = EndPoints.PRODUCT
            // Build request url
            if (searchQuery != null) {
                var newSearchQueryString: String
                try {
                    newSearchQueryString = URLEncoder.encode(searchQuery, "UTF-8")
                } catch (e: UnsupportedEncodingException) {
                    Timber.e(e, "Unsupported encoding exception")
                    newSearchQueryString = URLEncoder.encode(searchQuery)
                }
                url += "?search=" + newSearchQueryString
            } else {
                url += "?category=$categoryId"
            }

            // Add filters parameter if exist
            if (filterParameters != null && !filterParameters!!.isEmpty()) {
                url += filterParameters
            }

          /*  val sortItem = category_sort_spinner.selectedItem as SortItem
            if (sortItem != null) {
                url = url + "&orderby=" + sortItem.value
            }*/
        }
        Log.i("URL", url)

        var disposable = url?.let{
            productService.getAll(it)
                    .subscribeOn((Schedulers.io()))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ response ->
                        firstTimeSort = false
                        response.body()?.let { productsRecyclerAdapter.addProducts(it) }

                        productsNextLink = response.headers()["Link"]?.getNextUrl()
                        checkEmptyContent()
                        category_load_more_progress?.let{      it.visibility = View.INVISIBLE        }
                    },
                            { error ->
                                category_load_more_progress?.let{it.visibility = View.INVISIBLE}
                                checkEmptyContent()
                            })
        }
        disposable?.let { mCompositeDisposable.add(it) }
   }

    private fun checkEmptyContent() {
        if (productsRecyclerAdapter.itemCount > 0) {
            category_products_empty?.let{it.visibility = View.INVISIBLE}
            category_products_recycler?.let{it.visibility = View.VISIBLE}
        } else {
            category_products_empty?.let{it.visibility = View.VISIBLE}
            category_products_recycler?.let{it.visibility = View.INVISIBLE}
        }
    }

    override fun onStop() {
            // Hide progress dialog if exist.
            if (category_load_more_progress.visibility == View.VISIBLE) {
                // Fragment stopped during loading data. Allow new loading on return.
                endlessRecyclerScrollListener?.let{it.resetLoading()}
            }
            category_load_more_progress?.let{it.visibility = View.INVISIBLE}
        super.onStop()
    }

    override fun onDestroyView() {
        category_products_recycler.clearOnScrollListeners()
        super.onDestroyView()
    }

    companion object {

        private val TYPE = "type"
        private val CATEGORY_NAME = "categoryName"
        private val CATEGORY_ID = "categoryId"
        private val SEARCH_QUERY = "search_query"

        fun newInstance(categoryId: Int, name: String, type: String): CategoryFragment {
            val args = Bundle()
            args.putInt(CATEGORY_ID, categoryId)
            args.putString(CATEGORY_NAME, name)
            args.putString(TYPE, type)
            args.putString(SEARCH_QUERY, null)

            val fragment = CategoryFragment()
            fragment.arguments = args
            return fragment
        }
           fun newInstance(searchQuery: String): CategoryFragment {
            val args = Bundle()
            args.putString(SEARCH_QUERY, searchQuery)

            val fragment = CategoryFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
