package ru.appkode.base.repository.task

import io.reactivex.Completable
import io.reactivex.Observable
import ru.appkode.base.ui.task.list.entities.TaskUM

interface TaskRepository {

  fun addTask(task: TaskUM): Completable

  fun updateTask(task: TaskUM): Completable

  fun tasks(): Observable<List<TaskUM>>
}
