package ru.appkode.base.repository.task

import io.reactivex.Observable
import ru.appkode.base.data.storage.DatabaseHelper
import ru.appkode.base.entities.core.mappers.task.toStorageModel
import ru.appkode.base.entities.core.mappers.task.toUiModel
import ru.appkode.base.ui.task.list.entities.TaskUM

class TaskRepositoryImpl: TaskRepository {

  private val taskPersistence = DatabaseHelper.getTaskPersistence()

  override fun addTask(task: TaskUM) {
    taskPersistence.insertTask(task.toStorageModel())
  }

  override fun updateTask(task: TaskUM) {
    taskPersistence.updateTask(task.toStorageModel())
  }

  override fun tasks(): Observable<List<TaskUM>> {
    return taskPersistence.getTasks().map { list ->
      list.map { it.toUiModel() }
    }
  }
}