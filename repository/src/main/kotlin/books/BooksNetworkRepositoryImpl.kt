package ru.appkode.base.repository.books

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.Function3
import ru.appkode.base.data.network.books.BooksApi
import ru.appkode.base.entities.core.books.details.BookDetailsUM
import ru.appkode.base.entities.core.books.details.toBookListItemUM
import ru.appkode.base.entities.core.books.details.toUiModel
import ru.appkode.base.entities.core.books.search.BookUM
import ru.appkode.base.entities.core.books.search.toUiModel
import ru.appkode.base.ui.core.core.LceState
import ru.appkode.base.ui.core.core.util.AppSchedulers
import ru.appkode.base.ui.core.core.util.toLceEventObservable

class BooksNetworkRepositoryImpl(
    val schedulers: AppSchedulers,
    private val booksApi: BooksApi
) : BooksNetworkRepository {

    override fun getBookDetails(
        bookId: Long,
        localRepository: BooksLocalRepository
    ): Observable<LceState<BookDetailsUM>> {
        return booksApi.getBooksDetails(bookId)
            .map { book -> book.toUiModel() }
            .flatMap { book -> getInBaseState(book, localRepository) }
            .toLceEventObservable { it }
    }

    override fun getBookSearch(text: String, page: Int): Single<List<BookUM>> {
        return booksApi.getBooksSearch(text, page)
            .map { list -> list.toUiModel() }
    }

    private fun getInBaseState(book: BookDetailsUM, localRepository: BooksLocalRepository): Observable<BookDetailsUM> {
        val isInHistory = localRepository.isInHistory(book.toBookListItemUM()).onErrorReturn { false }
        val isInWishLis = localRepository.isInWishList(book.toBookListItemUM()).onErrorReturn { false }
        val mBook = Observable.just(book)
        return  Observable.zip(
            mBook,
            isInHistory,
            isInWishLis,
            Function3 <BookDetailsUM, Boolean, Boolean, BookDetailsUM> { book, inHist, inWish ->
                book.copy(
                    isInHistory = inHist,
                    isInWishList = inWish
                )
            }
        )
    }

}