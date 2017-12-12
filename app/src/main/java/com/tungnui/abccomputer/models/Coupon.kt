package com.tungnui.abccomputer.models

/**
 * Created by thanh on 11/12/2017.
 */

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Coupon (

    @SerializedName("id")
    @Expose
    var id: Int? = null,
    @SerializedName("code")
    @Expose
    var code: String? = null,
    @SerializedName("amount")
    @Expose
    var amount: String? = null,
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
    @SerializedName("discount_type")
    @Expose
    var discountType: String? = null,
    @SerializedName("description")
    @Expose
    var description: String? = null,
    @SerializedName("date_expires")
    @Expose
    var dateExpires: String? = null,
    @SerializedName("date_expires_gmt")
    @Expose
    var dateExpiresGmt: String? = null,
    @SerializedName("usage_count")
    @Expose
    var usageCount: Int? = null,
    @SerializedName("individual_use")
    @Expose
    var individualUse: Boolean? = null,
    @SerializedName("product_ids")
    @Expose
    var productIds: List<Any>? = null,
    @SerializedName("excluded_product_ids")
    @Expose
    var excludedProductIds: List<Any>? = null,
    @SerializedName("usage_limit")
    @Expose
    var usageLimit: Any? = null,
    @SerializedName("usage_limit_per_user")
    @Expose
    var usageLimitPerUser: Int? = null,
    @SerializedName("limit_usage_to_x_items")
    @Expose
    var limitUsageToXItems: Any? = null,
    @SerializedName("free_shipping")
    @Expose
    var freeShipping: Boolean? = null,
    @SerializedName("product_categories")
    @Expose
    var productCategories: List<Any>? = null,
    @SerializedName("excluded_product_categories")
    @Expose
    var excludedProductCategories: List<Any>? = null,
    @SerializedName("exclude_sale_items")
    @Expose
    var excludeSaleItems: Boolean? = null,
    @SerializedName("minimum_amount")
    @Expose
    var minimumAmount: String? = null,
    @SerializedName("maximum_amount")
    @Expose
    var maximumAmount: String? = null,
    @SerializedName("email_restrictions")
    @Expose
    var emailRestrictions: List<Any>? = null,
    @SerializedName("used_by")
    @Expose
    var usedBy: List<Any>? = null

)