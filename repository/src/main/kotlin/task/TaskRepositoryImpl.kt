package ru.appkode.base.repository.task

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.internal.operators.completable.CompletableFromAction
import ru.appkode.base.data.storage.persistence.TaskPersistence
import ru.appkode.base.entities.core.mappers.task.toStorageModel
import ru.appkode.base.entities.core.mappers.task.toUiModel
import ru.appkode.base.entities.core.ui.task.TaskUM
import ru.appkode.base.ui.core.core.util.AppSchedulers

class TaskRepositoryImpl(
  private val schedulers: AppSchedulers,
  private val taskPersistence: TaskPersistence
) : TaskRepository {

  override fun addTask(task: TaskUM): Completable {
    return Completable
      .fromAction { taskPersistence.insertTask(task.toStorageModel()) }
      .subscribeOn(schedulers.io)
  }

  override fun updateTask(task: TaskUM): Completable {
    return Completable.fromAction { taskPersistence.updateTask(task.toStorageModel()) }
      .subscribeOn(schedulers.io)
  }

  override fun deleteTask(task: TaskUM): Completable {
    return CompletableFromAction { taskPersistence.deleteTask(task.toStorageModel()) }
      .subscribeOn(schedulers.io)
  }

  override fun task(taskId: Long): Observable<TaskUM> {
    return taskPersistence.getTaskById(taskId)
      .map { it.toUiModel() }
      .subscribeOn(schedulers.io)
  }

  override fun tasks(): Observable<List<TaskUM>> {
    return taskPersistence.getTasks()
      .map { list ->
        list.map { it.toUiModel() }
      }
      .subscribeOn(schedulers.io)
  }
}
