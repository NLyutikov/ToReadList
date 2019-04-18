package ru.appkode.base.repository.books

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.internal.operators.completable.CompletableFromAction
import ru.appkode.base.data.storage.persistence.books.HistoryPersistence
import ru.appkode.base.data.storage.persistence.books.WishListPersistence
import ru.appkode.base.entities.core.books.lists.BookListItemUM
import ru.appkode.base.entities.core.books.lists.history.toBookListItemUM
import ru.appkode.base.entities.core.books.lists.toHistorySM
import ru.appkode.base.entities.core.books.lists.toWishListSM
import ru.appkode.base.entities.core.books.lists.wish.WishListSM
import ru.appkode.base.entities.core.books.lists.wish.toBookListItemUM
import ru.appkode.base.ui.core.core.util.AppSchedulers
import java.util.*

private const val PAGE_SIZE = 20
private const val BOTTOM_ORDER_LINE = Long.MIN_VALUE + 1
private const val TOP_ORDER_LINE = Long.MAX_VALUE - 1

class BooksLocalRepositoryImpl(
    private val appSchedulers: AppSchedulers,
    private val wishListPersistence: WishListPersistence,
    private val historyPersistence: HistoryPersistence,
    private val context: Context
) : BooksLocalRepository {

    override fun addToWishList(book: BookListItemUM): Completable {
        loadImg(book.imagePath)
        return CompletableFromAction { wishListPersistence.insert(book.toWishListSM()) }.subscribeOn(appSchedulers.io)
    }

    override fun addToWishListFromHistory(book: BookListItemUM): Completable {
        val add = CompletableFromAction { wishListPersistence.insert(book.toWishListSM()) }.subscribeOn(appSchedulers.io)
        val del = CompletableFromAction { historyPersistence.delete(book.toHistorySM()) }.subscribeOn(appSchedulers.io)
        return del.mergeWith(add)
    }

//    override fun addToWishList(book: BookListItemUM, leftBook: BookListItemUM, rightBook: BookListItemUM): Completable {
//        var newOrder = 0L
//        if (rightBook.order!! - leftBook.order!! > 1)
//            newOrder = (rightBook.order!! - leftBook.order!!)/2
//        else
//            newOrder = recalculateOrders()
//        return Completable.fromAction { wishListPersistence.insert(book.toWishListSM(newOrder)) }
//    }

    override fun addToHistory(book: BookListItemUM): Completable {
        loadImg(book.imagePath)
        return CompletableFromAction {
            historyPersistence.insert(book.toHistorySM(getDateInMillis()))
        }.subscribeOn(appSchedulers.io)
    }

    override fun addToHistoryFromWishList(book: BookListItemUM): Completable {
        val del = CompletableFromAction {
            wishListPersistence.delete(book.toWishListSM())
        }.subscribeOn(appSchedulers.io)

        val add = CompletableFromAction {
            historyPersistence.insert(book.toHistorySM(getDateInMillis()))
        }.subscribeOn(appSchedulers.io)

        return del.mergeWith(add)
    }

    override fun deleteFromWishList(book: BookListItemUM): Completable {
        return Completable.fromAction {
            wishListPersistence.delete(book.toWishListSM())
        }.subscribeOn(appSchedulers.io)
    }

    override fun deleteFromHistory(book: BookListItemUM): Completable {
        return Completable.fromAction {
            historyPersistence.delete(book.toHistorySM())
        }.subscribeOn(appSchedulers.io)
    }

    override fun getWishList(): Observable<List<BookListItemUM>> {
        return wishListPersistence.getAllBooks()
            .map { bookSM ->  bookSM.toBookListItemUM()}
            .subscribeOn(appSchedulers.io)
    }

    override fun getHistory(): Observable<List<BookListItemUM>> {
        return historyPersistence.getAllBooks()
            .map { bookSM -> bookSM.toBookListItemUM()}
            .subscribeOn(appSchedulers.io)
    }

    override fun getWishListPage(page: Int): Observable<List<BookListItemUM>> {
        return wishListPersistence
            .getBooks(
                limit = PAGE_SIZE,
                offset = if (page != 0) (page - 1) * PAGE_SIZE else 0
            )
            .map { bookSM -> bookSM.toBookListItemUM() }
            .subscribeOn(appSchedulers.io)
    }

    override fun getHistoryPage(page: Int): Observable<List<BookListItemUM>> {
        return historyPersistence
            .getBooks(
                limit = PAGE_SIZE,
                offset = if (page != 0)(page - 1) * PAGE_SIZE else 0
            )
            .map { bookSM -> bookSM.toBookListItemUM() }
            .subscribeOn(appSchedulers.io)
    }

    override fun getFirstWishListPages(numPages: Int): Observable<List<BookListItemUM>> {
        return wishListPersistence
            .getBooks(
                limit = numPages * PAGE_SIZE,
                offset = 0
            ).map { bookSM -> bookSM.toBookListItemUM() }
            .subscribeOn(appSchedulers.io)
    }

    override fun getFirstHistoryPages(numPages: Int): Observable<List<BookListItemUM>> {
        return historyPersistence
            .getBooks(
                limit = numPages * PAGE_SIZE,
                offset = 0
            ).map { bookSM -> bookSM.toBookListItemUM() }
            .subscribeOn(appSchedulers.io)
    }

    override fun isInHistory(book: BookListItemUM): Observable<Boolean> {
        return historyPersistence.countNumById(book.id)
            .map { num -> num > 0}
            .subscribeOn(appSchedulers.io)
    }

    override fun isInWishList(book: BookListItemUM): Observable<Boolean> {
        return wishListPersistence.countNumById(book.id)
            .map { num -> num > 0}
            .subscribeOn(appSchedulers.io)
    }

//    private fun recalculateOrders(): Completable {
//        val wishSize = wishListPersistence.getSize()
//        val books = wishListPersistence.getAllBooks()
//        val newWishList =  Observable.zip(
//            wishSize,
//            books,
//            BiFunction<Int, List<WishListSM>, List<WishListSM>> { size, books ->
//                val dif = TOP_ORDER_LINE/size
//                var currentItemOrder = BOTTOM_ORDER_LINE
//                books.map { book ->
//                    currentItemOrder += dif
//                    book.copy(order = currentItemOrder)
//                }
//            })
//        }
//        return
//    }

    private fun loadImg(imagePath: String?) {
        Glide.with(context)
            .load(imagePath)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .preload()
    }

    private fun getDateInMillis()  = Calendar.getInstance().time.time

}
