package com.meruvia.recyclerviewkotlin.data

import com.google.gson.annotations.SerializedName

class Result {

    @SerializedName("popularity")
    var popularity: Double = 0.0
    @SerializedName("vote_count")
    var vote_count: Int = 0
    @SerializedName("video")
    var video: Boolean = false
    @SerializedName("poster_path")
    var poster_path: String = ""
    @SerializedName("id")
    var id: Int = 0
    @SerializedName("adult")
    var adult: Boolean = false
    @SerializedName("backdrop_path")
    var backdrop_path: String = ""
    @SerializedName("original_language")
    var original_language: String = ""
    @SerializedName("original_title")
    var original_title: String = ""
    @SerializedName("genre_ids")
    var genre_ids: List<Int> = ArrayList()
    @SerializedName("title")
    var title: String = ""
    @SerializedName("vote_average")
    var vote_average: Double = 0.0
    @SerializedName("overview")
    var overview: String = ""
    @SerializedName("release_date")
    var release_date: String = ""

}