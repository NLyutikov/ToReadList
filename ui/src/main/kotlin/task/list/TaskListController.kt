package ru.appkode.base.ui.task.list

import kotlinx.android.synthetic.main.task_list_controller.*
import ru.appkode.base.ui.R
import ru.appkode.base.ui.core.core.BaseMviController
import ru.appkode.base.ui.core.core.util.DefaultAppSchedulers
import ru.appkode.base.ui.task.list.TaskListScreen.View
import ru.appkode.base.ui.task.list.TaskListScreen.ViewState

internal class TaskListController : BaseMviController<ViewState, View, TaskListPresenter>(){
    override fun createConfig(): Config {
        return object : Config {
            override val viewLayoutResource: Int
                get() = R.layout.task_list_controller
        }
    }

    override fun initializeView(rootView: android.view.View) {}

    override fun renderViewState(viewState: ViewState) {
        text.text = viewState.text
    }

    override fun createPresenter(): TaskListPresenter {
        return TaskListPresenter(DefaultAppSchedulers)
    }
}