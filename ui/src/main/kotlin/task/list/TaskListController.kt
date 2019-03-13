package ru.appkode.base.ui.task.list

import androidx.core.widget.doOnTextChanged
import io.reactivex.Observable
import kotlinx.android.synthetic.main.task_list_controller.*
import ru.appkode.base.ui.R
import ru.appkode.base.ui.core.core.BaseMviController
import ru.appkode.base.ui.core.core.util.*
import ru.appkode.base.ui.task.list.TaskListScreen.View
import ru.appkode.base.ui.task.list.TaskListScreen.ViewState

internal class TaskListController : BaseMviController<ViewState, View, TaskListPresenter>(), View {
    override fun createConfig(): Config {
        return object : Config {
            override val viewLayoutResource: Int
                get() = R.layout.task_list_controller
        }
    }

    override fun initializeView(rootView: android.view.View) {
        task_list_input.doOnTextChanged { text, _, _, _ ->
            if (text != null)
                eventsRelay.accept(EVENT_ID_TEXT_CHANGED to text.toString())
        }
        task_list_button.setOnClickListener { eventsRelay.accept(EVENT_ID_BUTTON_CKICKED to Unit) }
    }

    override fun renderViewState(viewState: ViewState) {
        task_list_text.text = viewState.text
    }

    override fun updateTextIntent(): Observable<String> {
        return eventsRelay.filterEvents(EVENT_ID_TEXT_CHANGED)
    }

    override fun popRouterIntent(): Observable<Unit> {
        return eventsRelay.filterEvents(EVENT_ID_BUTTON_CKICKED)
    }

    override fun createPresenter(): TaskListPresenter {
        return TaskListPresenter(DefaultAppSchedulers, this.getAppRouter())
    }
}

private const val EVENT_ID_TEXT_CHANGED = 0
private const val EVENT_ID_BUTTON_CKICKED = 1