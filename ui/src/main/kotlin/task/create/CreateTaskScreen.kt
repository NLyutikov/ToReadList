package ru.appkode.base.ui.task.create

import io.reactivex.Observable
import ru.appkode.base.entities.core.ui.task.TaskUM
import ru.appkode.base.ui.core.core.LceState
import ru.appkode.base.ui.core.core.MviView

interface CreateTaskScreen {
  interface View : MviView<ViewState> {
    fun changeTaskTitleIntent(): Observable<String>
    fun changeTaskDescription(): Observable<String>
    fun createTaskIntent(): Observable<Unit>
  }

  data class ViewState(
    val task: TaskUM,
    val state: LceState<Unit>,
    val isButtonEnabled: Boolean
  )
}
