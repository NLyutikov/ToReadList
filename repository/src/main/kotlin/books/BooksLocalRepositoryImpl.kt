package ru.appkode.base.repository.books

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.Single
import ru.appkode.base.data.storage.persistence.books.HistoryPersistence
import ru.appkode.base.data.storage.persistence.books.WishListPersistence
import ru.appkode.base.entities.core.books.details.BookDetailsUM
import ru.appkode.base.entities.core.books.lists.BookListItemUM
import ru.appkode.base.entities.core.books.lists.history.toBookListItemUM
import ru.appkode.base.entities.core.books.lists.toHistoryListSM
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

class BooksLocalRepositoryImpl(
    private val appSchedulers: AppSchedulers,
    private val wishListPersistence: WishListPersistence,
    private val historyPersistence: HistoryPersistence,
    private val context: Context
) : BooksLocalRepository {

    override fun addToWishList(book: BookListItemUM): Completable {
        val imageName = getImageNameById(book.id)
        Picasso.get().load(book.imagePath).into(picassoImageTarget(IMAGE_DIR, imageName))

        return Completable.fromAction {
            wishListPersistence.insert(book.toWishListSM())
        }.subscribeOn(appSchedulers.io)
    }

    override fun addToHistory(book: BookListItemUM): Completable {
        val imageName = getImageNameById(book.id)
        Picasso.get().load(book.imagePath).into(picassoImageTarget(IMAGE_DIR, imageName))

        return Completable.fromAction {
            historyPersistence.insert(book.toHistoryListSM())
        }.subscribeOn(appSchedulers.io)
    }

    override fun deleteFromWishList(book: BookListItemUM): Completable {
        val imageName = getImageNameById(book.id)
        val directory = context.getDir(IMAGE_DIR, Context.MODE_PRIVATE)
        val imageFile = File(directory, imageName.toString())
        val d = imageFile.delete()
        Timber.d("Image delete $imageFile, result $d")

        return Completable.fromAction {
            wishListPersistence.delete(book.toWishListSM())
        }.subscribeOn(appSchedulers.io)
    }

    override fun deleteFromHistory(book: BookListItemUM): Completable {
        val imageName = getImageNameById(book.id)
        val directory = context.getDir(IMAGE_DIR, Context.MODE_PRIVATE)
        val imageFile = File(directory, imageName)
        val d = imageFile.delete()
        Timber.d("Image delete $imageFile, result $d")

        return Completable.fromAction {
            historyPersistence.delete(book.toHistoryListSM())
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

    override fun isInHistory(book: BookListItemUM): Single<Boolean> {
        return historyPersistence.countNumById(book.id)
            .map { num -> num > 0}
            .subscribeOn(appSchedulers.io)
    }

    override fun isInWishList(book: BookListItemUM): Single<Boolean> {
        return wishListPersistence.countNumById(book.id)
            .map { num -> num > 0}
            .subscribeOn(appSchedulers.io)
    }

    private fun getImageNameById(id: Long) = BASE_IMAGE_NAME + id.toString() + "png"

    private fun picassoImageTarget(imageDir: String, imageName: String): Target {
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