package ru.appkode.base.ui.task.list

import io.reactivex.Observable
import ru.appkode.base.ui.core.core.MviView
import ru.appkode.base.ui.task.list.entities.TaskUM
import java.util.*

interface TaskListScreen {

  interface View : MviView<ViewState> {
    fun switchTaskIntent(): Observable<String>
  }

  data class ViewState(
    val tasks: List<TaskUM>,
    val checkedTask: LinkedList<String>
  )
}