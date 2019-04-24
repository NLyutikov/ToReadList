package ru.appkode.base.data.storage.persistence.books

import androidx.room.Dao
import androidx.room.Query
import io.reactivex.Observable
import io.reactivex.Single
import ru.appkode.base.data.storage.persistence.BasePersistence
import ru.appkode.base.entities.core.books.lists.history.HistorySM
import ru.appkode.base.entities.core.books.lists.wish.WishListSM

@Dao
interface HistoryPersistence : BasePersistence<HistorySM>{

    @Query("SELECT * FROM history WHERE id=:bookId")
    fun getBookById(bookId: Long): Observable<HistorySM>

    @Query("SELECT COUNT(id) FROM history WHERE id=:bookId")
    fun countNumById(bookId: Long): Observable<Int>

    @Query("SELECT * FROM history ORDER BY date DESC ")
    fun getAllBooks(): Observable<List<HistorySM>>

    @Query("SELECT * FROM history ORDER BY date DESC LIMIT :limit OFFSET :offset")
    fun getBooks(limit: Int, offset: Int): Observable<List<HistorySM>>

    @Query("SELECT COUNT(id) FROM history")
    fun getSize(): Observable<Int>

}