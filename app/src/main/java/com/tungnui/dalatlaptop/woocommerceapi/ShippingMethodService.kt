package com.tungnui.dalatlaptop.woocommerceapi

import com.tungnui.dalatlaptop.models.ShippingMethod
import io.reactivex.Observable
import retrofit2.http.GET

/**
 * Created by thanh on 29/10/2017.
 */
interface ShippingMethodService {
    @GET("/wp-json/wc/v2/shipping_methods")
    fun getAll(): Observable<ShippingMethod>
}