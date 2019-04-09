package ru.appkode.base.data.network.books

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query
import ru.appkode.base.entities.core.books.details.BookDetailsNM
import ru.appkode.base.entities.core.books.search.BookSearchNM

interface BooksApi {

    @GET("book/show.xml")
    fun getBooksDetails(@Query("key") apiKey: String,
                        @Query("id") bookId: Int): Observable<BookDetailsNM>

    @GET("search/index.xml")
    fun getBooksSearch(@Query("key") apiKey: String,
                       @Query("q") text: String
    ): Observable<BookSearchNM>
}