package ru.appkode.base.repository

import ru.appkode.base.data.storage.DatabaseHelper
import ru.appkode.base.repository.task.TaskRepository
import ru.appkode.base.repository.task.TaskRepositoryImpl
import ru.appkode.base.ui.core.core.util.AppSchedulers

object RepositoryHelper {

  fun getTaskRepository(schedulers: AppSchedulers): TaskRepository {
    return TaskRepositoryImpl(schedulers, DatabaseHelper.getTaskPersistence())
  }

}
