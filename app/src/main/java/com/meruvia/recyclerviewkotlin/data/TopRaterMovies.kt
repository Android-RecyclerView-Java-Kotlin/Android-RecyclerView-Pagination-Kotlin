package com.meruvia.recyclerviewkotlin.data

import com.google.gson.annotations.SerializedName

class TopRaterMovies {

    @SerializedName("page")
    val page: Int = 0
    @SerializedName("total_results")
    val total_results: Int = 0
    @SerializedName("total_pages")
    val total_pages: Int = 0
    @SerializedName("results")
    val results: List<Result> = ArrayList()
}