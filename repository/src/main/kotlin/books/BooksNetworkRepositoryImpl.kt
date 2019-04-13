package ru.appkode.base.repository.books

import io.reactivex.Observable
import io.reactivex.Single
import ru.appkode.base.data.network.books.BooksApi
import ru.appkode.base.entities.core.books.details.BookDetailsUM
import ru.appkode.base.entities.core.books.details.toUiModel
import ru.appkode.base.entities.core.books.search.SearchResultUM
import ru.appkode.base.entities.core.books.search.toUiModel
import ru.appkode.base.ui.core.core.util.AppSchedulers

class BooksNetworkRepositoryImpl(
    val schedulers: AppSchedulers,
    private val booksApi: BooksApi
) : BooksNetworkRepository {

    override fun getBookDetails(bookId: Long): Observable<BookDetailsUM> {
        return booksApi.getBooksDetails(bookId)
            .map { book -> book.toUiModel() }
    }

    override fun getBookSearch(text: String, page: Int): Single<List<SearchResultUM>> {
        return booksApi.getBooksSearch(text, page)
            .map { list -> list.toUiModel() }
    }

}