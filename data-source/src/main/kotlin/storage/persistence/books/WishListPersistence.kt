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
    fun getBookById(bookId: Long): Observable<WishListSM>

    @Query("SELECT COUNT(id) FROM wish_list WHERE id=:bookId")
    fun countNumById(bookId: Long): Observable<Int>

    @Query("SELECT * FROM wish_list ORDER BY priority DESC")
    fun getAllBooks(): Observable<List<WishListSM>>

    @Query("SELECT * FROM wish_list ORDER BY priority DESC LIMIT :limit OFFSET  :offset")
    fun getBooks(limit: Int, offset: Int): Observable<List<WishListSM>>

    @Query("SELECT COUNT(id) FROM wish_list")
    fun getSize(): Observable<Int>

    @Query("SELECT MAX(priority) FROM wish_list")
    fun getMaxOrder(): Observable<Long?>

    @Query("DELETE FROM wish_list")
    fun deleteAll()

}