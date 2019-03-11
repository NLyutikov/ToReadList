package ru.appkode.base.ui.task.list

import io.reactivex.Observable
import ru.appkode.base.ui.core.core.BasePresenter
import ru.appkode.base.ui.core.core.Command
import ru.appkode.base.ui.core.core.util.AppSchedulers
import ru.appkode.base.ui.task.list.TaskListScreen.View
import ru.appkode.base.ui.task.list.TaskListScreen.ViewState

sealed class ScreenAction

internal class TaskListPresenter(
    schedulers: AppSchedulers
) : BasePresenter<View, ViewState, ScreenAction>(schedulers) {
    override fun createIntents(): List<Observable<out ScreenAction>> {
        return emptyList()
    }

    override fun reduceViewState(
        previousState: ViewState,
        action: ScreenAction
    ): Pair<ViewState, Command<ScreenAction>?>{
        return previousState to null
    }

    override fun createInitialState(): ViewState {
        return ViewState(text = "Test")
    }
}