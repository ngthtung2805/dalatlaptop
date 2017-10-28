package com.tungnui.dalatlaptop.woocommerceapi

import com.tungnui.dalatlaptop.models.Customer
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


/**
 * Created by thanh on 22/10/2017.
 */
interface CustomerServices{
    @GET("/wp-json/wc/v2/customers/{id}")
    fun single(@Path("id") customerId:Int):Observable<Customer>

    @POST("/wp-json/wc/v2/customers")
    fun create(@Body customer:Customer):Observable<Customer>
}