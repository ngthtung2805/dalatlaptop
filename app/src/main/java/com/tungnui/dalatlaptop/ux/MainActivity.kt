
package com.tungnui.dalatlaptop.ux

import android.app.SearchManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.database.MatrixCursor
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.BaseColumns
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.content.LocalBroadcastManager
import android.support.v4.view.MenuItemCompat
import android.support.v4.widget.DrawerLayout
import android.support.v4.widget.SimpleCursorAdapter
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.support.v7.widget.Toolbar
import android.transition.TransitionInflater
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView

import com.facebook.appevents.AppEventsLogger
import com.tungnui.dalatlaptop.models.Category

import java.util.ArrayList

import com.tungnui.dalatlaptop.BuildConfig
import com.tungnui.dalatlaptop.CONST
import com.tungnui.dalatlaptop.MyApplication
import com.tungnui.dalatlaptop.R
import com.tungnui.dalatlaptop.SettingsMy
import com.tungnui.dalatlaptop.entities.Banner
import com.tungnui.dalatlaptop.entities.drawerMenu.DrawerItemCategory
import com.tungnui.dalatlaptop.entities.drawerMenu.DrawerItemPage
import com.tungnui.dalatlaptop.interfaces.LoginDialogInterface
import com.tungnui.dalatlaptop.models.Order
import com.tungnui.dalatlaptop.utils.MsgUtils
import com.tungnui.dalatlaptop.utils.cartHelper
import com.tungnui.dalatlaptop.utils.totalItem
import com.tungnui.dalatlaptop.ux.dialogs.LoginDialogFragment
import com.tungnui.dalatlaptop.ux.dialogs.LoginDialogFragment2
import com.tungnui.dalatlaptop.ux.fragments.AccountEditFragment
import com.tungnui.dalatlaptop.ux.fragments.AccountFragment
import com.tungnui.dalatlaptop.ux.fragments.BannersFragment
import com.tungnui.dalatlaptop.ux.fragments.CartFragment
import com.tungnui.dalatlaptop.ux.fragments.CategoryFragment
import com.tungnui.dalatlaptop.ux.fragments.DrawerFragment
import com.tungnui.dalatlaptop.ux.fragments.OrderCreateFragment
import com.tungnui.dalatlaptop.ux.fragments.OrderFragment
import com.tungnui.dalatlaptop.ux.fragments.OrdersHistoryFragment
import com.tungnui.dalatlaptop.ux.fragments.PageFragment
import com.tungnui.dalatlaptop.ux.fragments.ProductFragment
import com.tungnui.dalatlaptop.ux.fragments.SettingsFragment
import kotlinx.android.synthetic.main.action_icon_shopping_cart.*
import timber.log.Timber

/**
 * Application is based on one core activity, which handles fragment operations.
 */
class MainActivity : AppCompatActivity(), DrawerFragment.FragmentDrawerListener {



    /**
     * Reference tied drawer menu, represented as fragment.
     */
    var drawerFragment: DrawerFragment? = null
    /**
     * Indicate that app will be closed on next back press
     */
    private var isAppReadyToFinish = false
    /**
     * Reference view showing number of products in shopping cart.
     */
    private var cartCountView: TextView? = null

    /**
     * BroadcastReceiver used in service for Gcm registration.
     */
    private val mRegistrationBroadcastReceiver: BroadcastReceiver? = null

    // Fields used in searchView.
    private val searchSuggestionsAdapter: SimpleCursorAdapter? = null
    private var searchSuggestionsList: ArrayList<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        instance = this
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<View>(R.id.main_toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        drawerFragment = supportFragmentManager.findFragmentById(R.id.main_navigation_drawer_fragment) as DrawerFragment
        drawerFragment?.setUp(findViewById<View>(R.id.main_drawer_layout) as DrawerLayout, toolbar, this)
        addInitialFragment()

        // Opened by notification with some data
        if (this.intent != null && this.intent.extras != null) {
            val target = this.intent.extras!!.getString(CONST.BUNDLE_PASS_TARGET, "")
            val title = this.intent.extras!!.getString(CONST.BUNDLE_PASS_TITLE, "")
            Timber.d("Start notification with banner target: %s", target)

            val banner = Banner()
            banner.target = target
            banner.name = title
            onBannerSelected(banner)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)

        // Prepare search view
        val searchItem = menu.findItem(R.id.action_search)
        if (searchItem != null) {
            prepareSearchView(searchItem)
        }

        // Prepare cart count info
        val cartItem = menu.findItem(R.id.action_cart)
        MenuItemCompat.setActionView(cartItem, R.layout.action_icon_shopping_cart)
        val view = MenuItemCompat.getActionView(cartItem)
        cartCountView = view.findViewById<View>(R.id.shopping_cart_notify) as TextView
        showNotifyCount()
        view.setOnClickListener { onCartSelected() }
        return super.onCreateOptionsMenu(menu)
    }


