package ru.appkode.base.repository.books

import io.reactivex.Observable
import io.reactivex.Single
import ru.appkode.base.entities.core.books.details.BookDetailsUM
import ru.appkode.base.entities.core.books.lists.BookListItemUM
import ru.appkode.base.entities.core.books.search.BookUM

interface BooksNetworkRepository {
    fun getBookDetails(bookId: Long): Observable<BookDetailsUM>
    fun getBookSearch(text: String, page: Int = 1): Observable<List<BookListItemUM>>
}