
package com.tungnui.dalatlaptop.ux

import android.app.SearchManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
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
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView

import com.facebook.appevents.AppEventsLogger
import com.tungnui.dalatlaptop.models.Category

import java.util.ArrayList

import com.tungnui.dalatlaptop.BuildConfig
import com.tungnui.dalatlaptop.R
import com.tungnui.dalatlaptop.SettingsMy
import com.tungnui.dalatlaptop.interfaces.LoginDialogInterface
import com.tungnui.dalatlaptop.models.Customer
import com.tungnui.dalatlaptop.models.Order
import com.tungnui.dalatlaptop.utils.MsgUtils
import com.tungnui.dalatlaptop.utils.cartHelper
import com.tungnui.dalatlaptop.utils.totalItem
import com.tungnui.dalatlaptop.ux.Order.OrderActivity
import com.tungnui.dalatlaptop.ux.dialogs.LoginDialogFragment
import com.tungnui.dalatlaptop.ux.fragments.AccountEditFragment
import com.tungnui.dalatlaptop.ux.fragments.AccountFragment
import com.tungnui.dalatlaptop.ux.fragments.BannersFragment
import com.tungnui.dalatlaptop.ux.fragments.CartFragment
import com.tungnui.dalatlaptop.ux.fragments.CategoryFragment
import com.tungnui.dalatlaptop.ux.fragments.DrawerFragment
import com.tungnui.dalatlaptop.ux.fragments.OrderFragment
import com.tungnui.dalatlaptop.ux.fragments.OrdersHistoryFragment
import com.tungnui.dalatlaptop.ux.fragments.ProductFragment
import com.tungnui.dalatlaptop.ux.fragments.SettingsFragment
import timber.log.Timber

class MainActivity : AppCompatActivity(), DrawerFragment.FragmentDrawerListener {

    var drawerFragment: DrawerFragment? = null
    private var isAppReadyToFinish = false
    private var cartCountView: TextView? = null
    private val mRegistrationBroadcastReceiver: BroadcastReceiver? = null
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


    private fun addInitialFragment() {
        val fragment = BannersFragment()
        val frgManager = supportFragmentManager
        val fragmentTransaction = frgManager.beginTransaction()
        fragmentTransaction.add(R.id.main_content_frame, fragment).commit()
        frgManager.executePendingTransactions()
    }


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

     private fun onSearchSubmitted(searchQuery: String) {
        clearBackStack()
        val fragment = CategoryFragment.newInstance(searchQuery)
        replaceFragment(fragment, CategoryFragment::class.java.simpleName)
    }




    override fun onAccountSelected() {
        val fragment = AccountFragment()
        replaceFragment(fragment, AccountFragment::class.java.simpleName)
    }





    fun onProductSelected(productId: Int) {
        val fragment = ProductFragment.newInstance(productId)
        if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            fragment.returnTransition = TransitionInflater.from(this).inflateTransition(android.R.transition.fade)
        }
        replaceFragment(fragment, ProductFragment::class.java.simpleName)
    }


    fun startSettingsFragment() {
        val fragment = SettingsFragment()
        replaceFragment(fragment, SettingsFragment::class.java.simpleName)
    }


    fun onCartSelected() {
        replaceFragment(CartFragment(),CartFragment::class.java.simpleName)

    }



    fun onOrderCreateSelected() {
        if (SettingsMy.getActiveUser() != null) {
            val mainIntent = Intent(this@MainActivity, OrderActivity::class.java)
            startActivityForResult(mainIntent,1)
        } else {
            val loginDialogFragment = LoginDialogFragment.newInstance(object : LoginDialogInterface {
                override fun successfulLoginOrRegistration(customer: Customer) {
                    val mainIntent = Intent(this@MainActivity, OrderActivity::class.java)
                    startActivityForResult(mainIntent,1)
                }

            })
            loginDialogFragment.show(supportFragmentManager, LoginDialogFragment::class.java.simpleName)
        }
        //replaceFragment(OrderCreateFragment(),OrderCreateFragment::class.java.simpleName)
    }

    /**
     * If user is logged in then [AccountEditFragment] is launched . Otherwise is showed a login dialog.
     */
    fun onAccountEditSelected() {
        launchUserSpecificFragment(AccountEditFragment(), AccountEditFragment::class.java.simpleName,object: LoginDialogInterface {
            override fun successfulLoginOrRegistration(customer: Customer) {
               onAccountEditSelected()
            }
        })
    }


    fun onOrdersHistory() {
        launchUserSpecificFragment(OrdersHistoryFragment(), OrdersHistoryFragment::class.java.simpleName,object :LoginDialogInterface {
            override fun successfulLoginOrRegistration(customer: Customer) {
                onOrdersHistory()
            }
        })
    }


    private fun launchUserSpecificFragment(fragment: Fragment, transactionTag: String, loginListener: LoginDialogInterface) {
        if (SettingsMy.getActiveUser() != null) {
            replaceFragment(fragment, transactionTag)
        } else {
            val loginDialogFragment = LoginDialogFragment.newInstance(loginListener)
            loginDialogFragment.show(supportFragmentManager, LoginDialogFragment::class.java.simpleName)
        }
    }

    fun onOrderSelected(order: Order?) {
        if (order != null) {
            val fragment = OrderFragment.newInstance(order.id!!)
            replaceFragment(fragment, OrderFragment::class.java.simpleName)
        } else {
            Timber.e("Creating order detail with null data.")
        }
    }

    override fun onDrawerOrderSelected() {
        launchUserSpecificFragment(OrdersHistoryFragment(), OrdersHistoryFragment::class.java.simpleName, object:LoginDialogInterface {
            override fun successfulLoginOrRegistration(customer: Customer) {
                onOrdersHistory()
            }
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
