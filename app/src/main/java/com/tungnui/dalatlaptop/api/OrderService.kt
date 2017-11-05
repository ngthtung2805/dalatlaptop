package com.tungnui.dalatlaptop.api

import com.tungnui.dalatlaptop.models.Order
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST


/**
 * Created by thanh on 05/11/2017.
 */
interface OrderService{
    @POST("/wp-json/wc/v2/orders")
    fun create(@Body order:Order): Observable<Order>
}