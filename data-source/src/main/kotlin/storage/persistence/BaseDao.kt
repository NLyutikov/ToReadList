package ru.appkode.base.data.storage.persistence

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update

interface BaseDao<T> {

    @Insert
    fun insert(obj: T)

    @Insert
    fun insert(vararg obj: T)

    @Delete
    fun delete(obj: T)

    @Update
    fun update(obj: T)

}