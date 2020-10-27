package com.meruvia.recyclerviewkotlin

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.meruvia.recyclerviewkotlin.adapter.PaginationAdapter
import com.meruvia.recyclerviewkotlin.api.MovieApi
import com.meruvia.recyclerviewkotlin.data.Movie
import com.meruvia.recyclerviewkotlin.data.Result
import com.meruvia.recyclerviewkotlin.data.TopRaterMovies
import com.meruvia.recyclerviewkotlin.listener.PaginationScrollListener
import com.meruvia.recyclerviewkotlin.service.MovieService
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    val PAGE_START: Int = 1
    var isLoading: Boolean = false
    var isLastPage: Boolean = false
    val TOTAL_PAGES: Int = 3
    var currentPage: Int = PAGE_START

    lateinit var mRecyclerView : RecyclerView
    val mAdapter : PaginationAdapter = PaginationAdapter()

    var movieService: MovieService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        movieService = MovieApi.getRetrofit().create(MovieService::class.java)

        setUpRecyclerView()
    }

    fun setUpRecyclerView(){
        mRecyclerView = findViewById(R.id.main_recycler) as RecyclerView
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mAdapter.PaginationAdapter(this)
        mRecyclerView.adapter = mAdapter

        loadFirstPage()

        mRecyclerView.addOnScrollListener(object : PaginationScrollListener(mRecyclerView.layoutManager as LinearLayoutManager){
            override fun loadMoreItems() {
                isLoading = true
                currentPage += 1

                Handler().postDelayed({
                    loadNextPage()
                }, 1000)
            }

            override fun getTotalPageCount(): Int {
                return TOTAL_PAGES
            }

            override fun isLastPage(): Boolean {
                return isLastPage
            }

            override fun isLoading(): Boolean {
                return isLoading
            }

        })
    }

    /**
     * Performs a Retrofit call to the top rated movies API.
     */
    fun callTopRatedMoviesApi(): Call<TopRaterMovies> { //2
        return movieService!!.getTopRatedMovies(
            getString(R.string.my_api_key),
            "en_US",
            currentPage
        )
    }

    /*
 * Extracts List<Result> from response
 */
    fun fetchResults(response: Response<TopRaterMovies>): List<Result> { //3
        val topRatedMovies = response.body() as TopRaterMovies
        return topRatedMovies.results
    }

    fun loadFirstPage() {

        callTopRatedMoviesApi().enqueue(object: Callback<TopRaterMovies> {
            override fun onFailure(call: Call<TopRaterMovies>, t: Throwable) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onResponse(call: Call<TopRaterMovies>, response: Response<TopRaterMovies>) {

                val results: MutableList<Result> = fetchResults(response) as MutableList<Result>
                main_progress.visibility = View.GONE
                mAdapter.addAll(results)

                if (currentPage <= TOTAL_PAGES) mAdapter.addLoadingFooter()
                else isLastPage = true
            }

        })
    }

    fun loadNextPage() {
        callTopRatedMoviesApi().enqueue(object: Callback<TopRaterMovies> {
            override fun onFailure(call: Call<TopRaterMovies>, t: Throwable) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onResponse(call: Call<TopRaterMovies>, response: Response<TopRaterMovies>) {
                val results = fetchResults(response) as MutableList<Result>

                mAdapter.removeLoadingFooter()
                isLoading = false
                mAdapter.addAll(results)

                if(currentPage != TOTAL_PAGES) mAdapter.addLoadingFooter()
                else isLastPage = true
            }

        })

    }
}
