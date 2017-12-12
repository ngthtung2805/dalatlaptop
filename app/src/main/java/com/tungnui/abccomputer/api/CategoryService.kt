package com.tungnui.abccomputer.api

import com.tungnui.abccomputer.models.Category
import io.reactivex.Observable
import retrofit2.http.*

/**
 * Created by thanh on 22/09/2017.
 */
interface CategoryService{
    @GET("/wp-json/wc/v2/products/categories/{id}")
    fun single(@Path("id") categoryID:Int): Observable<Category>

    @GET("/wp-json/wc/v2/products/categories")
    fun paging(@Query("page") page:Int=1, @Query("per_page") perPage:Int = 20): Observable<List<Category>>

    @GET("/wp-json/wc/v2/products/categories")
    fun getAll():Observable<List<Category>>

    @GET("/wp-json/wc/v2/products/categories?parent=0")
    fun getCategory(@Query("page") page:Int=1, @Query("per_page") perPage:Int = 50): Observable<List<Category>>

    @GET("/wp-json/wc/v2/products/categories")
    fun getChildrenCategory(@Query("parent") parentID:Int,@Query("page") page:Int=1, @Query("per_page") perPage:Int = 50): Observable<List<Category>>

    @POST("/wp-json/wc/v2/products/categories")
    fun create(@Body category: Category): Observable<Category>

    @PUT("/wp-json/wc/v2/products/categories/{id}")
    fun update(@Path("id") categoryID:Int, @Body product: Category): Observable<Category>

    @DELETE("/wp-json/wc/v2/products/categories/{id}")
    fun delete(@Path("id") categoryID:Int, @Query("force") force:Boolean=true): Observable<Category>
}