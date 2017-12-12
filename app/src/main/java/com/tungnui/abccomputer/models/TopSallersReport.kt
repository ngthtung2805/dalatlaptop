package com.tungnui.abccomputer.models

/**
 * Created by thanh on 12/12/2017.
 */
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
data class TopSallersReport(
        var title:String,
        @SerializedName("product_id")
        @Expose
        var productId:Int,
        var quantity:Int
)