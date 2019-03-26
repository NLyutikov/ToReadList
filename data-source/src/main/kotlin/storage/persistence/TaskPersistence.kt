package ru.appkode.base.data.storage.persistence

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import io.reactivex.Completable
import io.reactivex.Observable
import ru.appkode.base.entities.core.datasource.task.TaskSM

@Dao
interface TaskPersistence {

  @Query("SELECT * FROM task")
  fun getTasks(): Observable<List<TaskSM>>

  @Query("SELECT * FROM task WHERE id=:taskId")
  fun getTaskById(taskId: Long): Observable<TaskSM>

  @Update
  fun updateTask(task: TaskSM)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertTask(task: TaskSM)

  @Delete
  fun deleteTask(task: TaskSM)
}