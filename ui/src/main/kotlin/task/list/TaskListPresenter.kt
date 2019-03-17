package ru.appkode.base.ui.task.list

import com.bluelinelabs.conductor.Router
import io.reactivex.Observable
import ru.appkode.base.ui.core.core.BasePresenter
import ru.appkode.base.ui.core.core.Command
import ru.appkode.base.ui.core.core.util.AppSchedulers
import ru.appkode.base.ui.task.list.TaskListScreen.View
import ru.appkode.base.ui.task.list.TaskListScreen.ViewState
import ru.appkode.base.ui.task.list.entities.TaskUM
import java.util.*

sealed class ScreenAction

data class SwitchTask(val id: String) : ScreenAction()

class TaskListPresenter(
  schedulers: AppSchedulers,
  private val router: Router
) : BasePresenter<View, ViewState, ScreenAction>(schedulers) {
  override fun createIntents(): List<Observable<out ScreenAction>> {
    return listOf(
      intent(View::switchTaskIntent)
        .map { SwitchTask(it) }
    )
  }

  override fun reduceViewState(
    previousState: ViewState,
    action: ScreenAction
  ): Pair<ViewState, Command<ScreenAction>?> {
    return when (action) {
      is SwitchTask -> processSwitchTask(previousState, action)
    }
  }

  private fun processSwitchTask(
    previousState: ViewState,
    action: SwitchTask
  ): Pair<ViewState, Command<ScreenAction>?> {
    val list = (previousState.checkedTask.clone() as LinkedList<String>).apply {
      if (this.contains(action.id)) {
        this.remove(action.id)
      } else this.add(action.id)
    }
    return previousState.copy(checkedTask = list) to null
  }

  override fun createInitialState(): ViewState {
    return ViewState(
      tasks = createMockTasks(),
      checkedTask = LinkedList()
    )
  }
}

private fun createMockTasks(): List<TaskUM> {
  return List(30) { index ->
    TaskUM(
      id = index.toString(),
      title = "Task $index",
      description = "This is description of task $index"
    )
  }
}
