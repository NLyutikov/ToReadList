package ru.appkode.base.repository.books

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.functions.Function3
import io.reactivex.functions.Function4
import io.reactivex.internal.operators.completable.CompletableFromAction
import ru.appkode.base.data.storage.persistence.books.HistoryPersistence
import ru.appkode.base.data.storage.persistence.books.WishListPersistence
import ru.appkode.base.entities.core.books.details.BookDetailsUM
import ru.appkode.base.entities.core.books.details.toBookListItemUM
import ru.appkode.base.entities.core.books.lists.BookListItemUM
import ru.appkode.base.entities.core.books.lists.history.toBookListItemUM
import ru.appkode.base.entities.core.books.lists.toHistorySM
import ru.appkode.base.entities.core.books.lists.toWishListSM
import ru.appkode.base.entities.core.books.lists.wish.WishListSM
import ru.appkode.base.entities.core.books.lists.wish.toBookListItemUM
import ru.appkode.base.entities.core.movies.details.MovieDetailsUM
import ru.appkode.base.entities.core.movies.details.toBookListUM
import ru.appkode.base.ui.core.core.util.AppSchedulers
import java.util.*
import java.util.concurrent.TimeUnit

private const val PAGE_SIZE = 20
private const val BOTTOM_ORDER_LINE = Long.MIN_VALUE + 1
private const val TOP_ORDER_LINE = Long.MAX_VALUE - 1
private const val FIRST_ITEM_ORDER = 1L

