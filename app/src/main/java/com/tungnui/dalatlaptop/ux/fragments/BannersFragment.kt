package com.tungnui.dalatlaptop.ux.fragments


import android.app.Activity
import android.app.ProgressDialog
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.tungnui.dalatlaptop.CONST
import com.tungnui.dalatlaptop.MyApplication
import com.tungnui.dalatlaptop.R
import com.tungnui.dalatlaptop.entities.Banner
import com.tungnui.dalatlaptop.entities.Metadata
import com.tungnui.dalatlaptop.interfaces.BannersRecyclerInterface
import com.tungnui.dalatlaptop.listeners.OnSingleClickListener
import com.tungnui.dalatlaptop.utils.EndlessRecyclerScrollListener
import com.tungnui.dalatlaptop.utils.Utils
import com.tungnui.dalatlaptop.utils.loadImg
import com.tungnui.dalatlaptop.ux.MainActivity
import com.tungnui.dalatlaptop.ux.adapters.BannersRecyclerAdapter
import kotlinx.android.synthetic.main.fragment_banners.*
import timber.log.Timber

/**
 * Provides "welcome" screen customizable from web administration. Often contains banners with sales or best products.
 */
class BannersFragment : Fragment() {

    private var progressDialog: ProgressDialog? = null

    // content related fields.
    private var bannersRecycler: RecyclerView? = null
    private var bannersRecyclerAdapter: BannersRecyclerAdapter? = null
    private var endlessRecyclerScrollListener: EndlessRecyclerScrollListener? = null
    private val bannersMetadata: Metadata? = null

    /**
     * Indicates if user data should be loaded from server or from memory.
     */
    private var mAlreadyLoaded = false

    /**
     * Holds reference for empty view. Show to user when no data loaded.
     */

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_banners, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MainActivity.setActionBarTitle(getString(R.string.app_name))
        progressDialog = Utils.generateProgressDialog(activity, false)

        prepareEmptyContent()
        initCarousel()
        // Don't reload data when return from backStack. Reload if a new instance was created or data was empty.
      /*  if (savedInstanceState == null && !mAlreadyLoaded || bannersRecyclerAdapter == null || bannersRecyclerAdapter!!.isEmpty) {
            Timber.d("Reloading banners.")
            mAlreadyLoaded = true

            // Prepare views and listeners
            prepareContentViews(view, true)
            loadBanners(null)
        } else {
            Timber.d("Banners already loaded.")
            prepareContentViews(view, false)
            // Already loaded
        }
*/

    }


    /**
     * Prepares views and listeners associated with content.
     *
     * @param view       fragment root view.
     * @param freshStart indicates when everything should be recreated.
     */
    /*private fun prepareContentViews(freshStart: Boolean) {
        bannersRecycler = view.findViewById<View>(R.id.banners_recycler) as RecyclerView
        if (freshStart) {
            bannersRecyclerAdapter = BannersRecyclerAdapter(activity, BannersRecyclerInterface { banner ->
                val activity = activity
                (activity as? MainActivity)?.onBannerSelected(banner)
            })
        }
        val layoutManager = LinearLayoutManager(banners_recycler.context)
        bannersRecycler!!.layoutManager = layoutManager
        bannersRecycler!!.itemAnimator = DefaultItemAnimator()
        bannersRecycler!!.setHasFixedSize(true)
        bannersRecycler!!.adapter = bannersRecyclerAdapter
        endlessRecyclerScrollListener = object : EndlessRecyclerScrollListener(layoutManager) {
            override fun onLoadMore(currentPage: Int) {
                if (bannersMetadata != null && bannersMetadata.links != null && bannersMetadata.links.next != null) {
                    loadBanners(bannersMetadata.links.next)
                } else {
                    Timber.d("CustomLoadMoreDataFromApi NO MORE DATA")
                }
            }
        }
        bannersRecycler!!.addOnScrollListener(endlessRecyclerScrollListener)
    }*/
    /**
     * Prepares views and listeners associated with empty content. Visible only when no content loads.
     *
     * @param view fragment root view.
     */
    private fun prepareEmptyContent() {
        banners_empty_action.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View) {
                // Just open drawer menu.
                val activity = activity
                if (activity is MainActivity) {
                    if (activity.drawerFragment != null)
                        activity.drawerFragment!!.toggleDrawerMenu()
                }
            }
        })
    }
    private fun initCarousel() {
        var adImages = listOf(
                "https://dalatlaptop.com/images/slider/image-slide-1.png",
                "https://dalatlaptop.com/images/slider/image-slide-2.png",
                "https://dalatlaptop.com/images/slider/image-slide-3.png",
                "https://dalatlaptop.com/images/slider/image-slide-4.png"
        )
        carouselView.setImageListener { position, imageView ->
            imageView.loadImg(adImages[position])
            imageView.scaleType = ImageView.ScaleType.FIT_XY

        }
        carouselView.setPageCount(adImages.size)
    }
    /**
     * Endless content loader. Should be used after views inflated.
     *
     * @param url null for fresh load. Otherwise use URLs from response metadata.
     */
    private fun loadBanners(url: String?) {
        // progressDialog.show();
        if (url == null) {
            bannersRecyclerAdapter!!.clear()
            // url = String.format(EndPoints.BANNERS, SettingsMy.getActualNonNullShop(getActivity()).getId());
        }
        /* GsonRequest<BannersResponse> getBannersRequest = new GsonRequest<>(Request.Method.GET, url, null, BannersResponse.class,
                new Response.Listener<BannersResponse>() {
                    @Override
                    public void onResponse(@NonNull BannersResponse response) {
                        Timber.d("response: %s", response.toString());
                        bannersMetadata = response.getMetadata();
                        bannersRecyclerAdapter.addBanners(response.getRecords());

                        if (bannersRecyclerAdapter.getItemCount() > 0) {
                            banners_empty.setVisibility(View.INVISIBLE);
                            bannersRecycler.setVisibility(View.VISIBLE);
                        } else {
                            banners_empty.setVisibility(View.VISIBLE);
                            bannersRecycler.setVisibility(View.INVISIBLE);
                        }

                        progressDialog.cancel();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (progressDialog != null) progressDialog.cancel();
                MsgUtils.logAndShowErrorMessage(getActivity(), error);
            }
        });*/
    }

    override fun onStop() {
        if (progressDialog != null) {
            // Hide progress dialog if exist.
            if (progressDialog!!.isShowing && endlessRecyclerScrollListener != null) {
                // Fragment stopped during loading data. Allow new loading on return.
                endlessRecyclerScrollListener!!.resetLoading()
            }
            progressDialog!!.cancel()
        }
        MyApplication.getInstance().cancelPendingRequests(CONST.BANNER_REQUESTS_TAG)
        super.onStop()
    }

    override fun onDestroyView() {
        if (bannersRecycler != null) bannersRecycler!!.clearOnScrollListeners()
        super.onDestroyView()
    }
}
