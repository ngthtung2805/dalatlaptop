package com.tungnui.dalatlaptop.ux.fragments


import android.os.Bundle
import android.os.SystemClock
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.tungnui.dalatlaptop.CONST
import com.tungnui.dalatlaptop.MyApplication
import com.tungnui.dalatlaptop.R
import com.tungnui.dalatlaptop.SettingsMy
import com.tungnui.dalatlaptop.ux.adapters.DrawerRecyclerAdapter
import com.tungnui.dalatlaptop.api.CategoryService
import com.tungnui.dalatlaptop.api.ServiceGenerator
import com.tungnui.dalatlaptop.models.Category
import com.tungnui.dalatlaptop.utils.loadImg
import com.tungnui.dalatlaptop.ux.MainActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_drawer.*

class DrawerFragment : Fragment(), NavigationView.OnNavigationItemSelectedListener {
    private var mCompositeDisposable: CompositeDisposable
    val categoryService: CategoryService

    init {
        mCompositeDisposable = CompositeDisposable()
        categoryService = ServiceGenerator.createService(CategoryService::class.java)
    }

    private var drawerLoading = false
    private lateinit var drawerListener: FragmentDrawerListener
    private lateinit var mDrawerToggle: ActionBarDrawerToggle
    private lateinit var mDrawerLayout: DrawerLayout
    private lateinit var drawerMenuRecyclerAdapter: DrawerRecyclerAdapter
    private lateinit var drawerSubmenuRecyclerAdapter: DrawerRecyclerAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_drawer, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        nav_view.setNavigationItemSelectedListener(this)
        drawer_menu_retry_btn.setOnClickListener {
            if (!drawerLoading)
                getDrawerItems()
        }


