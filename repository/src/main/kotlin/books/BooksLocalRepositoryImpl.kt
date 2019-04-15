package ru.appkode.base.repository.books

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.internal.operators.completable.CompletableFromAction
import ru.appkode.base.data.storage.persistence.books.HistoryPersistence
import ru.appkode.base.data.storage.persistence.books.WishListPersistence
import ru.appkode.base.entities.core.books.lists.BookListItemUM
import ru.appkode.base.entities.core.books.lists.history.toBookListItemUM
import ru.appkode.base.entities.core.books.lists.toHistorySM
import ru.appkode.base.entities.core.books.lists.toWishListSM
import ru.appkode.base.entities.core.books.lists.wish.toBookListItemUM
import ru.appkode.base.ui.core.core.util.AppSchedulers
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception
import kotlin.concurrent.thread

const val BASE_IMAGE_NAME = "to_read_list_book_"
const val IMAGE_DIR = "image_dir"
private const val PAGE_SIZE = 20

class BooksLocalRepositoryImpl(
    private val appSchedulers: AppSchedulers,
    private val wishListPersistence: WishListPersistence,
    private val historyPersistence: HistoryPersistence,
    private val context: Context
) : BooksLocalRepository {

    companion object {
        fun getImageNameById(id: Long) = BASE_IMAGE_NAME + id.toString() + "png"
    }

    override fun addToWishList(book: BookListItemUM): Completable {
        loadImg(getImageNameById(book.id))
        return CompletableFromAction { wishListPersistence.insert(book.toWishListSM()) }.subscribeOn(appSchedulers.io)
    }

    override fun addToWishListFromHistory(book: BookListItemUM): Completable {
        val add = CompletableFromAction { wishListPersistence.insert(book.toWishListSM()) }.subscribeOn(appSchedulers.io)
        val del = CompletableFromAction { historyPersistence.delete(book.toHistorySM()) }.subscribeOn(appSchedulers.io)
        return del.mergeWith(add)
    }

    override fun addToHistory(book: BookListItemUM): Completable {
        loadImg(getImageNameById(book.id))
        return CompletableFromAction { historyPersistence.insert(book.toHistorySM()) }.subscribeOn(appSchedulers.io)
    }

    override fun addToHistoryFromWishList(book: BookListItemUM): Completable {
        val del = CompletableFromAction { wishListPersistence.delete(book.toWishListSM()) }.subscribeOn(appSchedulers.io)
        val add = CompletableFromAction { historyPersistence.insert(book.toHistorySM()) }.subscribeOn(appSchedulers.io)
        loadImg(book.imagePath)
        return del.mergeWith(add)
    }

    override fun deleteFromWishList(book: BookListItemUM): Completable {
        val directory = context.getDir(IMAGE_DIR, Context.MODE_PRIVATE)
        val imageFile = File(directory, book.imagePath)
        imageFile.delete()
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
        return wishListPersistence.getBooks(
            limit = PAGE_SIZE,
            offset = if (page != 0) (page - 1) * PAGE_SIZE else 0
        ).map { bookSM -> bookSM.toBookListItemUM() }
            .subscribeOn(appSchedulers.io)
    }

    override fun getHistoryPage(page: Int): Observable<List<BookListItemUM>> {
        return historyPersistence.getBooks(
            limit = PAGE_SIZE,
            offset = if (page != 0)(page - 1) * PAGE_SIZE else 0
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

    private fun loadImg(imagePath: String?) {
        Picasso.get().load(imagePath).into(picassoImageTarget(IMAGE_DIR, imagePath))
    }

    private fun picassoImageTarget(imageDir: String, imageName: String?): Target {
        val contextWrapper = ContextWrapper(context)
        val directory = contextWrapper.getDir(imageDir, Context.MODE_PRIVATE)
        return object : Target {
            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}

            override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {}

            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                thread {
                    val imageFile = File(directory, imageName)
                    var fos: FileOutputStream? = null
                    try {
                        fos = FileOutputStream(imageFile)
                        bitmap?.compress(Bitmap.CompressFormat.PNG, 100, fos)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    } finally {
                        fos?.close()
                    }
                }
            }
        }
    }
}