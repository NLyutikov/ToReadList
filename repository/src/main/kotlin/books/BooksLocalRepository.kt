package ru.appkode.base.repository.books

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import ru.appkode.base.entities.core.books.details.BookDetailsUM
import ru.appkode.base.entities.core.books.lists.BookListItemUM

interface BooksLocalRepository {

    fun addToWishList(book: BookListItemUM): Completable

    fun addToHistory(book: BookListItemUM): Completable

    fun deleteFromWishList(book: BookListItemUM): Completable

    fun deleteFromHistory(book: BookListItemUM): Completable

    fun getWishList(): Observable<List<BookListItemUM>>

    fun getHistory(): Observable<List<BookListItemUM>>

    fun isInHistory(book: BookListItemUM): Single<Boolean>

    fun isInWishList(book: BookListItemUM): Single<Boolean>

}