        prepareDrawerRecycler()
        drawer_menu_back_btn.setOnClickListener(object : View.OnClickListener {
            private var mLastClickTime: Long = 0

            override fun onClick(v: View) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000)
                    return
                mLastClickTime = SystemClock.elapsedRealtime()

                animateMenuHide()
            }
        })
        drawer_submenu_back_btn.setOnClickListener(object : View.OnClickListener {
            private var mLastClickTime: Long = 0

            override fun onClick(v: View) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000)
                    return
                mLastClickTime = SystemClock.elapsedRealtime()

                animateSubMenuHide()
            }
        })
        val headerView = nav_view.getHeaderView(0)
        headerView.setOnClickListener { _ ->
            drawerListener?.onAccountSelected()
            toggleDrawerMenu()
        }
        invalidateHeader()
    }


    private fun prepareDrawerRecycler() {
        drawerMenuRecyclerAdapter = DrawerRecyclerAdapter { category ->
            if (category.display != "subcategories") {
                drawerListener?.onDrawerItemCategorySelected(category)
                toggleDrawerMenu()
            } else
                animateSubMenuShow(category)
        }
        drawer_menu_recycler.layoutManager = LinearLayoutManager(context)
        drawer_menu_recycler.setHasFixedSize(true)
        drawer_menu_recycler.adapter = drawerMenuRecyclerAdapter

        drawerSubmenuRecyclerAdapter = DrawerRecyclerAdapter { category ->
            drawerListener?.onDrawerItemCategorySelected(category)
            toggleDrawerMenu()
        }
        drawer_submenu_recycler.layoutManager = LinearLayoutManager(context)
        drawer_submenu_recycler.itemAnimator = DefaultItemAnimator()
        drawer_submenu_recycler.setHasFixedSize(true)
        drawer_submenu_recycler.adapter = drawerSubmenuRecyclerAdapter
    }



    fun setUp(drawerLayout: DrawerLayout, toolbar: Toolbar, eventsListener: FragmentDrawerListener) {
        mDrawerLayout = drawerLayout
        this.drawerListener = eventsListener
        mDrawerToggle = object : ActionBarDrawerToggle(activity, drawerLayout, toolbar, R.string.content_description_open_navigation_drawer, R.string.content_description_close_navigation_drawer) {
            override fun onDrawerOpened(drawerView: View?) {
                super.onDrawerOpened(drawerView)
                activity.invalidateOptionsMenu()
            }

            override fun onDrawerClosed(drawerView: View?) {
                super.onDrawerClosed(drawerView)
                activity.invalidateOptionsMenu()
            }

            override fun onDrawerSlide(drawerView: View?, slideOffset: Float) {
                super.onDrawerSlide(drawerView, slideOffset)
                //                toolbar.setAlpha(1 - slideOffset / 2);
            }
        }

        toolbar.setOnClickListener { toggleDrawerMenu() }

        mDrawerLayout.addDrawerListener(mDrawerToggle)
        mDrawerLayout.post { mDrawerToggle.syncState() }
    }

    fun toggleDrawerMenu() {
        if (mDrawerLayout.isDrawerVisible(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START)
        } else {
            mDrawerLayout.openDrawer(GravityCompat.START)
        }
    }

    fun closeDrawerMenu() {
            mDrawerLayout.closeDrawer(GravityCompat.START)
    }

    fun onBackHide(): Boolean {
        mDrawerLayout.let {
            if (it.isDrawerVisible(GravityCompat.START)) {
                if (drawer_submenu_layout.visibility == View.VISIBLE) {
                    animateSubMenuHide()
                } else
                    it.closeDrawer(GravityCompat.START)
                return true
            }
        }
        return false
    }


    fun invalidateHeader() {
        val headerView = nav_view.getHeaderView(0)
        val txtUserName = headerView.findViewById<TextView>(R.id.navigation_drawer_list_header_text)
        val avatarImage = headerView.findViewById<ImageView>(R.id.navigation_drawer_header_avatar)
        val user = SettingsMy.getActiveUser()
        if (user != null) {
            txtUserName.text = user.username
            if (user.avatarUrl == null) {
                avatarImage.setImageResource(R.drawable.user)
            } else {
                avatarImage.loadImg(user.avatarUrl)
            }
        }else{
            txtUserName.text = "Xin chào, Khách!"
            avatarImage.setImageResource(R.drawable.user)
        }
    }

    private fun getDrawerItems() {

    }

    private fun animateSubMenuHide() {
        val slideAwayDisappear = AnimationUtils.loadAnimation(activity, R.anim.slide_away_disappear)
        val slideAwayAppear = AnimationUtils.loadAnimation(activity, R.anim.slide_away_appear)
        slideAwayDisappear.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                drawer_menu_layout.visibility = View.VISIBLE
                drawer_menu_layout.startAnimation(slideAwayAppear)
            }

            override fun onAnimationEnd(animation: Animation) {
                drawer_submenu_layout.visibility = View.GONE
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
        drawer_submenu_layout.startAnimation(slideAwayDisappear)
    }

    private fun animateMenuHide() {
        val slideAwayDisappear = AnimationUtils.loadAnimation(activity, R.anim.slide_away_disappear)
        val slideAwayAppear = AnimationUtils.loadAnimation(activity, R.anim.slide_away_appear)
        slideAwayDisappear.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                nav_view.visibility = View.VISIBLE
                nav_view.startAnimation(slideAwayAppear)
            }

            override fun onAnimationEnd(animation: Animation) {
                drawer_menu_layout.visibility = View.GONE
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
        drawer_menu_layout.startAnimation(slideAwayDisappear)
    }

    private fun animateMenuShow() {
        val slideInDisappear = AnimationUtils.loadAnimation(activity, R.anim.slide_in_disappear)
        val slideInAppear = AnimationUtils.loadAnimation(activity, R.anim.slide_in_appear)
        slideInDisappear.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                drawer_menu_layout.visibility = View.VISIBLE
                drawer_menu_layout.startAnimation(slideInAppear)
            }

            override fun onAnimationEnd(animation: Animation) {
                nav_view.visibility = View.GONE
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
        nav_view.startAnimation(slideInDisappear)
        drawerLoading = true
        if (drawerMenuRecyclerAdapter.itemCount == 0)
            drawer_menu_progress.visibility = View.VISIBLE
        var disposable = categoryService.getCategory()
                .subscribeOn((Schedulers.io()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ items ->
                    drawer_menu_progress.visibility = View.GONE
                    drawer_menu_retry_btn.visibility = View.GONE
                    drawerMenuRecyclerAdapter.changeDrawerItems(items)
                    drawerLoading = false
                },
                        { _ ->
                            drawerLoading = false
                            drawer_menu_progress.visibility = View.GONE
                            drawer_menu_retry_btn.visibility = View.VISIBLE
                        })
        mCompositeDisposable.add(disposable)
    }

    private fun animateSubMenuShow(category: Category) {
        val slideInDisappear = AnimationUtils.loadAnimation(activity, R.anim.slide_in_disappear)
        val slideInAppear = AnimationUtils.loadAnimation(activity, R.anim.slide_in_appear)
        slideInDisappear.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                drawer_submenu_layout.visibility = View.VISIBLE
                drawer_submenu_layout.startAnimation(slideInAppear)
            }

            override fun onAnimationEnd(animation: Animation) {
                drawer_menu_layout.visibility = View.GONE
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
        drawer_menu_layout.startAnimation(slideInDisappear)
        drawer_submenu_back_btn.text = category.name
        if (drawerSubmenuRecyclerAdapter.itemCount == 0)
            drawer_submenu_progress.visibility = View.VISIBLE
        var disposable = categoryService.getChildrenCategory(category.id!!)
                .subscribeOn((Schedulers.io()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ items ->
                    drawer_submenu_progress.visibility = View.GONE
                    drawer_submenu_retry_btn.visibility = View.GONE
                    drawerSubmenuRecyclerAdapter.changeDrawerItems(items)
                    drawerLoading = false
                },
                        { _ ->
                            drawer_submenu_progress.visibility = View.GONE
                            drawer_submenu_retry_btn.visibility = View.VISIBLE
                        })
        mCompositeDisposable.add(disposable)

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                drawerListener.onDrawerHomeSelected()
                toggleDrawerMenu()
            }
            R.id.nav_category -> animateMenuShow()
            R.id.nav_cart -> {
                drawerListener.onDrawerCartSelected()
                toggleDrawerMenu()
            }
            R.id.nav_order -> {
                drawerListener.onDrawerOrderSelected()
                toggleDrawerMenu()
            }
        }

        return true;
    }

    override fun onPause() {
        if (drawerLoading) {
            drawer_menu_progress.visibility = View.GONE
            drawer_menu_retry_btn.visibility = View.VISIBLE
            drawerLoading = false
        }
        super.onPause()
    }

    override fun onDestroy() {
        mDrawerLayout?.removeDrawerListener(mDrawerToggle)
        super.onDestroy()
    }


    interface FragmentDrawerListener {
        fun onDrawerItemCategorySelected(category: Category)
        fun onAccountSelected()
        fun onDrawerCartSelected()
        fun onDrawerOrderSelected()
        fun onDrawerHomeSelected()
        fun onDrawerSettingSelected()
    }


}
