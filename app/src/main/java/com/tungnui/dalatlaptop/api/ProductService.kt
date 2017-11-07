package com.tungnui.dalatlaptop.api

import com.tungnui.dalatlaptop.models.Product
import io.reactivex.Observable
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*


/**
 * Created by thanh on 22/09/2017.
 */
interface ProductService{
    @GET("/wp-json/wc/v2/products/{id}")
    fun single(@Path("id") productID:Int): Observable<Product>

    @GET("/wp-json/wc/v2/products")
    fun paging(@Query("page") page:Int =1, @Query("per_page") perPage:Int=15):Observable<List<Product>>

    @GET("/wp-json/wc/v2/products")
    fun getRecommentProducts(@Query("include") ids:List<Int>):Observable<List<Product>>

    @GET
    fun getAll(@Url url:String):Observable<Response<List<Product>>>

    @POST("/wp-json/wc/v2/products")
    fun create(@Body product: Product):Observable<Product>

    @PUT("/wp-json/wc/v2/products/{id}")
    fun update(@Path("id") productID:Int,@Body product: Product):Observable<Product>

    @DELETE("/wp-json/wc/v2/products/{id}")
    fun delete(@Path("id") productID:Int,@Query("force") force:Boolean=true):Observable<Product>

    @GET("/wp-json/wc/v2/products")
    fun getByCategory(@Query("category") categoryId:Int,@Query("page") page:Int, @Query("per_page") perPage: Int=15)

    @GET("/wp-json/wc/v2/products?order=desc&orderby=date")
    fun getNewest(@Query("page") page:Int=1, @Query("per_page") perPage: Int=15):Observable<List<Product>>

    @GET("/wp-json/wc/v2/products?on_sale=true")
    fun getSale(@Query("page") page:Int=1, @Query("per_page") perPage: Int=15):Observable<List<Product>>

    @GET("/wp-json/wc/v2/products?featured=true")
    fun getFeatured(@Query("page") page:Int=1, @Query("per_page") perPage: Int=15):Observable<List<Product>>
}