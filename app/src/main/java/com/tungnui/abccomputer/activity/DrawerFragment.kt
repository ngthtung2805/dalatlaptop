package com.tungnui.abccomputer.activity


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
import android.widget.Button
import android.widget.TextView
import com.facebook.accountkit.AccountKit
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.tungnui.abccomputer.R
import com.tungnui.abccomputer.SettingsMy
import com.tungnui.abccomputer.adapter.DrawerRecyclerAdapter
import com.tungnui.abccomputer.api.CategoryService
import com.tungnui.abccomputer.api.ServiceGenerator
import com.tungnui.abccomputer.models.Category
import com.tungnui.abccomputer.utils.*
import de.hdodenhof.circleimageview.CircleImageView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_drawer.*

class DrawerFragment : Fragment() {
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
        nav_view?.setNavigationItemSelectedListener(object : NavigationView.OnNavigationItemSelectedListener {
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                when (item.itemId) {
                    R.id.action_category -> animateMenuShow()
                    R.id.action_cart -> {
                        drawerListener.onDrawerCartSelected()
                        toggleDrawerMenu()
                    }
                    R.id.action_wish -> {
                        drawerListener.onDrawerWishSelected()
                        toggleDrawerMenu()
                    }
                    R.id.action_orders -> {
                        drawerListener.onDrawerOrderSelected()
                        toggleDrawerMenu()
                    }
                    R.id.action_call -> {
                        drawerListener.onDrawerCallSelected()
                        toggleDrawerMenu()
                    }
                    R.id.action_message -> {
                        drawerListener.onDrawerMessageSelected()
                        toggleDrawerMenu()
                    }
                    R.id.action_messenger -> {
                        drawerListener.onDrawerMessengerSelected()
                        toggleDrawerMenu()
                    }
                    R.id.action_email -> {
                        drawerListener.onDrawerEmailSelected()
                        toggleDrawerMenu()
                    }
                    R.id.action_share -> {
                        drawerListener.onDrawerShareSelected()
                        toggleDrawerMenu()
                    }
                    R.id.action_rate_app -> {
                        drawerListener.onDrawerRateAppSelected()
                        toggleDrawerMenu()
                    }
                    R.id.action_settings -> {
                        drawerListener.onDrawerSettingSelected()
                        toggleDrawerMenu()
                    }
                    R.id.action_home -> {
                        drawerListener.onDrawerHomeSelected()
                        toggleDrawerMenu()
                    }
                }// others
                return true
            }

        })
        drawer_menu_retry_btn.setOnClickListener {
            if (!drawerLoading)
                animateMenuShow()
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
        val btnLoginDrawer = headerView.findViewById<Button>(R.id.btnLoginDrawer)
        val btnLogoutDrawer = headerView.findViewById<Button>(R.id.btnLogoutDrawer)
        btnLoginDrawer?.let {
            it.setOnClickListener {
                drawerListener.onAccountSelected()
                toggleDrawerMenu()
            }
        }
        btnLogoutDrawer?.let {
            it.setOnClickListener {
                AccountKit.logOut()
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .build()
                var mGoogleSignInClient = GoogleSignIn.getClient(activity, gso);
                mGoogleSignInClient.signOut()
                SettingsMy.setActiveUser(null)
                invalidateHeader()
            }
        }

        invalidateHeader()
    }


    private fun prepareDrawerRecycler() {
        drawerMenuRecyclerAdapter = DrawerRecyclerAdapter { category ->
            if (category.display != "subcategories") {
                drawerListener.onDrawerItemCategorySelected(category)
                toggleDrawerMenu()
            } else
                animateSubMenuShow(category)
        }
        drawer_menu_recycler.layoutManager = LinearLayoutManager(context)
        drawer_menu_recycler.setHasFixedSize(true)
        drawer_menu_recycler.adapter = drawerMenuRecyclerAdapter

        drawerSubmenuRecyclerAdapter = DrawerRecyclerAdapter { category ->
            drawerListener.onDrawerItemCategorySelected(category)
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
        val txtUserName = headerView.findViewById<TextView>(R.id.tvNavProfileName)
        val avatarImage = headerView.findViewById<CircleImageView>(R.id.cvProfilePicture)
        var btnLogin = headerView.findViewById<Button>(R.id.btnLoginDrawer)
        var btnLogout = headerView.findViewById<Button>(R.id.btnLogoutDrawer)
        var menu = nav_view.menu
        val user = SettingsMy.getActiveUser()
        if (user != null) {
            txtUserName.text = user.username
            if (user.avatarUrl == null) {
                avatarImage.setImageResource(R.drawable.user)
            } else {
                avatarImage.loadGlideImg(user.avatarUrl)
            }
            btnLogin.visibility=View.GONE
            btnLogout.visibility =View.VISIBLE
            if(user.role == "administrator"){
                menu.findItem(R.id.action_admin).setVisible(true)
            }else{
                menu.findItem(R.id.action_admin).setVisible(false)
            }

        } else {
            txtUserName.text = "Xin chào, Khách!"
            avatarImage.setImageResource(R.drawable.user)
            btnLogin.visibility=View.VISIBLE
            btnLogout.visibility =View.GONE
            menu.findItem(R.id.action_admin).setVisible(false)
        }
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
        categoryService.getCategory()
                .subscribeOn((Schedulers.io()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ items ->
                    drawer_menu_progress.visibility = View.GONE
                    drawer_menu_retry_btn.visibility = View.GONE
                    drawerMenuRecyclerAdapter.changeDrawerItems(items)
                    drawerLoading = false
                },
                        { error ->
                            Log.e("DrawerFragment", error.message)
                            drawerLoading = false
                            drawer_menu_progress.visibility = View.GONE
                            drawer_menu_retry_btn.visibility = View.VISIBLE
                        })
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
        categoryService.getChildrenCategory(category.id!!)
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
        mDrawerLayout.removeDrawerListener(mDrawerToggle)
        super.onDestroy()
    }


    interface FragmentDrawerListener {
        fun onDrawerItemCategorySelected(category: Category)
        fun onAccountSelected()
        fun onDrawerCartSelected()
        fun onDrawerOrderSelected()
        fun onDrawerHomeSelected()
        fun onDrawerSettingSelected()
        fun onDrawerWishSelected()
        fun onDrawerCallSelected()
        fun onDrawerMessageSelected()
        fun onDrawerMessengerSelected()
        fun onDrawerEmailSelected()
        fun onDrawerShareSelected()
        fun onDrawerRateAppSelected()
    }


}
