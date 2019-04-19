package ru.appkode.base.repository.books

import io.reactivex.Observable
import io.reactivex.Single
import ru.appkode.base.entities.core.books.details.BookDetailsUM
import ru.appkode.base.entities.core.books.search.BookUM
import ru.appkode.base.ui.core.core.LceState

interface BooksNetworkRepository {
    fun getBookDetails(bookId: Long, localRepository: BooksLocalRepository): Observable<LceState<BookDetailsUM>>
    fun getBookSearch(text: String, page: Int = 1): Single<List<BookUM>>
}