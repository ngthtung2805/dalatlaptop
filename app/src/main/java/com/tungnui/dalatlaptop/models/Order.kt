package com.tungnui.dalatlaptop.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.tungnui.dalatlaptop.entities.Links

data class Order (
    @SerializedName("id")
    @Expose
    var id: Int? = null,
    @SerializedName("parent_id")
    @Expose
    var parentId: Int? = null,
    @SerializedName("number")
    @Expose
    var number: String? = null,
    @SerializedName("order_key")
    @Expose
    var orderKey: String? = null,
    @SerializedName("created_via")
    @Expose
    var createdVia: String? = null,
    @SerializedName("version")
    @Expose
    var version: String? = null,
    @SerializedName("status")
    @Expose
    var status: String? = null,
    @SerializedName("currency")
    @Expose
    var currency: String? = null,
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
    @SerializedName("discount_total")
    @Expose
    var discountTotal: String? = null,
    @SerializedName("discount_tax")
    @Expose
    var discountTax: String? = null,
    @SerializedName("shipping_total")
    @Expose
    var shippingTotal: String? = null,
    @SerializedName("shipping_tax")
    @Expose
    var shippingTax: String? = null,
    @SerializedName("cart_tax")
    @Expose
    var cartTax: String? = null,
    @SerializedName("total")
    @Expose
    var total: String? = null,
    @SerializedName("total_tax")
    @Expose
    var totalTax: String? = null,
    @SerializedName("prices_include_tax")
    @Expose
    var pricesIncludeTax: Boolean? = null,
    @SerializedName("customer_id")
    @Expose
    var customerId: Int? = null,
    @SerializedName("customer_ip_address")
    @Expose
    var customerIpAddress: String? = null,
    @SerializedName("customer_user_agent")
    @Expose
    var customerUserAgent: String? = null,
    @SerializedName("customer_note")
    @Expose
    var customerNote: String? = null,
    @SerializedName("billing")
    @Expose
    var billing: Billing? = null,
    @SerializedName("shipping")
    @Expose
    var shipping: Shipping? = null,
    @SerializedName("payment_method")
    @Expose
    var paymentMethod: String? = null,
    @SerializedName("payment_method_title")
    @Expose
    var paymentMethodTitle: String? = null,
    @SerializedName("transaction_id")
    @Expose
    var transactionId: String? = null,
    @SerializedName("date_paid")
    @Expose
    var datePaid: Any? = null,
    @SerializedName("date_paid_gmt")
    @Expose
    var datePaidGmt: Any? = null,
    @SerializedName("date_completed")
    @Expose
    var dateCompleted: Any? = null,
    @SerializedName("date_completed_gmt")
    @Expose
    var dateCompletedGmt: Any? = null,
    @SerializedName("cart_hash")
    @Expose
    var cartHash: String? = null,
    @SerializedName("line_items")
    @Expose
    var lineItems: List<LineItem>? = null,
    @SerializedName("tax_lines")
    @Expose
    var taxLines: List<Any>? = null,
    @SerializedName("shipping_lines")
    @Expose
    var shippingLines: List<ShippingLine>? = null,
  /*  @SerializedName("fee_lines")
    @Expose
    var feeLines: List<Any>? = null,
    @SerializedName("coupon_lines")
    @Expose
    var couponLines: List<Any>? = null,*/
    @SerializedName("refunds")
    @Expose
    var refunds: List<Any>? = null
)
