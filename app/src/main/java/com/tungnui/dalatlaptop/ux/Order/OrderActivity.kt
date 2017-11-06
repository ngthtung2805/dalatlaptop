package com.tungnui.dalatlaptop.ux.Order

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.NavUtils
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.tungnui.dalatlaptop.R
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
            fragmentTransaction.addToBackStack(transactionTag)
            fragmentTransaction.replace(R.id.order_content_frame, newFragment).commit()
            frgManager.executePendingTransactions()
        }
    }
    fun onAddAddress(isUpdate:Boolean = false){
        val fragment = OrderCreateAddAddressFragment.newInstance(isUpdate)
        replaceFragment(fragment, OrderCreateAddAddressFragment::class.java.simpleName)
    }
    fun onNextStep2(note:String){
        val fragment = OrderCreatePaymentFragment.newInstance(note)
        replaceFragment(fragment, OrderCreatePaymentFragment::class.java.simpleName)

    }
    fun onNextStep3(note:String, paymentMethod:String){
        val fragment = OrderCreateFinishFragment.newInstance(note, paymentMethod)
        replaceFragment(fragment, OrderCreateFinishFragment::class.java.simpleName)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home ->{
                if (supportFragmentManager.backStackEntryCount > 0)
                    this.onBackPressed()
                else{
                    NavUtils.navigateUpFromSameTask(this)
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
    fun onOrderSuccessContinousShopping(){
        NavUtils.navigateUpFromSameTask(this)
    }
}
