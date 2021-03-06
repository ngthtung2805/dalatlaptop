package com.tungnui.abccomputer.api

import android.text.TextUtils
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import okhttp3.Credentials
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by thanh on 22/09/2017.
 */
object ServiceGenerator {
    val API_BASE_URL ="https://abccomputer.tungnui.com"
    val CONSUMER_KEY ="ck_9c06a645be257c36f5b8545064559caf7d7eab05"
    val CONSUMER_SECRET = "cs_eba612121c821ba1fb8c3ba50692a27c204a0226"
    private val httpClient = OkHttpClient.Builder()
    private val json = GsonBuilder().setLenient().create()
    private val builder = Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(json))
    private var retrofit = builder.build()

    fun <S> createService(serviceClass: Class<S>): S
        = createService(serviceClass, Credentials.basic(CONSUMER_KEY, CONSUMER_SECRET))

    fun <S> createService(serviceClass: Class<S>, authToken: String): S {
        if (!TextUtils.isEmpty(authToken)) {
            val interceptor = AuthenticationInterceptor(authToken)
            if (!httpClient.interceptors().contains(interceptor)) {
                httpClient.addInterceptor(interceptor)
                builder.client(httpClient.build())
                retrofit = builder.build()
            }
        }
        return retrofit.create(serviceClass)
    }
}