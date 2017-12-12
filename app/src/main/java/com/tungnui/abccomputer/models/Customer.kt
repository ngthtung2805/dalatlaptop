package com.tungnui.abccomputer.models

/**
 * Created by thanh on 22/10/2017.
 */

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Customer (

    @SerializedName("id")
    @Expose
    var id: Int? = null,
    @SerializedName("date_created")
    @Expose
    var dateCreated: String? = null,
    @SerializedName("date_created_gmt")
    @Expose
    var dateCreatedGmt: String? = null,
    @SerializedName("date_modified")
    @Expose
    var dateModified: String? = null,
    @SerializedName("date_modified_gmt")
    @Expose
    var dateModifiedGmt: String? = null,
    @SerializedName("email")
    @Expose
    var email: String? = null,
    @SerializedName("first_name")
    @Expose
    var firstName: String? = null,
    @SerializedName("last_name")
    @Expose
    var lastName: String? = null,
    @SerializedName("role")
    @Expose
    var role: String? = null,
    @SerializedName("username")
    @Expose
    var username: String? = null,
    @SerializedName("password")
    @Expose
    var password:String?=null,
    @SerializedName("billing")
    @Expose
    var billing: Billing? = null,
    @SerializedName("shipping")
    @Expose
    var shipping: Shipping? = null,
    @SerializedName("is_paying_customer")
    @Expose
    var isPayingCustomer: Boolean? = null,
    @SerializedName("orders_count")
    @Expose
    var ordersCount: Int? = null,
    @SerializedName("total_spent")
    @Expose
    var totalSpent: String? = null,
    @SerializedName("avatar_url")
    @Expose
    var avatarUrl: String? = null,
    var code: String?=null
    )