class BooksLocalRepositoryImpl(
    private val appSchedulers: AppSchedulers,
    private val wishListPersistence: WishListPersistence,
    private val historyPersistence: HistoryPersistence,
    private val context: Context
) : BooksLocalRepository {

    override fun addToWishList(book: BookListItemUM): Completable {
        loadImg(book.imagePath)
        val book = Observable.just(book)
        val wishListSize = getWishListSize()
//        val add = getWishListSize()
//            .flatMap { size ->
//                Timber.e("size $size")
//                if (size > 0)
//                    getMaxOrder()
//                else
//                    Observable.just(BOTTOM_ORDER_LINE)
//            }.flatMap { maxOrder ->
//                Timber.e("max $maxOrder")
//                Observable.fromCallable { wishListPersistence.insert(book.toWishListSM(TOP_ORDER_LINE / 2 + maxOrder / 2)) }
//            }.subscribeOn(appSchedulers.io)
//        return Completable.fromObservable<Unit> { add }
        val maxOrder = getMaxOrder()
        return Completable.fromObservable<Unit> (
            Observable.zip(
                book,
                wishListSize,
                maxOrder,
                Function3<BookListItemUM, Int, Long?, Unit> { book, size, maxOrder ->
                    var newOrder = FIRST_ITEM_ORDER
                    if (size > 0)
                        newOrder = TOP_ORDER_LINE / 2 + maxOrder / 2
                    return@Function3 wishListPersistence.insert(book.toWishListSM(newOrder))
                }).subscribeOn(appSchedulers.io)
        ).subscribeOn(appSchedulers.io)
    }

    override fun addToWishListFromHistory(book: BookListItemUM): Completable {
        val add = addToWishList(book)
        val del = CompletableFromAction { historyPersistence.delete(book.toHistorySM()) }.subscribeOn(appSchedulers.io)
        return del.mergeWith(add)
    }

    override fun changeItemOrderInWishList(
        oldPos: Int,
        newPos: Int,
        book: BookListItemUM,
        bottom: BookListItemUM?,
        top: BookListItemUM?
    ): Observable<List<BookListItemUM>> {
        var newOrder = 0L
        when {
            top == null && bottom == null -> newOrder = FIRST_ITEM_ORDER
            top != null && bottom != null -> newOrder =  top.order!! / 2 + bottom.order!! / 2
            bottom != null -> newOrder = TOP_ORDER_LINE / 2 + bottom.order!! / 2
            top != null -> newOrder = top.order!! / 2 + BOTTOM_ORDER_LINE / 2
        }
        if (newOrder != 0L) {
            val bookWithNewOrder = book.copy(order = newOrder)
            return Observable.fromCallable { wishListPersistence.update(bookWithNewOrder.toWishListSM()) }
                .subscribeOn(appSchedulers.io)
                .flatMap { getWishList() }
        }
//
//        recalculateOrders(oldPos, newPos, book.toWishListSM())
//            .singleOrError()
//            .map {
//                wishListPersistence.deleteAll()
//                it
//            }
        return recalculateOrders(oldPos, newPos, book.toWishListSM())
            .map { books -> wishListPersistence.insert(*books.toTypedArray()) }
            .concatMap { getWishList() }
    }

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

        val add = addToHistory(book)

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

    override fun getMaxOrder(): Observable<Long?> {
        return wishListPersistence.getMaxOrder()
            .timeout(1, TimeUnit.SECONDS)
            .subscribeOn(appSchedulers.io)
            .onErrorReturn { BOTTOM_ORDER_LINE }
    }

    override fun getWishListSize(): Observable<Int> {
        return wishListPersistence.getSize().subscribeOn(appSchedulers.io).onErrorReturn { 0 }
    }

    override fun deleteAllFromWishList(): Completable {
        return Completable.fromAction { wishListPersistence.deleteAll() }.subscribeOn(appSchedulers.io)
    }

    override fun getInBaseState(book: BookDetailsUM): Observable<BookDetailsUM> {
        val isInHistory = isInHistory(book.toBookListItemUM()).onErrorReturn { false }
        val isInWishLis = isInWishList(book.toBookListItemUM()).onErrorReturn { false }
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

    override fun getInBaseState(book: MovieDetailsUM): Observable<MovieDetailsUM> {
        val isInHistory = isInHistory(book.toBookListUM()).onErrorReturn { false }
        val isInWishLis = isInWishList(book.toBookListUM()).onErrorReturn { false }
        val mBook = Observable.just(book)
        return  Observable.zip(
            mBook,
            isInHistory,
            isInWishLis,
            Function3 <MovieDetailsUM, Boolean, Boolean, MovieDetailsUM> { book, inHist, inWish ->
                book.copy(
                    isInHistory = inHist,
                    isInWishList = inWish
                )
            }
        )
    }

    override fun getInBaseState(book: BookListItemUM): Observable<BookListItemUM> {
        val isInHistory = isInHistory(book).onErrorReturn { false }
        val isInWishLis = isInWishList(book).onErrorReturn { false }
        val mBook = Observable.just(book)
        return  Observable.zip(
            mBook,
            isInHistory,
            isInWishLis,
            Function3 <BookListItemUM, Boolean, Boolean, BookListItemUM> { book, inHist, inWish ->
                book.copy(
                    isInHistory = inHist,
                    isInWishList = inWish
                )
            }
        )
    }

    private fun recalculateOrders(oldPos: Int, newPos: Int, item: WishListSM): Observable<List<WishListSM>> {
        val wishSize = wishListPersistence.getSize().subscribeOn(appSchedulers.io)
        val books = wishListPersistence.getAllBooks().subscribeOn(appSchedulers.io).timeout(1, TimeUnit.SECONDS)
        val pos = Observable.just(newPos)
        val book = Observable.just(item)
        return Observable.zip(
            wishSize,
            books,
            pos,
            book,
            Function4<Int, List<WishListSM>, Int, WishListSM, List<WishListSM>> { size, books, pos, book->
                val dif = TOP_ORDER_LINE / (size + 1) * 2
                var currentItemOrder = BOTTOM_ORDER_LINE

                val list = ArrayList(books)
                list.removeAt(oldPos)
                list.add(newPos, books[oldPos])

                val recalcBooks  = list.toList()
                return@Function4 recalcBooks.map { item ->
                    currentItemOrder += dif
                    item.copy(order = currentItemOrder)
                }
            })
    }

    private fun loadImg(imagePath: String?) {
        Glide.with(context)
            .load(imagePath)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .preload()
    }

    private fun getDateInMillis()  = Calendar.getInstance().time.time

}
