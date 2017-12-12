package com.tungnui.abccomputer.models

/**
 * Created by thanh on 28/10/2017.
 */
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ShippingLine (

    @SerializedName("id")
    @Expose
    var id: Int? = null,
    @SerializedName("method_title")
    @Expose
    var methodTitle: String? = null,
    @SerializedName("method_id")
    @Expose
    var methodId: String? = null,
    @SerializedName("total")
    @Expose
    var total: String? = null,
    @SerializedName("total_tax")
    @Expose
    var totalTax: String? = null,
    @SerializedName("taxes")
    @Expose
    var taxes: List<Any>? = null
)