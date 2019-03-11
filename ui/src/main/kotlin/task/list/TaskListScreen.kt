package ru.appkode.base.ui.task.list

import ru.appkode.base.ui.core.core.MviView

internal interface TaskListScreen {

    interface View: MviView<ViewState>

    data class ViewState(
        val text: String
    )

}