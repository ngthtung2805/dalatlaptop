package com.tungnui.dalatlaptop.models

/**
 * Created by thanh on 23/10/2017.
 */
data class Cart(
        var id :Int? = null,
        var productId:Int? = null,
        var productName:String? = null,
        var quantity :Int? = null,
        var price:Int? = null,
        var image:String?=null
)