package ru.appkode.base.ui.task.list

import com.bluelinelabs.conductor.Router
import io.reactivex.Observable
import ru.appkode.base.ui.core.core.BasePresenter
import ru.appkode.base.ui.core.core.Command
import ru.appkode.base.ui.core.core.command
import ru.appkode.base.ui.core.core.util.AppSchedulers
import ru.appkode.base.ui.core.core.util.obtainHorizontalTransaction
import ru.appkode.base.ui.task.create.CreateTaskController
import ru.appkode.base.ui.task.list.TaskListScreen.View
import ru.appkode.base.ui.task.list.TaskListScreen.ViewState
import ru.appkode.base.ui.task.list.entities.TaskUM

sealed class ScreenAction

data class SwitchTask(val id: String) : ScreenAction()
object CreateTask : ScreenAction()

class TaskListPresenter(
  schedulers: AppSchedulers,
  private val router: Router
) : BasePresenter<View, ViewState, ScreenAction>(schedulers) {
  override fun createIntents(): List<Observable<out ScreenAction>> {
    return listOf(
      intent(View::switchTaskIntent)
        .map { SwitchTask(it) },
      intent(View::createTaskIntent)
        .map { CreateTask }
    )
  }

  override fun reduceViewState(
    previousState: ViewState,
    action: ScreenAction
  ): Pair<ViewState, Command<ScreenAction>?> {
    return when (action) {
      is SwitchTask -> processSwitchTask(previousState, action)
      is CreateTask -> processCreateTask(previousState, action)
    }
  }

  private fun processSwitchTask(
    previousState: ViewState,
    action: SwitchTask
  ): Pair<ViewState, Command<ScreenAction>?> {
    val list = previousState.tasks.map { task ->
      if (task.id == action.id) task.copy(isChecked = !task.isChecked)
      else task
    }
    return previousState.copy(tasks = list) to null
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
      tasks = createMockTasks()
    )
  }
}

private fun createMockTasks(): List<TaskUM> {
  return List(30) { index ->
    TaskUM(
      id = index.toString(),
      title = "Task $index",
      description = "This is description of task $index",
      isChecked = false
    )
  }
}
