package com.tungnui.abccomputer.api

import com.tungnui.abccomputer.models.Order
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.*

/**
 * Created by thanh on 28/10/2017.
 */
interface OrderServices{
    @GET
    fun getAll(@Url url:String): Observable<Response<List<Order>>>

    @GET("/wp-json/wc/v2/orders")
    fun getOrderByCustomer(@Query("customer") id:Int):Observable<List<Order>>

    @PUT("/wp-json/wc/v2/orders/{id}")
    fun update(@Path("id") id:Int,@Body order:Order):Observable<Order>
    @GET("/wp-json/wc/v2/orders/{id}")
    fun single(@Path("id") id:Int):Observable<Order>
    @POST("/wp-json/wc/v2/orders/")
    fun create(@Body order:Order):Observable<Order>
}