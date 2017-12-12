package com.tungnui.abccomputer.api

import com.tungnui.abccomputer.models.TopSallersReport
import io.reactivex.Observable
import retrofit2.http.GET

/**
 * Created by thanh on 12/12/2017.
 */
interface ReportServices{
    @GET("/wp-json/wc/v2/reports/top_sellers")
    fun getTopSaller():Observable<List<TopSallersReport>>
}