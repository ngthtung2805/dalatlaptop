package com.tungnui.abccomputer.activity

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.LinearLayout

import com.google.android.gms.ads.AdListener
import com.tungnui.abccomputer.data.constant.AppConstants
import com.tungnui.abccomputer.R
import com.tungnui.abccomputer.SettingsMy
import com.tungnui.abccomputer.models.Category
import com.tungnui.abccomputer.utils.ActivityUtils
import com.tungnui.abccomputer.utils.AdUtils
import com.tungnui.abccomputer.utils.AnalyticsUtils
import com.tungnui.abccomputer.utils.AppUtility
import com.tungnui.abccomputer.utils.PermissionUtils


open class BaseActivity : AppCompatActivity(),DrawerFragment.FragmentDrawerListener {
    private  var drawerFragment: DrawerFragment? = null
    private var mContext: Context? = null
    private var mActivity: Activity? = null
    private var mDrawerLayout: DrawerLayout? = null
    private var loadingView: LinearLayout? = null
    private var noDataView: LinearLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initVariable()
        // uncomment this line to disable ad
        // AdUtils.getInstance(mContext).disableBannerAd();
        // AdUtils.getInstance(mContext).disableInterstitialAd();

    }
  fun initDrawer() {
        var toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        mDrawerLayout = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        drawerFragment = supportFragmentManager.findFragmentById(R.id.main_navigation_drawer_fragment) as DrawerFragment
        drawerFragment?.setUp(mDrawerLayout!!, toolbar, this)
    }

    fun invalidateHeader(){
        drawerFragment?.invalidateHeader()
    }

    private fun initVariable() {
        mContext = applicationContext
        mActivity = this@BaseActivity
    }

    fun initToolbar() {
        var toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
    }

    fun enableBackButton() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    fun setToolbarTitle(title: String) {
        if (supportActionBar != null) {
            supportActionBar?.title = title
        }
    }




    fun initLoader() {
        loadingView = findViewById<View>(R.id.loadingView) as LinearLayout
        noDataView = findViewById<View>(R.id.noDataView) as LinearLayout
    }

    fun showLoader() {
        if (loadingView != null) {
            loadingView?.visibility = View.VISIBLE
        }

        if (noDataView != null) {
            noDataView?.visibility = View.GONE
        }
    }

    fun hideLoader() {
        if (loadingView != null) {
            loadingView?.visibility = View.GONE
        }
        if (noDataView != null) {
            noDataView?.visibility = View.GONE
        }
    }

    fun showEmptyView() {
        if (loadingView != null) {
            loadingView?.visibility = View.GONE
        }
        if (noDataView != null) {
            noDataView?.visibility = View.VISIBLE
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (PermissionUtils.isPermissionResultGranted(grantResults)) {
            if (requestCode == PermissionUtils.REQUEST_CALL) {
                AppUtility.makePhoneCall(mActivity, AppConstants.CALL_NUMBER)
            }
        } else {
            AppUtility.showToast(mActivity, getString(R.string.permission_not_granted))
        }
    }


    override fun onDrawerItemCategorySelected(category: Category) {
            ActivityUtils.instance.invokeProducts(
                    this@BaseActivity,
                    categoryId = category.id!!,
                    title = category.name!!)
    }

    override fun onAccountSelected() {
        if(SettingsMy.getActiveUser()==null){
            ActivityUtils.instance.invokeActivity(this@BaseActivity,LoginActivity::class.java,false)
        }else{

        }
    }

    override fun onDrawerCartSelected() {
        AnalyticsUtils.getAnalyticsUtils(mContext).trackEvent("Drawer : Cart list")
        ActivityUtils.instance.invokeActivity(this@BaseActivity, CartListActivity::class.java, false)
    }

    override fun onDrawerOrderSelected() {
        AnalyticsUtils.getAnalyticsUtils(mContext).trackEvent("Drawer : Order list")

        ActivityUtils.instance.invokeActivity(this@BaseActivity, OrderListActivity::class.java, false)
    }

    override fun onDrawerHomeSelected() {
        ActivityUtils.instance.invokeActivity(this@BaseActivity, MainActivity::class.java, true)
    }

    override fun onDrawerSettingSelected() {
        ActivityUtils.instance.invokeActivity(this@BaseActivity, SettingsActivity::class.java, false)
    }

    override fun onDrawerWishSelected() {
        AnalyticsUtils.getAnalyticsUtils(mContext).trackEvent("Drawer : Wish list")

        ActivityUtils.instance.invokeActivity(this@BaseActivity, WishListActivity::class.java, false)
    }

    override fun onDrawerCallSelected() {
        AppUtility.makePhoneCall(mActivity, AppConstants.CALL_NUMBER)
    }

    override fun onDrawerMessageSelected() {
        AppUtility.sendSMS(mActivity, AppConstants.SMS_NUMBER, AppConstants.SMS_TEXT)
    }

    override fun onDrawerMessengerSelected() {
        AppUtility.invokeMessengerBot(mActivity)
    }

    override fun onDrawerEmailSelected() {
        AppUtility.sendEmail(mActivity, AppConstants.EMAIL_ADDRESS, AppConstants.EMAIL_SUBJECT, AppConstants.EMAIL_BODY)
    }

    override fun onDrawerShareSelected() {
        AppUtility.shareApp(mActivity)
    }

    override fun onDrawerRateAppSelected() {

        AppUtility.rateThisApp(mActivity) // this feature will only work after publish the app
    }

}
