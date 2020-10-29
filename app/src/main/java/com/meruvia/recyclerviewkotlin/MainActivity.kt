package com.meruvia.recyclerviewkotlin

import android.content.Context
import android.net.ConnectivityManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.meruvia.recyclerviewkotlin.adapter.PaginationAdapter
import com.meruvia.recyclerviewkotlin.api.MovieApi
import com.meruvia.recyclerviewkotlin.data.Result
import com.meruvia.recyclerviewkotlin.data.TopRaterMovies
import com.meruvia.recyclerviewkotlin.listener.PaginationScrollListener
import com.meruvia.recyclerviewkotlin.service.MovieService
import com.meruvia.recyclerviewkotlin.util.PaginationAdapterCallback
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.error_layout.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeoutException

class MainActivity : AppCompatActivity(), PaginationAdapterCallback {

    val PAGE_START: Int = 1
    var isLoading: Boolean = false
    var isLastPage: Boolean = false
    val TOTAL_PAGES: Int = 3
    var currentPage: Int = PAGE_START

    lateinit var mRecyclerView : RecyclerView
    val mAdapter : PaginationAdapter = PaginationAdapter()

    //private lateinit var errorLayout: LinearLayout
    private lateinit var txtError: TextView
    private lateinit var btnRetry: Button

    private var movieService: MovieService? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        movieService = MovieApi.getRetrofit().create(MovieService::class.java)

        setUpRecyclerView()

        //errorLayout = findViewById(R.id.error_layout) as LinearLayout
        txtError = findViewById(R.id.error_txt_cause)
        btnRetry = findViewById(R.id.error_btn_retry)

        btnRetry.setOnClickListener {
            loadFirstPage()
        }

        main_swiperefresh.setOnRefreshListener {
            doRefresh()
        }
    }

    fun doRefresh() {
        main_progress.visibility = View.VISIBLE
        if(callTopRatedMoviesApi().isExecuted)
            callTopRatedMoviesApi().cancel()

        mAdapter.movies.clear()
        mAdapter.notifyDataSetChanged()
        loadFirstPage()
        main_swiperefresh.isRefreshing = false
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

        hideErrorView()

        callTopRatedMoviesApi().enqueue(object: Callback<TopRaterMovies> {
            override fun onFailure(call: Call<TopRaterMovies>, t: Throwable) {
                showErrorView(t)
            }

            override fun onResponse(call: Call<TopRaterMovies>, response: Response<TopRaterMovies>) {

                hideErrorView()

                val results: MutableList<Result> = fetchResults(response) as MutableList<Result>
                main_progress.visibility = View.GONE
                mAdapter.addAll(results)

                if (currentPage <= TOTAL_PAGES) mAdapter.addLoadingFooter()
                else isLastPage = true
            }

        })
    }

    fun showErrorView(throwable: Throwable){
        if(error_layout.visibility == View.GONE){
            error_layout.visibility = View.VISIBLE
            main_progress.visibility = View.GONE

            if(!isNetworkConnected()){
                txtError.setText(R.string.error_msg_no_internet)
            }else{
                if(throwable is TimeoutException){
                    txtError.setText(R.string.error_msg_timeout)
                }else{
                    txtError.setText(R.string.error_msg_unknown)
                }
            }
        }
    }

    fun hideErrorView(){
        if(error_layout.visibility == View.VISIBLE){
            error_layout.visibility = View.GONE
            main_progress.visibility = View.VISIBLE
        }
    }

    fun loadNextPage() {
        callTopRatedMoviesApi().enqueue(object: Callback<TopRaterMovies> {
            override fun onFailure(call: Call<TopRaterMovies>, t: Throwable) {
                mAdapter.showRetry(true, fetchErrorMessage(t))
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

    fun fetchErrorMessage(throwable: Throwable): String{
        var errorMsg: String = resources.getString(R.string.error_msg_unknown)

        if(!isNetworkConnected()){
            errorMsg = resources.getString(R.string.error_msg_no_internet)
        }else if(throwable is TimeoutException){
            errorMsg = resources.getString(R.string.error_msg_timeout)
        }

        return errorMsg
    }

    fun isNetworkConnected(): Boolean {
        val cm: ConnectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo != null
    }

    override fun retryPageLoad() {
        loadNextPage()
    }
}
