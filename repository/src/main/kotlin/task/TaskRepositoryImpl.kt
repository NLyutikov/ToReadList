package ru.appkode.base.repository.task

import io.reactivex.Completable
import io.reactivex.Observable
import ru.appkode.base.data.storage.DatabaseHelper
import ru.appkode.base.entities.core.mappers.task.toStorageModel
import ru.appkode.base.entities.core.mappers.task.toUiModel
import ru.appkode.base.ui.core.core.util.AppSchedulers
import ru.appkode.base.ui.task.list.entities.TaskUM

class TaskRepositoryImpl(private val schedulers: AppSchedulers) : TaskRepository {

  private val taskPersistence = DatabaseHelper.getTaskPersistence()

  override fun addTask(task: TaskUM): Completable {
    return Completable.fromAction { taskPersistence.insertTask(task.toStorageModel()) }
      .subscribeOn(schedulers.io)
  }

  override fun updateTask(task: TaskUM): Completable {
    return Completable.fromAction { taskPersistence.updateTask(task.toStorageModel()) }
      .subscribeOn(schedulers.io)
  }

  override fun tasks(): Observable<List<TaskUM>> {
    return taskPersistence.getTasks().map { list ->
      list.map { it.toUiModel() }
    }
  }
}