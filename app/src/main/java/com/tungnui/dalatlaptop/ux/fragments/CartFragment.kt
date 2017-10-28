package com.tungnui.dalatlaptop.ux.fragments

import android.app.ProgressDialog
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.android.volley.VolleyError

import com.tungnui.dalatlaptop.R
import com.tungnui.dalatlaptop.interfaces.CartRecyclerInterface
import com.tungnui.dalatlaptop.interfaces.RequestListener
import com.tungnui.dalatlaptop.listeners.OnSingleClickListener
import com.tungnui.dalatlaptop.utils.*
import com.tungnui.dalatlaptop.ux.MainActivity
import com.tungnui.dalatlaptop.ux.adapters.CartRecyclerAdapter
import com.tungnui.dalatlaptop.ux.dialogs.DiscountDialogFragment
import kotlinx.android.synthetic.main.fragment_cart.*
import kotlinx.android.synthetic.main.list_item_cart_product.*
import com.travijuu.numberpicker.library.Enums.ActionEnum
import com.travijuu.numberpicker.library.Interface.ValueChangedListener
import com.tungnui.dalatlaptop.models.Cart
import kotlinx.android.synthetic.main.list_item_cart_discount.*
import kotlinx.android.synthetic.main.list_item_products.view.*
import java.text.DecimalFormat


/**
 * Fragment handles shopping cart.
 */
class CartFragment : Fragment() {

    private lateinit var progressDialog: ProgressDialog
    private lateinit var cartRecyclerAdapter: CartRecyclerAdapter


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_cart, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MainActivity.setActionBarTitle(getString(R.string.Shopping_cart))
        progressDialog = Utils.generateProgressDialog(activity, false)
        prepareCartRecycler()

        cart_empty_action.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(view: View) {
                // Just open drawer menu.
                val activity = activity
                if (activity is MainActivity) {
                    if (activity.drawerFragment != null)
                        activity.drawerFragment?.toggleDrawerMenu()
                }
            }
        })

        cart_footer_action.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(view: View) {
                val discountDialog = DiscountDialogFragment.newInstance(object : RequestListener {
                    override fun requestSuccess(newId: Long) {
                        getCartContent()
                    }

                    override fun requestFailed(error: VolleyError) {
                        MsgUtils.logAndShowErrorMessage(activity, error)
                    }
                })

                discountDialog.show(fragmentManager, DiscountDialogFragment::class.java.simpleName)
            }
        })

        cart_order.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View) {
                if (activity is MainActivity) {
                    (activity as MainActivity).onOrderCreateSelected()
                }
            }
        })

        getCartContent()
    }


    private fun getCartContent() {
        val carts = context.cartHelper.getAll();
        MainActivity.updateCartCountNotification();
        if (carts.count() == 0) {
            setCartVisibility(false);
        } else {
            setCartVisibility(true);
            cartRecyclerAdapter.refreshItems(carts);
            cart_footer_quantity.text="Tổng số sản phẩm: ${context.cartHelper.totalItem()}"
            val formatter = DecimalFormat("#,###")
            cart_footer_price.text = "${formatter.format(context.cartHelper.total())}đ"
        }
    }


    private fun setCartVisibility(visible: Boolean) {
        if (visible) {
            cart_empty.visibility = View.GONE
            cart_recycler.visibility = View.VISIBLE
            cart_footer.visibility = View.VISIBLE
        } else {
            cartRecyclerAdapter.cleatCart()
            cart_empty.visibility = View.VISIBLE
            cart_recycler.visibility = View.GONE
            cart_footer.visibility = View.GONE
        }
    }

    private fun prepareCartRecycler() {
        cart_recycler.addItemDecoration(RecyclerDividerDecorator(activity))
        cart_recycler.itemAnimator = DefaultItemAnimator()
        cart_recycler.setHasFixedSize(true)
        cart_recycler.layoutManager = LinearLayoutManager(activity)
        cartRecyclerAdapter = CartRecyclerAdapter(object : CartRecyclerInterface {
            override fun onQuantityChange(cart: Cart, quantity: Int, actionEnum: ActionEnum) {
                cart.id?.let{context.cartHelper.updateQuantity(it,quantity)}
                getCartContent()
            }

            override fun onProductDelete(cart: com.tungnui.dalatlaptop.models.Cart) {
                cart.id?.let { context.cartHelper.delete(it) }
                getCartContent()
            }

            override fun onProductSelect(productId: Int) {
                if (activity is MainActivity)
                    (activity as MainActivity).onProductSelected(productId)
            }
        })
        cart_recycler.adapter = cartRecyclerAdapter
    }

    override fun onStop() {
        progressDialog.cancel()
        super.onStop()
    }
}
