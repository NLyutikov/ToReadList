package ru.appkode.base.data.storage.persistence

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update

interface BasePersistence<T> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(obj: T)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg obj: T)

    @Delete
    fun delete(obj: T)

    @Update
    fun update(obj: T)

}