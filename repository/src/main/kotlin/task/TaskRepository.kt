package ru.appkode.base.repository.task

import io.reactivex.Completable
import io.reactivex.Observable
import ru.appkode.base.entities.core.ui.task.TaskUM

interface TaskRepository {

  fun addTask(task: TaskUM): Completable

  fun updateTask(task: TaskUM): Completable

  fun deleteTask(task: TaskUM): Completable

  fun task(taskId: Long): Observable<TaskUM>

  fun tasks(): Observable<List<TaskUM>>
}
