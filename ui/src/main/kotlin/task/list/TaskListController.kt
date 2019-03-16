package ru.appkode.base.ui.task.list

import androidx.core.widget.doOnTextChanged
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.Observable
import kotlinx.android.synthetic.main.task_list_controller.*
import ru.appkode.base.ui.R
import ru.appkode.base.ui.core.core.BaseMviController
import ru.appkode.base.ui.core.core.util.DefaultAppSchedulers
import ru.appkode.base.ui.core.core.util.filterEvents
import ru.appkode.base.ui.task.list.TaskListScreen.View
import ru.appkode.base.ui.task.list.TaskListScreen.ViewState

class TaskListController : BaseMviController<ViewState, View, TaskListPresenter>(), View {
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
  }

  override fun renderViewState(viewState: ViewState) {
    task_list_text.text = viewState.text
  }

  override fun updateTextIntent(): Observable<String> {
    return eventsRelay.filterEvents(EVENT_ID_TEXT_CHANGED)
  }

  override fun popRouterIntent(): Observable<Unit> {
    return task_list_button.clicks()
  }

  override fun createPresenter(): TaskListPresenter {
    return TaskListPresenter(DefaultAppSchedulers, this.router!!)
  }
}

private const val EVENT_ID_TEXT_CHANGED = 0
