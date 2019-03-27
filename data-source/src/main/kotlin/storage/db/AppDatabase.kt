package ru.appkode.base.data.storage.db

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.appkode.base.data.storage.persistence.TaskPersistence
import ru.appkode.base.entities.core.datasource.task.TaskSM


private const val DATABASE_VERSION = 1
const val DATABASE_NAME = "task.db"

@Database(
  entities = [TaskSM::class],
  version = DATABASE_VERSION
)
abstract class AppDatabase : RoomDatabase() {
  abstract fun taskPersistence(): TaskPersistence
}
