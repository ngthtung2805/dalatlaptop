package com.tungnui.dalatlaptop.models

/**
 * Created by thanh on 22/10/2017.
 */
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class JSONAuthUser(

    @SerializedName("id")
    @Expose
    var id: Int? = null,
    @SerializedName("username")
    @Expose
    var username: String? = null,
    @SerializedName("nicename")
    @Expose
    var nicename: String? = null,
    @SerializedName("email")
    @Expose
    var email: String? = null,
    @SerializedName("url")
    @Expose
    var url: String? = null,
    @SerializedName("registered")
    @Expose
    var registered: String? = null,
    @SerializedName("displayname")
    @Expose
    var displayname: String? = null,
    @SerializedName("firstname")
    @Expose
    var firstname: String? = null,
    @SerializedName("lastname")
    @Expose
    var lastname: String? = null,
    @SerializedName("nickname")
    @Expose
    var nickname: String? = null,
    @SerializedName("description")
    @Expose
    var description: String? = null,
    @SerializedName("capabilities")
    @Expose
    var capabilities: String? = null,
    @SerializedName("avatar")
    @Expose
    var avatar: Any? = null

)