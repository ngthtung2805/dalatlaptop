package com.tungnui.dalatlaptop.ux.fragments

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView

import com.tungnui.dalatlaptop.CONST
import com.tungnui.dalatlaptop.MyApplication
import com.tungnui.dalatlaptop.R
import com.tungnui.dalatlaptop.SettingsMy
import com.tungnui.dalatlaptop.api.EndPoints
import com.tungnui.dalatlaptop.api.GsonRequest
import com.tungnui.dalatlaptop.api.ServiceGenerator
import com.tungnui.dalatlaptop.entities.User
import com.tungnui.dalatlaptop.entities.cart.Cart
import com.tungnui.dalatlaptop.entities.cart.CartProductItem
import com.tungnui.dalatlaptop.entities.delivery.Delivery
import com.tungnui.dalatlaptop.entities.delivery.Payment
import com.tungnui.dalatlaptop.entities.delivery.Shipping
import com.tungnui.dalatlaptop.entities.order.Order
import com.tungnui.dalatlaptop.interfaces.PaymentDialogInterface
import com.tungnui.dalatlaptop.interfaces.ShippingDialogInterface
import com.tungnui.dalatlaptop.listeners.OnSingleClickListener
import com.tungnui.dalatlaptop.models.ShippingMethod
import com.tungnui.dalatlaptop.utils.*
import com.tungnui.dalatlaptop.ux.MainActivity
import com.tungnui.dalatlaptop.ux.dialogs.LoginExpiredDialogFragment
import com.tungnui.dalatlaptop.ux.dialogs.PaymentDialogFragment
import com.tungnui.dalatlaptop.ux.dialogs.ShippingDialogFragment
import com.tungnui.dalatlaptop.woocommerceapi.OrderServices
import com.tungnui.dalatlaptop.woocommerceapi.ShippingMethodService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_order_create.*
import kotlinx.android.synthetic.main.fragment_orders_history.*
import timber.log.Timber

/**
 * Fragment allowing the user to create order.
 */
class OrderCreateFragment : Fragment() {
    private var mCompositeDisposable: CompositeDisposable
    val shippingMethodService: ShippingMethodService
    init {
        mCompositeDisposable = CompositeDisposable()
        shippingMethodService = ServiceGenerator.createService(ShippingMethodService::class.java)
    }
    private lateinit var progressDialog: ProgressDialog


    private var cart: Cart? = null
    private var orderTotalPrice: Double = 0.toDouble()

    // View with user information used to create order
    private val nameInputWrapper: TextInputLayout? = null
    private val streetInputWrapper: TextInputLayout? = null
    private val houseNumberInputWrapper: TextInputLayout? = null
    private val cityInputWrapper: TextInputLayout? = null
    private val zipInputWrapper: TextInputLayout? = null
    private val phoneInputWrapper: TextInputLayout? = null
    private val emailInputWrapper: TextInputLayout? = null

    // Shipping and payment
    private var delivery: ShippingMethod? = null
    private var selectedPayment: Payment? = null
    private var selectedShipping: Shipping? = null
    private val postOrderRequest: GsonRequest<Order>? = null

