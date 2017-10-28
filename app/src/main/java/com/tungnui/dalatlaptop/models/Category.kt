package com.tungnui.dalatlaptop.models

/**
 * Created by thanh on 28/09/2017.
 */

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName



data class Category(
        @SerializedName("id")
        @Expose
        var id: Int? = null,
        @SerializedName("name")
        @Expose
        var name: String? = null,
        @SerializedName("slug")
        @Expose
        var slug: String? = null,
        @SerializedName("parent")
        @Expose
        var parent: Int? = null,
        @SerializedName("description")
        @Expose
        var description: String? = null,
        @SerializedName("display")
        @Expose
        var display: String? = null,
        @SerializedName("image")
        @Expose
        var image: Image? = null,
        @SerializedName("menu_order")
        @Expose
        var menuOrder: Int? = null,
        @SerializedName("count")
        @Expose
        var count: Int? = null
)  {

    fun hasChildren():Boolean{
        return true;
    }
}
