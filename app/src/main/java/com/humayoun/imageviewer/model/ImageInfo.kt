package com.humayoun.imageviewer.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * data class to represent Image Info object returned from the Picsum service
 * */

@JsonClass(generateAdapter = true)
data class ImageInfo(
    @Json(name = "id") val id: String?,
    @Json(name = "author") val author:String?,
    @Json(name = "width") val width:Int?,
    @Json(name = "height") val height:Int?,
    @Json(name = "url")val url:String?,
    @Json(name = "download_url") val downloadUrl:String?
)
