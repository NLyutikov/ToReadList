package ru.appkode.base.data.network.books

import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query
import ru.appkode.base.entities.core.books.details.BookDetailsNM
import ru.appkode.base.entities.core.books.search.BookSearchNM

interface BooksApi {

    @GET("book/show.xml")
    fun getBooksDetails(@Query("id") bookId: Long): Observable<BookDetailsNM>

    @GET("search/index.xml")
    fun getBooksSearch(@Query("q") text: String, @Query("page") page: Int): Single<BookSearchNM>
}