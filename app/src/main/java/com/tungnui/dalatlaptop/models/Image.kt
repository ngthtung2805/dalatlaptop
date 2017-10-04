package com.tungnui.dalatlaptop.models

/**
 * Created by thanh on 28/09/2017.
 */

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Image(
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
        @SerializedName("src")
        @Expose
        var src: String? = null,
        @SerializedName("name")
        @Expose
        var name: String? = null,
        @SerializedName("alt")
        @Expose
        var alt: String? = null,
        @SerializedName("position")
        @Expose
        var position: Int? = null
)
