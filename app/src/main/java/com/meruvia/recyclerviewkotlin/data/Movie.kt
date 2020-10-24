package com.meruvia.recyclerviewkotlin.data

class Movie{
    lateinit var title: String

    constructor(title: String){
        this.title = title
    }

    /**
     * Creating 10 dummy content for list.
     *
     * @param itemCount
     * @return
     */
    companion object {
        fun createMovies(itemCount: Int) : MutableList<Movie> {
            var movies: MutableList<Movie> = ArrayList()
            for (i in 1..20) {
                val movie: Movie = Movie("Movie $i")
                movies.add(movie)
            }
            return movies;
        }
    }

}