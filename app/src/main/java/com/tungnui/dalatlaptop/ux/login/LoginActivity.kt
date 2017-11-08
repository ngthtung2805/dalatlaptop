package com.tungnui.dalatlaptop.ux.login

import android.support.design.widget.TabLayout
import android.support.v7.app.AppCompatActivity

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.os.Bundle
import android.view.MenuItem


import com.tungnui.dalatlaptop.R
import com.tungnui.dalatlaptop.SettingsMy
import com.tungnui.dalatlaptop.interfaces.LoginDialogInterface
import com.tungnui.dalatlaptop.models.Customer
import com.tungnui.dalatlaptop.ux.MainActivity
import com.tungnui.dalatlaptop.ux.MainActivity.Companion.LOGIN_RESULT_CODE
import kotlinx.android.synthetic.main.activity_login.*




class LoginActivity : AppCompatActivity() {

    /**
     * The [android.support.v4.view.PagerAdapter] that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * [android.support.v4.app.FragmentStatePagerAdapter].
     */
    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null
    private var loginDialogInterface: LoginDialogInterface? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        this.title = "Đăng nhập/ Đăng kí"
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        // Set up the ViewPager with the sections adapter.
        container.adapter = mSectionsPagerAdapter

        container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container))

    }




    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home ->{
                this.finish()
                return true
            }
        }


        return super.onOptionsItemSelected(item)
    }


    /**
     * A [FragmentPagerAdapter] that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
           return  when(position){
                0 -> LoginFragment()
                else -> RegisterFragment()
            }
        }

        override fun getCount(): Int {
            // Show 3 total pages.
            return 2
        }
    }
    fun handleUserLogin(customer: Customer) {
        SettingsMy.setActiveUser(customer)
        MainActivity.invalidateDrawerMenuHeader()

        if (loginDialogInterface != null) {
            loginDialogInterface?.successfulLoginOrRegistration(customer)
        }
        setResult(LOGIN_RESULT_CODE,intent)
        this.finish()
    }
    companion object {
        fun logoutUser() {
            SettingsMy.setActiveUser(null)
            MainActivity.invalidateDrawerMenuHeader()
        }
    }
}
