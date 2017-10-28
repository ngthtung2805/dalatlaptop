package com.tungnui.dalatlaptop.api

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * Created by thanh on 22/09/2017.
 */
class AuthenticationInterceptor(private val authToken: String) : Interceptor {
    @Throws(IOException::class)
    public override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()

        val builder = original.newBuilder()
                .header("Authorization", authToken)

        val request = builder.build()
        return chain.proceed(request)
    }
}