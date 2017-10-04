package com.tungnui.dalatlaptop.api

import com.tungnui.dalatlaptop.models.Product
import io.reactivex.Observable
import retrofit2.http.*


/**
 * Created by thanh on 22/09/2017.
 */
interface ProductService{
    @GET("/wp-json/wc/v2/products/{id}")
    fun Single(@Path("id") productID:Int): Observable<Product>

    @GET("/wp-json/wc/v2/products")
    fun paging(@Query("page") page:Int =1, @Query("per_page") perPage:Int=15):Observable<List<Product>>

    @POST("/wp-json/wc/v2/products")
    fun Create(@Body product: Product):Observable<Product>

    @PUT("/wp-json/wc/v2/products/{id}")
    fun Update(@Path("id") productID:Int,@Body product: Product):Observable<Product>

    @DELETE("/wp-json/wc/v2/products/{id}")
    fun Delete(@Path("id") productID:Int,@Query("force") force:Boolean=true):Observable<Product>

    @GET("/wp-json/wc/v2/products")
    fun GetByCategory(@Query("category") categoryId:Int,@Query("page") page:Int, @Query("per_page") perPage: Int=15)

    @GET("/wp-json/wc/v2/products?order=desc&orderby=date")
    fun getNewest(@Query("page") page:Int=1, @Query("per_page") perPage: Int=15):Observable<List<Product>>

    @GET("/wp-json/wc/v2/products?on_sale=true")
    fun getSale(@Query("page") page:Int=1, @Query("per_page") perPage: Int=15):Observable<List<Product>>
}