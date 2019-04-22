package ru.appkode.base.repository.books

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.Function3
import ru.appkode.base.data.network.books.BooksApi
import ru.appkode.base.data.network.movies.MovieAPI
import ru.appkode.base.entities.core.books.details.BookDetailsUM
import ru.appkode.base.entities.core.books.details.toBookListItemUM
import ru.appkode.base.entities.core.books.details.toUiModel
import ru.appkode.base.entities.core.books.lists.BookListItemUM
import ru.appkode.base.entities.core.books.search.BookUM
import ru.appkode.base.entities.core.books.search.toUiModel
import ru.appkode.base.entities.core.movies.details.MovieDetailsUM
import ru.appkode.base.entities.core.movies.details.toUiModel
import ru.appkode.base.entities.core.movies.search.toListOfBookListUM
import ru.appkode.base.ui.core.core.LceState
import ru.appkode.base.ui.core.core.util.AppSchedulers
import ru.appkode.base.ui.core.core.util.toLceEventObservable

class BooksNetworkRepositoryImpl(
    val schedulers: AppSchedulers,
    private val booksApi: BooksApi,
    private val movieAPI: MovieAPI
) : BooksNetworkRepository {

    override fun getBookDetails(
        bookId: Long,
        localRepository: BooksLocalRepository
    ): Observable<LceState<BookDetailsUM>> {
        return booksApi.getBooksDetails(bookId)
            .map { book -> book.toUiModel() }
            .flatMap { book -> localRepository.getInBaseState(book) }
            .toLceEventObservable { it }
    }

    override fun getBookSearch(
        text: String,
        localRepository: BooksLocalRepository,
        page: Int
    ): Observable<List<BookListItemUM>> {
        return booksApi.getBooksSearch(text, page)
            .map { list -> list.toUiModel() }
            .flatMapIterable { it }
            .flatMap { localRepository.getInBaseState(it) }
            .map { listOf(it) }
    }

    override fun getMovieDetails(
        id: Long,
        localRepository: BooksLocalRepository
    ): Observable<MovieDetailsUM> {
        return movieAPI.getMovieById(Math.abs(id))
            .map { it.toUiModel() }
            .flatMap { localRepository.getInBaseState(it) }
    }

    override fun getMovieSearch(
        text: String,
        localRepository: BooksLocalRepository,
        page: Int
    ): Observable<List<BookListItemUM>> {
        return movieAPI.searchMoviesPaged(text, page)
            .map { it.toListOfBookListUM() }
    }
}