package ru.appkode.base.ui.task.list

import io.reactivex.Observable
import ru.appkode.base.entities.core.ui.task.TaskUM
import ru.appkode.base.ui.core.core.LceState
import ru.appkode.base.ui.core.core.MviView

interface TaskListScreen {

  interface View : MviView<ViewState> {
    fun switchTaskIntent(): Observable<Long>
    fun changeTaskIntent(): Observable<Long>
    fun createTaskIntent(): Observable<Unit>
  }

  data class ViewState(
    val taskState: LceState<List<TaskUM>>
  )
}