    /**
     * Check if all input fields are filled and also that is selected shipping and payment.
     * Method highlights all unfilled input fields.
     *
     * @return true if everything is Ok.
     */
    private// Check and show all missing values
            // Check if shipping and payment is selected
    val isRequiredFieldsOk: Boolean
        get() {
            var fieldRequired = getString(R.string.Required_field)
            var nameCheck = Utils.checkTextInputLayoutValueRequirement(order_create_name_wrapper, fieldRequired)
            var streetCheck = Utils.checkTextInputLayoutValueRequirement(order_create_street_wrapper, fieldRequired)
            var houseNumberCheck = Utils.checkTextInputLayoutValueRequirement(order_create_houseNumber_wrapper, fieldRequired)
            var cityCheck = Utils.checkTextInputLayoutValueRequirement(order_create_city_wrapper, fieldRequired)
            var zipCheck = Utils.checkTextInputLayoutValueRequirement(order_create_zip_wrapper, fieldRequired)
            var phoneCheck = Utils.checkTextInputLayoutValueRequirement(order_create_phone_wrapper, fieldRequired)
            var emailCheck = Utils.checkTextInputLayoutValueRequirement(order_create_email_wrapper, fieldRequired)

            if (nameCheck && streetCheck && houseNumberCheck && cityCheck && zipCheck && phoneCheck && emailCheck) {
                if (selectedShipping == null) {
                    MsgUtils.showToast(activity, MsgUtils.TOAST_TYPE_MESSAGE, getString(R.string.Choose_shipping_method), MsgUtils.ToastLength.SHORT)
                    order_create_scroll_layout.smoothScrollTo(0, order_create_delivery_shipping_layout.top)
                    return false
                }

                if (selectedPayment == null) {
                    MsgUtils.showToast(activity, MsgUtils.TOAST_TYPE_MESSAGE, getString(R.string.Choose_payment_method), MsgUtils.ToastLength.SHORT)
                    order_create_scroll_layout.smoothScrollTo(0, order_create_delivery_shipping_layout.top)
                    return false
                }
                return true
            } else {
                return false
            }
        }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_order_create, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MainActivity.setActionBarTitle(getString(R.string.Order_summary))
        progressDialog = Utils.generateProgressDialog(activity, false)
        order_create_summary_terms_and_condition.text = Html.fromHtml(getString(R.string.Click_on_Order_to_allow_our_Terms_and_Conditions))
        order_create_summary_terms_and_condition.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(view: View) {
                if (activity is MainActivity)
                    (activity as MainActivity).onTermsAndConditionsSelected()
            }
        })

        prepareFields()
        prepareDeliveryLayout()

        order_create_finish.setOnClickListener(object : OnSingleClickListener() {
            override fun onSingleClick(v: View) {
                if (isRequiredFieldsOk) {
                    // Prepare data
                    val order = Order()
                    order.name = Utils.getTextFromInputLayout(nameInputWrapper)
                    order.city = Utils.getTextFromInputLayout(cityInputWrapper)
                    order.street = Utils.getTextFromInputLayout(streetInputWrapper)
                    order.houseNumber = Utils.getTextFromInputLayout(houseNumberInputWrapper)
                    order.zip = Utils.getTextFromInputLayout(zipInputWrapper)
                    order.email = Utils.getTextFromInputLayout(emailInputWrapper)
                    order.shippingType = selectedShipping!!.id
                    if (selectedPayment != null) {
                        order.paymentType = selectedPayment!!.id
                    } else {
                        order.paymentType = -1
                    }
                    order.phone = Utils.getTextFromInputLayout(phoneInputWrapper)
                    order.note = Utils.getTextFromInputLayout(order_create_note_wrapper)

                    // Hide keyboard
                    v.clearFocus()
                    val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)

                    postOrder(order)
                }
            }
        })

        showSelectedShipping(selectedShipping)
        showSelectedPayment(selectedPayment)

        getUserCart()
    }


    /**
     * Prepare content views, adapters and listeners.
     *
     * @param view fragment base view.
     */
    private fun prepareFields() {
        var user = SettingsMy.getActiveUser();
        if (user != null) {
            Utils.setTextToInputLayout(nameInputWrapper, user.lastName+" "+ user.firstName);
           /* Utils.setTextToInputLayout(streetInputWrapper, user.);
            Utils.setTextToInputLayout(houseNumberInputWrapper, user.getHouseNumber());
            Utils.setTextToInputLayout(cityInputWrapper, user.getCity());
            Utils.setTextToInputLayout(zipInputWrapper, user.getZip());
            Utils.setTextToInputLayout(emailInputWrapper, user.getEmail());
            Utils.setTextToInputLayout(phoneInputWrapper, user.);*/
        } else {
            var loginExpiredDialogFragment = LoginExpiredDialogFragment();
            loginExpiredDialogFragment.show(getFragmentManager(), MSG_LOGIN_EXPIRED_DIALOG_FRAGMENT);
        }
    }


    private fun prepareDeliveryLayout() {
       /* order_create_delivery_shipping_layout.setOnClickListener {
            val shippingDialogFragment = ShippingDialogFragment.newInstance(delivery, selectedShipping) { shipping ->
                // Save selected value
                selectedShipping = shipping

                // Update shipping related values
                showSelectedShipping(shipping)

                // Continue for payment
                selectedPayment = null
                order_create_delivery_payment_name.text = getString(R.string.Choose_payment_method)
                order_create_delivery_payment_price.text = ""
                order_create_delivery_payment_layout.performClick()
            }
            shippingDialogFragment.show(fragmentManager, ShippingDialogFragment::class.java.simpleName)
        }*/

        order_create_delivery_payment_layout.setOnClickListener {
            val paymentDialogFragment = PaymentDialogFragment.newInstance(selectedShipping, selectedPayment) { payment ->
                selectedPayment = payment
                showSelectedPayment(payment)
            }
            paymentDialogFragment.show(fragmentManager, "PaymentDialog")
        }
    }

    /**
     * Show and update shipping related values.
     *
     * @param shipping values to show.
     */
    private fun showSelectedShipping(shipping: Shipping?) {
        if (shipping != null && order_create_delivery_shipping_name != null && order_create_delivery_shipping_price != null) {
            order_create_delivery_shipping_name.text = shipping.name
            if (shipping.price != 0) {
                order_create_delivery_shipping_price.text = shipping.priceFormatted
            } else {
                order_create_delivery_shipping_price.text = getText(R.string.free)
            }

            // Set total order price
            orderTotalPrice = shipping.totalPrice
            order_create_summary_total_price.text = shipping.totalPriceFormatted
            order_create_delivery_payment_layout.visibility = View.VISIBLE
        } else {
            Timber.e("Showing selected shipping with null values.")
        }
    }


    /**
     * Show and update payment related values.
     *
     * @param payment values to show.
     */
    private fun showSelectedPayment(payment: Payment?) {
        if (payment != null && order_create_delivery_payment_name != null && order_create_delivery_payment_price != null) {
            order_create_delivery_payment_name.text = payment.name
            if (payment.price != 0.0) {
                order_create_delivery_payment_price.text = payment.priceFormatted
            } else {
                order_create_delivery_payment_price.text = getText(R.string.free)
            }

            // Set total order price
            orderTotalPrice = payment.totalPrice
            order_create_summary_total_price.text = payment.totalPriceFormatted
        } else {
            Timber.e("Showing selected payment with null values.")
        }
    }

    private fun getUserCart() {
        refreshScreenContent()
    }

    private fun refreshScreenContent() {
        var carts = context.cartHelper.getAll()
        if (carts.count() == 0) {
            Timber.e(RuntimeException(), "Received null cart during order creation.")
            if (activity is MainActivity) (activity as MainActivity).onDrawerBannersSelected()
        } else {
            val inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            for (cart in carts) {
                val llRow = inflater.inflate(R.layout.order_create_cart_item, order_create_cart_items_layout, false) as LinearLayout
                val tvItemName = llRow.findViewById<View>(R.id.order_create_cart_item_name) as TextView
                tvItemName.text = cart.productName
                val tvItemPrice = llRow.findViewById<View>(R.id.order_create_cart_item_price) as TextView
                tvItemPrice.text = cart.price.toString().formatPrice()
                val tvItemQuantity = llRow.findViewById<View>(R.id.order_create_cart_item_quantity) as TextView
                tvItemQuantity.text = getString(R.string.format_quantity, cart.quantity)
                order_create_cart_items_layout.addView(llRow)
            }
           /* if (cart?.discounts != null) {
                for (i in 0 until cart.discounts.size) {
                    val llRow = inflater.inflate(R.layout.order_create_cart_item, order_create_cart_items_layout, false) as LinearLayout
                    val tvItemName = llRow.findViewById<View>(R.id.order_create_cart_item_name) as TextView
                    val tvItemPrice = llRow.findViewById<View>(R.id.order_create_cart_item_price) as TextView
                    tvItemName.text = cart.discounts[i].discount.name
                    tvItemPrice.text = cart.discounts[i].discount.valueFormatted
                    tvItemPrice.setTextColor(ContextCompat.getColor(context, R.color.colorAccent))
                    order_create_cart_items_layout.addView(llRow)
                }
            }*/

            order_create_total_price.text = context.cartHelper.total().toString().formatPrice()
            order_create_summary_total_price.text = context.cartHelper.total().toString().formatPrice()


            delivery_progress.setVisibility(View.VISIBLE);
            var disposable = shippingMethodService.getAll()
                    .subscribeOn((Schedulers.io()))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ response ->
                        delivery = response
                        delivery_progress.setVisibility(View.GONE);
                        order_create_delivery_shipping_layout.visibility = View.VISIBLE
                    },
                            { error ->
                                delivery_progress.setVisibility(View.GONE);
                            })

            mCompositeDisposable.add(disposable)

        }
    }

    private fun postOrder(order: Order) {
        /* final User user = SettingsMy.getActiveUser();
        if (user != null) {
            JSONObject jo;
            try {
                jo = JsonUtils.createOrderJson(order);
            } catch (JSONException e) {
                Timber.e(e, "Post order Json exception.");
                MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_INTERNAL_ERROR, null, MsgUtils.ToastLength.SHORT);
                return;
            }

            Timber.d("Post order jo: %s", jo.toString());
            String url = String.format(EndPoints.ORDERS, SettingsMy.getActualNonNullShop(getActivity()).getId());

            progressDialog.show();
            postOrderRequest = new GsonRequest<>(Request.Method.POST, url, jo.toString(), Order.class, new Response.Listener<Order>() {
                @Override
                public void onResponse(Order order) {
                    Timber.d("response: %s", order.toString());
                    progressDialog.cancel();

                    Analytics.logOrderCreatedEvent(cart, order.getRemoteId(), orderTotalPrice, selectedShipping);

                    updateUserData(user, order);
                    MainActivity.updateCartCountNotification();

                    DialogFragment thankYouDF = OrderCreateSuccessDialogFragment.newInstance(false);
                    thankYouDF.show(getFragmentManager(), OrderCreateSuccessDialogFragment.class.getSimpleName());
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.cancel();
                    // Return 501 for sample application.
                    if (postOrderRequest != null && postOrderRequest.getStatusCode() == 501) {
                        DialogFragment thankYouDF = OrderCreateSuccessDialogFragment.newInstance(true);
                        thankYouDF.show(getFragmentManager(), OrderCreateSuccessDialogFragment.class.getSimpleName());
                    } else {
                        MsgUtils.logAndShowErrorMessage(getActivity(), error);
                    }
                }
            }, getFragmentManager(), user.getAccessToken());
            postOrderRequest.setRetryPolicy(MyApplication.getDefaultRetryPolice());
            postOrderRequest.setShouldCache(false);
            MyApplication.getInstance().addToRequestQueue(postOrderRequest, CONST.ORDER_CREATE_REQUESTS_TAG);
        } else {
            LoginExpiredDialogFragment loginExpiredDialogFragment = new LoginExpiredDialogFragment();
            loginExpiredDialogFragment.show(getFragmentManager(), MSG_LOGIN_EXPIRED_DIALOG_FRAGMENT);
        }*/
    }

    /**
     * Update user information after successful order.
     *
     * @param user  actual user which will be updated
     * @param order order response for obtain user information
     */
    private fun updateUserData(user: User, order: Order) {
        /*   if (user != null) {
            if (order.getName() != null && !order.getName().isEmpty()) {
                user.setName(order.getName());
            }
            user.setEmail(order.getEmail());
            user.setPhone(order.getPhone());
            user.setCity(order.getCity());
            user.setStreet(order.getStreet());
            user.setZip(order.getZip());
            user.setHouseNumber(order.getHouseNumber());
            SettingsMy.setActiveUser(user);
        } else {
            Timber.e(new NullPointerException(), "Null user after successful order.");
        }*/
    }

    override fun onStop() {
        super.onStop()
        MyApplication.getInstance().cancelPendingRequests(CONST.ORDER_CREATE_REQUESTS_TAG)
        if (progressDialog != null) progressDialog!!.cancel()
        delivery_progress.visibility = View.GONE
    }

    companion object {

        val MSG_LOGIN_EXPIRED_DIALOG_FRAGMENT = "loginExpiredDialogFragment"
    }
}
