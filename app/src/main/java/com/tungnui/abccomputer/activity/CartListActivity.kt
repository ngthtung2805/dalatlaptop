package com.tungnui.abccomputer.activity

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.MenuItem
import android.view.View
import com.travijuu.numberpicker.library.Enums.ActionEnum
import com.tungnui.abccomputer.R
import com.tungnui.abccomputer.adapter.CartListAdapter
import com.tungnui.abccomputer.adapter.CartRecyclerInterface
import com.tungnui.abccomputer.data.sqlite.*
import com.tungnui.abccomputer.listeners.OnSingleClickListener
import com.tungnui.abccomputer.models.Cart
import com.tungnui.abccomputer.utils.ActivityUtils
import com.tungnui.abccomputer.utils.formatPrice
import kotlinx.android.synthetic.main.activity_cart_list.*
import com.tungnui.abccomputer.R.id.cartList
import com.tungnui.abccomputer.R.string.quantity
import android.R.attr.name
import com.tungnui.abccomputer.SettingsMy
import com.tungnui.abccomputer.data.constant.AppConstants
import com.tungnui.abccomputer.models.LineItem


class CartListActivity : BaseActivity() {

    // initialize variables
    private var mContext: Context? = null
    private var mActivity: Activity? = null
    private var cartListAdapter: CartListAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initVariables()
        initView()
        loadCartData()
        initLister()
    }

    private fun initVariables() {
        mContext = applicationContext
        mActivity = this@CartListActivity
    }

    private fun initView() {
        setContentView(R.layout.activity_cart_list)
        initToolbar()
        enableBackButton()
        setToolbarTitle(getString(R.string.cart_list))
        initLoader()

        // init RecyclerView
        rvCartList?.setHasFixedSize(true)
        rvCartList?.layoutManager = LinearLayoutManager(this)
        cartListAdapter = CartListAdapter(object :CartRecyclerInterface{
            override fun onQuantityChange(cart: Cart, quantity: Int, actionEnum: ActionEnum) {
                cart.id?.let{applicationContext.DbHelper.updateCartQuantity(it,quantity)}
                loadCartData()
            }

            override fun onProductDelete(cart: Cart) {
                cart.id?.let { applicationContext.DbHelper.deleteCart(it) }
                loadCartData()
            }

            override fun onProductSelect(productId: Int) {
               /* if (activity is MainActivity)
                    (activity as MainActivity).onProductSelected(productId)*/
            }

        })
        rvCartList?.adapter = cartListAdapter

    }

    private fun initLister() {
        btnOrder.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View) {
                val user = SettingsMy.getActiveUser()
                if(user == null)
                    ActivityUtils.instance.invokeLoginAndOrder(this@CartListActivity)
                else
                    ActivityUtils.instance.invokeOrderShipping(this@CartListActivity)
            }
        })

    }
    private fun setCartVisibility(visible: Boolean) {
        if (visible) {
            cart_empty.visibility = View.GONE
            cart_recycler.visibility = View.VISIBLE
            cart_footer.visibility = View.VISIBLE
        } else {
            cartListAdapter?.cleatCart()
            cart_empty.visibility = View.VISIBLE
            cart_recycler.visibility = View.GONE
            cart_footer.visibility = View.GONE
        }
    }
    private fun loadCartData() {
        val carts = applicationContext.DbHelper.getAllCart();
        if (carts.isEmpty()) {
            setCartVisibility(false)
        } else {
            cartListAdapter?.refreshItems(carts);
            cart_footer_price.text =applicationContext.DbHelper.totalCart().toString().formatPrice();
            setCartVisibility(true)
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }





}
