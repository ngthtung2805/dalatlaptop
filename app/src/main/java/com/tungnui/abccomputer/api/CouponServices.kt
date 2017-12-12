package com.tungnui.abccomputer.api

import com.tungnui.abccomputer.models.Coupon
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query


/**
 * Created by thanh on 11/12/2017.
 */
interface CouponServices{
    @GET("/wp-json/wc/v2/coupons")
    fun getCouponByCode(@Query("code") code:String): Observable<List<Coupon>>
}