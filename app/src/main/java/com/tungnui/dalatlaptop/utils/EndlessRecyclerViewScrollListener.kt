package com.tungnui.dalatlaptop.utils

import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.StaggeredGridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log


/**
 * Created by thanh on 25/09/2017.
 */
abstract class EndlessRecyclerViewScrollListener : RecyclerView.OnScrollListener {
    // The minimum amount of items to have below your current scroll position
    // before loading more.
    //set visibleThreshold   default: 5
    var visibleThreshold = 5
        private set
    // The current offset index of data you have loaded
    private var currentPage = 1

    // The total number of items in the dataset after the last load
    private var previousTotalItemCount = 0
    // True if we are still waiting for the last set of data to load.
    private var loading = true
    // Sets the starting page index
    //set startingPageIndex   default: 0
    var startingPageIndex = 0
        private set

    // Sets the  footerViewType
    private val defaultNoFooterViewType = -1
    private var footerViewType = -1


    private val mTag = "scroll-listener"


    internal var mLayoutManager: RecyclerView.LayoutManager

    constructor(layoutManager: LinearLayoutManager) {
        init()
        this.mLayoutManager = layoutManager
    }

    constructor(layoutManager: GridLayoutManager) {
        init()
        this.mLayoutManager = layoutManager
        visibleThreshold = visibleThreshold * layoutManager.spanCount
    }

    constructor(layoutManager: StaggeredGridLayoutManager) {
        init()
        this.mLayoutManager = layoutManager
        visibleThreshold = visibleThreshold * layoutManager.spanCount
    }

    //init from  self-define
    private fun init() {
        startingPageIndex = startingPageIndex

        val threshold = visibleThreshold
        if (threshold > visibleThreshold) {
            visibleThreshold = threshold
        }
    }


    // This happens many times a second during a scroll, so be wary of the code you place here.
    // We are given a few useful parameters to help us work out if we need to load some more data,
    // but first we check if we are waiting for the previous load to finish.
    override fun onScrolled(view: RecyclerView?, dx: Int, dy: Int) {

        ////when dy=0---->list is clear totalItemCount == 0 or init load  previousTotalItemCount=0
        if (dy <= 0) return
        //        Log.i(mTag, "onScrolled-------dy:" + dy);

        val adapter = view!!.adapter
        val totalItemCount = adapter.itemCount

        val lastVisibleItemPosition = lastVisibleItemPosition

        val isAllowLoadMore = lastVisibleItemPosition + visibleThreshold > totalItemCount

        if (isAllowLoadMore) {

            if (totalItemCount < previousTotalItemCount) {//swiprefresh reload result to change listsize ,reset pageindex
                this.currentPage = this.startingPageIndex
                //                            Log.i(mTag, "****totalItemCount:" + totalItemCount + ",previousTotalItemCount:" + previousTotalItemCount + ",currentpage=startingPageIndex");
            } else if (totalItemCount == previousTotalItemCount) {//if load failure or load empty data , we rollback  pageindex
                currentPage = if (currentPage == startingPageIndex) startingPageIndex else --currentPage
                //                            Log.i(mTag, "!!!!currentpage:" + currentPage);
            }

            loading = false


            if (!loading) {

                // If it isn’t currently loading, we check to see if we have breached
                // the visibleThreshold and need to reload more data.
                // If we do need to reload some more data, we execute onLoadMore to fetch the data.
                // threshold should reflect how many total columns there are too

                previousTotalItemCount = totalItemCount
                currentPage++
                onLoadMore(currentPage, totalItemCount)
                loading = true
                Log.i(mTag, "request pageindex:$currentPage,totalItemsCount:$totalItemCount")

            }
        }
    }


    override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)

    }


    private // get maximum element within the list
    val lastVisibleItemPosition: Int
        get() {
            var lastVisibleItemPosition = 0

            if (mLayoutManager is StaggeredGridLayoutManager) {
                val lastVisibleItemPositions = (mLayoutManager as StaggeredGridLayoutManager).findLastVisibleItemPositions(null)
                lastVisibleItemPosition = getLastVisibleItem(lastVisibleItemPositions)
            } else if (mLayoutManager is LinearLayoutManager) {
                lastVisibleItemPosition = (mLayoutManager as LinearLayoutManager).findLastVisibleItemPosition()
            } else if (mLayoutManager is GridLayoutManager) {
                lastVisibleItemPosition = (mLayoutManager as GridLayoutManager).findLastVisibleItemPosition()
            }
            return lastVisibleItemPosition
        }


    fun getLastVisibleItem(lastVisibleItemPositions: IntArray): Int {
        var maxSize = 0
        for (i in lastVisibleItemPositions.indices) {
            if (i == 0) {
                maxSize = lastVisibleItemPositions[i]
            } else if (lastVisibleItemPositions[i] > maxSize) {
                maxSize = lastVisibleItemPositions[i]
            }
        }
        return maxSize
    }


    // Defines the process for actually loading more data based on page
    abstract fun onLoadMore(page: Int, totalItemsCount: Int)

}