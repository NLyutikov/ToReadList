package ru.appkode.base.ui.task.list

import io.reactivex.Observable
import ru.appkode.base.ui.core.core.MviView

internal interface TaskListScreen {

    interface View: MviView<ViewState> {
        fun updateTextIntent(): Observable<String>
        fun popRouterIntent(): Observable<Unit>
    }

    data class ViewState(
        val text: String
    )
}