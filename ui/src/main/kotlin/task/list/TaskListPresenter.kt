package ru.appkode.base.ui.task.list

import com.bluelinelabs.conductor.Router
import io.reactivex.Observable
import ru.appkode.base.entities.core.task.TaskUM
import ru.appkode.base.repository.task.TaskRepository
import ru.appkode.base.ui.core.core.BasePresenter
import ru.appkode.base.ui.core.core.Command
import ru.appkode.base.ui.core.core.LceState
import ru.appkode.base.ui.core.core.command
import ru.appkode.base.ui.core.core.util.AppSchedulers
import ru.appkode.base.ui.core.core.util.obtainHorizontalTransaction
import ru.appkode.base.ui.core.core.util.obtainVerticalTransaction
import ru.appkode.base.ui.task.change.ChangeTaskController
import ru.appkode.base.ui.task.create.CreateTaskController
import ru.appkode.base.ui.task.list.TaskListScreen.View
import ru.appkode.base.ui.task.list.TaskListScreen.ViewState

sealed class ScreenAction

data class SwitchTask(val id: Long) : ScreenAction()
object CreateTask : ScreenAction()
data class ChangeTask(val id: Long) : ScreenAction()
data class UpdateList(val state: LceState<List<TaskUM>>) : ScreenAction()

class TaskListPresenter(
  schedulers: AppSchedulers,
  private val taskRepository: TaskRepository,
  private val router: Router
) : BasePresenter<View, ViewState, ScreenAction>(schedulers) {
  override fun createIntents(): List<Observable<out ScreenAction>> {
    return listOf(
      intent(View::switchTaskIntent)
        .map { SwitchTask(it) },
      intent(View::createTaskIntent)
        .map { CreateTask },
      intent(View::changeTaskIntent)
        .map { ChangeTask(it) },
      intent { taskRepository.tasks() }
        .map { UpdateList(LceState.Content(it)) }
    )
  }

  override fun reduceViewState(
    previousState: ViewState,
    action: ScreenAction
  ): Pair<ViewState, Command<Observable<ScreenAction>>?> {
    return when (action) {
      is SwitchTask -> processSwitchTask(previousState, action)
      is CreateTask -> processCreateTask(previousState, action)
      is UpdateList -> processUpdateList(previousState, action)
      is ChangeTask -> processChangeTask(previousState, action)
    }
  }

  private fun processChangeTask(
    previousState: ViewState,
    action: ChangeTask
  ): Pair<ViewState, Command<Observable<ScreenAction>>?> {
    return previousState to command {
      router.pushController(
        ChangeTaskController.createController(action.id)
          .obtainVerticalTransaction()
      )
    }
  }

  private fun processUpdateList(
    previousState: ViewState,
    action: UpdateList
  ): Pair<ViewState, Command<Observable<ScreenAction>>?> {
    return previousState.copy(duckState = action.state) to null
  }

  private fun processSwitchTask(
    previousState: ViewState,
    action: SwitchTask
  ): Pair<ViewState, Command<Observable<ScreenAction>>?> {
    var currentTask: TaskUM? = null
    val tasks = previousState.duckState.asContent()
    val list = tasks.map { task ->
      if (task.id == action.id) {
        currentTask = task.copy(isChecked = !task.isChecked)
        currentTask!!
      } else task
    }
    return previousState.copy(duckState = LceState.Content(list)) to command(
      taskRepository.updateTask(currentTask!!)
        .toObservable()
    )
  }

  private fun processCreateTask(
    previousState: ViewState,
    action: CreateTask
  ): Pair<ViewState, Command<Observable<ScreenAction>>?> {
    return previousState to command {
      router.pushController(CreateTaskController().obtainHorizontalTransaction())
    }
  }

  override fun createInitialState(): ViewState {
    return ViewState(
      duckState = LceState.Loading()
    )
  }
}