    private fun showNotifyCount() {
       var  newCartCount =  this.cartHelper.totalItem();
             runOnUiThread {
                if (newCartCount != 0) {
                    cartCountView?.text = getString(R.string.format_number, newCartCount)
                    cartCountView?.visibility = View.VISIBLE
                } else {
                         cartCountView?.visibility = View.GONE
                }
            }
    }

    /**
     * Prepare toolbar search view. Invoke search suggestions and handle search queries.
     *
     * @param searchItem corresponding menu item.
     */
    private fun prepareSearchView(searchItem: MenuItem) {
        val searchView = searchItem.actionView as SearchView
        searchView.isSubmitButtonEnabled = true
        val searchManager = this@MainActivity.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView.setSearchableInfo(searchManager.getSearchableInfo(this@MainActivity.componentName))
        val queryTextListener = object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                Timber.d("Search query text changed to: %s", newText)
                showSearchSuggestions(newText, searchView)
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                // Submit search query and hide search action view.
                onSearchSubmitted(query)
                searchView.setQuery("", false)
                searchView.isIconified = true
                searchItem.collapseActionView()
                return true
            }
        }

        searchView.setOnSuggestionListener(object : SearchView.OnSuggestionListener {
            override fun onSuggestionSelect(position: Int): Boolean {
                return false
            }

            override fun onSuggestionClick(position: Int): Boolean {
                // Submit search suggestion query and hide search action view.
                val c = searchSuggestionsAdapter!!.getItem(position) as MatrixCursor
                onSearchSubmitted(c.getString(1))
                searchView.setQuery("", false)
                searchView.isIconified = true
                searchItem.collapseActionView()
                return true
            }
        })
        searchView.setOnQueryTextListener(queryTextListener)
    }

    /**
     * Show user search whisperer with generated suggestions.
     *
     * @param query      actual search query
     * @param searchView corresponding search action view.
     */
    private fun showSearchSuggestions(query: String, searchView: SearchView) {
        if (searchSuggestionsAdapter != null && searchSuggestionsList != null) {
            Timber.d("Populate search adapter - mySuggestions.size(): %d", searchSuggestionsList!!.size)
            val c = MatrixCursor(arrayOf(BaseColumns._ID, "categories"))
            for (i in searchSuggestionsList!!.indices) {
                if (searchSuggestionsList!![i] != null && searchSuggestionsList!![i].toLowerCase().startsWith(query.toLowerCase()))
                    c.addRow(arrayOf(i, searchSuggestionsList!![i]))
            }
            searchView.suggestionsAdapter = searchSuggestionsAdapter
            searchSuggestionsAdapter.changeCursor(c)
        } else {
            Timber.e("Search adapter is null or search data suggestions missing")
        }
    }


    /*   public void prepareSearchSuggestions(List<DrawerItemCategory> navigation) {
        final String[] from = new String[]{"categories"};
        final int[] to = new int[]{android.R.id.text1};

        searchSuggestionsAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1,
                null, from, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        if (navigation != null && !navigation.isEmpty()) {
            for (int i = 0; i < navigation.size(); i++) {
                if (!searchSuggestionsList.contains(navigation.get(i).getName())) {
                    searchSuggestionsList.add(navigation.get(i).getName());
                }

                if (navigation.get(i).hasChildren()) {
                    for (int j = 0; j < navigation.get(i).getChildren().size(); j++) {
                        if (!searchSuggestionsList.contains(navigation.get(i).getChildren().get(j).getName())) {
                            searchSuggestionsList.add(navigation.get(i).getChildren().get(j).getName());
                        }
                    }
                }
            }
            searchSuggestionsAdapter.notifyDataSetChanged();
        } else {
            Timber.e("Search suggestions loading failed.");
            searchSuggestionsAdapter = null;
        }
    }
*/
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        if (id == R.id.action_cart) {
            onCartSelected()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    /**
     * Add first fragment to the activity. This fragment will be attached to the bottom of the fragments stack.
     * When fragment stack is cleared [.clearBackStack], this fragment will be shown.
     */
    private fun addInitialFragment() {
        val fragment = BannersFragment()
        val frgManager = supportFragmentManager
        val fragmentTransaction = frgManager.beginTransaction()
        fragmentTransaction.add(R.id.main_content_frame, fragment).commit()
        frgManager.executePendingTransactions()
    }

    /**
     * Method creates fragment transaction and replace current fragment with new one.
     *
     * @param newFragment    new fragment used for replacement.
     * @param transactionTag text identifying fragment transaction.
     */
    private fun replaceFragment(newFragment: Fragment?, transactionTag: String) {
        if (newFragment != null) {
            val frgManager = supportFragmentManager
            val fragmentTransaction = frgManager.beginTransaction()
            fragmentTransaction.setAllowOptimization(false)
            fragmentTransaction.addToBackStack(transactionTag)
            fragmentTransaction.replace(R.id.main_content_frame, newFragment).commit()
            frgManager.executePendingTransactions()
        } else {
            Timber.e(RuntimeException(), "Replace fragments with null newFragment parameter.")
        }
    }

    /**
     * Method clear fragment backStack (back history). On bottom of stack will remain Fragment added by [.addInitialFragment].
     */
    private fun clearBackStack() {
        Timber.d("Clearing backStack")
        val manager = supportFragmentManager
        if (manager.backStackEntryCount > 0) {
            if (BuildConfig.DEBUG) {
                for (i in 0 until manager.backStackEntryCount) {
                    Timber.d("BackStack content_%d= id: %d, name: %s", i, manager.getBackStackEntryAt(i).id, manager.getBackStackEntryAt(i).name)
                }
            }
            val first = manager.getBackStackEntryAt(0)
            manager.popBackStackImmediate(first.id, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
        Timber.d("backStack cleared.")
        //        TODO maybe implement own fragment backStack handling to prevent banner fragment recreation during clearing.
        //        http://stackoverflow.com/questions/12529499/problems-with-android-fragment-back-stack
    }

    /**
     * Method create new [CategoryFragment] with defined search query.
     *
     * @param searchQuery text used for products search.
     */
    private fun onSearchSubmitted(searchQuery: String) {
        clearBackStack()
        Timber.d("Called onSearchSubmitted with text: %s", searchQuery)
        val fragment = CategoryFragment.newInstance(searchQuery)
        replaceFragment(fragment, CategoryFragment::class.java.simpleName)
    }

    override fun onDrawerBannersSelected() {
        clearBackStack()
        val f = supportFragmentManager.findFragmentById(R.id.main_content_frame)
        if (f == null || f !is BannersFragment) {
            val fragment = BannersFragment()
            replaceFragment(fragment, BannersFragment::class.java.simpleName)
        } else {
            Timber.d("Banners already displayed.")
        }
    }


    override fun onDrawerItemPageSelected(drawerItemPage: DrawerItemPage) {
        clearBackStack()
        val fragment = PageFragment.newInstance(drawerItemPage.id)
        replaceFragment(fragment, PageFragment::class.java.simpleName)
    }

    override fun onAccountSelected() {
        val fragment = AccountFragment()
        replaceFragment(fragment, AccountFragment::class.java.simpleName)
    }

    /**
     * Launch [PageFragment] with default values. It leads to load terms and conditions defined on server.
     */
    fun onTermsAndConditionsSelected() {
        val fragment = PageFragment.newInstance()
        replaceFragment(fragment, PageFragment::class.java.simpleName)
    }

    /**
     * Method parse selected banner and launch corresponding fragment.
     * If banner type is 'list' then launch [CategoryFragment].
     * If banner type is 'detail' then launch [ProductFragment].
     *
     * @param banner selected banner for display.
     */
    fun onBannerSelected(banner: Banner?) {
        if (banner != null) {
            val target = banner.target
            Timber.d("Open banner with target: %s", target)
            val targetParams = target.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (targetParams.size >= 2) {
                when (targetParams[0]) {
                    "list" -> {
                        val fragment = CategoryFragment.newInstance(Integer.parseInt(targetParams[1]), banner.name)
                        replaceFragment(fragment, CategoryFragment::class.java.simpleName + " - banner")
                    }
                    "detail" -> {
                        val fragment = ProductFragment.newInstance(Integer.parseInt(targetParams[1]))
                        replaceFragment(fragment, ProductFragment::class.java.simpleName + " - banner select")
                    }
                    else -> Timber.e("Unknown banner target type.")
                }
            } else {
                Timber.e(RuntimeException(), "Parsed banner target has too less parameters.")
            }
        } else {
            Timber.e("onBannerSelected called with null parameters.")
        }
    }

    /**
     * Launch [ProductFragment].
     *
     * @param productId id of product for display.
     */
    fun onProductSelected(productId: Int) {
        val fragment = ProductFragment.newInstance(productId)
        if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            fragment.returnTransition = TransitionInflater.from(this).inflateTransition(android.R.transition.fade)
        }
        replaceFragment(fragment, ProductFragment::class.java.simpleName)
    }

    /**
     * Launch [SettingsFragment].
     */
    fun startSettingsFragment() {
        val fragment = SettingsFragment()
        replaceFragment(fragment, SettingsFragment::class.java.simpleName)
    }

    /**
     * If user is logged in then [CartFragment] is launched . Otherwise is showed a login dialog.
     */
    fun onCartSelected() {
        replaceFragment(CartFragment(),CartFragment::class.java.simpleName)
       /* launchUserSpecificFragment(CartFragment(), CartFragment::class.java.simpleName, LoginDialogInterface { (id, dateCreated, dateCreatedGmt, dateModified, dateModifiedGmt, email, firstName, lastName, role, username, password, billing, shipping, isPayingCustomer, ordersCount, totalSpent, avatarUrl) ->
            // If login was successful launch CartFragment.
            onCartSelected()
        })*/
    }



    fun onOrderCreateSelected() {
        replaceFragment(OrderCreateFragment(),OrderCreateFragment::class.java.simpleName)
     /*   launchUserSpecificFragment(OrderCreateFragment(), OrderCreateFragment::class.java.simpleName, LoginDialogInterface { (id, dateCreated, dateCreatedGmt, dateModified, dateModifiedGmt, email, firstName, lastName, role, username, password, billing, shipping, isPayingCustomer, ordersCount, totalSpent, avatarUrl) ->
            // If login was successful launch CartFragment.
            onCartSelected()
        })*/
    }

    /**
     * If user is logged in then [AccountEditFragment] is launched . Otherwise is showed a login dialog.
     */
    fun onAccountEditSelected() {
        launchUserSpecificFragment(AccountEditFragment(), AccountEditFragment::class.java.simpleName, LoginDialogInterface { (id, dateCreated, dateCreatedGmt, dateModified, dateModifiedGmt, email, firstName, lastName, role, username, password, billing, shipping, isPayingCustomer, ordersCount, totalSpent, avatarUrl) ->
            // If login was successful launch AccountEditFragment.
            onAccountEditSelected()
        })
    }

    /**
     * If user is logged in then [OrdersHistoryFragment] is launched . Otherwise is showed a login dialog.
     */
    fun onOrdersHistory() {
        launchUserSpecificFragment(OrdersHistoryFragment(), OrdersHistoryFragment::class.java.simpleName, LoginDialogInterface { (id, dateCreated, dateCreatedGmt, dateModified, dateModifiedGmt, email, firstName, lastName, role, username, password, billing, shipping, isPayingCustomer, ordersCount, totalSpent, avatarUrl) ->
            // If login was successful launch orderHistoryFragment.
            onOrdersHistory()
        })
    }

    /**
     * Check if user is logged in. If so then start defined fragment, otherwise show login dialog.
     *
     * @param fragment       fragment to launch.
     * @param transactionTag text identifying fragment transaction.
     * @param loginListener  listener on successful login.
     */
    private fun launchUserSpecificFragment(fragment: Fragment, transactionTag: String, loginListener: LoginDialogInterface) {
        if (SettingsMy.getActiveUser() != null) {
            replaceFragment(fragment, transactionTag)
        } else {
            val loginDialogFragment = LoginDialogFragment.newInstance(loginListener)
            loginDialogFragment.show(supportFragmentManager, LoginDialogFragment::class.java.simpleName)
        }
    }

    /**
     * Launch [OrderFragment].
     *
     * @param order order to show
     */
    fun onOrderSelected(order: Order?) {
        if (order != null) {
            val fragment = OrderFragment.newInstance(order.id!!)
            replaceFragment(fragment, OrderFragment::class.java.simpleName)
        } else {
            Timber.e("Creating order detail with null data.")
        }
    }

    override fun onDrawerOrderSelected() {
        launchUserSpecificFragment(OrdersHistoryFragment(), OrdersHistoryFragment::class.java.simpleName, LoginDialogInterface { (id, dateCreated, dateCreatedGmt, dateModified, dateModifiedGmt, email, firstName, lastName, role, username, password, billing, shipping, isPayingCustomer, ordersCount, totalSpent, avatarUrl) ->
            // If login was successful launch orderHistoryFragment.
            onOrdersHistory()
        })
    }
    override fun onDrawerHomeSelected() {
        replaceFragment(BannersFragment(),BannersFragment::class.java.simpleName)
    }

    override fun onDrawerSettingSelected() {
        replaceFragment(SettingsFragment(),SettingsFragment::class.java.simpleName)
    }
    override fun onBackPressed() {
        // If back button pressed, try close drawer if exist and is open. If drawer is already closed continue.
        if (drawerFragment == null || !drawerFragment!!.onBackHide()) {
            // If app should be finished or some fragment transaction still remains on backStack, let the system do the job.
            if (supportFragmentManager.backStackEntryCount > 0 || isAppReadyToFinish)
                super.onBackPressed()
            else {
                // BackStack is empty. For closing the app user have to tap the back button two times in two seconds.
                MsgUtils.showToast(this, MsgUtils.TOAST_TYPE_MESSAGE, getString(R.string.Another_click_for_leaving_app), MsgUtils.ToastLength.SHORT)
                isAppReadyToFinish = true
                Handler().postDelayed({ isAppReadyToFinish = false }, 2000)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // FB base events logging
        AppEventsLogger.activateApp(this)

        // GCM registration
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                IntentFilter(SettingsMy.REGISTRATION_COMPLETE))
    }

    override fun onPause() {
        super.onPause()
        // FB base events logging
        AppEventsLogger.deactivateApp(this)
        MyApplication.getInstance().cancelPendingRequests(CONST.MAIN_ACTIVITY_REQUESTS_TAG)

        // GCM registration
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver)
    }

    override fun onDrawerItemCategorySelected(category: Category) {
        clearBackStack()
        val fragment = CategoryFragment.newInstance(category.id!!, category.name!!)
        replaceFragment(fragment, CategoryFragment::class.java.simpleName)
    }

    override fun onDrawerCartSelected() {
        onCartSelected()
    }
    override fun prepareSearchSuggestions(navigation: List<DrawerItemCategory>) {

    }
    fun onCategorySelected(type:String, name:String){
        val fragment = CategoryFragment.newInstance(type, name)
        replaceFragment(fragment, CategoryFragment::class.java.simpleName)
    }

    companion object {
        val MSG_MAIN_ACTIVITY_INSTANCE_IS_NULL = "MainActivity instance is null."
        @get:Synchronized private var instance: MainActivity? = null

        fun updateCartCountNotification() {
            val instance = MainActivity.instance
            if (instance != null) {
                instance.showNotifyCount()
            } else {
                Timber.e(MSG_MAIN_ACTIVITY_INSTANCE_IS_NULL)
            }
        }

        fun setActionBarTitle(title: String?) {
           MainActivity.instance?.title = title
        }


        fun invalidateDrawerMenuHeader() {
            val instance = MainActivity.instance
            if (instance != null && instance.drawerFragment != null) {
                instance.drawerFragment?.invalidateHeader()
            } else {
                Timber.e(MSG_MAIN_ACTIVITY_INSTANCE_IS_NULL)
            }
        }
    }
}
