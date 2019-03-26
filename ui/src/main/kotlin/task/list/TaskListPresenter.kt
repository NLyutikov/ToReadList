package ru.appkode.base.ui.task.list

import com.bluelinelabs.conductor.Router
import io.reactivex.Observable
import ru.appkode.base.repository.task.TaskRepository
import ru.appkode.base.ui.core.core.Command
import ru.appkode.base.ui.core.core.LceState
import ru.appkode.base.ui.core.core.NewBasePresenter
import ru.appkode.base.ui.core.core.command
import ru.appkode.base.ui.core.core.util.AppSchedulers
import ru.appkode.base.ui.core.core.util.obtainHorizontalTransaction
import ru.appkode.base.ui.task.create.CreateTaskController
import ru.appkode.base.ui.task.list.TaskListScreen.View
import ru.appkode.base.ui.task.list.TaskListScreen.ViewState
import ru.appkode.base.ui.task.list.entities.TaskUM

sealed class ScreenAction

data class SwitchTask(val id: Long) : ScreenAction()
object CreateTask : ScreenAction()
data class UpdateList(val state: LceState<List<TaskUM>>) : ScreenAction()

class TaskListPresenter(
  schedulers: AppSchedulers,
  private val taskRepository: TaskRepository,
  private val router: Router
) : NewBasePresenter<View, ViewState, ScreenAction>(schedulers) {
  override fun createIntents(): List<Observable<out ScreenAction>> {
    return listOf(
      intent(View::switchTaskIntent)
        .map { SwitchTask(it) },
      intent(View::createTaskIntent)
        .map { CreateTask },
      intent { taskRepository.tasks() }
        .map { UpdateList(LceState.Content(it)) }
    )
  }

  override fun reduceViewState(
    previousState: ViewState,
    action: ScreenAction
  ): Pair<ViewState, Command<ScreenAction>?> {
    return when (action) {
      is SwitchTask -> processSwitchTask(previousState, action)
      is CreateTask -> processCreateTask(previousState, action)
      is UpdateList -> processUpdateList(previousState, action)
    }
  }

  private fun processUpdateList(
    previousState: ViewState,
    action: UpdateList
  ): Pair<ViewState, Command<ScreenAction>?> {
    return previousState.copy(taskState = action.state) to null
  }

  private fun processSwitchTask(
    previousState: ViewState,
    action: SwitchTask
  ): Pair<ViewState, Command<ScreenAction>?> {
    var currentTask: TaskUM? = null
    val tasks = previousState.taskState.asContent()
    val list = tasks.map { task ->
      if (task.id == action.id) {
        currentTask = task.copy(isChecked = !task.isChecked)
        currentTask!!
      } else task
    }
    return previousState.copy(taskState = LceState.Content(list)) to command {
      taskRepository.updateTask(currentTask!!)
        .safeSubscribe()
    }
  }

  private fun processCreateTask(
    previousState: ViewState,
    action: CreateTask
  ): Pair<ViewState, Command<ScreenAction>?> {
    return previousState to command {
      router.pushController(CreateTaskController().obtainHorizontalTransaction())
    }
  }

  override fun createInitialState(): ViewState {
    return ViewState(
      taskState = LceState.Loading()
    )
  }
}
