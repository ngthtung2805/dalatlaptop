package com.tungnui.abccomputer.models

/**
 * Created by thanh on 28/10/2017.
 */
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class LineItem (

    @SerializedName("id")
    @Expose
    var id: Int? = null,
    @SerializedName("name")
    @Expose
    var name: String? = null,
    @SerializedName("product_id")
    @Expose
    var productId: Int? = null,
    @SerializedName("variation_id")
    @Expose
    var variationId: Int? = null,
    @SerializedName("quantity")
    @Expose
    var quantity: Int? = null,
    @SerializedName("tax_class")
    @Expose
    var taxClass: String? = null,
    @SerializedName("subtotal")
    @Expose
    var subtotal: String? = null,
    @SerializedName("subtotal_tax")
    @Expose
    var subtotalTax: String? = null,
    @SerializedName("total")
    @Expose
    var total: String? = null,
    @SerializedName("total_tax")
    @Expose
    var totalTax: String? = null,
    @SerializedName("taxes")
    @Expose
    var taxes: List<Any>? = null,
    @SerializedName("meta_data")
    @Expose
    var metaData: List<Any>? = null,
    @SerializedName("sku")
    @Expose
    var sku: String? = null,
    @SerializedName("price")
    @Expose
    var price: String? = null

)