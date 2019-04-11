package ru.appkode.base.repository.books

import io.reactivex.Observable
import ru.appkode.base.entities.core.books.details.BookDetailsUM
import ru.appkode.base.entities.core.books.search.BookSearchUM

interface BooksNetworkRepository {
    fun getBookDetails(bookId: Long): Observable<BookDetailsUM>
    fun getBookSearch(apiKey: String, text: String): Observable<BookSearchUM>
}