package com.tungnui.abccomputer.models

/**
 * Created by thanh on 22/10/2017.
 */

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Shipping (

    @SerializedName("first_name")
    @Expose
    var firstName: String? = null,
    @SerializedName("last_name")
    @Expose
    var lastName: String? = null,
    @SerializedName("company")
    @Expose
    var company: String? = null,
    @SerializedName("address_1")
    @Expose
    var address1: String? = null,
    @SerializedName("address_2")
    @Expose
    var address2: String? = null,
    @SerializedName("city")
    @Expose
    var city: String? = null,
    @SerializedName("state")
    @Expose
    var state: String? = null,
    @SerializedName("postcode")
    @Expose
    var postcode: String? = null,
    @SerializedName("country")
    @Expose
    var country: String? = null

)