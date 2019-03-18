package ru.appkode.base.ui.task.list

import io.reactivex.Observable
import ru.appkode.base.ui.core.core.MviView
import ru.appkode.base.ui.task.list.entities.TaskUM

interface TaskListScreen {

  interface View : MviView<ViewState> {
    fun switchTaskIntent(): Observable<Long>
    fun createTaskIntent(): Observable<Unit>
  }

  data class ViewState(
    val tasks: List<TaskUM>
  )
}