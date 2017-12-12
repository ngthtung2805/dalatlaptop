package com.tungnui.abccomputer.models

/**
 * Created by thanh on 22/10/2017.
 */
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class JSONAuthNonce (

    @SerializedName("status")
    @Expose
    var status: String? = null,
    @SerializedName("controller")
    @Expose
    var controller: String? = null,
    @SerializedName("method")
    @Expose
    var method: String? = null,
    @SerializedName("nonce")
    @Expose
    var nonce: String? = null

)