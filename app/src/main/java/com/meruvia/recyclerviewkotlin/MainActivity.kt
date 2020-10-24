package com.meruvia.recyclerviewkotlin

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.meruvia.recyclerviewkotlin.adapter.PaginationAdapter
import com.meruvia.recyclerviewkotlin.data.Movie
import com.meruvia.recyclerviewkotlin.listener.PaginationScrollListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val PAGE_START: Int = 0
    var isLoading: Boolean = false
    var isLastPage: Boolean = false
    val TOTAL_PAGES: Int = 3
    var currentPage: Int = PAGE_START

    lateinit var mRecyclerView : RecyclerView
    val mAdapter : PaginationAdapter = PaginationAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setUpRecyclerView()
    }

    fun setUpRecyclerView(){
        mRecyclerView = findViewById(R.id.main_recycler) as RecyclerView
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mAdapter.PaginationAdapter(this)
        mRecyclerView.adapter = mAdapter

        Handler().postDelayed({
            loadFirstPage()
        }, 1000)

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

    fun loadFirstPage() {

        var movies = Movie.createMovies(mAdapter.itemCount)
        main_progress.visibility = View.GONE
        mAdapter.addAll(movies)

        if (currentPage <= TOTAL_PAGES) mAdapter.addLoadingFooter()
        else isLastPage = true
    }

    fun loadNextPage() {
        val movies: MutableList<Movie> = Movie.createMovies(mAdapter.itemCount)

        mAdapter.removeLoadingFooter()
        isLoading = false
        mAdapter.addAll(movies)

        if(currentPage != TOTAL_PAGES) mAdapter.addLoadingFooter()
        else isLastPage = true
    }
}
