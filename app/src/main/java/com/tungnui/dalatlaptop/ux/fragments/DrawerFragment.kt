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
import com.tungnui.dalatlaptop.entities.drawerMenu.DrawerItemCategory
import com.tungnui.dalatlaptop.entities.drawerMenu.DrawerItemPage
import com.tungnui.dalatlaptop.ux.adapters.DrawerRecyclerAdapter
import com.tungnui.dalatlaptop.api.CategoryService
import com.tungnui.dalatlaptop.api.ServiceGenerator
import com.tungnui.dalatlaptop.models.Category
import com.tungnui.dalatlaptop.utils.loadImg
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
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_category->{
                animateMenuShow()
                Log.i("ERROR","Lỗi rồi")
            }
            }

        return true;
    }

    /**
     * Indicates that menu is currently loading.
     */
    private var drawerLoading = false

    /**
     * Listener indicating events that occurred on the menu.
     */
    private var drawerListener: FragmentDrawerListener? = null

    private var mDrawerToggle: ActionBarDrawerToggle? = null

    // Drawer top menu fields.
    private var mDrawerLayout: DrawerLayout? = null
    private lateinit var drawerMenuRecyclerAdapter: DrawerRecyclerAdapter

    private var drawerSubmenuLayout: LinearLayout? = null
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
       headerView.setOnClickListener {
           _->drawerListener?.onAccountSelected()
           toggleDrawerMenu()
       }
        invalidateHeader()
    }

    /**
     * Prepare drawer menu content views, adapters and listeners.
     *
     * @param view fragment base view.
     */
    private fun prepareDrawerRecycler() {
        drawerMenuRecyclerAdapter = DrawerRecyclerAdapter { category ->
            if (category.display!="subcategories"){
                drawerListener?.onDrawerItemCategorySelected(category)
                toggleDrawerMenu()
            }

            else
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


    /**
     * Base method for layout preparation. Also set a listener that will respond to events that occurred on the menu.
     *
     * @param drawerLayout   drawer layout, which will be managed.
     * @param toolbar        toolbar bundled with a side menu.
     * @param eventsListener corresponding listener class.
     */
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

        mDrawerLayout!!.addDrawerListener(mDrawerToggle!!)
        mDrawerLayout!!.post { mDrawerToggle!!.syncState() }
    }

    /**
     * When the drawer menu is open, close it. Otherwise open it.
     */
    fun toggleDrawerMenu() {
        if (mDrawerLayout != null) {
            if (mDrawerLayout!!.isDrawerVisible(GravityCompat.START)) {
                mDrawerLayout!!.closeDrawer(GravityCompat.START)
            } else {
                mDrawerLayout!!.openDrawer(GravityCompat.START)
            }
        }
    }

    /**
     * When the drawer menu is open, close it.
     */
    fun closeDrawerMenu() {
        if (mDrawerLayout != null) {
            mDrawerLayout!!.closeDrawer(GravityCompat.START)
        }
    }

    /**
     * Check if drawer is open. If so close it.
     *
     * @return false if drawer was already closed
     */
    fun onBackHide(): Boolean {
        if (mDrawerLayout != null && mDrawerLayout!!.isDrawerVisible(GravityCompat.START)) {
            if (drawerSubmenuLayout!!.visibility == View.VISIBLE)
                animateSubMenuHide()
            else
                mDrawerLayout!!.closeDrawer(GravityCompat.START)
            return true
        }
        return false
    }

    /*
     * Method invalidates a drawer menu header. It is used primarily on a login state change.
    */
    fun invalidateHeader() {
        val headerView = nav_view.getHeaderView(0)
        val user = SettingsMy.getActiveUser()
        if(user!=null){
            val txtUserName = headerView.findViewById<TextView>(R.id.navigation_drawer_list_header_text)
            txtUserName.text  = user.username
            val avatarImage = headerView.findViewById<ImageView>(R.id.navigation_drawer_header_avatar)
            if(user.avatarUrl ==null){
                avatarImage.setImageResource(R.drawable.user)
            }else{
                avatarImage.loadImg(user.avatarUrl)
            }
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
        if(drawerMenuRecyclerAdapter.itemCount == 0)
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
        if(drawerSubmenuRecyclerAdapter.itemCount == 0)
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

    override fun onPause() {
        // Cancellation during onPause is needed because of app restarting during changing shop.
        MyApplication.getInstance().cancelPendingRequests(CONST.DRAWER_REQUESTS_TAG)
        if (drawerLoading) {
            drawer_menu_progress.visibility = View.GONE
            drawer_menu_retry_btn.visibility = View.VISIBLE
            drawerLoading = false
        }
        super.onPause()
    }

    override fun onDestroy() {
        mDrawerLayout!!.removeDrawerListener(mDrawerToggle!!)
        super.onDestroy()
    }

    companion object {

        private val BANNERS_ID = -123
        val NULL_DRAWER_LISTENER_WTF = "Null drawer listener. WTF."
    }

    /**
     * Interface defining events initiated by [DrawerFragment].
     */
    interface FragmentDrawerListener {

        /**
         * Launch [BannersFragment]. If fragment is already launched nothing happen.
         */
        fun onDrawerBannersSelected()

        /**
         * Launch [CategoryFragment].
         *
         * @param drawerItemCategory object specifying selected item in the drawer.
         */
        fun onDrawerItemCategorySelected(category: Category)

        /**
         * Launch [PageFragment], with downloadable content.
         *
         * @param drawerItemPage id of page for download and display. (Define in OpenShop server administration)
         */
        fun onDrawerItemPageSelected(drawerItemPage: DrawerItemPage)

        /**
         * Launch [AccountFragment].
         */
        fun onAccountSelected()

        /**
         * Prepare all search strings for search whisperer.
         *
         * @param navigation items for suggestions generating.
         */
        fun prepareSearchSuggestions(navigation: List<DrawerItemCategory>)
    }


}
