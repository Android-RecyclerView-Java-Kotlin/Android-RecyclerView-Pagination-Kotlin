package com.meruvia.recyclerviewkotlin.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.annotation.Nullable
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.meruvia.recyclerviewkotlin.R
import com.meruvia.recyclerviewkotlin.data.Result
import com.meruvia.recyclerviewkotlin.util.PaginationAdapterCallback
import org.jetbrains.annotations.NotNull

class PaginationAdapter :RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val ITEM: Int = 0
    private val LOADING: Int = 1
    private val HERO: Int = 2

    private var isLoadingAdded: Boolean = false
    private var retryPageLoad: Boolean = false

    private var mCallback: PaginationAdapterCallback? = null

    private var errorMsg: String? = ""

    var movies: MutableList<Result> = ArrayList()
    lateinit var context: Context

    fun PaginationAdapter(context: Context){
        this.movies = ArrayList()
        this.mCallback = context as PaginationAdapterCallback
        this.context = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        //return MovieVH(layoutInflater.inflate(R.layout.item_list, parent, false))

        return if(viewType == HERO){
            HeroVH(layoutInflater.inflate(R.layout.item_hero, parent, false))
        }else if(viewType == ITEM){
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

        if(getItemViewType(position) == HERO){
            val heroVH: HeroVH = holder as HeroVH
            heroVH.bind(movie, context)
        }else if(getItemViewType(position) == ITEM){
            val movieVH: MovieVH = holder as MovieVH
            movieVH.bind(movie, context)
        }else{
            val loadingVH: LoadingVH = holder as LoadingVH

            if (retryPageLoad) {
                loadingVH.mErrorLayout.visibility = View.VISIBLE
                loadingVH.mProgressBar.visibility = View.GONE

                if(errorMsg != null) loadingVH.mErrorTxt.text = errorMsg
                else loadingVH.mErrorTxt.text = context.getString(R.string.error_msg_unknown)

            } else {
                loadingVH.mErrorLayout.visibility = View.GONE
                loadingVH.mProgressBar.visibility = View.VISIBLE
            }




            loadingVH.mRetryBtn.setOnClickListener{
                        showRetry(false, "")
                        mCallback!!.retryPageLoad()
            }
            loadingVH.mErrorLayout.setOnClickListener{
                showRetry(false, "")
                mCallback!!.retryPageLoad()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if(position == 0){
            HERO
        }else {
            if (position == movies.size - 1 && isLoadingAdded) {
                LOADING
            } else {
                ITEM
            }
        }
    }

    fun addAll(movies: MutableList<Result>) {
        for(movie in movies){
            add(movie)
        }
    }

    fun add(movie: Result) {
        movies.add(movie)
        notifyItemInserted(movies.size - 1)
    }

    fun addLoadingFooter() {
        isLoadingAdded = true
        add(Result())
    }

    fun removeLoadingFooter() {
        isLoadingAdded = false

        val position: Int =movies.size -1
        val movie: Result = movies.get(position)

        if(movie != null){
            movies.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    class MovieVH(view: View) : RecyclerView.ViewHolder(view) {
        val movieTitle = view.findViewById<TextView>(R.id.movie_title)
        val movieYear = view.findViewById<TextView>(R.id.movie_year)
        val movieDesc = view.findViewById<TextView>(R.id.movie_desc)
        val moviePoster = view.findViewById<ImageView>(R.id.movie_poster)
        val movieProgress = view.findViewById<ProgressBar>(R.id.movie_progress)
        fun bind(movie: Result, context: Context){
            movieTitle.text = movie.title
            movieYear.text = movie.release_date.substring(0, 4) + " | " + movie.original_language.toUpperCase()
            movieDesc.text = movie.overview

            // Using Glide to handle image loading.
            val BASE_URL_IMG = "https://image.tmdb.org/t/p/w220_and_h330_face"
            Glide
                .with(context)
                .load(BASE_URL_IMG + movie.poster_path)
                .listener(object: RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        movieProgress.visibility = View.GONE
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        movieProgress.visibility = View.GONE
                        return false
                    }

                })
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(moviePoster)
        }
    }

    class LoadingVH(view: View) : RecyclerView.ViewHolder(view) {

        val mProgressBar = view.findViewById<ProgressBar>(R.id.loadmore_progress)
        val mRetryBtn = view.findViewById<ImageButton>(R.id.loadmore_retry)
        val mErrorTxt = view.findViewById<TextView>(R.id.loadmore_errortxt)
        val mErrorLayout = view.findViewById<LinearLayout>(R.id.loadmore_errorlayout)
    }

    class HeroVH(view: View) : RecyclerView.ViewHolder(view) {
        val mMovieTitle = view.findViewById<TextView>(R.id.movie_title)
        val mMovieDesc = view.findViewById<TextView>(R.id.movie_desc)
        val mYear = view.findViewById<TextView>(R.id.movie_year)
        val mPosterImg = view.findViewById<ImageView>(R.id.movie_poster)

        // Using Glide to handle image loading.
        val BASE_URL_IMG = "https://image.tmdb.org/t/p/w220_and_h330_face"
        lateinit var context: Context

        fun bind(movie: Result, context: Context){
            this.context = context
            mMovieTitle.text = movie.title
            mMovieDesc.text = movie.overview
            mYear.text = formatYearLabel(movie)

            loadImage(movie.backdrop_path).into(mPosterImg)
        }

        fun formatYearLabel(result: Result): String {
            return result.release_date.substring(0, 4) + " | " + result.original_language.toUpperCase()
        }

        fun loadImage(@NotNull posterPath: String): RequestBuilder<Drawable> {
            return Glide.with(context).load(BASE_URL_IMG + posterPath).centerCrop()
        }
    }

    fun showRetry(show: Boolean, @Nullable errorMsg: String) {
        retryPageLoad = show
        notifyItemChanged(movies.size - 1)

        if(errorMsg != null) this.errorMsg = errorMsg
    }
}