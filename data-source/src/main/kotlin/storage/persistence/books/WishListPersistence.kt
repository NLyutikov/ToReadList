package ru.appkode.base.data.storage.persistence.books

import androidx.room.Dao
import androidx.room.Query
import io.reactivex.Observable
import io.reactivex.Single
import ru.appkode.base.data.storage.persistence.BasePersistence
import ru.appkode.base.entities.core.books.lists.wish.WishListSM

@Dao
interface WishListPersistence : BasePersistence<WishListSM> {

    @Query("SELECT * FROM wish_list WHERE id=:bookId")
    fun getBookById(bookId: Long): Single<WishListSM>

    @Query("SELECT COUNT(id) FROM wish_list WHERE id=:bookId")
    fun countNumById(bookId: Long): Single<Int>

    @Query("SELECT * FROM wish_list")
    fun getAllBooks(): Observable<List<WishListSM>>

    @Query("SELECT COUNT(id) FROM wish_list")
    fun getSize(): Single<Int>

}