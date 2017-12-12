package com.tungnui.abccomputer.models

/**
 * Created by thanh on 29/10/2017.
 */
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ShippingMethod (

    @SerializedName("id")
    @Expose
    var id: String? = null,
    @SerializedName("title")
    @Expose
    var title: String? = null,
    @SerializedName("description")
    @Expose
    var description: String? = null


)