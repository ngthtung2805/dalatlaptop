package com.tungnui.abccomputer.utils

import android.app.Activity
import android.content.Intent
import com.tungnui.abccomputer.activity.*

import com.tungnui.abccomputer.data.constant.AppConstants
import com.tungnui.abccomputer.model.Customer

class ActivityUtils {
    fun invokeActivity(activity: Activity, tClass: Class<*>, shouldFinish: Boolean) {
        val intent = Intent(activity, tClass)
        activity.startActivity(intent)
        if (shouldFinish) {
            activity.finish()
        }
    }
    fun invokeRegisterActivity(activity: Activity, name: String, email:String, isOrder:Boolean){
        val intent = Intent(activity, ProductListActivity::class.java)
        intent.putExtra(AppConstants.NAME,name)
        intent.putExtra(AppConstants.EMAIL, email)
        intent.putExtra(AppConstants.KEY_LOGIN_ORDER, isOrder)
        activity.startActivity(intent)
    }
    fun invokeMainActivity(activity: Activity) {
        val intent = Intent(activity, MainActivity::class.java)
        activity.startActivity(intent)
        activity.finish()
   }

    fun invokeProducts(activity: Activity,categoryId:Int=0, title:String="", searchQuery:String="", type:String="") {
        val intent = Intent(activity, ProductListActivity::class.java)
        intent.putExtra(AppConstants.CATEGORY_ID, categoryId)
        intent.putExtra(AppConstants.CATEGORY_NAME, title)
        intent.putExtra(AppConstants.SEARCH_QUERY, searchQuery)
        intent.putExtra(AppConstants.TYPE, type)
        activity.startActivity(intent)
    }

    fun invokeImage(activity: Activity, imageUrl: String) {
        val intent = Intent(activity, LargeImageViewActivity::class.java)
        intent.putExtra(AppConstants.KEY_IMAGE_URL, imageUrl)
        activity.startActivity(intent)
    }

    fun invokeProductDetails(activity: Activity, productId: Int) {
        val intent = Intent(activity, ProductDetailsActivity::class.java)
        intent.putExtra(AppConstants.PRODUCT_ID, productId)
        activity.startActivity(intent)
    }

    fun invokeAddressActivity(activity: Activity, editOnly: Boolean, shouldFinish: Boolean) {
        val intent = Intent(activity, MyAddressActivity::class.java)
        intent.putExtra(AppConstants.KEY_EDIT_ONLY, editOnly)
        activity.startActivity(intent)
        if (shouldFinish) {
            activity.finish()
        }
    }

    fun invokeLoginAndOrder(activity: Activity) {
        val intent = Intent(activity, LoginActivity::class.java)
        intent.putExtra(AppConstants.KEY_LOGIN_ORDER, true)
        activity.startActivity(intent)
        activity.finish()
    }

    fun invokeWebPageActivity(activity: Activity, pageTitle: String, url: String) {
        val intent = Intent(activity, WebPageActivity::class.java)
        intent.putExtra(AppConstants.BUNDLE_KEY_TITLE, pageTitle)
        intent.putExtra(AppConstants.BUNDLE_KEY_URL, url)
        activity.startActivity(intent)
    }

    fun invokeNotifyContentActivity(activity: Activity, title: String, message: String) {
        val intent = Intent(activity, NotificationContentActivity::class.java)
        intent.putExtra(AppConstants.BUNDLE_KEY_TITLE, title)
        intent.putExtra(AppConstants.BUNDLE_KEY_MESSAGE, message)
        activity.startActivity(intent)
    }

    fun invokeSearchActivity(activity: Activity, searchKey: String) {
        val intent = Intent(activity, SearchActivity::class.java)
        intent.putExtra(AppConstants.SEARCH_KEY, searchKey)
        activity.startActivity(intent)
    }

    fun invokeOrder(activity: Activity, orderId: String) {
        val intent = Intent(activity, OrderSuccessActivity::class.java)
        intent.putExtra(AppConstants.ORDER_ID, orderId)
        activity.startActivityForResult(intent, AppConstants.REQUEST_NONE)
    }
    fun invokeOrderConfirm(activity: Activity,shippingMethod: String, paymentMethod:String){
        val intent = Intent(activity, OrderConfirmActivity::class.java)
        intent.putExtra(AppConstants.SHIPPING_METHOD, shippingMethod)
        intent.putExtra(AppConstants.PAYMENT_METHOD, paymentMethod)
        activity.startActivityForResult(intent, AppConstants.REQUEST_NONE)
    }

    fun invokeOrderPayment(activity: Activity, shippingMethod: String) {
        val intent = Intent(activity, OrderPaymentActivity::class.java)
        intent.putExtra(AppConstants.SHIPPING_METHOD, shippingMethod)
        activity.startActivityForResult(intent, AppConstants.REQUEST_NONE)

    }

    fun invokeOrderShipping(activity: Activity) {
        val intentOrder = Intent(activity, OrderShippingActivity::class.java)
        activity.startActivityForResult(intentOrder, AppConstants.REQUEST_NONE)
    }

    companion object {

        private var sActivityUtils: ActivityUtils? = null

        val instance: ActivityUtils
            get() {
                if (sActivityUtils == null) {
                    sActivityUtils = ActivityUtils()
                }
                return sActivityUtils!!
            }
    }


}
