package ru.appkode.base.data.storage.db

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.appkode.base.data.storage.persistence.books.HistoryPersistence
import ru.appkode.base.data.storage.persistence.books.WishListPersistence
import ru.appkode.base.data.storage.persistence.task.TaskPersistence
import ru.appkode.base.entities.core.books.lists.history.HistorySM
import ru.appkode.base.entities.core.books.lists.wish.WishListSM
import ru.appkode.base.entities.core.task.TaskSM


private const val DATABASE_VERSION = 1
const val DATABASE_NAME = "task.db"

@Database(
  entities = [TaskSM::class, WishListSM::class, HistorySM::class],
  version = DATABASE_VERSION
)
abstract class AppDatabase : RoomDatabase() {
  abstract fun taskPersistence(): TaskPersistence
  abstract fun wishListPersistence(): WishListPersistence
  abstract fun historyPersistence(): HistoryPersistence
}
