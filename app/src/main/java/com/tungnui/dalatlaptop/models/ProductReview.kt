package com.tungnui.dalatlaptop.models

/**
 * Created by thanh on 07/11/2017.
 */
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ProductReview (
    @SerializedName("id")
    @Expose
    var id: Int? = null,
    @SerializedName("date_created")
    @Expose
    var dateCreated: String? = null,
    @SerializedName("date_created_gmt")
    @Expose
    var dateCreatedGmt: String? = null,
    @SerializedName("review")
    @Expose
    var review: String? = null,
    @SerializedName("rating")
    @Expose
    var rating: Int? = null,
    @SerializedName("name")
    @Expose
    var name: String? = null,
    @SerializedName("email")
    @Expose
    var email: String? = null,
    @SerializedName("verified")
    @Expose
    var verified: Boolean? = null
)