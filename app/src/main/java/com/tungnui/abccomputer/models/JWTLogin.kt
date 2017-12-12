package com.tungnui.abccomputer.models

/**
 * Created by thanh on 22/10/2017.
 */
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class JWTLogin (
    @SerializedName("token")
    @Expose
    var token: String? = null,
    @SerializedName("user_email")
    @Expose
    var userEmail: String? = null,
    @SerializedName("user_nicename")
    @Expose
    var userNicename: String? = null,
    @SerializedName("user_display_name")
    @Expose
    var userDisplayName: String? = null
)