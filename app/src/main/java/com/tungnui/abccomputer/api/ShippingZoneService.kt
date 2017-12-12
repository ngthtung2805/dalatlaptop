package com.tungnui.abccomputer.api

import com.tungnui.abccomputer.models.ShippingLocation
import com.tungnui.abccomputer.models.ShippingMethod
import com.tungnui.abccomputer.models.ShippingZone
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Created by thanh on 08/12/2017.
 */
interface ShippingZoneService{
    @GET("/wp-json/wc/v2/shipping/zones")
    fun getProvince(): Observable<List<ShippingZone>>

    @GET("/wp-json/wc/v2/shipping/zones/{id}/locations")
    fun getDistrict(@Path("id") provinceId:Int):Observable<List<ShippingLocation>>
}