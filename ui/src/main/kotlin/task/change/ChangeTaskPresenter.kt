package ru.appkode.base.ui.task.change

import com.bluelinelabs.conductor.Router
import io.reactivex.Observable
import ru.appkode.base.entities.core.task.TaskUM
import ru.appkode.base.repository.task.TaskRepository
import ru.appkode.base.ui.core.core.BasePresenter
import ru.appkode.base.ui.core.core.Command
import ru.appkode.base.ui.core.core.LceState
import ru.appkode.base.ui.core.core.command
import ru.appkode.base.ui.core.core.util.AppSchedulers
import ru.appkode.base.ui.task.change.ChangeTaskScreen.View
import ru.appkode.base.ui.task.change.ChangeTaskScreen.ViewState

sealed class ScreenAction

data class ChangeTaskTitle(val text: String) : ScreenAction()
data class ChangeTaskDescription(val text: String) : ScreenAction()
object SaveTask : ScreenAction()
data class UpdateState(val state: LceState<Unit>) : ScreenAction()
data class SetTask(val task: TaskUM) : ScreenAction()
object DeleteTask : ScreenAction()

class ChangeTaskPresenter(
  schedulers: AppSchedulers,
  private val taskRepository: TaskRepository,
  private val router: Router,
  private val taskId: Long
) : BasePresenter<View, ViewState, ScreenAction>(schedulers) {

  override fun createIntents(): List<Observable<out ScreenAction>> {
    return listOf(
      intent(View::changeTaskTitleIntent)
        .map { ChangeTaskTitle(it) },
      intent(View::changeTaskDescription)
        .map { ChangeTaskDescription(it) },
      intent(View::saveTaskIntent)
        .map { SaveTask },
      intent(View::deleteTask)
        .map { DeleteTask },
      intent { taskRepository.task(taskId) }
        .map { SetTask(it) }
    )
  }

  override fun reduceViewState(
    previousState: ViewState,
    action: ScreenAction
  ): Pair<ViewState, Command<Observable<ScreenAction>>?> {
    return when (action) {
      is ChangeTaskTitle -> processChangeTaskTitle(previousState, action)
      is ChangeTaskDescription -> processChangeTaskDescription(previousState, action)
      is SaveTask -> processSaveTask(previousState, action)
      is UpdateState -> processUpdateState(previousState, action)
      is SetTask -> processSetTask(previousState, action)
      is DeleteTask -> processDeleteTask(previousState, action)
    }
  }

  private fun processDeleteTask(
    previousState: ViewState,
    action: DeleteTask
  ): Pair<ViewState, Command<Observable<ScreenAction>>?> {
    return previousState to command(
      taskRepository.deleteTask(previousState.task)
        .doLceAction { UpdateState(it) }
        .doOnComplete { router.popCurrentController() }
    )
  }

  private fun processSetTask(
    previousState: ViewState,
    action: SetTask
  ): Pair<ViewState, Command<Observable<ScreenAction>>?> {
    return previousState.copy(
      task = action.task,
      isButtonEnabled = true
    ) to null
  }

  private fun processUpdateState(
    previousState: ViewState,
    action: UpdateState
  ): Pair<ViewState, Command<Observable<ScreenAction>>?> {
    return previousState.copy(state = action.state) to null
  }

  private fun processSaveTask(
    previousState: ViewState,
    action: SaveTask
  ): Pair<ViewState, Command<Observable<ScreenAction>>?> {
    return previousState to command(
      taskRepository.updateTask(previousState.task)
        .doLceAction { UpdateState(it) }
        .doOnComplete { router.popCurrentController() }
    )
  }

  private fun processChangeTaskTitle(
    previousState: ViewState,
    action: ChangeTaskTitle
  ): Pair<ViewState, Command<Observable<ScreenAction>>?> {
    return previousState.copy(
      task = previousState.task.copy(title = action.text),
      isButtonEnabled = action.text.isNotBlank()
    ) to null
  }

  private fun processChangeTaskDescription(
    previousState: ViewState,
    action: ChangeTaskDescription
  ): Pair<ViewState, Command<Observable<ScreenAction>>?> {
    return previousState.copy(task = previousState.task.copy(description = action.text)) to null
  }

  override fun createInitialState(): ViewState {
    return ViewState(
      task = TaskUM(
        id = NONE_ID_VALUE,
        title = "",
        description = "",
        isChecked = false
      ),
      state = LceState.Content(Unit),
      isButtonEnabled = false
    )
  }
}

private const val NONE_ID_VALUE = 0L
