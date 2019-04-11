package ru.appkode.base.repository.books

import io.reactivex.Observable
import ru.appkode.base.data.network.books.BooksApi
import ru.appkode.base.entities.core.books.details.BookDetailsUM
import ru.appkode.base.entities.core.books.details.toUiModel
import ru.appkode.base.entities.core.books.search.BookSearchUM
import ru.appkode.base.entities.core.books.search.toUiModel
import ru.appkode.base.ui.core.core.util.AppSchedulers

class BooksNetworkRepositoryImpl(
    val schedulers: AppSchedulers,
    val booksApi: BooksApi
) : BooksNetworkRepository {

    override fun getBookDetails(bookId: Long): Observable<BookDetailsUM> {
        return booksApi.getBooksDetails(bookId)
            .map { book -> book.toUiModel() }
    }

    override fun getBookSearch(apiKey: String, text: String): Observable<BookSearchUM> {
        return booksApi.getBooksSearch(apiKey, text)
            .map { list -> list.toUiModel() }
    }

}