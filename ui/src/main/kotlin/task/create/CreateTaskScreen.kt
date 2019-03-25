package ru.appkode.base.ui.task.create

import io.reactivex.Observable
import ru.appkode.base.ui.core.core.MviView
import ru.appkode.base.ui.task.list.entities.TaskUM

interface CreateTaskScreen {
  interface View : MviView<ViewState>{
    fun changeTaskTitleIntent(): Observable<String>
    fun changeTaskDescription(): Observable<String>
    fun createTaskIntent(): Observable<Unit>
  }

  data class ViewState(
    val task: TaskUM,
    val isLoading: Boolean
  )
}