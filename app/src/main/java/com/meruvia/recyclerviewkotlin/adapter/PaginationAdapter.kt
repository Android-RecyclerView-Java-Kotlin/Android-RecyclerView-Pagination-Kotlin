package com.meruvia.recyclerviewkotlin.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.meruvia.recyclerviewkotlin.R
import com.meruvia.recyclerviewkotlin.data.Movie

class PaginationAdapter :RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val ITEM: Int = 0
    private val LOADING: Int = 1

    private var isLoadingAdded: Boolean = false

    var movies: MutableList<Movie> = ArrayList()
    lateinit var context: Context

    fun PaginationAdapter(context: Context){
        this.movies = ArrayList()
        this.context = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        //return MovieVH(layoutInflater.inflate(R.layout.item_list, parent, false))

        return if(viewType == ITEM){
            MovieVH(layoutInflater.inflate(R.layout.item_list, parent, false))
        }else{
            LoadingVH(layoutInflater.inflate(R.layout.item_progress, parent, false))
        }
    }

    override fun getItemCount(): Int {
        return movies.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val movie = movies.get(position)
        //holder.bind(movie, context)

        if(getItemViewType(position) == ITEM){
            val movieVH: MovieVH = holder as MovieVH
            movieVH.bind(movie, context)
        }else{

        }
    }

    override fun getItemViewType(position: Int): Int {
        return if(position == movies.size - 1 && isLoadingAdded){
            LOADING
        }else{
            ITEM
        }
    }

    fun addAll(movies: MutableList<Movie>) {
        for(movie in movies){
            add(movie)
        }
    }

    fun add(movie: Movie) {
        movies.add(movie)
        notifyItemInserted(movies.size - 1)
    }

    fun addLoadingFooter() {
        isLoadingAdded = true
        add(Movie(""))
    }

    fun removeLoadingFooter() {
        isLoadingAdded = false

        val position: Int =movies.size -1
        val movie: Movie = movies.get(position)

        if(movie != null){
            movies.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    class MovieVH(view: View) : RecyclerView.ViewHolder(view) {
        val title = view.findViewById<TextView>(R.id.title)
        fun bind(movie: Movie, context: Context){
            title.text = movie.title
        }
    }

    class LoadingVH(view: View) : RecyclerView.ViewHolder(view)
}