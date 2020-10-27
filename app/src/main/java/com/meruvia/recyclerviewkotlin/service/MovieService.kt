package com.meruvia.recyclerviewkotlin.service

import com.meruvia.recyclerviewkotlin.data.TopRaterMovies
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieService {

    @GET("movie/top_rated")
    fun getTopRatedMovies(@Query("api_key") apiKey: String, @Query("language") language: String, @Query("page") pageIndex: Int): Call<TopRaterMovies>
}