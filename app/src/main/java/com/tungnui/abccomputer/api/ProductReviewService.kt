package com.tungnui.abccomputer.api

import com.tungnui.abccomputer.models.ProductReview
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Created by thanh on 07/11/2017.
 */
interface ProductReviewService{
    @GET("/wp-json/wc/v2/products/{id}/reviews")
    fun getReview(@Path("id") productId:Int):Observable<List<ProductReview>>
}