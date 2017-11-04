package com.tungnui.dalatlaptop.ux.Order

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.tungnui.dalatlaptop.R
import com.tungnui.dalatlaptop.ux.fragments.OrderCreateFragment
import kotlinx.android.synthetic.main.activity_order.*

class OrderActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        this.title = "Địa chỉ nhận hàng"
        addInitialFragment()

    }
    private fun addInitialFragment() {
        val fragment = OrderCreateAddressFragment()
        val frgManager = supportFragmentManager
        val fragmentTransaction = frgManager.beginTransaction()
        fragmentTransaction.add(R.id.order_content_frame, fragment).commit()
        frgManager.executePendingTransactions()
    }


    private fun replaceFragment(newFragment: Fragment?, transactionTag: String) {
        if (newFragment != null) {
            val frgManager = supportFragmentManager
            val fragmentTransaction = frgManager.beginTransaction()
            fragmentTransaction.setAllowOptimization(false)
            fragmentTransaction.addToBackStack(transactionTag)
            fragmentTransaction.replace(R.id.order_content_frame, newFragment).commit()
            frgManager.executePendingTransactions()
        }
    }
    fun onAddAddress(){
        var fragment = OrderCreateAddAddressFragment()
        replaceFragment(fragment, OrderCreateAddAddressFragment::class.java.simpleName)
    }
    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0)
                super.onBackPressed()
    }
    companion object {
        @get:Synchronized private var instance: OrderActivity? = null
        fun setActionBarTitle(title: String?) {
            OrderActivity.instance?.title = title
        }
    }
}
