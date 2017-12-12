package com.tungnui.abccomputer.api

import com.tungnui.abccomputer.models.Customer
import io.reactivex.Observable
import retrofit2.http.*


/**
 * Created by thanh on 22/10/2017.
 */
interface CustomerServices{
    @GET("/wp-json/wc/v2/customers/{id}")
    fun single(@Path("id") customerId:Int):Observable<Customer>

    @GET("/wp-json/wc/v2/customers")
    fun getByEmail(@Query("email") email:String):Observable<List<Customer>>

    @POST("/wp-json/wc/v2/customers")
    fun create(@Body customer:Customer):Observable<Customer>

    @PUT(" /wp-json/wc/v2/customers/{id}")
    fun update(@Path("id") customerId: Int, @Body customer: Customer):Observable<Customer>
}