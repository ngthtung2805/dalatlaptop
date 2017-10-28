package com.tungnui.dalatlaptop.api

import com.tungnui.dalatlaptop.models.Category
import com.tungnui.dalatlaptop.models.Product
import io.reactivex.Observable
import retrofit2.http.*

/**
 * Created by thanh on 22/09/2017.
 */
interface CategoryService{
    @GET("/wp-json/wc/v2/products/categories/{id}")
    fun Single(@Path("id") categoryID:Int): Observable<Category>

    @GET("/wp-json/wc/v2/products/categories")
    fun paging(@Query("page") page:Int=1, @Query("per_page") perPage:Int = 20): Observable<List<Category>>

    @GET("/wp-json/wc/v2/products/categories?parent=0")
    fun getCategory(@Query("page") page:Int=1, @Query("per_page") perPage:Int = 50): Observable<List<Category>>

    @GET("/wp-json/wc/v2/products/categories")
    fun getChildrenCategory(@Query("parent") parentID:Int,@Query("page") page:Int=1, @Query("per_page") perPage:Int = 50): Observable<List<Category>>

    @POST("/wp-json/wc/v2/products/categories")
    fun Create(@Body category: Category): Observable<Category>

    @PUT("/wp-json/wc/v2/products/categories/{id}")
    fun Update(@Path("id") categoryID:Int, @Body product: Category): Observable<Category>

    @DELETE("/wp-json/wc/v2/products/categories/{id}")
    fun Delete(@Path("id") categoryID:Int, @Query("force") force:Boolean=true): Observable<Category>
}