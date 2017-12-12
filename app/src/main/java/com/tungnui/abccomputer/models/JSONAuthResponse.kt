package com.tungnui.abccomputer.models

/**
 * Created by thanh on 22/10/2017.
 */
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class JSONAuthResponse (

    @SerializedName("status")
    @Expose
    var status: String? = null,
    @SerializedName("cookie")
    @Expose
    var cookie: String? = null,
    @SerializedName("cookie_name")
    @Expose
    var cookieName: String? = null,
    @SerializedName("user")
    @Expose
    var user: JSONAuthUser? = null

)