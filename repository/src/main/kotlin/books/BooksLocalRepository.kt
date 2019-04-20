package ru.appkode.base.repository.books

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import ru.appkode.base.entities.core.books.details.BookDetailsUM
import ru.appkode.base.entities.core.books.lists.BookListItemUM

interface BooksLocalRepository {

    fun addToWishList(book: BookListItemUM): Completable

    fun addToWishListFromHistory(book: BookListItemUM): Completable

    fun changeItemOrderInWishList(
        oldPos: Int,
        newPos: Int,
        book: BookListItemUM,
        left: BookListItemUM?,
        right: BookListItemUM?
    ): Observable<List<BookListItemUM>>

    fun addToHistory(book: BookListItemUM): Completable

    fun addToHistoryFromWishList(book: BookListItemUM): Completable

    fun deleteFromWishList(book: BookListItemUM): Completable

    fun deleteFromHistory(book: BookListItemUM): Completable

    fun getWishList(): Observable<List<BookListItemUM>>

    fun getWishListPage(page: Int): Observable<List<BookListItemUM>>

    fun getFirstWishListPages(numPages: Int): Observable<List<BookListItemUM>>

    fun getHistory(): Observable<List<BookListItemUM>>

    fun getHistoryPage(page: Int): Observable<List<BookListItemUM>>

    fun getFirstHistoryPages(numPages: Int): Observable<List<BookListItemUM>>

    fun isInHistory(book: BookListItemUM): Observable<Boolean>

    fun isInWishList(book: BookListItemUM): Observable<Boolean>

    fun getMaxOrder(): Observable<Long?>

    fun getWishListSize(): Observable<Int>

    fun deleteAllFromWishList(): Completable

    fun getInBaseState(book: BookDetailsUM): Observable<BookDetailsUM>

}