package ru.appkode.base.ui.task.list

import io.reactivex.Observable
import ru.appkode.base.ui.core.core.BasePresenter
import ru.appkode.base.ui.core.core.Command
import ru.appkode.base.ui.core.core.command
import ru.appkode.base.ui.core.core.routing.Route
import ru.appkode.base.ui.core.core.routing.Router
import ru.appkode.base.ui.core.core.util.AppSchedulers
import ru.appkode.base.ui.task.list.TaskListScreen.View
import ru.appkode.base.ui.task.list.TaskListScreen.ViewState

sealed class ScreenAction

data class UpdateText(val text: String): ScreenAction()
object PopRouter: ScreenAction()

internal class TaskListPresenter(
    schedulers: AppSchedulers,
    private val router: Router<Route>
) : BasePresenter<View, ViewState, ScreenAction>(schedulers) {
    override fun createIntents(): List<Observable<out ScreenAction>> {
        return listOf(
            intent (View::updateTextIntent)
                .map { UpdateText(it) },
            intent(View::popRouterIntent)
                .map { PopRouter }
        )
    }

    override fun reduceViewState(
        previousState: ViewState,
        action: ScreenAction
    ): Pair<ViewState, Command<ScreenAction>?> {
        return when (action) {
            is UpdateText -> processUpdateText(previousState, action)
            is PopRouter -> processPopRouter(previousState, action)
        }
    }

    private fun processPopRouter(
        previousState: ViewState,
        action: PopRouter
    ): Pair<ViewState, Command<ScreenAction>?> {
        return previousState to command { router.pop() }
    }

    private fun processUpdateText(
        previousState: ViewState,
        action: UpdateText
    ): Pair<ViewState, Command<ScreenAction>?> {
        val newText = if (action.text.isNotBlank()) action.text else "Enter text"
        return previousState.copy(text = newText) to null
    }

    override fun createInitialState(): ViewState {
        return ViewState(text = "Enter text")
    }
}