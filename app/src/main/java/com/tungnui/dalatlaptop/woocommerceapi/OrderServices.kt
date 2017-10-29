package com.tungnui.dalatlaptop.woocommerceapi

import com.tungnui.dalatlaptop.models.Order
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

/**
 * Created by thanh on 28/10/2017.
 */
interface OrderServices{
    @GET
    fun getAll(@Url url:String): Observable<Response<List<Order>>>
}