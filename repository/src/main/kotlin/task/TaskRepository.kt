package ru.appkode.base.repository.task

import io.reactivex.Observable
import ru.appkode.base.ui.task.list.entities.TaskUM

interface TaskRepository {

  fun addTask(task: TaskUM)

  fun updateTask(task: TaskUM)

  fun tasks(): Observable<List<TaskUM>>
}
