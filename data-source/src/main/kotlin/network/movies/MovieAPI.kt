package ru.appkode.base.data.network.movies

import io.reactivex.Observable
import movies.details.MovieDetailsNM
import movies.search.SearchMovieNM
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieAPI {
  @GET("/3/movie/{id}?append_to_response=videos,credits")
  fun getMovieById(@Path("id") id: Long): Observable<MovieDetailsNM>

  @GET("/3/search/movie")
  fun searchMoviesPaged(
    @Query("query") title: String,
    @Query("page") page: Int
  ): Observable<SearchMovieNM>

}