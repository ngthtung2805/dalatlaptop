package com.tungnui.dalatlaptop.woocommerceapi

import com.tungnui.dalatlaptop.models.JSONAuthResponse
import io.reactivex.Observable
import retrofit2.http.*

/**
 * Created by thanh on 22/10/2017.
 */
interface JSONAuthService{
    @POST("/api/auth/generate_auth_cookie/?")
    fun login(@Query("nonce") nonce :String,@Query("username") username:String, @Query("password") password:String):Observable<JSONAuthResponse>

}