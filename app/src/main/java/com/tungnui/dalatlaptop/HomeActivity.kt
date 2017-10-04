package com.tungnui.dalatlaptop

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.tungnui.dalatlaptop.features.home.HomeFragment
import com.tungnui.dalatlaptop.features.home.HomePresenter
import kotlinx.android.synthetic.main.home_activity.*
import android.app.SearchManager
import android.content.Context
import android.support.v7.widget.SearchView
import com.tungnui.dalatlaptop.features.category.CategoryFragment


/**
 * Created by thanh on 27/09/2017.
 */
class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var homePresenter: HomePresenter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_activity)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        showFragment(HomeFragment())

    }



    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
        if(supportFragmentManager.backStackEntryCount == 0)
            finish()

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.option_menu, menu)
        var searchItem=menu.findItem(R.id.search)
        val searchView = searchItem.actionView as? SearchView
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val info = searchManager.getSearchableInfo(componentName)
        searchView?.setSearchableInfo(info)
        // Any other things you have to do when creating the options menu…
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
      return when(item.itemId){
           R.id.search-> true //Handle search
           R.id.shoppingCart->true
           else->super.onOptionsItemSelected(item)
       }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_home -> {
                this.showFragment(HomeFragment())
            }
            R.id.nav_category -> {
                this.showFragment(CategoryFragment())
            }
            R.id.nav_cart -> {

            }
            R.id.nav_order -> {

            }
            R.id.nav_setting -> {

            }
            R.id.nav_info -> {

            }
            R.id.nav_instruc -> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    public fun showFragment(fragment: Fragment) {
        var TAG= fragment.javaClass.simpleName
        var fragmentTransaction= supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.contentFrame,fragment,TAG)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commitAllowingStateLoss()
    }
    protected fun backstackFragment(){
        if(supportFragmentManager.backStackEntryCount==0){
            finish()
        }
        supportFragmentManager.popBackStack()
        removeCurrentFragment()
    }

    protected fun removeCurrentFragment() {
        var fragmentTransaction = supportFragmentManager.beginTransaction()
        var currentFrag= supportFragmentManager.findFragmentById(R.id.contentFrame)
        if(currentFrag!=null)
        {
            fragmentTransaction.remove(currentFrag)
        }
        fragmentTransaction.commitAllowingStateLoss()
    }

    fun enableNavigationIcon(){
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        toolbar.setNavigationOnClickListener{
            _->backstackFragment()
        }
    }
    protected fun disableNavigationIcon(){
        toolbar.setNavigationIcon(null)
    }

    fun setToolbarTitle(resId:Int){
        toolbar.setTitle(resId)
    }
}