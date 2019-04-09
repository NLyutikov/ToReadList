package ru.appkode.base.repository.books

import io.reactivex.Observable
import ru.appkode.base.entities.core.books.details.BookDetailsUM

interface BooksNetworkRepository {
    fun getBookDetails(bookId: Long): Observable<BookDetailsUM>
}