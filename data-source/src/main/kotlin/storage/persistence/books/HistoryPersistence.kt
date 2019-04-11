package ru.appkode.base.data.storage.persistence.books

import androidx.room.Dao
import androidx.room.Query
import io.reactivex.Observable
import io.reactivex.Single
import ru.appkode.base.data.storage.persistence.BasePersistence
import ru.appkode.base.entities.core.books.lists.history.HistorySM

@Dao
interface HistoryPersistence : BasePersistence<HistorySM>{

    @Query("SELECT * FROM history WHERE id=:bookId")
    fun getBookById(bookId: Long): Single<HistorySM>

    @Query("SELECT COUNT(id) FROM history WHERE id=:bookId")
    fun countNumById(bookId: Long): Single<Int>

    @Query("SELECT * FROM history")
    fun getAllBooks(): Observable<List<HistorySM>>

    @Query("SELECT COUNT(id) FROM history")
    fun getSize(): Single<Int>

}