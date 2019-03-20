package ru.appkode.base.ui.task.create

import io.reactivex.Observable
import ru.appkode.base.repository.task.TaskRepository
import ru.appkode.base.ui.core.core.BasePresenter
import ru.appkode.base.ui.core.core.Command
import ru.appkode.base.ui.core.core.command
import ru.appkode.base.ui.core.core.util.AppSchedulers
import ru.appkode.base.ui.task.create.CreateTaskScreen.View
import ru.appkode.base.ui.task.create.CreateTaskScreen.ViewState
import ru.appkode.base.ui.task.list.entities.TaskUM

sealed class ScreenAction

data class ChangeTaskTitle(val text: String) : ScreenAction()
data class ChangeTaskDescription(val text: String) : ScreenAction()
object CreateTask : ScreenAction()

class CreateTaskPresenter(
  schedulers: AppSchedulers,
  private val taskRepository: TaskRepository
) : BasePresenter<View, ViewState, ScreenAction>(schedulers) {

  override fun createIntents(): List<Observable<out ScreenAction>> {
    return listOf(
      intent(View::changeTaskTitleIntent)
        .map { ChangeTaskTitle(it) },
      intent(View::changeTaskDescription)
        .map { ChangeTaskDescription(it) },
      intent(View::createTaskIntent)
        .map { CreateTask }
    )
  }

  override fun reduceViewState(
    previousState: ViewState,
    action: ScreenAction
  ): Pair<ViewState, Command<ScreenAction>?> {
    return when (action) {
      is ChangeTaskTitle -> processChangeTaskTitle(previousState, action)
      is ChangeTaskDescription -> processChangeTaskDescription(previousState, action)
      is CreateTask -> processCreateTask(previousState, action)
    }
  }

  private fun processCreateTask(
    previousState: ViewState,
    action: CreateTask
  ): Pair<ViewState, Command<ScreenAction>?> {
    return previousState to command {
      taskRepository.addTask(previousState.task)
    }
  }

  private fun processChangeTaskTitle(
    previousState: ViewState,
    action: ChangeTaskTitle
  ): Pair<ViewState, Command<ScreenAction>?> {
    return previousState.copy(task = previousState.task.copy(title = action.text)) to null
  }

  private fun processChangeTaskDescription(
    previousState: ViewState,
    action: ChangeTaskDescription
  ): Pair<ViewState, Command<ScreenAction>?> {
    return previousState.copy(task = previousState.task.copy(description = action.text)) to null
  }

  override fun createInitialState(): ViewState {
    return ViewState(
      task = TaskUM(
        id = NONE_ID_VALUE,
        title = "",
        description = "",
        isChecked = false
      )
    )
  }
}

private const val NONE_ID_VALUE = 0L
